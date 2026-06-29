export interface PaginatedResponse<T> {
  data: T[]; total: number; page: number; pageSize: number; totalPages: number;
}
export interface PageParams { page: number; pageSize: number; }
