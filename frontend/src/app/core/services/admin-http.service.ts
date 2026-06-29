import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdminHttpService {
  private base = `${environment.apiUrl}/admin`;
  constructor(private http: HttpClient) {}

  getDashboard(): Observable<any> { return this.http.get<any>(`${this.base}/dashboard`); }
  getCollaborators(): Observable<any[]> { return this.http.get<any>(`${this.base}/collaborators`).pipe(map(r => r?.data ?? r ?? [])); }
  createCollaborator(data: any): Observable<any> { return this.http.post<any>(`${this.base}/collaborators`, data); }
  updateCollaborator(id: string, data: any): Observable<any> { return this.http.patch<any>(`${this.base}/collaborators/${id}`, data); }
  deleteCollaborator(id: string): Observable<void> { return this.http.delete<void>(`${this.base}/collaborators/${id}`); }
}
