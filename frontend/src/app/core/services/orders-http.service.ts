import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OrdersHttpService {
  private base = `${environment.apiUrl}/order`;
  constructor(private http: HttpClient) {}

  getAll(params?: any): Observable<any> {
    let p = new HttpParams();
    if (params) Object.keys(params).forEach(k => params[k] != null && (p = p.set(k, params[k])));
    return this.http.get<any>(this.base, { params: p }).pipe(map(r => r?.data ?? r));
  }
  getById(id: string): Observable<any> { return this.http.get<any>(`${this.base}/${id}`); }
  updateStatus(id: string, status: string, note?: string): Observable<any> {
    return this.http.patch<any>(`${this.base}/${id}/status`, { status, note });
  }
  getRefunds(params?: any): Observable<any> {
    let p = new HttpParams();
    if (params) Object.keys(params).forEach(k => params[k] != null && (p = p.set(k, params[k])));
    return this.http.get<any>(`${this.base}/refund`, { params: p }).pipe(map(r => r?.data ?? r));
  }
  getDevolutions(params?: any): Observable<any> {
    let p = new HttpParams();
    if (params) Object.keys(params).forEach(k => params[k] != null && (p = p.set(k, params[k])));
    return this.http.get<any>(`${this.base}/devolution`, { params: p }).pipe(map(r => r?.data ?? r));
  }
  getByUserId(userId: string): Observable<any[]> { return this.http.get<any[]>(`${this.base}/user/${userId}`); }
}
