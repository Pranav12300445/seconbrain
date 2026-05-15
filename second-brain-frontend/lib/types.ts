/**
 * Type definitions for Second Brain API responses and requests
 * These types match the backend DTO classes exactly.
 */

// Auth Types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

// Matches backend AuthResponse.java
export interface AuthResponse {
  id: number;
  name: string;
  email: string;
  role: string;
  token: string;
  refreshToken: string;
}

export interface User {
  id: string;
  email: string;
}

// Notes Types — matches backend NoteResponse.java
export interface Note {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
  favorite: boolean;
}

export interface CreateNoteRequest {
  title: string;
  content: string;
  favorite?: boolean;
}

export interface UpdateNoteRequest {
  title: string;
  content: string;
  favorite?: boolean;
}

// Files Types — matches backend FileAttachmentResponse.java
export interface FileData {
  id: number;
  originalName: string;
  publicUrl: string;
  contentType: string;
  fileSize: number;
  noteId?: number;
  uploadedAt: string;
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
