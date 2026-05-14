/**
 * Type definitions for Second Brain API responses and requests
 */

// Auth Types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userId: string;
  id?: string;
  email: string;
}

export interface User {
  id: string;
  email: string;
}

// Notes Types
export interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateNoteRequest {
  title: string;
  content: string;
}

export interface UpdateNoteRequest {
  title: string;
  content: string;
}

export interface NotesResponse {
  data: Note[];
  message?: string;
}

// Files Types
export interface FileData {
  id: string;
  fileName: string;
  fileSize: number;
  fileType: string;
  noteId?: string;
  createdAt: string;
  uploadedAt?: string;
}

export interface FilesResponse {
  data: FileData[];
  message?: string;
}

export interface FileUploadResponse {
  id: string;
  fileName: string;
  fileSize: number;
  fileType: string;
  message?: string;
}

// API Error Type
export interface ApiError {
  message: string;
  status?: number;
  code?: string;
}

// Generic API Response
export interface ApiResponse<T> {
  data: T;
  message?: string;
  status?: number;
}
