export interface Category { id: string; name: string; slug?: string; parentId?: string; imageUrl?: string; }
export interface Subcategory { id: string; name: string; categoryId: string; category?: Category; imageUrl?: string; }
export interface ProductImage { id: string; url: string; alt?: string; isPrimary: boolean; }

export interface Product {
  id: string;
  sku?: string;
  name: string;
  description?: string;
  price: number;
  compareAtPrice?: number;
  stock: number;
  categoryId: string;
  category?: Category;
  subcategoryId?: string;
  subcategory?: Subcategory;
  images: ProductImage[];
  isActive: boolean;
  isFeatured?: boolean;
  createdAt: string;
  updatedAt?: string;
  userId?: string;
  userName?: string;
}

export interface ProductFilters {
  search?: string;
  categoryId?: string;
  isActive?: boolean;
  page?: number;
  pageSize?: number;
}
