import { Component } from '@angular/core';
import { NgFor, NgClass, AsyncPipe } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [NgFor, NgClass, AsyncPipe],
  template: `
    <div class="toast-container">
      <div *ngFor="let t of (toastService.toasts$ | async)" class="toast" [ngClass]="'toast--' + t.type">
        <span class="toast__icon">{{ icons[t.type] }}</span>
        <span class="toast__msg">{{ t.message }}</span>
        <button class="toast__close" (click)="toastService.remove(t.id)">✕</button>
      </div>
    </div>`,
  styles: [`
    .toast-container { position: fixed; top: 80px; right: 16px; z-index: 9999; display: flex; flex-direction: column; gap: 8px; }
    .toast {
      display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: var(--radius-md);
      box-shadow: var(--shadow-md); min-width: 280px; max-width: 380px; animation: slideIn .25s ease;
      background: var(--color-surface); border-left: 4px solid;
    }
    .toast--success { border-color: var(--color-success); }
    .toast--error   { border-color: var(--color-error); }
    .toast--info    { border-color: var(--color-info); }
    .toast--warning { border-color: #ffc107; }
    .toast__msg { flex: 1; font-size: 13px; font-weight: 500; }
    .toast__close { background: none; border: none; color: var(--color-text-muted); padding: 2px; cursor: pointer; }
    @keyframes slideIn { from { transform: translateX(100%); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
  `]
})
export class ToastContainerComponent {
  icons: Record<string, string> = { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' };
  constructor(public toastService: ToastService) {}
}
