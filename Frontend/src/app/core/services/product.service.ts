import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Page } from '../models/api-response.model';
import { ProductResponse, ProductSearchParams } from '../models/product.model';

export interface ProductCreateRequest {
  name: string;
  description: string;
  price: number;
  originalPrice: number;
  stock: number;
  sku: string;
  imageUrl?: string;
  categoryId: string;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly API = '/api/products';

  constructor(private http: HttpClient) {}

  search(params: ProductSearchParams): Observable<ApiResponse<Page<ProductResponse>>> {
    let httpParams = new HttpParams();
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.categoryId) httpParams = httpParams.set('categoryId', params.categoryId);
    if (params.precoMin != null) httpParams = httpParams.set('precoMin', params.precoMin);
    if (params.precoMax != null) httpParams = httpParams.set('precoMax', params.precoMax);
    httpParams = httpParams.set('page', params.page ?? 0);
    httpParams = httpParams.set('size', params.size ?? 12);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);
    return this.http.get<ApiResponse<Page<ProductResponse>>>(`${this.API}/search`, {
      params: httpParams,
    });
  }

  findById(id: string): Observable<ApiResponse<ProductResponse>> {
    return this.http.get<ApiResponse<ProductResponse>>(`${this.API}/search/${id}`);
  }

  createProduct(body: ProductCreateRequest): Observable<ApiResponse<ProductResponse>> {
    return this.http.post<ApiResponse<ProductResponse>>(`${this.API}/create`, body);
  }

  uploadImage(id: string, file: File): Observable<ApiResponse<ProductResponse>> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ApiResponse<ProductResponse>>(`${this.API}/upload/image/${id}`, form);
  }

  updateProduct(
    id: string,
    body: Partial<ProductCreateRequest>,
  ): Observable<ApiResponse<ProductResponse>> {
    return this.http.put<ApiResponse<ProductResponse>>(`${this.API}/update/${id}`, body);
  }

  setActive(id: string, active: boolean): Observable<ApiResponse<void>> {
    return active
      ? this.http.put<ApiResponse<void>>(`${this.API}/reactivate/${id}`, {})
      : this.http.delete<ApiResponse<void>>(`${this.API}/deactivate/${id}`);
  }
}
