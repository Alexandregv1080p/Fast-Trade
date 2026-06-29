import { Injectable } from '@angular/core';
import { AuthSession } from '../models/user.model';

const SESSION_KEY = 'ft_session';

@Injectable({ providedIn: 'root' })
export class StorageService {
  setSession(s: AuthSession): void { localStorage.setItem(SESSION_KEY, JSON.stringify(s)); }
  getSession(): AuthSession | null {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
  }
  clearSession(): void { localStorage.removeItem(SESSION_KEY); }
}
