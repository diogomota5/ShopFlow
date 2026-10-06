import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { CategoryService } from '../../core/services/category.service';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { ProductResponse } from '../../core/models/product.model';
import { CategoryResponse } from '../../core/models/category.model';

import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, TitleCasePipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  private productService = inject(ProductService);
  private categoryService = inject(CategoryService);
  private cartService = inject(CartService);
  protected auth = inject(AuthService);
  private router = inject(Router);

  featuredProducts = signal<ProductResponse[]>([]);
  categories = signal<CategoryResponse[]>([]);
  loadingProducts = signal(true);
  loadingCategories = signal(true);
  addingToCart = signal<string | null>(null); // productId being added

  ngOnInit(): void {
    this.loadCategories();
    this.loadFeatured();
  }

  private loadCategories(): void {
    this.categoryService.findAll().subscribe({
      next: (res) => {
        this.categories.set(res.data.filter((c) => c.active).slice(0, 8));
        this.loadingCategories.set(false);
      },
      error: () => this.loadingCategories.set(false),
    });
  }

  private loadFeatured(): void {
    this.productService.search({ size: 8, sort: 'createdAt,desc' }).subscribe({
      next: (res) => {
        this.featuredProducts.set(res.data.content);
        this.loadingProducts.set(false);
      },
      error: () => this.loadingProducts.set(false),
    });
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
}
