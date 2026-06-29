import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [NgFor, NgIf],
  template: `
    <nav class="pagination" *ngIf="totalPages > 1">
      <button class="pagination__btn" (click)="change(currentPage - 1)" [disabled]="currentPage === 1">&#8249;</button>
      <button *ngFor="let p of pages" class="pagination__btn" [class.pagination__btn--active]="p === currentPage"
        (click)="change(p)">{{ p }}</button>
      <button class="pagination__btn" (click)="change(currentPage + 1)" [disabled]="currentPage === totalPages">&#8250;</button>
    </nav>`,
  styles: [`
    .pagination { display: flex; align-items: center; justify-content: center; gap: 6px; padding: 16px 0; }
    .pagination__btn {
      width: 36px; height: 36px; border-radius: 50%; border: none;
      background: var(--color-surface); color: var(--color-text);
      font-size: 14px; font-weight: 500; cursor: pointer; transition: var(--transition);
      display: flex; align-items: center; justify-content: center;
      box-shadow: var(--shadow-sm);
    }
    .pagination__btn:hover:not(:disabled):not(.pagination__btn--active) { background: var(--color-bg); }
    .pagination__btn:disabled { opacity: .4; cursor: default; }
    .pagination__btn--active { background: var(--color-accent); color: #fff; font-weight: 700; }
  `]
})
export class PaginationComponent {
  @Input() currentPage = 1;
  @Input() totalPages = 1;
  @Output() pageChange = new EventEmitter<number>();

  get pages(): number[] {
    const delta = 2;
    const range: number[] = [];
    for (let i = Math.max(1, this.currentPage - delta); i <= Math.min(this.totalPages, this.currentPage + delta); i++) range.push(i);
    return range;
  }
  change(p: number): void { if (p >= 1 && p <= this.totalPages) this.pageChange.emit(p); }
}
