import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { AssessmentService } from '../../../core/services/assessment.service';
import { ProductResponse } from '../../../core/models/product.model';
import { AssessmentResponse, ProductRatingResponse } from '../../../core/models/assessment.model';
import { Page } from '../../../core/models/api-response.model';

import { TitleCasePipe } from '@angular/common';
import { Meta, Title } from '@angular/platform-browser';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [RouterLink, FormsModule, DatePipe, DecimalPipe, TitleCasePipe],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css',
})
export class ProductDetailComponent implements OnInit {
  protected Math = Math;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  private assessmentService = inject(AssessmentService);
  protected auth = inject(AuthService);
  private titleService = inject(Title);
  private meta = inject(Meta);

  product = signal<ProductResponse | null>(null);
  rating = signal<ProductRatingResponse | null>(null);
  reviews = signal<Page<AssessmentResponse> | null>(null);
  loadingProduct = signal(true);
  quantity = signal(1);
  addingToCart = signal(false);
  cartAdded = signal(false);

  // Review form
  newRating = signal(5);
  newTitle = '';
  newComment = '';
  submittingReview = signal(false);
  reviewError = signal('');

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) this.loadProduct(id);
    });
  }

  private loadProduct(id: string): void {
    this.loadingProduct.set(true);
    this.productService.findById(id).subscribe({
      next: (res) => {
        this.product.set(res.data);
        this.updateMetadata(res.data);
        this.loadingProduct.set(false);
        this.loadRating(id);
        this.loadReviews(id);
      },
      error: () => {
        this.loadingProduct.set(false);
        this.router.navigate(['/products']);
      },
    });
  }

  private updateMetadata(product: ProductResponse): void {
    const name = product.name.replace(/\s+/g, ' ').trim();
    const description = product.description.replace(/\s+/g, ' ').trim().slice(0, 155);
    const title = `${name} | ShopFlow`;
    this.titleService.setTitle(title);
    this.meta.updateTag({ name: 'description', content: description });
    this.meta.updateTag({ property: 'og:title', content: title });
    this.meta.updateTag({ property: 'og:description', content: description });
    if (product.imageUrl) this.meta.updateTag({ property: 'og:image', content: product.imageUrl });
  }

  private loadRating(id: string): void {
    this.assessmentService
      .getStats(id)
      .subscribe({ next: (res) => this.rating.set(res.data), error: () => {} });
  }

  private loadReviews(id: string, page = 0): void {
    this.assessmentService
      .getByProduct(id, page)
      .subscribe({ next: (res) => this.reviews.set(res.data), error: () => {} });
  }

  addToCart(): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    const p = this.product();
    if (!p) return;
    this.addingToCart.set(true);
    this.cartService.addItem({ productId: p.id, quantity: this.quantity() }).subscribe({
      next: () => {
        this.addingToCart.set(false);
        this.cartAdded.set(true);
        setTimeout(() => this.cartAdded.set(false), 2500);
      },
      error: () => this.addingToCart.set(false),
    });
  }

  submitReview(): void {
    const p = this.product();
    if (!p || !this.auth.isLoggedIn()) return;
    this.submittingReview.set(true);
    this.reviewError.set('');
    this.assessmentService
      .create({
        productId: p.id,
        rating: this.newRating(),
        title: this.newTitle.trim(),
        comment: this.newComment.trim() || undefined,
      })
      .subscribe({
        next: () => {
          this.submittingReview.set(false);
          this.newComment = '';
          this.newTitle = '';
          this.newRating.set(5);
          this.loadRating(p.id);
          this.loadReviews(p.id);
        },
        error: (err) => {
          this.submittingReview.set(false);
          this.reviewError.set(err.error?.message ?? 'Failed to submit review.');
        },
      });
  }

  getDiscountPct(): number | null {
    const p = this.product();
    if (!p?.originalPrice || p.originalPrice <= p.price) return null;
    return Math.round((1 - p.price / p.originalPrice) * 100);
  }

  formatPrice(val: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  }

  starsArray(n: number): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }
}
