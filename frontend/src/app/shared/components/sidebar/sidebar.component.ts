import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgIf } from '@angular/common';
import { Subscription } from 'rxjs';
import { ChatService } from '../../../core/services/chat.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgIf],
  template: `
    <aside class="sidebar">
      <div class="sidebar__brand">
        <div class="sidebar__logo-mark">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M3 6l7-3 7 3-7 3-7-3z" fill="white" opacity=".9"/>
            <path d="M3 10l7 3 7-3" stroke="white" stroke-width="1.5" fill="none" opacity=".7"/>
            <path d="M3 14l7 3 7-3" stroke="white" stroke-width="1.5" fill="none" opacity=".5"/>
          </svg>
        </div>
        <div class="sidebar__brand-text">
          <span class="sidebar__brand-name">Fast Trade</span>
          <span class="sidebar__brand-sub">Admin Panel</span>
        </div>
      </div>

      <div class="sidebar__divider"></div>

      <nav class="sidebar__nav">
        <span class="sidebar__section-label">Principal</span>

        <a routerLink="/admin/dashboard" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
              <rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Dashboard</span>
        </a>

        <a routerLink="/admin/users" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 00-3-3.87m-4-12a4 4 0 010 7.75"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Usuarios</span>
        </a>

        <a routerLink="/admin/orders" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/>
              <path d="M16 10a4 4 0 01-8 0"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Pedidos</span>
        </a>
      </nav>

      <div class="sidebar__divider"></div>

      <nav class="sidebar__nav">
        <span class="sidebar__section-label">Catalogo</span>

        <a routerLink="/admin/products" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 7.5l-9-5.25L3 7.5m18 0l-9 5.25m9-5.25v9l-9 5.25M3 7.5l9 5.25M3 7.5v9l9 5.25m0-9v9"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Produtos</span>
        </a>

        <a routerLink="/admin/categories" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Categorias</span>
        </a>

        <a routerLink="/admin/subcategories" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M16.5 8.25V6a2.25 2.25 0 00-2.25-2.25H6A2.25 2.25 0 003.75 6v8.25A2.25 2.25 0 006 16.5h2.25m8.25-8.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-7.5A2.25 2.25 0 018.25 18v-1.5m8.25-8.25h-6a2.25 2.25 0 00-2.25 2.25v6"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Subcategorias</span>
        </a>
      </nav>

      <div class="sidebar__divider"></div>

      <nav class="sidebar__nav">
        <span class="sidebar__section-label">Gestao</span>

        <a routerLink="/admin/financial" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Financeiro</span>
        </a>

        <a routerLink="/admin/access" routerLinkActive="sidebar__link--active" class="sidebar__link">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 11-7.778 7.778 5.5 5.5 0 017.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Acessos</span>
        </a>

        <a routerLink="/admin/chat" routerLinkActive="sidebar__link--active" class="sidebar__link sidebar__link--chat">
          <span class="sidebar__link-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
            </svg>
          </span>
          <span class="sidebar__link-label">Chat</span>
          <span class="sidebar__badge" *ngIf="unreadTotal > 0">{{ unreadTotal > 99 ? '99+' : unreadTotal }}</span>
        </a>
      </nav>

      <div class="sidebar__footer">
        <div class="sidebar__version">v1.0.0</div>
      </div>
    </aside>`,
  styles: [`
    .sidebar { width: var(--sidebar-width); background: var(--color-primary); display: flex; flex-direction: column; position: fixed; top: 0; left: 0; bottom: 0; z-index: 100; overflow-y: auto; overflow-x: hidden; }
    .sidebar__brand { display: flex; align-items: center; gap: 10px; padding: 18px 16px 16px; }
    .sidebar__logo-mark { width: 36px; height: 36px; background: rgba(255,255,255,.12); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .sidebar__brand-text { display: flex; flex-direction: column; gap: 1px; }
    .sidebar__brand-name { font-size: 14px; font-weight: 700; color: #fff; letter-spacing: .3px; }
    .sidebar__brand-sub  { font-size: 10px; color: rgba(255,255,255,.4); text-transform: uppercase; letter-spacing: .8px; }
    .sidebar__divider { height: 1px; background: rgba(255,255,255,.07); margin: 4px 0; }
    .sidebar__nav { display: flex; flex-direction: column; padding: 8px 10px; gap: 1px; }
    .sidebar__section-label { font-size: 10px; font-weight: 600; color: rgba(255,255,255,.3); text-transform: uppercase; letter-spacing: 1px; padding: 8px 8px 4px; }
    .sidebar__link { display: flex; align-items: center; gap: 10px; padding: 8px 10px; border-radius: var(--radius-md); color: rgba(255,255,255,.55); font-size: 13px; font-weight: 500; transition: all var(--transition); text-decoration: none; }
    .sidebar__link:hover { background: rgba(255,255,255,.07); color: rgba(255,255,255,.9); }
    .sidebar__link--active { background: rgba(255,255,255,.12); color: #fff; font-weight: 600; }
    .sidebar__link-icon { display: flex; align-items: center; justify-content: center; width: 20px; flex-shrink: 0; opacity: .7; }
    .sidebar__link--active .sidebar__link-icon { opacity: 1; }
    .sidebar__link-label { white-space: nowrap; }
    .sidebar__footer { margin-top: auto; padding: 12px 18px; }
    .sidebar__version { font-size: 11px; color: rgba(255,255,255,.2); }
    .sidebar__badge { margin-left: auto; background: #ef4444; color: #fff; border-radius: 20px; padding: 1px 7px; font-size: 10px; font-weight: 800; line-height: 1.6; }
  `]
})
export class SidebarComponent implements OnInit, OnDestroy {
  private chatSvc = inject(ChatService);
  private sub?: Subscription;
  unreadTotal = 0;

  ngOnInit(): void {
    this.chatSvc.loadUnread();
    this.sub = this.chatSvc.unread.subscribe(u => this.unreadTotal = u.total);
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }
}