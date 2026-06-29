import { Component, OnInit, AfterViewInit, OnDestroy, ViewChild, ElementRef, inject } from '@angular/core';
import { NgFor, NgIf, DecimalPipe, DatePipe, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminHttpService } from '../../../core/services/admin-http.service';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';

type Range = '3M' | '6M' | '1A';

const ALL_DATA = [
  { month: 'Jan', users: 60,  orders: 80  }, { month: 'Fev', users: 75,  orders: 90  },
  { month: 'Mar', users: 55,  orders: 70  }, { month: 'Abr', users: 80,  orders: 100 },
  { month: 'Mai', users: 65,  orders: 85  }, { month: 'Jun', users: 90,  orders: 110 },
  { month: 'Jul', users: 70,  orders: 95  }, { month: 'Ago', users: 85,  orders: 105 },
  { month: 'Set', users: 95,  orders: 115 }, { month: 'Out', users: 75,  orders: 90  },
  { month: 'Nov', users: 100, orders: 120 }, { month: 'Dez', users: 110, orders: 130 },
];

const STATUS_ORDER = ['PENDING','CONFIRMED','PICKING','PACKED','SHIPPED','DELIVERED','CANCELLED','REFUNDED'];
const STATUS_LABELS: Record<string, string> = {
  PENDING:'Pendente', CONFIRMED:'Confirmado', PICKING:'Separando', PACKED:'Embalado',
  SHIPPED:'Enviado', DELIVERED:'Entregue', CANCELLED:'Cancelado', REFUNDED:'Reembolsado',
};
const STATUS_BADGES: Record<string, string> = {
  PENDING:'warning', CONFIRMED:'info', PICKING:'info', PACKED:'info',
  SHIPPED:'primary', DELIVERED:'success', CANCELLED:'error', REFUNDED:'error',
};
const AVATAR_COLORS = ['#2563eb','#7c3aed','#ea580c','#16a34a','#0891b2','#be185d','#d97706','#4f46e5'];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, StatCardComponent, SpinnerComponent, DecimalPipe, DatePipe, CurrencyPipe],
  template: `
    <div class="page">

      <!-- Header -->
      <div class="page__head">
        <div class="page__greeting">
          <h1 class="page__title">{{ greeting }}, Admin</h1>
          <p class="page__date">{{ today | date:"EEEE, d 'de' MMMM 'de' yyyy" : '' : 'pt-BR' }}</p>
        </div>
        <div class="page__meta">
          <span class="page__uptime">
            <span class="pulse"></span>
            Sistema online
          </span>
        </div>
      </div>

      <app-spinner *ngIf="loading" />

      <ng-container *ngIf="!loading">

        <!-- KPI Cards -->
        <div class="stats-grid">
          <app-stat-card [value]="stats.users" label="Usuários" [trend]="stats.usersTrend" [trendUp]="true" color="blue">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/>
            </svg>
          </app-stat-card>

          <app-stat-card [value]="stats.products" label="Produtos" color="purple">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 7.5l-9-5.25L3 7.5m18 0l-9 5.25m9-5.25v9l-9 5.25M3 7.5l9 5.25M3 7.5v9l9 5.25m0-9v9"/>
            </svg>
          </app-stat-card>

          <app-stat-card [value]="stats.orders" label="Pedidos" color="orange">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/>
              <path d="M16 10a4 4 0 01-8 0"/>
            </svg>
          </app-stat-card>

          <app-stat-card [value]="stats.trades" label="TRADEs Emitidos" color="green">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/>
            </svg>
          </app-stat-card>
        </div>

        <!-- Main grid: chart + orders -->
        <div class="main-grid">

          <!-- Chart -->
          <div class="card chart-card">
            <div class="card__head">
              <div>
                <h3 class="card__title">Usuários x Pedidos</h3>
                <p class="card__subtitle">Evolução mensal — {{ currentYear }}</p>
              </div>
              <div class="range-btns">
                <button *ngFor="let r of ranges" class="range-btn"
                  [class.range-btn--active]="dateRange === r.value"
                  (click)="setRange(r.value)">{{ r.label }}</button>
              </div>
            </div>
            <div #chartEl style="min-height:280px;"></div>
          </div>

          <!-- Recent orders -->
          <div class="card orders-card">
            <div class="card__head">
              <h3 class="card__title">Últimos Pedidos</h3>
              <a routerLink="/admin/orders" class="card__link">
                Ver todos
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
              </a>
            </div>
            <div class="order-list">
              <a *ngFor="let o of recentOrders; let i = index" [routerLink]="['/admin/orders', o.id]" class="order-item">
                <div class="order-item__avatar" [style.background]="avatarColor(i)">
                  {{ o.userName?.charAt(0)?.toUpperCase() || 'U' }}
                </div>
                <div class="order-item__info">
                  <span class="order-item__number">#{{ o.orderNumber || o.id }}</span>
                  <span class="order-item__user">{{ o.userName }}</span>
                </div>
                <span class="status-dot status-dot--{{ statusBadge(o.status) }}" [title]="statusLabel(o.status)"></span>
                <span class="order-item__badge badge badge--{{ statusBadge(o.status) }}">{{ statusLabel(o.status) }}</span>
              </a>
              <div *ngIf="!recentOrders.length" class="order-list__empty">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="color:var(--color-text-muted)"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>
                <span>Nenhum pedido recente.</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Status summary row -->
        <div class="status-row">
          <div *ngFor="let s of statusSummary" class="status-pill">
            <span class="status-pill__dot status-pill__dot--{{ s.badge }}"></span>
            <span class="status-pill__label">{{ s.label }}</span>
            <span class="status-pill__count">{{ s.count }}</span>
          </div>
        </div>

      </ng-container>
    </div>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 24px; }

    /* Header */
    .page__head { display: flex; align-items: flex-end; justify-content: space-between; }
    .page__title { font-size: 26px; font-weight: 900; color: var(--color-text); margin: 0 0 3px; letter-spacing: -.5px; }
    .page__date { font-size: 13px; color: var(--color-text-muted); font-weight: 500; margin: 0; text-transform: capitalize; }
    .page__meta { display: flex; align-items: center; gap: 12px; }
    .page__uptime { display: flex; align-items: center; gap: 7px; font-size: 12px; font-weight: 600; color: #16a34a; background: #dcfce7; padding: 6px 14px; border-radius: 999px; border: 1px solid #bbf7d0; }
    .pulse { width: 8px; height: 8px; border-radius: 50%; background: #16a34a; animation: pulse 2s infinite; flex-shrink: 0; }
    @keyframes pulse { 0%,100% { opacity:1; transform:scale(1); } 50% { opacity:.6; transform:scale(1.3); } }

    /* Stats grid */
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }

    /* Main grid */
    .main-grid { display: grid; grid-template-columns: 1fr 340px; gap: 16px; }

    /* Cards */
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 22px 24px; box-shadow: var(--shadow-sm); border: 1.5px solid var(--color-border); }
    .card__head { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 12px; gap: 12px; }
    .card__title { font-size: 15px; font-weight: 700; margin: 0 0 2px; color: var(--color-text); }
    .card__subtitle { font-size: 12px; color: var(--color-text-muted); margin: 0; }
    .card__link { font-size: 12px; color: var(--color-accent); font-weight: 600; display: flex; align-items: center; gap: 3px; white-space: nowrap; text-decoration: none; padding-top: 2px; }
    .card__link:hover { text-decoration: underline; }

    /* Range buttons */
    .range-btns { display: flex; gap: 4px; }
    .range-btn { padding: 5px 11px; font-size: 11px; font-weight: 700; border: 1.5px solid var(--color-border); border-radius: 7px; background: none; cursor: pointer; color: var(--color-text-muted); transition: all .15s; letter-spacing: .3px; }
    .range-btn:hover { background: var(--color-bg); color: var(--color-text); }
    .range-btn--active { background: var(--color-primary); border-color: var(--color-primary); color: #fff; }

    /* Orders list */
    .order-list { display: flex; flex-direction: column; gap: 4px; }
    .order-item { display: flex; align-items: center; gap: 11px; text-decoration: none; color: inherit; padding: 10px 10px; border-radius: var(--radius-md); transition: background .12s; cursor: pointer; }
    .order-item:hover { background: var(--color-bg); }
    .order-item__avatar { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 13px; color: #fff; flex-shrink: 0; letter-spacing: -.3px; }
    .order-item__info { display: flex; flex-direction: column; gap: 1px; flex: 1; min-width: 0; }
    .order-item__number { font-weight: 700; font-size: 13px; color: var(--color-text); }
    .order-item__user { font-size: 11px; color: var(--color-text-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .order-item__badge { font-size: 10px; padding: 2px 8px; }
    .order-list__empty { display: flex; flex-direction: column; align-items: center; gap: 8px; font-size: 13px; color: var(--color-text-muted); padding: 32px 20px; text-align: center; }

    /* Status row */
    .status-row { display: flex; gap: 10px; flex-wrap: wrap; }
    .status-pill { background: var(--color-surface); border: 1.5px solid var(--color-border); border-radius: 999px; padding: 7px 16px; display: flex; align-items: center; gap: 8px; font-size: 12px; font-weight: 600; color: var(--color-text-muted); box-shadow: var(--shadow-sm); }
    .status-pill__dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
    .status-pill__dot--warning { background: #f59e0b; }
    .status-pill__dot--info    { background: #3b82f6; }
    .status-pill__dot--success { background: #16a34a; }
    .status-pill__dot--error   { background: #dc2626; }
    .status-pill__dot--primary { background: #6366f1; }
    .status-pill__label { color: var(--color-text); }
    .status-pill__count { background: var(--color-bg); border: 1px solid var(--color-border); border-radius: 999px; padding: 1px 8px; font-size: 11px; font-weight: 700; color: var(--color-text); min-width: 24px; text-align: center; }
  `]
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chartEl') chartEl!: ElementRef<HTMLDivElement>;
  private adminHttp = inject(AdminHttpService);
  private apexChart: any = null;

  loading = false;
  currentYear = new Date().getFullYear();
  today = new Date();
  stats = { users: 0, products: 0, orders: 0, trades: 0, usersTrend: '+0%' };
  recentOrders: any[] = [];
  statusSummary: { label: string; count: number; badge: string }[] = [];

  dateRange: Range = '1A';
  ranges = [
    { label: '3M', value: '3M' as Range },
    { label: '6M', value: '6M' as Range },
    { label: '1A', value: '1A' as Range },
  ];

  get greeting(): string {
    const h = new Date().getHours();
    if (h < 12) return 'Bom dia';
    if (h < 18) return 'Boa tarde';
    return 'Boa noite';
  }

  avatarColor(i: number): string {
    return AVATAR_COLORS[i % AVATAR_COLORS.length];
  }

  private get slicedData() {
    const n = this.dateRange === '3M' ? 3 : this.dateRange === '6M' ? 6 : 12;
    return ALL_DATA.slice(-n);
  }

  private buildChartOptions() {
    const d = this.slicedData;
    return {
      series: [
        { name: 'Pedidos',  type: 'bar',  data: d.map(x => x.orders) },
        { name: 'Usuários', type: 'line', data: d.map(x => x.users)  },
      ],
      chart: { type: 'bar', height: 280, toolbar: { show: false }, fontFamily: 'inherit',
               animations: { enabled: true, easing: 'easeinout', speed: 600 },
               background: 'transparent' },
      colors: ['#6366f1', '#22c55e'],
      stroke: { width: [0, 3], curve: 'smooth' },
      fill: {
        type: ['gradient', 'solid'],
        gradient: {
          shade: 'light', type: 'vertical',
          gradientToColors: ['#818cf8'],
          shadeIntensity: .3, opacityFrom: .95, opacityTo: .7, stops: [0, 100]
        }
      },
      plotOptions: { bar: { borderRadius: 6, columnWidth: '48%', borderRadiusApplication: 'end' } },
      dataLabels: { enabled: false },
      xaxis: {
        categories: d.map(x => x.month),
        labels: { style: { colors: '#94a3b8', fontSize: '12px', fontFamily: 'inherit', fontWeight: 600 } },
        axisBorder: { show: false }, axisTicks: { show: false }
      },
      yaxis: {
        labels: { style: { colors: ['#94a3b8'], fontSize: '12px', fontFamily: 'inherit' } }
      },
      grid: {
        borderColor: '#f1f5f9', strokeDashArray: 5,
        xaxis: { lines: { show: false } },
        padding: { left: 4, right: 4, top: -8 }
      },
      tooltip: {
        shared: true, intersect: false,
        style: { fontSize: '13px', fontFamily: 'inherit' },
        y: { formatter: (v: number) => String(v) },
        theme: 'light',
      },
      legend: {
        show: true, position: 'top', horizontalAlign: 'right',
        fontSize: '12px', fontFamily: 'inherit', fontWeight: 600,
        markers: { size: 7, offsetY: 1 },
        itemMargin: { horizontal: 12 }
      },
    };
  }

  ngOnInit(): void {
    this.adminHttp.getDashboard().subscribe({
      next: d => {
        this.stats = {
          users:      d?.users      ?? d?.totalUsers    ?? 0,
          products:   d?.products   ?? d?.totalProducts ?? 0,
          orders:     d?.orders     ?? d?.totalOrders   ?? 0,
          trades:     d?.trades     ?? d?.totalTrades   ?? 0,
          usersTrend: d?.usersTrend ?? '+0%',
        };
        this.recentOrders = d?.recentOrders ?? [];
        this.buildStatusSummary(d?.ordersByStatus ?? {});
      },
      error: () => {},
    });
  }

  ngAfterViewInit(): void {
    import('apexcharts').then(m => {
      const ApexCharts = m.default ?? m;
      this.apexChart = new (ApexCharts as any)(this.chartEl.nativeElement, this.buildChartOptions());
      this.apexChart.render();
    }).catch(() => {});
  }

  ngOnDestroy(): void { this.apexChart?.destroy(); }

  setRange(r: Range): void {
    this.dateRange = r;
    if (this.apexChart) {
      const d = this.slicedData;
      this.apexChart.updateOptions({
        series: [
          { name: 'Pedidos',  data: d.map(x => x.orders) },
          { name: 'Usuários', data: d.map(x => x.users)  },
        ],
        xaxis: { categories: d.map(x => x.month) },
      });
    }
  }

  private buildStatusSummary(ordersByStatus: Record<string, number>): void {
    this.statusSummary = STATUS_ORDER
      .filter(s => ordersByStatus[s])
      .map(s => ({ label: STATUS_LABELS[s] ?? s, count: ordersByStatus[s], badge: STATUS_BADGES[s] ?? 'info' }));
  }

  statusLabel = (s: string) => STATUS_LABELS[s] ?? s;
  statusBadge = (s: string) => STATUS_BADGES[s] ?? 'info';
}
