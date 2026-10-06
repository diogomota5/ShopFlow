import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryResponse } from '../../../core/models/category.model';

@Component({
  selector: 'app-create-category',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-category.component.html',
  styleUrls: ['../create-product/create-product.component.css'],
})
export class CreateCategoryComponent implements OnInit {
  private fb = inject(FormBuilder);
  private categoryService = inject(CategoryService);

  categoryForm: FormGroup;
  selectedFile: File | null = null;
  message: string = '';
  categories: CategoryResponse[] = [];
  categorySearch = '';
  editingCategoryId: string | null = null;

  constructor() {
    this.categoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required]],
    });
  }

  ngOnInit() {}

  loadCategories(): void {
    this.categoryService.findAll().subscribe({
      next: (res) => {
        const query = this.categorySearch.trim().toLocaleLowerCase('pt-BR');
        this.categories = (res.data ?? []).filter(
          (category) => !query || category.name.toLocaleLowerCase('pt-BR').includes(query),
        );
      },
    });
  }

  startEditCategory(category: CategoryResponse): void {
    this.editingCategoryId = category.id;
    this.categoryForm.patchValue({
      name: category.name,
      description: category.description,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit(): void {
    this.editingCategoryId = null;
    this.categoryForm.reset();
    this.selectedFile = null;
  }

  toggleCategory(category: CategoryResponse): void {
    this.categoryService
      .setActive(category.id, !category.active)
      .subscribe({ next: () => this.loadCategories() });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {
    if (this.categoryForm.invalid) return;

    if (this.editingCategoryId) {
      this.categoryService
        .updateCategory(this.editingCategoryId, this.categoryForm.value)
        .subscribe({
          next: () => {
            this.message = 'Categoria atualizada com sucesso!';
            this.editingCategoryId = null;
            this.categoryForm.reset();
            this.selectedFile = null;
            this.loadCategories();
          },
          error: (err: any) => {
            this.message = 'Erro ao atualizar categoria: ' + (err.error?.message || err.message);
          },
        });
    } else {
      this.categoryService.createCategory(this.categoryForm.value).subscribe({
        next: (res) => {
          this.message = 'Categoria criada com sucesso!';
          if (this.selectedFile && res.data?.id) {
            this.categoryService.uploadImage(res.data.id, this.selectedFile).subscribe();
          }
          this.categoryForm.reset();
          this.selectedFile = null;
          this.loadCategories();
        },
        error: (err: any) => {
          this.message = 'Erro ao criar categoria: ' + (err.error?.message || err.message);
        },
      });
    }
  }
}
