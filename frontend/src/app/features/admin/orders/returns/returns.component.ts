import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrdersHttpService } from '../../../../core/services/orders-http.service';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { SpinnerComponent } from '../../../../shared/components/spinner/spinner.component';

@Component({
  selector: 'app-returns',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, DatePipe, CurrencyPipe, PaginationComponent, SpinnerComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <span class="page__breadcrumb">PEDIDOS / DEVOLUÇÕES</span>
        <h1 class="page__title">Devoluções</h1>
      </div>
      <div class="card">
        <app-spinner *ngIf="loading" />
        <div class="table-wrap" *ngIf="!loading">
          <table class="table">
            <thead><tr><th>Nº Pedido</th><th>Usuário</th><th>Data</th><th>Total</th><th>Status</th></tr></thead>
            <tbody>
              <tr *ngFor="let o of orders">
                <td class="td--bold">#{{ o.orderNumber || o.id }}</td>
                <td>{{ o.userName }}</td>
                <td>{{ o.createdAt | date:'dd/MM/yy' }}</td>
                <td>{{ o.total | currency:'BRL' }}</td>
                <td><span class="badge badge--warning">{{ o.status }}</span></td>
              </tr>
              <tr *ngIf="!orders.length"><td colspan="5" class="table__empty">Nenhuma devolução.</td></tr>
            </tbody>
          </table>
        </div>
        <app-pagination [currentPage]="page" [totalPages]="totalPages" (pageChange)="onPageChange($event)" />
      </div>
    </div>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 24px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px; }
    .page__head { display: flex; flex-direction: column; gap: 4px; }
    .page__title { font-size: 24px; font-weight: 800; }
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); }
    .table-wrap { overflow-x: auto; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); }
    td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); }
    tr:last-child td { border-bottom: none; }
    .td--bold { font-weight: 600; }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
  `]
})
export class ReturnsComponent implements OnInit {
  private ordersHttp = inject(OrdersHttpService);
  orders: any[] = []; loading = true; page = 1; totalPages = 1;
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true;
    this.ordersHttp.getDevolutions({ page: this.page, pageSize: 10 }).subscribe({ next: r => { this.orders = Array.isArray(r) ? r : r?.data ?? []; this.totalPages = r?.totalPages ?? 1; this.loading = false; }, error: () => this.loading = false });
  }
  onPageChange(p: number): void { this.page = p; this.load(); }
}
