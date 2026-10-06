export interface ProductResponse {
  id: string;
  name: string;
  description: string;
  price: number;
  originalPrice: number | null;
  stock: number;
  sku: string;
  imageUrl: string | null;
  active: boolean;
  categoryName: string;
  categoryId: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductSearchParams {
  name?: string;
  categoryId?: string;
  precoMin?: number;
  precoMax?: number;
  page?: number;
  size?: number;
  sort?: string;
}
