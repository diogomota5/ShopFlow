import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Page } from '../models/api-response.model';
import { OrderRequest, OrderResponse, OrderStatus } from '../models/order.model';

export interface DashboardSummary {
  productCount: number; categoryCount: number; orderCount: number; pendingOrderCount: number; revenue: number;
  topProducts: { name: string; quantity: number }[]; salesByDay: { date: string; total: number }[];
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly API = '/api/orders';

  create(request: OrderRequest): Observable<ApiResponse<OrderResponse>> {
    return this.http.post<ApiResponse<OrderResponse>>(`${this.API}/create`, request);
  }

  findById(orderId: string): Observable<ApiResponse<OrderResponse>> {
    return this.http.get<ApiResponse<OrderResponse>>(`${this.API}/search/${orderId}`);
  }

  findMyOrders(
    page = 0,
    size = 10,
    status?: OrderStatus,
  ): Observable<ApiResponse<Page<OrderResponse>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<ApiResponse<Page<OrderResponse>>>(`${this.API}/search`, { params });
  }

  findAllForAdmin(page = 0, size = 20, status?: OrderStatus): Observable<ApiResponse<Page<OrderResponse>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<ApiResponse<Page<OrderResponse>>>(`${this.API}/admin/search`, { params });
  }

  getDashboardSummary(startDate?: string, endDate?: string): Observable<ApiResponse<DashboardSummary>> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', `${startDate}T00:00:00`);
    if (endDate) params = params.set('endDate', `${endDate}T23:59:59`);
    return this.http.get<ApiResponse<DashboardSummary>>(`${this.API}/admin/dashboard`, { params });
  }

  updateStatus(orderId: string, status: OrderStatus, trackingCode?: string): Observable<ApiResponse<OrderResponse>> {
    const action: Record<Exclude<OrderStatus, 'PENDING'>, string> = { PAID: 'paid', SHIPPED: 'ship', DELIVERED: 'deliver', CANCELED: 'cancel' };
    if (status === 'PENDING') throw new Error('Não é possível retornar um pedido ao status pendente.');
    return this.http.put<ApiResponse<OrderResponse>>(`${this.API}/status/${orderId}/${action[status]}`, status === 'SHIPPED' ? { trackingCode } : {});
  }

  reopen(orderId: string): Observable<ApiResponse<OrderResponse>> {
    return this.http.put<ApiResponse<OrderResponse>>(`${this.API}/status/${orderId}/reopen`, {});
  }

  cancelByCustomer(orderId: string): Observable<ApiResponse<OrderResponse>> {
    return this.http.put<ApiResponse<OrderResponse>>(`${this.API}/${orderId}/cancel`, {});
  }
}
