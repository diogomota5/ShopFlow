import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { CartResponse, CartItemRequest, CartItemUpdateRequest } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);
  private readonly API = '/api/carts';

  private _cart = signal<CartResponse | null>(null);
  readonly cart = computed(() => this._cart());
  readonly cartCount = computed(() => this._cart()?.totalItems ?? 0);

  refreshCart(): Observable<ApiResponse<CartResponse>> {
    return this.http
      .get<ApiResponse<CartResponse>>(this.API)
      .pipe(tap((res) => this._cart.set(res.data)));
  }

  getMyCart(): Observable<ApiResponse<CartResponse>> {
    return this.refreshCart();
  }

  addItem(request: CartItemRequest): Observable<ApiResponse<CartResponse>> {
    return this.http
      .post<ApiResponse<CartResponse>>(`${this.API}/items`, request)
      .pipe(tap((res) => this._cart.set(res.data)));
  }

  updateItem(
    itemId: string,
    request: CartItemUpdateRequest,
  ): Observable<ApiResponse<CartResponse>> {
    return this.http
      .put<ApiResponse<CartResponse>>(`${this.API}/items/${itemId}`, request)
      .pipe(tap((res) => this._cart.set(res.data)));
  }

  removeItem(itemId: string): Observable<ApiResponse<CartResponse>> {
    return this.http
      .delete<ApiResponse<CartResponse>>(`${this.API}/items/clear/${itemId}`)
      .pipe(tap((res) => this._cart.set(res.data)));
  }

  clearCart(): Observable<ApiResponse<void>> {
    return this.http
      .delete<ApiResponse<void>>(`${this.API}/clear`)
      .pipe(tap(() => this._cart.set(null)));
  }

  clearLocal(): void {
    this._cart.set(null);
  }
}
