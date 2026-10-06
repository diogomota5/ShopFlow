import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { ProductResponse } from '../../../core/models/product.model';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryResponse } from '../../../core/models/category.model';

@Component({
  selector: 'app-create-product',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-product.component.html',
  styleUrls: ['./create-product.component.css'],
})
export class CreateProductComponent implements OnInit {
  private fb = inject(FormBuilder);
  private productService = inject(ProductService);
  private categoryService = inject(CategoryService);

  productForm: FormGroup;
  categories: CategoryResponse[] = [];
  selectedFile: File | null = null;
  message: string = '';
  products: ProductResponse[] = [];
  productSearch = '';
  editingProductId: string | null = null;

  constructor() {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required]],
      price: [0, [Validators.required, Validators.min(0.01)]],
      originalPrice: [0, [Validators.required, Validators.min(0.01)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      sku: ['', [Validators.required, Validators.maxLength(50)]],
      categoryId: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.categoryService.getAllCategories().subscribe({
      next: (res) => {
        if (res.data) this.categories = res.data;
      },
    });
  }

  loadProducts(): void {
    this.productService
      .search({ name: this.productSearch.trim() || undefined, size: 12, sort: 'createdAt,desc' })
      .subscribe({
        next: (res) => (this.products = res.data?.content ?? []),
      });
  }

  startEditProduct(product: ProductResponse): void {
    this.editingProductId = product.id;
    this.productForm.patchValue({
      name: product.name,
      description: product.description,
      price: product.price,
      originalPrice: product.originalPrice ?? product.price,
      stock: product.stock,
      sku: product.sku ?? '',
      categoryId: product.categoryId,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit(): void {
    this.editingProductId = null;
    this.productForm.reset();
    this.selectedFile = null;
  }

  toggleProduct(product: ProductResponse): void {
    this.productService
      .setActive(product.id, !product.active)
      .subscribe({ next: () => this.loadProducts() });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {
    if (this.productForm.invalid) return;

    if (this.editingProductId) {
      this.productService.updateProduct(this.editingProductId, this.productForm.value).subscribe({
        next: () => {
          this.message = 'Produto atualizado com sucesso!';
          const id = this.editingProductId;
          this.editingProductId = null;
          this.productForm.reset();
          if (this.selectedFile && id) {
            this.productService
              .uploadImage(id, this.selectedFile)
              .subscribe({ next: () => this.loadProducts() });
          } else {
            this.loadProducts();
          }
          this.selectedFile = null;
        },
        error: (err: any) => {
          this.message = 'Erro ao atualizar produto: ' + (err.error?.message || err.message);
        },
      });
    } else {
      this.productService.createProduct(this.productForm.value).subscribe({
        next: (res) => {
          this.message = 'Produto criado com sucesso!';
          if (this.selectedFile && res.data?.id) {
            this.productService
              .uploadImage(res.data.id, this.selectedFile)
              .subscribe({ next: () => this.loadProducts() });
          } else {
            this.loadProducts();
          }
          this.productForm.reset();
          this.selectedFile = null;
        },
        error: (err: any) => {
          this.message = 'Erro ao criar produto: ' + (err.error?.message || err.message);
        },
      });
    }
  }
}
