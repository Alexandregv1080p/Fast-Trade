import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [NgIf],
  template: `
    <button [type]="type" [class]="classes" [disabled]="disabled || loading">
      <span *ngIf="loading" class="btn__spinner"></span>
      <ng-content />
    </button>`,
  styles: [`
    button {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 7px;
      padding: 9px 18px;
      border-radius: var(--radius-md);
      font-size: 13px;
      font-weight: 600;
      letter-spacing: .1px;
      transition: all var(--transition);
      border: 1.5px solid transparent;
      white-space: nowrap;
      line-height: 1;
    }
    button:disabled { opacity: .55; cursor: not-allowed; }

    .btn--primary {
      background: var(--color-accent);
      color: #fff;
      border-color: var(--color-accent);
      box-shadow: 0 1px 3px rgba(37,99,235,.25);
    }
    .btn--primary:hover:not(:disabled) {
      background: var(--color-accent-dark);
      border-color: var(--color-accent-dark);
      box-shadow: 0 3px 8px rgba(37,99,235,.35);
    }

    .btn--accent {
      background: var(--color-primary);
      color: #fff;
      border-color: var(--color-primary);
    }
    .btn--accent:hover:not(:disabled) { background: var(--color-primary-dark); border-color: var(--color-primary-dark); }

    .btn--outline {
      background: transparent;
      color: var(--color-accent);
      border-color: var(--color-accent);
    }
    .btn--outline:hover:not(:disabled) { background: var(--color-accent-bg); }

    .btn--ghost {
      background: transparent;
      color: var(--color-text-muted);
      border-color: var(--color-border);
    }
    .btn--ghost:hover:not(:disabled) { background: var(--color-bg-alt); color: var(--color-text); }

    .btn--danger {
      background: var(--color-error);
      color: #fff;
      border-color: var(--color-error);
    }
    .btn--danger:hover:not(:disabled) { background: #b91c1c; border-color: #b91c1c; }

    .btn--sm { padding: 6px 12px; font-size: 12px; }
    .btn--lg { padding: 12px 24px; font-size: 15px; }
    .btn--full { width: 100%; }

    .btn__spinner {
      width: 14px; height: 14px; border-radius: 50%;
      border: 2px solid rgba(255,255,255,.3);
      border-top-color: #fff;
      animation: spin .65s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class ButtonComponent {
  @Input() variant: 'primary' | 'accent' | 'outline' | 'ghost' | 'danger' = 'primary';
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled = false;
  @Input() loading = false;
  @Input() fullWidth = false;

  get classes(): string {
    return ['btn', `btn--${this.variant}`, this.size !== 'md' ? `btn--${this.size}` : '', this.fullWidth ? 'btn--full' : ''].filter(Boolean).join(' ');
  }
}
