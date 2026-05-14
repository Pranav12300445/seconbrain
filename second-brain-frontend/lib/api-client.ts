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
              this.refreshPromise = this.refreshToken();
            }
            const newToken = await this.refreshPromise;
            this.refreshPromise = null;

            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return this.client(originalRequest);
          } catch (refreshError) {
            this.clearToken();
            window.location.href = '/login';
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  private getToken(): string | null {
    return Cookies.get('auth_token') || localStorage.getItem('auth_token');
  }

  private setToken(token: string): void {
    Cookies.set('auth_token', token, { expires: 7 });
    localStorage.setItem('auth_token', token);
  }

  private clearToken(): void {
    Cookies.remove('auth_token');
    localStorage.removeItem('auth_token');
  }

  private async refreshToken(): Promise<string> {
    const response = await this.client.post('/auth/refresh');
    const { token } = response.data;
    this.setToken(token);
    return token;
  }

  // Auth endpoints
  async register(email: string, password: string) {
    const response = await this.client.post('/auth/register', { email, password });
    if (response.data.token) {
      this.setToken(response.data.token);
    }
    return response.data;
  }

  async login(email: string, password: string) {
    const response = await this.client.post('/auth/login', { email, password });
    if (response.data.token) {
      this.setToken(response.data.token);
    }
    return response.data;
  }

  async logout() {
    try {
      await this.client.post('/auth/logout');
    } finally {
      this.clearToken();
    }
  }

  // Notes endpoints
  async createNote(title: string, content: string) {
    return this.client.post('/notes', { title, content });
  }

  async getNotes() {
    return this.client.get('/notes');
  }

  async updateNote(noteId: string, title: string, content: string) {
    return this.client.put(`/notes/${noteId}`, { title, content });
  }

  async deleteNote(noteId: string) {
    return this.client.delete(`/notes/${noteId}`);
  }

  // File endpoints
  async uploadFile(noteId: string, file: File) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('noteId', noteId);

    return this.client.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  async getFiles() {
    return this.client.get('/files');
  }

  async getNoteFiles(noteId: string) {
    return this.client.get(`/files/notes/${noteId}`);
  }

  async deleteFile(fileId: string) {
    return this.client.delete(`/files/${fileId}`);
  }

  getInstance(): AxiosInstance {
    return this.client;
  }
}

export const apiClient = new ApiClient();
