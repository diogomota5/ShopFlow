import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Page } from '../models/api-response.model';
import {
  AssessmentRequest,
  AssessmentResponse,
  ProductRatingResponse,
} from '../models/assessment.model';

@Injectable({ providedIn: 'root' })
export class AssessmentService {
  private readonly API = '/api/assessments';

  constructor(private http: HttpClient) {}

  create(request: AssessmentRequest): Observable<ApiResponse<AssessmentResponse>> {
    return this.http.post<ApiResponse<AssessmentResponse>>(`${this.API}/create`, request);
  }

  getByProduct(productId: string, page = 0): Observable<ApiResponse<Page<AssessmentResponse>>> {
    const params = new HttpParams().set('page', page).set('size', 5);
    return this.http.get<ApiResponse<Page<AssessmentResponse>>>(
      `${this.API}/product/${productId}`,
      { params },
    );
  }

  getStats(productId: string): Observable<ApiResponse<ProductRatingResponse>> {
    return this.http.get<ApiResponse<ProductRatingResponse>>(
      `${this.API}/product/${productId}/stats`,
    );
  }
}
