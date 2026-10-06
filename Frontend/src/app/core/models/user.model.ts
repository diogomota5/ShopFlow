export interface User {
  id: string;
  name: string;
  email: string;
  telephone: string;
  role: 'USER' | 'ADMIN';
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserUpdateRequest {
  name?: string;
  email?: string;
  telephone?: string;
  role?: string;
  active?: boolean;
}

export interface UserResponse {
  id: string;
  name: string;
  email: string;
  telephone: string;
  role: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
