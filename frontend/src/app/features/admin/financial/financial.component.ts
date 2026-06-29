import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, DatePipe, CurrencyPipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FinancialHttpService } from '../../../core/services/financial-http.service';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';

@Component({
  selector: 'app-financial',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule, FormsModule, DatePipe, CurrencyPipe, PaginationComponent, SpinnerComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <div><span class="page__breadcrumb">FINANCEIRO</span><h1 class="page__title">Financeiro</h1></div>
      </div>

      <!-- KPI cards -->
      <div class="kpi-row" *ngIf="!loading">
        <div class="kpi-card">
          <div class="kpi-card__icon kpi-card__icon--green">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>
          </div>
          <div class="kpi-card__body">
            <span class="kpi-card__label">Total Entradas</span>
            <span class="kpi-card__value kpi-card__value--green">{{ totalCredits | currency:'BRL' }}</span>
          </div>
          <div class="kpi-card__tag kpi-card__tag--green">CRÉDITO</div>
        </div>

        <div class="kpi-card">
          <div class="kpi-card__icon kpi-card__icon--red">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>
          </div>
          <div class="kpi-card__body">
            <span class="kpi-card__label">Total Saídas</span>
            <span class="kpi-card__value kpi-card__value--red">{{ totalDebits | currency:'BRL' }}</span>
          </div>
          <div class="kpi-card__tag kpi-card__tag--red">DÉBITO</div>
        </div>

        <div class="kpi-card">
          <div class="kpi-card__icon kpi-card__icon--blue">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
          </div>
          <div class="kpi-card__body">
            <span class="kpi-card__label">Saldo Líquido</span>
            <span class="kpi-card__value" [class.kpi-card__value--green]="balance>=0" [class.kpi-card__value--red]="balance<0">{{ balance | currency:'BRL' }}</span>
          </div>
          <div class="kpi-card__tag kpi-card__tag--blue">SALDO</div>
        </div>

        <div class="kpi-card">
          <div class="kpi-card__icon kpi-card__icon--purple">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
          </div>
          <div class="kpi-card__body">
            <span class="kpi-card__label">Transações</span>
            <span class="kpi-card__value">{{ totalTx }}</span>
          </div>
          <div class="kpi-card__tag kpi-card__tag--purple">TOTAL</div>
        </div>

        <div class="kpi-card">
          <div class="kpi-card__icon kpi-card__icon--yellow">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="kpi-card__body">
            <span class="kpi-card__label">Pendentes</span>
            <span class="kpi-card__value kpi-card__value--yellow">{{ pendingTx }}</span>
          </div>
          <div class="kpi-card__tag kpi-card__tag--yellow">AGUARDANDO</div>
        </div>
      </div>

      <!-- Tabs + Table -->
      <div class="tabs">
        <button *ngFor="let t of tabs" class="tabs__btn" [class.tabs__btn--active]="activeTab===t" (click)="activeTab=t; load()">{{ t }}</button>
      </div>
      <div class="card">
        <app-spinner *ngIf="loading" />
        <div class="table-wrap" *ngIf="!loading">
          <table class="table">
            <thead><tr>
              <th>Data</th><th>Usuário</th><th>Tipo</th><th>Descrição</th><th>Valor</th><th>Status</th><th></th>
            </tr></thead>
            <tbody>
              <tr *ngFor="let t of transactions">
                <td>{{ t.createdAt | date:'dd/MM/yy HH:mm' }}</td>
                <td>{{ t.userName || t.userId }}</td>
                <td>
                  <span class="type-badge" [class.type-badge--credit]="t.type==='CREDIT'" [class.type-badge--debit]="t.type==='DEBIT'">
                    <svg *ngIf="t.type==='CREDIT'" width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="19" x2="12" y2="5"/><polyline points="5 12 12 5 19 12"/></svg>
                    <svg *ngIf="t.type==='DEBIT'" width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><polyline points="19 12 12 19 5 12"/></svg>
                    {{ t.type === 'CREDIT' ? 'Crédito' : 'Débito' }}
                  </span>
                </td>
                <td>{{ t.description || t.paymentMethod || '—' }}</td>
                <td class="td--amount" [class.td--positive]="t.type==='CREDIT'" [class.td--negative]="t.type==='DEBIT'">
                  {{ t.type === 'CREDIT' ? '+' : '-' }}{{ t.amount | currency:'BRL' }}
                </td>
                <td>
                  <span class="badge" [class]="t.status==='COMPLETED' ? 'badge--success' : t.status==='PENDING' ? 'badge--warning' : 'badge--error'">
                    {{ t.status === 'COMPLETED' ? 'Concluído' : t.status === 'PENDING' ? 'Pendente' : 'Falhou' }}
                  </span>
                </td>
                <td class="td--view">
                  <button class="btn-view" (click)="openLabel(t)" title="Ver etiqueta">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  </button>
                </td>
              </tr>
              <tr *ngIf="!transactions.length"><td colspan="7" class="table__empty">Nenhuma transação.</td></tr>
            </tbody>
          </table>
        </div>
        <app-pagination [currentPage]="page" [totalPages]="totalPages" (pageChange)="onPageChange($event)" />
      </div>
    </div>

    <!-- Label Modal -->
    <div class="label-overlay" *ngIf="labelModal" (click)="labelModal=false">
      <div class="label-modal" (click)="$event.stopPropagation()" *ngIf="selected">

        <!-- Header strip -->
        <div class="label-header" [class.label-header--credit]="selected.type==='CREDIT'" [class.label-header--debit]="selected.type==='DEBIT'">
          <div class="label-header__left">
            <span class="label-header__tag">{{ selected.type === 'CREDIT' ? '↑ CRÉDITO' : '↓ DÉBITO' }}</span>
            <span class="label-header__amount">{{ (selected.type==='CREDIT' ? '+' : '-') }}{{ selected.amount | currency:'BRL' }}</span>
          </div>
          <span class="label-header__status"
            [class.label-header__status--ok]="selected.status==='COMPLETED'"
            [class.label-header__status--pending]="selected.status==='PENDING'"
            [class.label-header__status--fail]="selected.status==='FAILED'">
            {{ selected.status === 'COMPLETED' ? 'CONCLUÍDO' : selected.status === 'PENDING' ? 'PENDENTE' : 'FALHOU' }}
          </span>
        </div>

        <!-- Body -->
        <div class="label-body">
          <div class="label-row">
            <span class="label-row__key">ID Transação</span>
            <span class="label-row__val label-row__val--mono">#{{ selected.id }}</span>
          </div>
          <div class="label-row">
            <span class="label-row__key">Data / Hora</span>
            <span class="label-row__val">{{ selected.createdAt | date:'dd/MM/yyyy HH:mm:ss' }}</span>
          </div>
          <div class="label-row">
            <span class="label-row__key">Usuário</span>
            <span class="label-row__val">{{ selected.userName || selected.userId }}</span>
          </div>
          <div class="label-row">
            <span class="label-row__key">Método de Pagamento</span>
            <span class="label-row__val">{{ selected.paymentMethod || '—' }}</span>
          </div>
          <div class="label-row">
            <span class="label-row__key">Descrição</span>
            <span class="label-row__val">{{ selected.description || '—' }}</span>
          </div>
          <div class="label-divider"></div>
          <div class="label-total">
            <span>Valor Total</span>
            <span class="label-total__val" [class.label-total__val--credit]="selected.type==='CREDIT'" [class.label-total__val--debit]="selected.type==='DEBIT'">
              {{ (selected.type==='CREDIT' ? '+' : '-') }}{{ selected.amount | currency:'BRL' }}
            </span>
          </div>
        </div>

        <!-- Footer -->
        <div class="label-footer">
          <span class="label-footer__id">Fast Trade • Ref. #{{ selected.id }}</span>
          <button class="label-footer__close" (click)="labelModal=false">Fechar</button>
        </div>
      </div>
    </div>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 20px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px; }
    .page__head { display: flex; flex-direction: column; gap: 4px; }
    .page__title { font-size: 24px; font-weight: 800; }

    /* KPI */
    .kpi-row { display: flex; gap: 16px; flex-wrap: wrap; }
    .kpi-card { flex: 1; min-width: 160px; background: var(--color-surface); border-radius: var(--radius-lg); padding: 18px 20px; box-shadow: var(--shadow-sm); display: flex; align-items: center; gap: 14px; position: relative; overflow: hidden; }
    .kpi-card::before { content: ''; position: absolute; inset: 0; opacity: .04; }
    .kpi-card__icon { width: 44px; height: 44px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .kpi-card__icon--green { background: #dcfce7; color: #16a34a; }
    .kpi-card__icon--red { background: #fee2e2; color: #dc2626; }
    .kpi-card__icon--blue { background: #dbeafe; color: #2563eb; }
    .kpi-card__icon--purple { background: #ede9fe; color: #7c3aed; }
    .kpi-card__icon--yellow { background: #fef9c3; color: #ca8a04; }
    .kpi-card__body { flex: 1; display: flex; flex-direction: column; gap: 3px; }
    .kpi-card__label { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .4px; }
    .kpi-card__value { font-size: 20px; font-weight: 800; color: var(--color-text); }
    .kpi-card__value--green { color: #16a34a; }
    .kpi-card__value--red { color: #dc2626; }
    .kpi-card__value--yellow { color: #ca8a04; }
    .kpi-card__tag { position: absolute; top: 12px; right: 12px; font-size: 9px; font-weight: 800; letter-spacing: .8px; padding: 2px 7px; border-radius: 999px; }
    .kpi-card__tag--green { background: #dcfce7; color: #16a34a; }
    .kpi-card__tag--red { background: #fee2e2; color: #dc2626; }
    .kpi-card__tag--blue { background: #dbeafe; color: #2563eb; }
    .kpi-card__tag--purple { background: #ede9fe; color: #7c3aed; }
    .kpi-card__tag--yellow { background: #fef9c3; color: #ca8a04; }

    /* Tabs */
    .tabs { display: flex; border-bottom: 2px solid var(--color-border); }
    .tabs__btn { background: none; border: none; padding: 10px 20px; font-size: 14px; font-weight: 600; color: var(--color-text-muted); cursor: pointer; border-bottom: 2px solid transparent; margin-bottom: -2px; }
    .tabs__btn--active { color: var(--color-accent); border-bottom-color: var(--color-accent); }

    /* Card / Table */
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); }
    .table-wrap { overflow-x: auto; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); }
    td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
    .td--amount { font-weight: 700; }
    .td--positive { color: #16a34a; }
    .td--negative { color: #dc2626; }
    .type-badge { display: inline-flex; align-items: center; gap: 5px; padding: 3px 10px; border-radius: 999px; font-size: 12px; font-weight: 600; }
    .type-badge--credit { background: #dcfce7; color: #16a34a; }
    .type-badge--debit { background: #fee2e2; color: #dc2626; }

    /* View button */
    .td--view { width: 40px; text-align: center; }
    .btn-view { background: var(--color-bg); border: 1px solid var(--color-border); border-radius: var(--radius-sm); width: 30px; height: 30px; display: inline-flex; align-items: center; justify-content: center; cursor: pointer; color: var(--color-text-muted); transition: all .15s; }
    .btn-view:hover { background: var(--color-accent); color: #fff; border-color: var(--color-accent); }

    /* Label Modal */
    .label-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.45); z-index: 1000; display: flex; align-items: center; justify-content: center; }
    .label-modal { background: #fff; border-radius: 16px; width: 420px; max-width: 94vw; box-shadow: 0 24px 60px rgba(0,0,0,.25); overflow: hidden; font-family: inherit; }
    .label-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; }
    .label-header--credit { background: linear-gradient(135deg, #16a34a, #4ade80); color: #fff; }
    .label-header--debit  { background: linear-gradient(135deg, #dc2626, #f87171); color: #fff; }
    .label-header__left { display: flex; flex-direction: column; gap: 4px; }
    .label-header__tag { font-size: 11px; font-weight: 700; letter-spacing: 1.2px; opacity: .85; }
    .label-header__amount { font-size: 28px; font-weight: 900; letter-spacing: -.5px; }
    .label-header__status { font-size: 11px; font-weight: 800; letter-spacing: 1px; padding: 4px 12px; border-radius: 999px; background: rgba(255,255,255,.25); }
    .label-header__status--ok { background: rgba(255,255,255,.3); }
    .label-header__status--pending { background: rgba(255,255,255,.2); }
    .label-header__status--fail { background: rgba(255,255,255,.2); }
    .label-body { padding: 20px 24px; display: flex; flex-direction: column; gap: 0; }
    .label-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px dashed #e5e7eb; }
    .label-row:last-child { border-bottom: none; }
    .label-row__key { font-size: 12px; font-weight: 600; color: #6b7280; text-transform: uppercase; letter-spacing: .4px; }
    .label-row__val { font-size: 13px; font-weight: 600; color: #111827; text-align: right; }
    .label-row__val--mono { font-family: monospace; font-size: 13px; color: #374151; }
    .label-divider { height: 2px; background: #f3f4f6; margin: 12px 0; border-radius: 2px; }
    .label-total { display: flex; justify-content: space-between; align-items: center; padding: 4px 0 0; }
    .label-total span:first-child { font-size: 14px; font-weight: 700; color: #374151; text-transform: uppercase; letter-spacing: .5px; }
    .label-total__val { font-size: 22px; font-weight: 900; }
    .label-total__val--credit { color: #16a34a; }
    .label-total__val--debit  { color: #dc2626; }
    .label-footer { display: flex; align-items: center; justify-content: space-between; padding: 14px 24px; background: #f9fafb; border-top: 1px solid #e5e7eb; }
    .label-footer__id { font-size: 11px; color: #9ca3af; font-weight: 600; letter-spacing: .3px; }
    .label-footer__close { background: #111827; color: #fff; border: none; border-radius: 8px; padding: 8px 20px; font-size: 13px; font-weight: 700; cursor: pointer; transition: background .15s; }
    .label-footer__close:hover { background: #374151; }
  `]
})
export class FinancialComponent implements OnInit {
  private financialHttp = inject(FinancialHttpService);
  transactions: any[] = [];
  loading = true; page = 1; totalPages = 1;
  tabs = ['Transações', 'Carteiras']; activeTab = 'Transações';

  totalCredits = 0; totalDebits = 0; balance = 0; totalTx = 0; pendingTx = 0;
  selected: any = null;
  labelModal = false;

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.financialHttp.getTransactions({ page: this.page, pageSize: 10 }).subscribe({
      next: r => {
        this.transactions = Array.isArray(r) ? r : r?.data ?? [];
        this.totalPages = r?.totalPages ?? 1;
        this.loading = false;
        this.calcKpis();
      },
      error: () => this.loading = false
    });
  }

  calcKpis(): void {
    const all = this.transactions;
    this.totalCredits = all.filter(t => t.type === 'CREDIT').reduce((s, t) => s + Number(t.amount), 0);
    this.totalDebits  = all.filter(t => t.type === 'DEBIT').reduce((s, t) => s + Number(t.amount), 0);
    this.balance  = this.totalCredits - this.totalDebits;
    this.totalTx  = all.length;
    this.pendingTx = all.filter(t => t.status === 'PENDING').length;
  }

  onPageChange(p: number): void { this.page = p; this.load(); }

  openLabel(t: any): void { this.selected = t; this.labelModal = true; }
}
