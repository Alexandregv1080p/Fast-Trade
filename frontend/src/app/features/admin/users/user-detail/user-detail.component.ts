import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NgFor, NgIf, DatePipe, CurrencyPipe, CommonModule } from '@angular/common';
import { UsersHttpService } from '../../../../core/services/users-http.service';
import { OrdersHttpService } from '../../../../core/services/orders-http.service';
import { FinancialHttpService } from '../../../../core/services/financial-http.service';
import { SpinnerComponent } from '../../../../shared/components/spinner/spinner.component';

const STATUS_LABELS: Record<string, string> = {
  PENDING:'Pendente', CONFIRMED:'Confirmado', PICKING:'Separando', PACKED:'Embalado',
  SHIPPED:'Enviado', DELIVERED:'Entregue', CANCELLED:'Cancelado', REFUNDED:'Reembolsado', DEVOLUTION:'Devolução'
};
const STATUS_COLORS: Record<string, string> = {
  PENDING:'badge--warning', CONFIRMED:'badge--info', PICKING:'badge--info', PACKED:'badge--info',
  SHIPPED:'badge--primary', DELIVERED:'badge--success', CANCELLED:'badge--error', REFUNDED:'badge--error', DEVOLUTION:'badge--error'
};

@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule, RouterLink, DatePipe, CurrencyPipe, SpinnerComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <span class="page__breadcrumb"><a routerLink="/admin/users">USUÁRIOS</a> / DETALHE</span>
        <h1 class="page__title">Detalhes do Usuário</h1>
      </div>
      <app-spinner *ngIf="loading" />
      <ng-container *ngIf="!loading && user">

        <!-- Profile card -->
        <div class="card user-profile">
          <div class="user-profile__avatar">{{ user.name?.charAt(0) || 'U' }}</div>
          <div class="user-profile__fields">
            <div class="info-row">
              <div class="info-field"><span class="info-field__label">Nome</span><span>{{ user.name }}</span></div>
              <div class="info-field"><span class="info-field__label">E-mail</span><span>{{ user.email }}</span></div>
              <div class="info-field"><span class="info-field__label">Nascimento</span><span>{{ user.birthDate || '—' }}</span></div>
              <div class="info-field"><span class="info-field__label">Estado</span><span>{{ user.addressState || '—' }}</span></div>
              <div class="info-field"><span class="info-field__label">Cidade</span><span>{{ user.addressCity || '—' }}</span></div>
            </div>
            <div class="info-row">
              <div class="info-field"><span class="info-field__label">Telefone</span><span>{{ user.phone || '—' }}</span></div>
              <div class="info-field"><span class="info-field__label">TRADEs</span><span>{{ user.trades ?? 0 }}</span></div>
              <div class="info-field"><span class="info-field__label">CPF/CNPJ</span><span>{{ user.cpfCnpj || '—' }}</span></div>
              <div class="info-field"><span class="info-field__label">Sexo</span><span>{{ user.gender || '—' }}</span></div>
              <div class="info-field"><span class="info-field__label">Score</span><span>⭐ {{ user.score ?? 0 }}</span></div>
            </div>
          </div>
          <span class="badge" [class]="user.blocked ? 'badge--error' : 'badge--success'">{{ user.blocked ? 'Bloqueado' : 'Ativo' }}</span>
        </div>

        <!-- Tabs -->
        <div class="tabs">
          <button *ngFor="let t of tabs" class="tabs__btn" [class.tabs__btn--active]="activeTab === t.key"
            (click)="setTab(t.key)">
            {{ t.label }}
            <span class="tabs__count" *ngIf="t.count !== null">{{ t.count }}</span>
          </button>
        </div>

        <!-- PEDIDOS -->
        <div class="card" *ngIf="activeTab === 'orders'">
          <app-spinner *ngIf="loadingOrders" />
          <table class="table" *ngIf="!loadingOrders">
            <thead><tr><th>Nº Pedido</th><th>Data</th><th>Pagamento</th><th>Status</th><th>Total</th><th></th></tr></thead>
            <tbody>
              <tr *ngFor="let o of orders">
                <td class="td--mono">#{{ o.orderNumber || o.id }}</td>
                <td>{{ o.createdAt | date:'dd/MM/yy HH:mm' }}</td>
                <td>{{ o.paymentMethod || '—' }}</td>
                <td><span class="badge" [class]="statusColor(o.status)">{{ statusLabel(o.status) }}</span></td>
                <td class="td--bold">{{ o.total | currency:'BRL' }}</td>
                <td><a [routerLink]="['/admin/orders', o.id]" class="link">Ver</a></td>
              </tr>
              <tr *ngIf="!orders.length"><td colspan="6" class="table__empty">Nenhum pedido encontrado.</td></tr>
            </tbody>
          </table>
        </div>

        <!-- PRODUTOS (itens únicos comprados) -->
        <div class="card" *ngIf="activeTab === 'products'">
          <app-spinner *ngIf="loadingOrders" />
          <table class="table" *ngIf="!loadingOrders">
            <thead><tr><th>Produto</th><th>Qtd Total</th><th>Valor Total</th><th>Último Pedido</th></tr></thead>
            <tbody>
              <tr *ngFor="let p of purchasedProducts">
                <td class="td--bold">{{ p.name }}</td>
                <td>{{ p.qty }}</td>
                <td>{{ p.total | currency:'BRL' }}</td>
                <td>{{ p.lastOrder | date:'dd/MM/yy' }}</td>
              </tr>
              <tr *ngIf="!purchasedProducts.length"><td colspan="4" class="table__empty">Nenhum produto encontrado.</td></tr>
            </tbody>
          </table>
        </div>

        <!-- FINANCEIRO -->
        <div class="card" *ngIf="activeTab === 'financial'">
          <app-spinner *ngIf="loadingFinancial" />
          <ng-container *ngIf="!loadingFinancial">
            <div class="fin-summary">
              <div class="fin-kpi fin-kpi--green">
                <span class="fin-kpi__label">Total Créditos</span>
                <span class="fin-kpi__value">{{ totalCredits | currency:'BRL' }}</span>
              </div>
              <div class="fin-kpi fin-kpi--red">
                <span class="fin-kpi__label">Total Débitos</span>
                <span class="fin-kpi__value">{{ totalDebits | currency:'BRL' }}</span>
              </div>
              <div class="fin-kpi fin-kpi--blue">
                <span class="fin-kpi__label">Saldo</span>
                <span class="fin-kpi__value">{{ (totalCredits - totalDebits) | currency:'BRL' }}</span>
              </div>
            </div>
            <table class="table">
              <thead><tr><th>Data</th><th>Descrição</th><th>Método</th><th>Status</th><th>Valor</th></tr></thead>
              <tbody>
                <tr *ngFor="let t of transactions">
                  <td>{{ t.createdAt | date:'dd/MM/yy HH:mm' }}</td>
                  <td>{{ t.description || '—' }}</td>
                  <td>{{ t.paymentMethod || '—' }}</td>
                  <td><span class="badge" [class]="txStatusColor(t.status)">{{ t.status }}</span></td>
                  <td class="td--amount" [class.td--credit]="t.type==='CREDIT'" [class.td--debit]="t.type==='DEBIT'">
                    {{ t.type === 'CREDIT' ? '+' : '-' }}{{ t.amount | currency:'BRL' }}
                  </td>
                </tr>
                <tr *ngIf="!transactions.length"><td colspan="5" class="table__empty">Nenhuma transação encontrada.</td></tr>
              </tbody>
            </table>
          </ng-container>
        </div>

      </ng-container>
    </div>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 20px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .page__breadcrumb a { color: inherit; }
    .page__head { display: flex; flex-direction: column; gap: 4px; }
    .page__title { font-size: 24px; font-weight: 800; }
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm); }
    .user-profile { display: flex; align-items: flex-start; gap: 24px; }
    .user-profile__avatar { width: 72px; height: 72px; border-radius: 50%; background: var(--color-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: 800; flex-shrink: 0; }
    .user-profile__fields { flex: 1; }
    .info-row { display: flex; flex-wrap: wrap; gap: 24px; margin-bottom: 12px; }
    .info-field { display: flex; flex-direction: column; gap: 2px; min-width: 120px; }
    .info-field__label { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .tabs { display: flex; border-bottom: 2px solid var(--color-border); gap: 4px; }
    .tabs__btn { background: none; border: none; padding: 10px 18px; font-size: 14px; font-weight: 600; color: var(--color-text-muted); cursor: pointer; border-bottom: 2px solid transparent; margin-bottom: -2px; display: flex; align-items: center; gap: 7px; }
    .tabs__btn--active { color: var(--color-accent); border-bottom-color: var(--color-accent); }
    .tabs__count { background: var(--color-bg); border: 1px solid var(--color-border); border-radius: 999px; font-size: 11px; font-weight: 700; padding: 1px 7px; color: var(--color-text-muted); }
    .tabs__btn--active .tabs__count { background: var(--color-accent); border-color: var(--color-accent); color: #fff; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); }
    td { padding: 12px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .td--bold { font-weight: 600; }
    .td--mono { font-family: monospace; font-size: 12px; color: var(--color-text-muted); }
    .td--amount { font-weight: 700; }
    .td--credit { color: #16a34a; }
    .td--debit { color: #dc2626; }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 32px; }
    .link { color: var(--color-accent); font-size: 13px; font-weight: 600; text-decoration: none; }
    .link:hover { text-decoration: underline; }
    .fin-summary { display: flex; gap: 16px; margin-bottom: 24px; }
    .fin-kpi { flex: 1; border-radius: var(--radius-md); padding: 16px 20px; display: flex; flex-direction: column; gap: 4px; }
    .fin-kpi--green { background: #f0fdf4; border: 1.5px solid #bbf7d0; }
    .fin-kpi--red { background: #fef2f2; border: 1.5px solid #fecaca; }
    .fin-kpi--blue { background: #eff6ff; border: 1.5px solid #bfdbfe; }
    .fin-kpi__label { font-size: 11px; font-weight: 600; text-transform: uppercase; color: var(--color-text-muted); }
    .fin-kpi--green .fin-kpi__value { color: #16a34a; }
    .fin-kpi--red .fin-kpi__value { color: #dc2626; }
    .fin-kpi--blue .fin-kpi__value { color: #2563eb; }
    .fin-kpi__value { font-size: 20px; font-weight: 800; }
  `]
})
export class UserDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private usersHttp = inject(UsersHttpService);
  private ordersHttp = inject(OrdersHttpService);
  private financialHttp = inject(FinancialHttpService);

  user: any = null;
  loading = true;
  loadingOrders = false;
  loadingFinancial = false;

  orders: any[] = [];
  purchasedProducts: any[] = [];
  transactions: any[] = [];
  totalCredits = 0;
  totalDebits = 0;

  tabs = [
    { key: 'orders', label: 'Pedidos', count: null as number | null },
    { key: 'products', label: 'Produtos', count: null as number | null },
    { key: 'financial', label: 'Financeiro', count: null as number | null },
  ];
  activeTab = 'orders';

  statusLabel = (s: string) => STATUS_LABELS[s] ?? s;
  statusColor = (s: string) => STATUS_COLORS[s] ?? 'badge--gray';
  txStatusColor = (s: string) => ({ COMPLETED: 'badge--success', PENDING: 'badge--warning', FAILED: 'badge--error' }[s] ?? 'badge--gray');

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.usersHttp.getById(id).subscribe({
      next: u => { this.user = u?.data ?? u; this.loading = false; this.loadOrders(id); this.loadFinancial(id); },
      error: () => this.loading = false
    });
  }

  setTab(key: string): void {
    this.activeTab = key;
  }

  loadOrders(userId: string): void {
    this.loadingOrders = true;
    this.ordersHttp.getByUserId(userId).subscribe({
      next: (list: any[]) => {
        this.orders = list ?? [];
        this.tabs[0].count = this.orders.length;
        // Build purchased products summary
        const map = new Map<string, any>();
        for (const o of this.orders) {
          for (const item of (o.items ?? [])) {
            const key = item.productName;
            if (!map.has(key)) map.set(key, { name: key, qty: 0, total: 0, lastOrder: o.createdAt });
            const e = map.get(key);
            e.qty += item.quantity ?? 1;
            e.total += (item.subtotal ?? item.price ?? 0);
            if (o.createdAt > e.lastOrder) e.lastOrder = o.createdAt;
          }
        }
        this.purchasedProducts = Array.from(map.values());
        this.tabs[1].count = this.purchasedProducts.length;
        this.loadingOrders = false;
      },
      error: () => this.loadingOrders = false
    });
  }

  loadFinancial(userId: string): void {
    this.loadingFinancial = true;
    this.financialHttp.getStatement(userId).subscribe({
      next: (list: any[]) => {
        this.transactions = list ?? [];
        this.tabs[2].count = this.transactions.length;
        this.totalCredits = this.transactions.filter(t => t.type === 'CREDIT').reduce((s, t) => s + Number(t.amount), 0);
        this.totalDebits = this.transactions.filter(t => t.type === 'DEBIT').reduce((s, t) => s + Number(t.amount), 0);
        this.loadingFinancial = false;
      },
      error: () => this.loadingFinancial = false
    });
  }
}
