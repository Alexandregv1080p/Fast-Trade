import { Component, inject } from '@angular/core';
import { NgIf, AsyncPipe } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [NgIf, AsyncPipe],
  template: `
    <header class="header">
      <div class="header__left">
        <div class="header__greeting" *ngIf="user$ | async as user">
          <span class="header__hi">Olá, <strong>{{ user.name || 'Admin' }}</strong></span>
          <span class="header__date">{{ today }}</span>
        </div>
      </div>
      <div class="header__right" *ngIf="user$ | async as user">
        <div class="header__profile">
          <div class="header__avatar">{{ (user.name || 'A').charAt(0).toUpperCase() }}</div>
          <div class="header__info">
            <span class="header__name">{{ user.name || user.email }}</span>
            <span class="header__role">Administrador</span>
          </div>
        </div>
        <div class="header__sep"></div>
        <button class="header__logout" (click)="logout()" title="Sair">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          <span>Sair</span>
        </button>
      </div>
    </header>`,
  styles: [`
    .header {
      height: var(--header-height);
      background: var(--color-surface);
      border-bottom: 1px solid var(--color-border);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;
      position: fixed;
      top: 0;
      left: var(--sidebar-width);
      right: 0;
      z-index: 99;
    }

    .header__left {}
    .header__greeting { display: flex; flex-direction: column; gap: 1px; }
    .header__hi { font-size: 14px; color: var(--color-text); }
    .header__hi strong { font-weight: 700; }
    .header__date { font-size: 12px; color: var(--color-text-muted); }

    .header__right { display: flex; align-items: center; gap: 16px; }

    .header__profile { display: flex; align-items: center; gap: 10px; }
    .header__avatar {
      width: 34px; height: 34px;
      border-radius: var(--radius-full);
      background: var(--color-accent);
      color: #fff;
      display: flex; align-items: center; justify-content: center;
      font-weight: 700; font-size: 13px;
      flex-shrink: 0;
    }
    .header__info { display: flex; flex-direction: column; gap: 1px; }
    .header__name { font-size: 13px; font-weight: 600; color: var(--color-text); }
    .header__role { font-size: 11px; color: var(--color-text-muted); }

    .header__sep { width: 1px; height: 28px; background: var(--color-border); }

    .header__logout {
      display: flex; align-items: center; gap: 6px;
      background: none; border: 1.5px solid var(--color-border);
      color: var(--color-text-muted);
      font-size: 13px; font-weight: 500;
      padding: 6px 12px;
      border-radius: var(--radius-md);
      transition: all var(--transition);
    }
    .header__logout:hover {
      border-color: var(--color-error);
      color: var(--color-error);
      background: var(--color-error-bg);
    }
  `]
})
export class HeaderComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  user$ = this.auth.currentUser$;

  get today(): string {
    return new Date().toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long' });
  }

  logout(): void { this.auth.logout(); this.router.navigate(['/auth/login']); }
}
