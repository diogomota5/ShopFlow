import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CouponService } from '../../../core/services/coupon.service';
import { CouponResponse } from '../../../core/models/coupon.model';

@Component({
  selector: 'app-create-coupon',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-coupon.component.html',
  styleUrls: ['../create-product/create-product.component.css'],
})
export class CreateCouponComponent implements OnInit {
  private fb = inject(FormBuilder);
  private couponService = inject(CouponService);

  couponForm: FormGroup;
  message: string = '';
  coupons: CouponResponse[] = [];
  couponSearch = '';
  editingCouponId: string | null = null;

  constructor() {
    this.couponForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(3)]],
      discountValue: [0, [Validators.required, Validators.min(0.01)]],
      discountType: ['PERCENTAGE', Validators.required],
      minPurchaseValue: [0, [Validators.min(0)]],
      expirationDate: ['', Validators.required],
      maxUses: [0, [Validators.min(0)]],
    });
  }

  ngOnInit(): void {}

  loadCoupons(): void {
    this.couponService.findAll().subscribe({
      next: (res) => {
        const query = this.couponSearch.trim().toLocaleLowerCase('pt-BR');
        this.coupons = (res.data ?? []).filter(
          (coupon) => !query || coupon.code.toLocaleLowerCase('pt-BR').includes(query),
        );
      },
    });
  }

  startEditCoupon(coupon: CouponResponse): void {
    this.editingCouponId = coupon.id;
    // Format date for datetime-local input
    const expDate = coupon.expirationDate ? coupon.expirationDate.substring(0, 16) : '';
    this.couponForm.patchValue({
      code: coupon.code,
      discountValue: coupon.discountValue,
      discountType: coupon.discountType,
      minPurchaseValue: coupon.minPurchaseValue ?? 0,
      expirationDate: expDate,
      maxUses: coupon.maxUses ?? 0,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit(): void {
    this.editingCouponId = null;
    this.couponForm.reset({ discountType: 'PERCENTAGE', minPurchaseValue: 0, maxUses: 0 });
  }

  toggleCoupon(coupon: CouponResponse): void {
    this.couponService
      .setActive(coupon.id, !coupon.active)
      .subscribe({ next: () => this.loadCoupons() });
  }

  onSubmit() {
    if (this.couponForm.invalid) return;

    // Add seconds to conform strictly to LocalDateTime if necessary
    const rawVal = this.couponForm.value;
    const payload = {
      ...rawVal,
      expirationDate:
        rawVal.expirationDate.length === 16 ? rawVal.expirationDate + ':00' : rawVal.expirationDate,
    };

    if (this.editingCouponId) {
      this.couponService.updateCoupon(this.editingCouponId, payload).subscribe({
        next: () => {
          this.message = 'Cupom atualizado com sucesso!';
          this.editingCouponId = null;
          this.couponForm.reset({ discountType: 'PERCENTAGE', minPurchaseValue: 0, maxUses: 0 });
          this.loadCoupons();
        },
        error: (err: any) => {
          this.message = 'Erro ao atualizar cupom: ' + (err.error?.message || err.message);
        },
      });
    } else {
      this.couponService.createCoupon(payload).subscribe({
        next: (_res: any) => {
          this.message = 'Cupom criado com sucesso!';
          this.couponForm.reset({ discountType: 'PERCENTAGE', minPurchaseValue: 0, maxUses: 0 });
          this.loadCoupons();
        },
        error: (err: any) => {
          this.message = 'Erro ao criar cupom: ' + (err.error?.message || err.message);
        },
      });
    }
  }
}
