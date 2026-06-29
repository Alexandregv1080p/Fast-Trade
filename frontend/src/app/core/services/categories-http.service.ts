import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CategoriesHttpService {
  private base = `${environment.apiUrl}/category`;
  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> { return this.http.get<any>(this.base).pipe(map(r => r?.data ?? r ?? [])); }
  getById(id: string): Observable<any> { return this.http.get<any>(`${this.base}/${id}`); }
  create(data: any): Observable<any> { return this.http.post<any>(this.base, data); }
  update(id: string, data: any): Observable<any> { return this.http.patch<any>(`${this.base}/${id}`, data); }
  delete(id: string): Observable<void> { return this.http.delete<void>(`${this.base}/${id}`); }
  getSubcategories(): Observable<any[]> { return this.http.get<any>(`${environment.apiUrl}/subcategory`).pipe(map(r => r?.data ?? r ?? [])); }
  createSubcategory(data: any): Observable<any> { return this.http.post<any>(`${environment.apiUrl}/subcategory`, data); }
  updateSubcategory(id: string, data: any): Observable<any> { return this.http.patch<any>(`${environment.apiUrl}/subcategory/${id}`, data); }
  deleteSubcategory(id: string): Observable<void> { return this.http.delete<void>(`${environment.apiUrl}/subcategory/${id}`); }
}
