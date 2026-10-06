import { Injectable, signal } from '@angular/core';
import { CouponResponse } from '../models/coupon.model';

@Injectable({ providedIn: 'root' })
export class CheckoutStateService {
  private readonly COUPON_KEY = 'sf_checkout_coupon';

  readonly couponCode = signal<string | null>(this.loadCouponCode());
  readonly coupon = signal<CouponResponse | null>(null);

  setCoupon(code: string, coupon: CouponResponse): void {
    sessionStorage.setItem(this.COUPON_KEY, code);
    this.couponCode.set(code);
    this.coupon.set(coupon);
  }

  clearCoupon(): void {
    sessionStorage.removeItem(this.COUPON_KEY);
    this.couponCode.set(null);
    this.coupon.set(null);
  }

  private loadCouponCode(): string | null {
    return sessionStorage.getItem(this.COUPON_KEY);
  }
}
