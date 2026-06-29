import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { ToastContainerComponent } from '../../../shared/components/toast/toast-container.component';
import { LoadingService } from '../../../core/services/loading.service';
import { AsyncPipe, NgIf } from '@angular/common';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, HeaderComponent, ToastContainerComponent, AsyncPipe, NgIf],
  template: `
    <div class="layout">
      <app-sidebar />
      <div class="layout__main">
        <app-header />
        <main class="layout__content">
          <div *ngIf="loading.loading$ | async" class="layout__loading">
            <div class="layout__loading-bar"></div>
          </div>
          <router-outlet />
        </main>
      </div>
    </div>
    <app-toast-container />`,
  styles: [`
    .layout { display: flex; min-height: 100vh; }
    .layout__main { flex: 1; margin-left: var(--sidebar-width); display: flex; flex-direction: column; min-width: 0; }
    .layout__content { flex: 1; margin-top: var(--header-height); padding: 28px 32px; background: var(--color-bg); min-height: calc(100vh - var(--header-height)); }
    .layout__loading { position: fixed; top: var(--header-height); left: var(--sidebar-width); right: 0; z-index: 9998; }
    .layout__loading-bar { height: 2px; background: var(--color-accent); animation: progress 1.4s cubic-bezier(.4,0,.2,1) infinite; }
    @keyframes progress { 0% { width: 0; opacity: 1; } 70% { width: 75%; opacity: 1; } 100% { width: 100%; opacity: 0; } }
  `]
})
export class AdminLayoutComponent {
  constructor(public loading: LoadingService) {}
}
