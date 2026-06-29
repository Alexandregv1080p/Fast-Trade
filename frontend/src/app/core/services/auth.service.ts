import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User, AuthSession } from '../models/user.model';
import { StorageService } from './storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSubject = new BehaviorSubject<User | null>(null);
  private authSubject = new BehaviorSubject<boolean>(false);
  currentUser$ = this.userSubject.asObservable();
  isAuthenticated$ = this.authSubject.asObservable();

  constructor(private storage: StorageService) { this.loadSession(); }

  private loadSession(): void {
    const s = this.storage.getSession();
    if (s && new Date(s.expiresAt) > new Date()) {
      this.userSubject.next(s.user);
      this.authSubject.next(true);
    } else { this.storage.clearSession(); }
  }

  setSession(s: AuthSession): void {
    this.storage.setSession(s);
    this.userSubject.next(s.user);
    this.authSubject.next(true);
  }

  getToken(): string | null { return this.storage.getSession()?.token ?? null; }
  getCurrentUser(): User | null { return this.userSubject.value; }
  isAuthenticated(): boolean { return this.authSubject.value; }
  logout(): void { this.storage.clearSession(); this.userSubject.next(null); this.authSubject.next(false); }
}
