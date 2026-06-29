import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';
@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [NgIf],
  template: `
    <div class="empty">
      <div class="empty__icon">{{ icon }}</div>
      <p class="empty__title">{{ title }}</p>
      <p *ngIf="subtitle" class="empty__subtitle">{{ subtitle }}</p>
      <ng-content />
    </div>`,
  styles: [`
    .empty { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 60px 20px; text-align: center; gap: 8px; }
    .empty__icon { font-size: 48px; opacity: .5; }
    .empty__title { font-size: 16px; font-weight: 600; color: var(--color-text); }
    .empty__subtitle { font-size: 13px; color: var(--color-text-muted); }
  `]
})
export class EmptyStateComponent {
  @Input() icon = '📭';
  @Input() title = 'Nenhum resultado encontrado';
  @Input() subtitle?: string;
}
