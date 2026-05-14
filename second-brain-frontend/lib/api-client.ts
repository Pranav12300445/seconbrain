import axios, { AxiosError, AxiosInstance } from 'axios';
import Cookies from 'js-cookie';

const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

class ApiClient {
  private client: AxiosInstance;
  private refreshPromise: Promise<string> | null = null;

  constructor() {
    this.client = axios.create({
      baseURL: BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = this.getToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor for token refresh
    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        const originalRequest = error.config as any;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            if (!this.refreshPromise) {
              this.refreshPromise = this.refreshAccessToken();
            }
            const newToken = await this.refreshPromise;
            this.refreshPromise = null;

            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return this.client(originalRequest);
          } catch (refreshError) {
            this.clearTokens();
            if (typeof window !== 'undefined') {
              window.location.href = '/login';
            }
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  // ─── Token helpers ────────────────────────────────────────────────────
  private getToken(): string | null {
    if (typeof window === 'undefined') return null;
    return Cookies.get('auth_token') || localStorage.getItem('auth_token');
  }

  private getRefreshToken(): string | null {
    if (typeof window === 'undefined') return null;
    return Cookies.get('refresh_token') || localStorage.getItem('refresh_token');
  }

  private setTokens(token: string, refreshToken?: string): void {
    Cookies.set('auth_token', token, { expires: 7 });
    localStorage.setItem('auth_token', token);
    if (refreshToken) {
      Cookies.set('refresh_token', refreshToken, { expires: 7 });
      localStorage.setItem('refresh_token', refreshToken);
    }
  }

  private clearTokens(): void {
    Cookies.remove('auth_token');
    Cookies.remove('refresh_token');
    localStorage.removeItem('auth_token');
    localStorage.removeItem('refresh_token');
  }

  // ─── Token refresh ────────────────────────────────────────────────────
  private async refreshAccessToken(): Promise<string> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    const response = await this.client.post('/auth/refresh', { refreshToken });
    const { token, refreshToken: newRefreshToken } = response.data;
    this.setTokens(token, newRefreshToken);
    return token;
  }

  // ─── Auth endpoints ───────────────────────────────────────────────────
  async register(email: string, password: string) {
    const response = await this.client.post('/auth/register', { email, password });
    if (response.data.token) {
      this.setTokens(response.data.token, response.data.refreshToken);
    }
    return response.data;
  }

  async login(email: string, password: string) {
    const response = await this.client.post('/auth/login', { email, password });
    if (response.data.token) {
      this.setTokens(response.data.token, response.data.refreshToken);
    }
    return response.data;
  }

  async logout() {
    try {
      const refreshToken = this.getRefreshToken();
      if (refreshToken) {
        await this.client.post('/auth/logout', { refreshToken });
      }
    } finally {
      this.clearTokens();
    }
  }

  // ─── Notes endpoints ──────────────────────────────────────────────────
  async createNote(title: string, content: string) {
    const response = await this.client.post('/notes', { title, content });
    return response;
  }

  async getNotes() {
    const response = await this.client.get('/notes');
    return response;
  }

  async updateNote(noteId: string | number, title: string, content: string) {
    const response = await this.client.put(`/notes/${noteId}`, { title, content });
    return response;
  }

  async deleteNote(noteId: string | number) {
    const response = await this.client.delete(`/notes/${noteId}`);
    return response;
  }

  // ─── File endpoints ───────────────────────────────────────────────────
  async uploadFile(file: File, noteId?: number | string | null) {
    const formData = new FormData();
    formData.append('file', file);

    // noteId is a query parameter, not a form field
    const params: Record<string, any> = {};
    if (noteId && noteId !== 'default') {
      params.noteId = noteId;
    }

    return this.client.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      params,
    });
  }

  async getFiles() {
    return this.client.get('/files');
  }

  async getNoteFiles(noteId: string | number) {
    // Backend path is /files/note/{noteId}  (singular "note")
    return this.client.get(`/files/note/${noteId}`);
  }

  async deleteFile(fileId: string | number) {
    return this.client.delete(`/files/${fileId}`);
  }

  getInstance(): AxiosInstance {
    return this.client;
  }
}

export const apiClient = new ApiClient();
