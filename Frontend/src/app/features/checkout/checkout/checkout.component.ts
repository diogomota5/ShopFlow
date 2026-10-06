import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { CartService } from '../../../core/services/cart.service';
import { AddressService } from '../../../core/services/address.service';
import { OrderService } from '../../../core/services/order.service';
import { CheckoutStateService } from '../../../core/services/checkout-state.service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';
import { AddressesResponse } from '../../../core/models/address.model';
import { CartResponse } from '../../../core/models/cart.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css',
})
export class CheckoutComponent implements OnInit {
  private fb = inject(FormBuilder);
  private cartService = inject(CartService);
  private addressService = inject(AddressService);
  private orderService = inject(OrderService);
  protected checkoutState = inject(CheckoutStateService);
  private router = inject(Router);
  private confirmService = inject(ConfirmDialogService);

  cart = signal<CartResponse | null>(null);
  addresses = signal<AddressesResponse[]>([]);
  selectedAddressId = signal<string | null>(null);
  loading = signal(true);
  submitting = signal(false);
  error = signal('');
  showAddressForm = signal(false);
  loadingCep = signal(false);

  addressForm = this.fb.group({
    nickname: [''],
    zipCode: ['', [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
    street: ['', Validators.required],
    streetNumber: ['', Validators.required],
    complement: [''],
    neighborhood: ['', Validators.required],
    city: ['', Validators.required],
    state: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]],
    primaryAddress: [true],
  });

  notes = '';

  get finalPrice(): number {
    const total = this.cart()?.totalPrice ?? 0;
    const coupon = this.checkoutState.coupon();
    if (!coupon) return total;
    if (coupon.discountType === 'PERCENTAGE')
      return Math.max(0, total * (1 - coupon.discountValue / 100));
    return Math.max(0, total - coupon.discountValue);
  }

  ngOnInit(): void {
    this.cartService.getMyCart().subscribe({
      next: (res) => {
        if (!res.data.items.length) {
          this.router.navigate(['/cart']);
          return;
        }
        this.cart.set(res.data);
        this.loadAddresses();
      },
      error: () => this.router.navigate(['/cart']),
    });
  }

  private loadAddresses(): void {
    this.addressService.findAll().subscribe({
      next: (res) => {
        this.addresses.set(res.data);
        const primary = res.data.find((a) => a.primaryAddress);
        if (primary) this.selectedAddressId.set(primary.id);
        else if (res.data.length) this.selectedAddressId.set(res.data[0].id);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  lookupCep(): void {
    const cep = this.addressForm.get('zipCode')?.value ?? '';
    if (!/^\d{5}-\d{3}$/.test(cep)) return;
    this.loadingCep.set(true);
    this.addressService.fetchCep(cep).subscribe({
      next: (data) => {
        if ('erro' in data) return;
        this.addressForm.patchValue({
          street: data.logradouro,
          neighborhood: data.bairro,
          city: data.localidade,
          state: data.uf,
        });
        this.loadingCep.set(false);
      },
      error: () => this.loadingCep.set(false),
    });
  }

  saveAddress(): void {
    if (this.addressForm.invalid) {
      this.addressForm.markAllAsTouched();
      return;
    }
    this.addressService.create(this.addressForm.getRawValue() as never).subscribe({
      next: (res) => {
        this.addresses.update((list) => [...list, res.data]);
        this.selectedAddressId.set(res.data.id);
        this.showAddressForm.set(false);
        this.addressForm.reset({ primaryAddress: true });
      },
      error: (err: any) => {
        this.error.set(err.error?.message ?? 'Erro ao salvar endereço.');
      },
    });
  }

  async placeOrder(): Promise<void> {
    const addressId = this.selectedAddressId();
    if (!addressId) {
      this.error.set('Selecione um endereço de entrega.');
      return;
    }

    const priceFormatted = this.formatPrice(this.finalPrice);
    const confirmed = await this.confirmService.confirm({
      title: 'Finalizar Compra',
      message: `Deseja realmente confirmar o seu pedido e o pagamento no valor total de ${priceFormatted}?`,
      confirmText: 'Confirmar Compra',
      cancelText: 'Cancelar',
      type: 'warning',
    });
    if (!confirmed) return;

    this.submitting.set(true);
    this.error.set('');
    const couponCode = this.checkoutState.couponCode();
    this.orderService
      .create({
        addressId,
        notes: this.notes.trim() || undefined,
        couponCode: couponCode ?? undefined,
      })
      .subscribe({
        next: (res) => {
          this.checkoutState.clearCoupon();
          this.cartService.clearLocal();
          this.router.navigate(['/orders'], { queryParams: { success: res.data.id } });
        },
        error: (err: any) => {
          this.error.set(err.error?.message ?? 'Não foi possível finalizar o pedido.');
          this.submitting.set(false);
        },
      });
  }

  formatPrice(val: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  }
}
