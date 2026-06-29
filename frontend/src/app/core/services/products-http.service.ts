import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductsHttpService {
  private base = `${environment.apiUrl}/product`;
  constructor(private http: HttpClient) {}

  getAll(params?: any): Observable<any> {
    let p = new HttpParams();
    if (params) Object.keys(params).forEach(k => params[k] != null && (p = p.set(k, params[k])));
    return this.http.get<any>(this.base, { params: p }).pipe(map(r => r?.data ?? r));
  }
  getById(id: string): Observable<any> { return this.http.get<any>(`${this.base}/${id}`); }
  create(data: any): Observable<any> { return this.http.post<any>(this.base, data); }
  update(id: string, data: any): Observable<any> { return this.http.patch<any>(`${this.base}/${id}`, data); }
  delete(id: string): Observable<void> { return this.http.delete<void>(`${this.base}/${id}`); }
  activate(id: string): Observable<any> { return this.http.patch<any>(`${this.base}/${id}/activate`, {}); }
  deactivate(id: string): Observable<any> { return this.http.patch<any>(`${this.base}/${id}/deactivate`, {}); }
}
