import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="stat-card" [attr.data-color]="color">
      <div class="stat-card__icon-wrap">
        <ng-content />
      </div>
      <div class="stat-card__body">
        <span class="stat-card__label">{{ label }}</span>
        <span class="stat-card__value">{{ value | number }}</span>
        <span *ngIf="trend" class="stat-card__trend" [class.stat-card__trend--up]="trendUp" [class.stat-card__trend--down]="!trendUp">
          <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
            <path *ngIf="trendUp" d="M12 19V5m-7 7l7-7 7 7"/>
            <path *ngIf="!trendUp" d="M12 5v14m7-7l-7 7-7-7"/>
          </svg>
          {{ trend }}
        </span>
        <span *ngIf="!trend" class="stat-card__trend stat-card__trend--neutral">vs. mês anterior</span>
      </div>
    </div>`,
  styles: [`
    .stat-card {
      background: var(--color-surface);
      border-radius: var(--radius-lg);
      padding: 20px 22px;
      display: flex;
      align-items: center;
      gap: 18px;
      box-shadow: var(--shadow-sm);
      min-width: 0;
      position: relative;
      overflow: hidden;
      border: 1.5px solid var(--color-border);
      transition: box-shadow .2s, transform .2s;
    }
    .stat-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }

    .stat-card::after {
      content: '';
      position: absolute;
      bottom: 0; left: 0; right: 0;
      height: 3px;
      border-radius: 0 0 var(--radius-lg) var(--radius-lg);
    }
    .stat-card[data-color="blue"]::after   { background: #2563eb; }
    .stat-card[data-color="purple"]::after { background: #7c3aed; }
    .stat-card[data-color="orange"]::after { background: #ea580c; }
    .stat-card[data-color="green"]::after  { background: #16a34a; }

    .stat-card__icon-wrap {
      width: 52px; height: 52px;
      border-radius: 14px;
      display: flex; align-items: center; justify-content: center;
      flex-shrink: 0;
    }
    .stat-card[data-color="blue"]   .stat-card__icon-wrap { background: #dbeafe; color: #2563eb; }
    .stat-card[data-color="purple"] .stat-card__icon-wrap { background: #ede9fe; color: #7c3aed; }
    .stat-card[data-color="orange"] .stat-card__icon-wrap { background: #ffedd5; color: #ea580c; }
    .stat-card[data-color="green"]  .stat-card__icon-wrap { background: #dcfce7; color: #16a34a; }

    .stat-card__body { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
    .stat-card__label { font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .5px; }
    .stat-card__value { font-size: 32px; font-weight: 900; color: var(--color-text); line-height: 1.1; letter-spacing: -1px; }
    .stat-card__trend { font-size: 11px; font-weight: 600; display: flex; align-items: center; gap: 3px; margin-top: 2px; }
    .stat-card__trend--up { color: #16a34a; }
    .stat-card__trend--down { color: #dc2626; }
    .stat-card__trend--neutral { color: var(--color-text-muted); }
  `]
})
export class StatCardComponent {
  @Input() value: number = 0;
  @Input() label = '';
  @Input() trend?: string;
  @Input() trendUp = true;
  @Input() color: 'blue' | 'purple' | 'orange' | 'green' = 'blue';
}
