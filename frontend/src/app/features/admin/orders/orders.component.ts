import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, DatePipe, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { OrdersHttpService } from '../../../core/services/orders-http.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';

const STATUS_LABELS: Record<string, string> = { PENDING:'Pendente', CONFIRMED:'Confirmado', PICKING:'Separando', PACKED:'Embalado', SHIPPED:'Enviado', DELIVERED:'Entregue', CANCELLED:'Cancelado', REFUNDED:'Reembolsado', DEVOLUTION:'Devolução' };
const STATUS_BADGES: Record<string, string> = { PENDING:'warning', CONFIRMED:'info', PICKING:'info', PACKED:'info', SHIPPED:'info', DELIVERED:'success', CANCELLED:'error', REFUNDED:'error', DEVOLUTION:'warning' };

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, FormsModule, DatePipe, CurrencyPipe, ButtonComponent, PaginationComponent, SpinnerComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <div>
          <span class="page__breadcrumb">PEDIDOS</span>
          <h1 class="page__title">Pedidos</h1>
        </div>
        <div class="page__head-actions">
          <app-button variant="outline" routerLink="/admin/orders/returns">Devoluções</app-button>
        </div>
      </div>
      <div class="card">
        <div class="card__toolbar">
          <div class="search-box">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            <input [(ngModel)]="search" (input)="onSearch()" placeholder="Buscar pedido ou usuário..." />
          </div>
          <select class="filter-select" [(ngModel)]="statusFilter" (change)="onSearch()">
            <option value="">Todos os status</option>
            <option *ngFor="let s of statusOptions" [value]="s.value">{{ s.label }}</option>
          </select>
        </div>
        <app-spinner *ngIf="loading" />
        <div class="table-wrap" *ngIf="!loading">
          <table class="table">
            <thead><tr><th>Nº Pedido</th><th>Usuário</th><th>Data</th><th>Total</th><th>Pagamento</th><th>Status</th><th></th></tr></thead>
            <tbody>
              <tr *ngFor="let o of orders">
                <td class="td--bold">#{{ o.orderNumber || o.id }}</td>
                <td>{{ o.userName }}</td>
                <td>{{ o.createdAt | date:'dd/MM/yy' }}</td>
                <td>{{ o.total | currency:'BRL' }}</td>
                <td>{{ o.paymentMethod || '—' }}</td>
                <td><span class="badge" [class]="'badge--' + badge(o.status)">{{ label(o.status) }}</span></td>
                <td>
                  <a [routerLink]="['/admin/orders', o.id]" class="btn-icon" title="Ver detalhes">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  </a>
                </td>
              </tr>
              <tr *ngIf="!orders.length"><td colspan="7" class="table__empty">Nenhum pedido encontrado.</td></tr>
            </tbody>
          </table>
        </div>
        <app-pagination [currentPage]="page" [totalPages]="totalPages" (pageChange)="onPageChange($event)" />
      </div>
    </div>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 24px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px; }
    .page__head { display: flex; align-items: flex-start; justify-content: space-between; }
    .page__head-actions { display: flex; gap: 8px; }
    .page__title { font-size: 24px; font-weight: 800; margin-top: 4px; }
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); }
    .card__toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
    .search-box { display: flex; align-items: center; gap: 8px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); padding: 8px 12px; flex: 1; max-width: 320px; }
    .search-box input { border: none; outline: none; font-size: 13px; width: 100%; }
    .filter-select { padding: 8px 12px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 13px; outline: none; background: var(--color-surface); }
    .table-wrap { overflow-x: auto; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); }
    td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .td--bold { font-weight: 600; }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
    .btn-icon { background: none; border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 5px 8px; cursor: pointer; font-size: 14px; }
    .btn-icon:hover { background: var(--color-bg); }
  `]
})
export class OrdersComponent implements OnInit {
  private ordersHttp = inject(OrdersHttpService);
  orders: any[] = []; loading = true; page = 1; totalPages = 1; search = ''; statusFilter = '';
  statusOptions = Object.entries(STATUS_LABELS).map(([value, label]) => ({ value, label }));
  label = (s: string) => STATUS_LABELS[s] ?? s;
  badge = (s: string) => STATUS_BADGES[s] ?? 'gray';

  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true;
    this.ordersHttp.getAll({ page: this.page, pageSize: 10, search: this.search || undefined, status: this.statusFilter || undefined }).subscribe({
      next: r => { this.orders = Array.isArray(r) ? r : r?.data ?? []; this.totalPages = r?.totalPages ?? 1; this.loading = false; },
      error: () => this.loading = false
    });
  }
  onSearch(): void { this.page = 1; this.load(); }
  onPageChange(p: number): void { this.page = p; this.load(); }
}
