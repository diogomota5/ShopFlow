import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { CouponResponse } from '../models/coupon.model';

export interface CouponCreateRequest {
  code: string;
  discountValue: number;
  discountType: 'PERCENTAGE' | 'FIXED';
  minPurchaseValue?: number;
  expirationDate: string; // ISO string - backend expects LocalDateTime
  maxUses?: number;
  active?: boolean;
}

@Injectable({ providedIn: 'root' })
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly API = '/api/coupons';

  validate(code: string, cartTotal: number): Observable<ApiResponse<CouponResponse>> {
    const params = new HttpParams().set('code', code).set('cartTotal', cartTotal);
    return this.http.get<ApiResponse<CouponResponse>>(`${this.API}/validate`, { params });
  }

  createCoupon(body: CouponCreateRequest): Observable<ApiResponse<CouponResponse>> {
    return this.http.post<ApiResponse<CouponResponse>>(`${this.API}/create`, body);
  }

  findAll(): Observable<ApiResponse<CouponResponse[]>> {
    return this.http.get<ApiResponse<CouponResponse[]>>(`${this.API}/search`);
  }

  updateCoupon(
    id: string,
    body: Partial<CouponCreateRequest>,
  ): Observable<ApiResponse<CouponResponse>> {
    return this.http.put<ApiResponse<CouponResponse>>(`${this.API}/update/${id}`, body);
  }

  setActive(id: string, active: boolean): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.API}/${id}/active`, null, {
      params: new HttpParams().set('active', active),
    });
  }
}
