import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterUserResponse,
  JwtPayload,
} from '../models/auth.model';
import { CartService } from './cart.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly cartService = inject(CartService);

  private readonly TOKEN_KEY = 'sf_token';
  private readonly API = '/api/auth';

  private _token = signal<string | null>(this.loadToken());
  readonly isLoggedIn = computed(() => !!this._token() && !this.isTokenExpired(this._token()!));
  readonly currentUser = computed(() => (this._token() ? this.decodeToken(this._token()!) : null));
  readonly role = computed(() => this.currentUser()?.role ?? null);
  readonly isAdmin = computed(() => this.role() === 'ADMIN');

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API}/login`, request).pipe(
      tap((res) => {
        this.saveToken(res.token);
        this._token.set(res.token);
        this.cartService.refreshCart().subscribe();
      }),
    );
  }

  register(request: RegisterRequest): Observable<RegisterUserResponse> {
    return this.http.post<RegisterUserResponse>(`${this.API}/register`, request);
  }

  verifyEmail(token: string): Observable<string> {
    return this.http.get(`${this.API}/verify-email`, { params: { token }, responseType: 'text' });
  }

  resendVerification(email: string): Observable<void> {
    return this.http.post<void>(`${this.API}/resend-verification`, { email });
  }

  forgotPassword(email: string): Observable<void> {
    return this.http.post<void>(`${this.API}/forgot-password`, { email });
  }

  resetPassword(token: string, password: string): Observable<void> {
    return this.http.post<void>(`${this.API}/reset-password`, { token, password });
  }

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    this._token.set(null);
    this.cartService.clearLocal();
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this._token();
  }

  initSession(): void {
    if (this.isLoggedIn()) {
      this.cartService.refreshCart().subscribe();
    }
  }

  private saveToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  private loadToken(): string | null {
    const token = sessionStorage.getItem(this.TOKEN_KEY);
    if (token && this.isTokenExpired(token)) {
      sessionStorage.removeItem(this.TOKEN_KEY);
      return null;
    }
    return token;
  }

  private decodeToken(token: string): JwtPayload | null {
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64)) as JwtPayload;
    } catch {
      return null;
    }
  }

  private isTokenExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    if (!payload) return true;
    return payload.exp * 1000 < Date.now();
  }
}
