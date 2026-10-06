import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { AddressesRequest, AddressesResponse } from '../models/address.model';

@Injectable({ providedIn: 'root' })
export class AddressService {
  private readonly http = inject(HttpClient);
  private readonly API = '/api/adresses';

  findAll(): Observable<ApiResponse<AddressesResponse[]>> {
    return this.http.get<ApiResponse<AddressesResponse[]>>(`${this.API}/search`);
  }

  findAllForUser(userId: string): Observable<ApiResponse<AddressesResponse[]>> {
    return this.http.get<ApiResponse<AddressesResponse[]>>(`${this.API}/user/${userId}`);
  }

  create(request: AddressesRequest): Observable<ApiResponse<AddressesResponse>> {
    return this.http.post<ApiResponse<AddressesResponse>>(`${this.API}/create`, request);
  }

  createForUser(
    userId: string,
    request: AddressesRequest,
  ): Observable<ApiResponse<AddressesResponse>> {
    return this.http.post<ApiResponse<AddressesResponse>>(
      `${this.API}/user/${userId}/create`,
      request,
    );
  }

  update(
    addressId: string,
    request: AddressesRequest,
    isAdmin = false,
  ): Observable<ApiResponse<AddressesResponse>> {
    const endpoint = isAdmin
      ? `${this.API}/update/admin/${addressId}`
      : `${this.API}/update/${addressId}`;
    return this.http.put<ApiResponse<AddressesResponse>>(endpoint, request);
  }

  delete(addressId: string, isAdmin = false): Observable<ApiResponse<void>> {
    const endpoint = isAdmin
      ? `${this.API}/delete/admin/${addressId}`
      : `${this.API}/delete/${addressId}`;
    return this.http.delete<ApiResponse<void>>(endpoint);
  }

  fetchCep(
    cep: string,
  ): Observable<{ logradouro: string; bairro: string; localidade: string; uf: string }> {
    const clean = cep.replace(/\D/g, '');
    return this.http.get<{ logradouro: string; bairro: string; localidade: string; uf: string }>(
      `https://viacep.com.br/ws/${clean}/json/`,
    );
  }
}
