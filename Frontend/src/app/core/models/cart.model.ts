export interface CartItemResponse {
  id: string;
  productId: string;
  productName: string;
  productImageUrl: string | null;
  price: number;
  originalPrice: number | null;
  quantity: number;
  subtotal: number;
  addedAt: string;
}

export interface CartResponse {
  id: string;
  items: CartItemResponse[];
  totalItems: number;
  totalPrice: number;
}

export interface CartItemRequest {
  productId: string;
  quantity: number;
}

export interface CartItemUpdateRequest {
  quantity: number;
}
