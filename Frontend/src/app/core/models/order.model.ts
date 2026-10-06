export type OrderStatus = 'PENDING' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELED';

export interface OrderItemResponse {
  id: string;
  productId: string;
  productName: string;
  unitPrice: number;
  imageUrl: string | null;
  quantity: number;
  subtotal: number;
}

export interface OrderResponse {
  id: string;
  status: OrderStatus;
  subtotal: number;
  discount: number;
  total: number;
  shippingZipCode: string;
  shippingStreet: string;
  shippingNumber: string;
  shippingComplement: string | null;
  shippingNeighborhood: string;
  shippingCity: string;
  shippingState: string;
  trackingCode: string | null;
  notes: string | null;
  items: OrderItemResponse[];
  statusHistory: OrderStatusHistoryResponse[];
  createdAt: string;
}

export interface OrderStatusHistoryResponse {
  id: string;
  status: OrderStatus;
  note: string | null;
  createdAt: string;
}

export interface OrderRequest {
  addressId: string;
  notes?: string;
  couponCode?: string;
}
