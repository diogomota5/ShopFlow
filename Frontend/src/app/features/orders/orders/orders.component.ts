import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { OrderService } from '../../../core/services/order.service';
import { OrderResponse, OrderStatus } from '../../../core/models/order.model';
import { Page } from '../../../core/models/api-response.model';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css',
})
export class OrdersComponent implements OnInit {
  private orderService = inject(OrderService);
  private route = inject(ActivatedRoute);
  private confirmDialog = inject(ConfirmDialogService);

  orders = signal<OrderResponse[]>([]);
  pageData = signal<Page<OrderResponse> | null>(null);
  loading = signal(true);
  successOrderId = signal<string | null>(null);
  currentPage = signal(0);
  error = signal('');

  ngOnInit(): void {
    const success = this.route.snapshot.queryParamMap.get('success');
    if (success) this.successOrderId.set(success);
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true);
    this.orderService.findMyOrders(this.currentPage()).subscribe({
      next: (res) => {
        this.orders.set(res.data.content);
        this.pageData.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadOrders();
  }

  async cancel(order: OrderResponse): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Cancelar pedido?',
      message: 'O pedido será cancelado e os itens retornarão ao estoque.',
      confirmText: 'Cancelar pedido',
      type: 'danger',
    });
    if (!confirmed) return;
    this.orderService.cancelByCustomer(order.id).subscribe({
      next: () => this.loadOrders(),
      error: (err) => this.error.set(err.error?.message ?? 'Não foi possível cancelar o pedido.'),
    });
  }

  statusLabel(status: OrderStatus): string {
    const labels: Record<OrderStatus, string> = {
      PENDING: 'Pendente',
      PAID: 'Pago',
      SHIPPED: 'Enviado',
      DELIVERED: 'Entregue',
      CANCELED: 'Cancelado',
    };
    return labels[status];
  }

  statusClass(status: OrderStatus): string {
    const classes: Record<OrderStatus, string> = {
      PENDING: 'status-pending',
      PAID: 'status-paid',
      SHIPPED: 'status-shipped',
      DELIVERED: 'status-delivered',
      CANCELED: 'status-canceled',
    };
    return classes[status];
  }

  formatPrice(val: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  }

  get pages(): number[] {
    const total = this.pageData()?.totalPages ?? 0;
    return Array.from({ length: total }, (_, i) => i);
  }
}
