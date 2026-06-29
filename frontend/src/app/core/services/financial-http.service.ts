import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FinancialHttpService {
  private base = `${environment.apiUrl}/financial`;
  constructor(private http: HttpClient) {}

  getTransactions(params?: any): Observable<any> {
    let p = new HttpParams();
    if (params) Object.keys(params).forEach(k => params[k] != null && (p = p.set(k, params[k])));
    return this.http.get<any>(`${this.base}/transactions`, { params: p }).pipe(map(r => r?.data ?? r));
  }
  getWallets(params?: any): Observable<any> {
    let p = new HttpParams();
    if (params) Object.keys(params).forEach(k => params[k] != null && (p = p.set(k, params[k])));
    return this.http.get<any>(`${this.base}/wallets`, { params: p }).pipe(map(r => r?.data ?? r));
  }
  getStatement(userId: string): Observable<any> { return this.http.get<any>(`${this.base}/statement/${userId}`); }
}
