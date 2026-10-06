import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { CategoryResponse } from '../models/category.model';

export interface CategoryCreateRequest {
  name: string;
  description: string;
  imageUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private readonly API = '/api/categories';

  constructor(private http: HttpClient) {}

  findAll(): Observable<ApiResponse<CategoryResponse[]>> {
    return this.http.get<ApiResponse<CategoryResponse[]>>(`${this.API}/search`);
  }

  /** Alias usado pelos componentes admin */
  getAllCategories(): Observable<ApiResponse<CategoryResponse[]>> {
    return this.findAll();
  }

  findById(id: string): Observable<ApiResponse<CategoryResponse>> {
    return this.http.get<ApiResponse<CategoryResponse>>(`${this.API}/search/${id}`);
  }

  createCategory(body: CategoryCreateRequest): Observable<ApiResponse<CategoryResponse>> {
    return this.http.post<ApiResponse<CategoryResponse>>(`${this.API}/create`, body);
  }

  uploadImage(id: string, file: File): Observable<ApiResponse<CategoryResponse>> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ApiResponse<CategoryResponse>>(`${this.API}/upload/image/${id}`, form);
  }

  updateCategory(
    id: string,
    body: Partial<CategoryCreateRequest>,
  ): Observable<ApiResponse<CategoryResponse>> {
    return this.http.put<ApiResponse<CategoryResponse>>(`${this.API}/update/${id}`, body);
  }

  setActive(id: string, active: boolean): Observable<ApiResponse<void>> {
    return active
      ? this.http.put<ApiResponse<void>>(`${this.API}/reactivate/${id}`, {})
      : this.http.delete<ApiResponse<void>>(`${this.API}/deactivate/${id}`);
  }
}
