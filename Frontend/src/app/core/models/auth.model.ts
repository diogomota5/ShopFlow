export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  telephone?: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterUserResponse {
  nome: string;
  email: string;
}

export interface JwtPayload {
  sub: string;
  role: 'USER' | 'ADMIN';
  userId?: string;
  exp: number;
  iat: number;
}
