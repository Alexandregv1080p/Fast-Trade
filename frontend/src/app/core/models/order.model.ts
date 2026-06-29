import { Address } from './user.model';

export enum OrderStatus {
  PENDING = 'PENDING', CONFIRMED = 'CONFIRMED', PICKING = 'PICKING',
  PACKED = 'PACKED', SHIPPED = 'SHIPPED', DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED', REFUNDED = 'REFUNDED', DEVOLUTION = 'DEVOLUTION'
}

export interface OrderItem {
  id: string; productId: string; productName: string;
  productImage?: string; sku?: string; quantity: number; price: number; subtotal: number;
}

export interface Order {
  id: string; orderNumber: string; userId: string; userName: string; userEmail?: string;
  items: OrderItem[]; subtotal: number; discount: number; shipping: number; total: number;
  status: OrderStatus; shippingAddress?: Address; paymentMethod?: string;
  paymentStatus?: string; notes?: string; createdAt: string; updatedAt?: string;
  trackingCode?: string;
}

export interface OrderFilters { status?: OrderStatus; search?: string; page?: number; pageSize?: number; }
