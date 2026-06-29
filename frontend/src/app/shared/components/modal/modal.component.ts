import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgIf } from '@angular/common';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [NgIf, ButtonComponent],
  template: `
    <div class="modal-backdrop" *ngIf="open" (click)="onBackdropClick($event)">
      <div class="modal" [style.width]="width" (click)="$event.stopPropagation()">
        <div class="modal__header">
          <h3 class="modal__title">{{ title }}</h3>
          <button class="modal__close" (click)="closeModal.emit()">✕</button>
        </div>
        <div class="modal__body"><ng-content /></div>
        <div class="modal__footer" *ngIf="showFooter">
          <app-button variant="ghost" (click)="closeModal.emit()">Cancelar</app-button>
          <app-button [variant]="confirmVariant" [loading]="loading" [disabled]="confirmDisabled" (click)="confirm.emit()">{{ confirmLabel }}</app-button>
        </div>
      </div>
    </div>`,
  styles: [`
    .modal-backdrop {
      position: fixed; inset: 0; background: rgba(0,0,0,.45); backdrop-filter: blur(2px);
      z-index: 1000; display: flex; align-items: center; justify-content: center; padding: 16px;
    }
    .modal {
      background: var(--color-surface); border-radius: var(--radius-lg); box-shadow: var(--shadow-lg);
      display: flex; flex-direction: column; max-height: 90vh; overflow: hidden;
    }
    .modal__header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; border-bottom: 1px solid var(--color-border); }
    .modal__title { font-size: 18px; font-weight: 700; }
    .modal__close { background: none; border: none; font-size: 18px; color: var(--color-text-muted); padding: 4px 8px; border-radius: var(--radius-sm); cursor: pointer; }
    .modal__close:hover { background: var(--color-bg); }
    .modal__body { padding: 24px; overflow-y: auto; flex: 1; }
    .modal__footer { display: flex; justify-content: flex-end; gap: 12px; padding: 16px 24px; border-top: 1px solid var(--color-border); }
  `]
})
export class ModalComponent {
  @Input() open = false;
  @Input() title = '';
  @Input() width = '480px';
  @Input() showFooter = true;
  @Input() confirmLabel = 'Salvar';
  @Input() confirmVariant: any = 'primary';
  @Input() confirmDisabled = false;
  @Input() loading = false;
  @Output() closeModal = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  onBackdropClick(e: MouseEvent): void { this.closeModal.emit(); }
}
