import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { ProductResponse } from '../../../core/models/product.model';
import { CategoryResponse } from '../../../core/models/category.model';
import { Page } from '../../../core/models/api-response.model';

import { TitleCasePipe } from '@angular/common';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [RouterLink, FormsModule, TitleCasePipe],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css',
})
export class ProductsComponent implements OnInit {
  private productService = inject(ProductService);
  private categoryService = inject(CategoryService);
  private cartService = inject(CartService);
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private titleService = inject(Title);

  products = signal<ProductResponse[]>([]);
  categories = signal<CategoryResponse[]>([]);
  pageData = signal<Page<ProductResponse> | null>(null);
  loading = signal(true);
  addingToCart = signal<string | null>(null);

  // Filters
  searchName = signal('');
  selectedCategory = signal('');
  minPrice = signal<number | null>(null);
  maxPrice = signal<number | null>(null);
  currentPage = signal(0);

  ngOnInit(): void {
    this.titleService.setTitle('Produtos | ShopFlow');
    this.categoryService
      .findAll()
      .subscribe((res) => this.categories.set(res.data.filter((c) => c.active)));
    this.route.queryParamMap.subscribe((params) => {
      this.searchName.set(params.get('name') ?? '');
      this.selectedCategory.set(params.get('categoryId') ?? '');
      this.currentPage.set(0);
      this.loadProducts();
    });
  }

  loadProducts(): void {
    this.loading.set(true);
    this.productService
      .search({
        name: this.searchName() || undefined,
        categoryId: this.selectedCategory() || undefined,
        precoMin: this.minPrice() ?? undefined,
        precoMax: this.maxPrice() ?? undefined,
        page: this.currentPage(),
        size: 12,
      })
      .subscribe({
        next: (res) => {
          this.products.set(res.data.content);
          this.pageData.set(res.data);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  applyFilters(): void {
    this.currentPage.set(0);
    this.loadProducts();
  }

  clearFilters(): void {
    this.searchName.set('');
    this.selectedCategory.set('');
    this.minPrice.set(null);
    this.maxPrice.set(null);
    this.currentPage.set(0);
    this.loadProducts();
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadProducts();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  addToCart(product: ProductResponse): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    this.addingToCart.set(product.id);
    this.cartService.addItem({ productId: product.id, quantity: 1 }).subscribe({
      next: () => setTimeout(() => this.addingToCart.set(null), 800),
      error: () => this.addingToCart.set(null),
    });
  }

  getDiscountPct(p: ProductResponse): number | null {
    if (!p.originalPrice || p.originalPrice <= p.price) return null;
    return Math.round((1 - p.price / p.originalPrice) * 100);
  }

  formatPrice(val: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  }

  get pages(): number[] {
    const total = this.pageData()?.totalPages ?? 0;
    return Array.from({ length: total }, (_, i) => i);
  }
}
