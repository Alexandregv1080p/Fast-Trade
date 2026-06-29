import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthSession, LoginCredentials, UserRole } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthHttpService {
  private base = environment.apiUrl;
  constructor(private http: HttpClient) {}

  login(c: LoginCredentials): Observable<AuthSession> {
    return this.http.post<any>(`${this.base}/auth/login/adm`, { credential: c.email, password: c.password }).pipe(
      map(r => ({
        token: r.accessToken ?? r.token ?? '',
        expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000),
        user: {
          id: String(r.id ?? ''),
          name: r.name ?? c.email,
          email: r.email ?? c.email,
          role: UserRole.ADMIN,
          addresses: [],
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        }
      }))
    );
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.base}/admin/recovery`, { email, userType: 'admin' });
  }

  resetPassword(email: string, code: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.base}/admin/reset`, { email, code, newPassword });
  }
}
