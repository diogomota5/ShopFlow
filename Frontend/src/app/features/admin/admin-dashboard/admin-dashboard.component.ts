import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';
import { OrderService } from '../../../core/services/order.service';

interface AdminNavItem {
  label: string;
  route: string;
  icon: 'grid' | 'box' | 'tag' | 'ticket' | 'users';
}

interface AdminNavSection {
  title: string;
  items: AdminNavItem[];
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css',
})
export class AdminDashboardComponent implements OnInit {
  private router = inject(Router);
  protected auth = inject(AuthService);
  protected theme = inject(ThemeService);
  private ordersService = inject(OrderService);

  sidebarOpen = signal(false);
  metrics = signal({ products: 0, categories: 0, orders: 0, revenue: 0, pending: 0 });
  topProducts = signal<{ name: string; quantity: number }[]>([]);
  salesByDay = signal<{ label: string; total: number }[]>([]);
  startDate = '';
  endDate = '';
  isOverview = signal(true);
  pageTitle = signal('Visão Geral');

  readonly navSections = [
    {
      title: 'Principal',
      items: [{ label: 'Visão Geral', route: '/admin', icon: 'grid' as const }],
    },
    {
      title: 'Catálogo',
      items: [
        { label: 'Novo Produto', route: '/admin/product/new', icon: 'box' as const },
        { label: 'Nova Categoria', route: '/admin/category/new', icon: 'tag' as const },
      ],
    },
    {
      title: 'Marketing',
      items: [{ label: 'Novo Cupom', route: '/admin/coupon/new', icon: 'ticket' as const }],
    },
    {
      title: 'Operação',
      items: [{ label: 'Pedidos', route: '/admin/orders', icon: 'box' as const }],
    },
    {
      title: 'Sistema',
      items: [{ label: 'Usuários', route: '/admin/users', icon: 'users' as const }],
    },
  ];

  ngOnInit(): void {
    this.syncRoute(this.router.url);
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => this.syncRoute(e.urlAfterRedirects));
  }

  private loadMetrics(): void {
    this.ordersService
      .getDashboardSummary(this.startDate || undefined, this.endDate || undefined)
      .subscribe({
        next: (r) => {
          const data = r.data;
          if (!data) return;
          this.metrics.set({
            products: data.productCount || 0,
            categories: data.categoryCount || 0,
            orders: data.orderCount || 0,
            revenue: data.revenue || 0,
            pending: data.pendingOrderCount || 0,
          });
          this.topProducts.set(data.topProducts || []);
          const sales = data.salesByDay || [];
          this.salesByDay.set(
            sales.map((item) => ({
              label: new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: '2-digit' }).format(
                new Date(`${item.date}T00:00:00`),
              ),
              total: item.total,
            })),
          );
        },
        error: (err) => console.error('Error loading metrics:', err),
      });
  }

  applyDateFilter(): void {
    this.loadMetrics();
  }
  clearDateFilter(): void {
    this.startDate = '';
    this.endDate = '';
    this.loadMetrics();
  }

  private syncRoute(url: string): void {
    const path = url.split('?')[0];
    this.isOverview.set(path === '/admin' || path === '/admin/');
    if (this.isOverview()) this.loadMetrics();

    const titles: Record<string, string> = {
      '/admin': 'Visão Geral',
      '/admin/product/new': 'Novo Produto',
      '/admin/category/new': 'Nova Categoria',
      '/admin/coupon/new': 'Novo Cupom',
      '/admin/orders': 'Gestão de Pedidos',
      '/admin/users': 'Gerenciar Usuários',
    };

    if (path.startsWith('/admin/users/edit')) {
      this.pageTitle.set('Editar Usuário');
      this.isOverview.set(false);
      return;
    }

    this.pageTitle.set(titles[path] ?? 'Administração');
  }

  toggleSidebar(): void {
    this.sidebarOpen.update((v) => !v);
  }

  closeSidebar(): void {
    this.sidebarOpen.set(false);
  }
}
