import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrderResponse, OrderStatus } from '../../../core/models/order.model';
import { OrderService } from '../../../core/services/order.service';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';

@Component({
  selector: 'app-manage-orders', standalone: true, imports: [CommonModule, FormsModule],
  templateUrl: './manage-orders.component.html', styleUrl: './manage-orders.component.css',
})
export class ManageOrdersComponent implements OnInit {
  private ordersService = inject(OrderService);
  private confirmDialog = inject(ConfirmDialogService);
  orders: OrderResponse[] = [];
  status = '';
  trackingCodes: Record<string, string> = {};
  loading = false;
  errorMessage = '';
  page = 0;
  totalPages = 0;
  readonly statuses: OrderStatus[] = ['PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELED'];
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.ordersService.findAllForAdmin(this.page, 20, this.status as OrderStatus || undefined).subscribe({
      next: r => { this.orders = r.data?.content ?? []; this.totalPages = r.data?.totalPages ?? 0; this.loading = false; },
      error: () => { this.errorMessage = 'Não foi possível carregar os pedidos.'; this.loading = false; },
    });
  }
  changePage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.page = page;
    this.load();
  }
  advance(order: OrderResponse): void {
    const next: Partial<Record<OrderStatus, OrderStatus>> = { PENDING: 'PAID', PAID: 'SHIPPED', SHIPPED: 'DELIVERED' };
    const status = next[order.status]; if (!status) return;
    if (status === 'SHIPPED' && !this.trackingCodes[order.id]?.trim()) return;
    this.ordersService.updateStatus(order.id, status, this.trackingCodes[order.id]).subscribe({ next: () => this.load(), error: () => this.errorMessage = 'Não foi possível atualizar o pedido.' });
  }
  async cancel(order: OrderResponse): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Cancelar pedido?',
      message: 'Os itens voltarão ao estoque e o pedido poderá ser reaberto posteriormente.',
      confirmText: 'Cancelar pedido',
      type: 'danger',
    });
    if (!confirmed) return;
    this.ordersService.updateStatus(order.id, 'CANCELED').subscribe({ next: () => this.load(), error: () => this.errorMessage = 'Não foi possível cancelar o pedido.' });
  }
  async reopen(order: OrderResponse): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Reabrir pedido?',
      message: 'O pedido voltará para pendente e reservará novamente os itens no estoque.',
      confirmText: 'Reabrir pedido',
      type: 'warning',
    });
    if (!confirmed) return;
    this.ordersService.reopen(order.id).subscribe({ next: () => this.load(), error: () => this.errorMessage = 'Não foi possível reabrir o pedido. Verifique o estoque.' });
  }
  countByStatus(value: OrderStatus): number { return this.orders.filter(order => order.status === value).length; }
  statusLabel(status: OrderStatus): string { return ({ PENDING: 'Pendente', PAID: 'Pago', SHIPPED: 'Enviado', DELIVERED: 'Entregue', CANCELED: 'Cancelado' })[status]; }
}
