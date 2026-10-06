import { Component, OnInit, inject, signal } from '@angular/core';
import { TitleCasePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { CouponService } from '../../../core/services/coupon.service';
import { CheckoutStateService } from '../../../core/services/checkout-state.service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { CartItemResponse, CartResponse } from '../../../core/models/cart.model';
import { CouponResponse } from '../../../core/models/coupon.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [RouterLink, FormsModule, TitleCasePipe],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent implements OnInit {
  private cartService = inject(CartService);
  private auth = inject(AuthService);
  private router = inject(Router);
  private couponService = inject(CouponService);
  private checkoutState = inject(CheckoutStateService);
  private confirmService = inject(ConfirmDialogService);

  cart = signal<CartResponse | null>(null);
  loading = signal(true);
  updating = signal<string | null>(null);
  couponCode = '';
  coupon = signal<CouponResponse | null>(null);
  couponError = signal('');
  validatingCoupon = signal(false);

  get finalPrice(): number {
    const total = this.cart()?.totalPrice ?? 0;
    const c = this.coupon();
    if (!c) return total;
    if (c.discountType === 'PERCENTAGE') return Math.max(0, total * (1 - c.discountValue / 100));
    return Math.max(0, total - c.discountValue);
  }

  get discount(): number {
    return (this.cart()?.totalPrice ?? 0) - this.finalPrice;
  }

  ngOnInit(): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.cartService.getMyCart().subscribe({
      next: (res) => {
        this.cart.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  updateQty(item: CartItemResponse, delta: number): void {
    const newQty = item.quantity + delta;
    if (newQty < 1) {
      this.removeItem(item);
      return;
    }
    this.updating.set(item.id);
    this.cartService.updateItem(item.id, { quantity: newQty }).subscribe({
      next: (res) => {
        this.cart.set(res.data);
        this.updating.set(null);
      },
      error: () => this.updating.set(null),
    });
  }

  async removeItem(item: CartItemResponse): Promise<void> {
    const confirmed = await this.confirmService.confirm({
      title: 'Remover Item',
      message: `Deseja realmente remover o produto "${item.productName}" do seu carrinho?`,
      confirmText: 'Remover',
      cancelText: 'Cancelar',
      type: 'danger',
    });
    if (!confirmed) return;

    this.updating.set(item.id);
    this.cartService.removeItem(item.id).subscribe({
      next: (res) => {
        this.cart.set(res.data);
        this.updating.set(null);
      },
      error: () => this.updating.set(null),
    });
  }

  async clearCart(): Promise<void> {
    const confirmed = await this.confirmService.confirm({
      title: 'Esvaziar Carrinho',
      message: 'Tem certeza que deseja remover todos os itens do seu carrinho?',
      confirmText: 'Esvaziar',
      cancelText: 'Cancelar',
      type: 'danger',
    });
    if (!confirmed) return;

    this.cartService.clearCart().subscribe(() => {
      this.cart.set(null);
      this.coupon.set(null);
      this.checkoutState.clearCoupon();
    });
  }

  applyCoupon(): void {
    const code = this.couponCode.trim();
    if (!code) return;
    this.validatingCoupon.set(true);
    this.couponError.set('');
    const total = this.cart()?.totalPrice ?? 0;
    this.couponService.validate(code, total).subscribe({
      next: (res) => {
        this.coupon.set(res.data);
        this.checkoutState.setCoupon(code, res.data);
        this.validatingCoupon.set(false);
      },
      error: (err: any) => {
        this.couponError.set(err.error?.message ?? 'Cupom inválido ou expirado.');
        this.coupon.set(null);
        this.checkoutState.clearCoupon();
        this.validatingCoupon.set(false);
      },
    });
  }

  removeCoupon(): void {
    this.coupon.set(null);
    this.couponCode = '';
    this.couponError.set('');
    this.checkoutState.clearCoupon();
  }

  goToCheckout(): void {
    if (this.coupon()) {
      this.checkoutState.setCoupon(this.couponCode, this.coupon()!);
    }
    this.router.navigate(['/checkout']);
  }

  formatPrice(val: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  }
}
