import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NgFor, NgIf, DatePipe, CurrencyPipe, CommonModule } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { OrdersHttpService } from '../../../../core/services/orders-http.service';
import { ToastService } from '../../../../core/services/toast.service';
import { SpinnerComponent } from '../../../../shared/components/spinner/spinner.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';

const STATUS_LABELS: Record<string, string> = { PENDING:'Pendente', CONFIRMED:'Confirmado', PICKING:'Separando', PACKED:'Embalado', SHIPPED:'Enviado', DELIVERED:'Entregue', CANCELLED:'Cancelado', REFUNDED:'Reembolsado', DEVOLUTION:'Devolução' };

const STATUS_FLOW = ['PENDING','CONFIRMED','PICKING','PACKED','SHIPPED','DELIVERED'];
const STATUS_ICONS: Record<string, string> = {
  PENDING:   '<circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>',
  CONFIRMED: '<polyline points="20 6 9 17 4 12"/>',
  PICKING:   '<rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 00-4 0v2M8 7V5a2 2 0 00-4 0v2"/>',
  PACKED:    '<path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 003 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z"/>',
  SHIPPED:   '<rect x="1" y="3" width="15" height="13" rx="1"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/>',
  DELIVERED: '<path d="M22 11.08V12a10 10 0 11-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>',
};

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule, RouterLink, DatePipe, CurrencyPipe, FormsModule, SpinnerComponent, ButtonComponent, ModalComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <div>
          <span class="page__breadcrumb"><a routerLink="/admin/orders">PEDIDOS</a> / DETALHE</span>
          <h1 class="page__title">Pedido #{{ order?.orderNumber || order?.id }}</h1>
        </div>
        <app-button variant="outline" (click)="statusModal=true" *ngIf="order">Atualizar Status</app-button>
      </div>
      <app-spinner *ngIf="loading" />
      <ng-container *ngIf="!loading && order">

        <!-- Status Stepper -->
        <div class="stepper-card" *ngIf="!isCancelledOrRefunded()">
          <div class="stepper">
            <ng-container *ngFor="let step of steps; let i = index; let last = last">
              <div class="step" [class.step--done]="isStepDone(step.key)" [class.step--active]="isStepActive(step.key)">
                <div class="step__icon" [innerHTML]="step.icon"></div>
                <span class="step__label">{{ step.label }}</span>
              </div>
              <div *ngIf="!last" class="step__line" [class.step__line--done]="isStepDone(step.key)"></div>
            </ng-container>
          </div>
        </div>
        <div class="stepper-card stepper-card--cancelled" *ngIf="isCancelledOrRefunded()">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
          <span>Pedido {{ statusLabel(order.status) }}</span>
        </div>

        <div class="detail-grid">
          <div class="card">
            <h3 class="card__title">Informações do Pedido</h3>
            <div class="info-list">
              <div class="info-row-item"><span>Status</span><span class="badge badge--info">{{ statusLabel(order.status) }}</span></div>
              <div class="info-row-item"><span>Data</span><span>{{ order.createdAt | date:'dd/MM/yyyy HH:mm' }}</span></div>
              <div class="info-row-item"><span>Pagamento</span><span>{{ order.paymentMethod || '—' }}</span></div>
              <div class="info-row-item"><span>Subtotal</span><span>{{ order.subtotal | currency:'BRL' }}</span></div>
              <div class="info-row-item"><span>Frete</span><span>{{ order.shipping | currency:'BRL' }}</span></div>
              <div class="info-row-item info-row-item--total"><span>Total</span><span>{{ order.total | currency:'BRL' }}</span></div>
            </div>
          </div>
          <div class="card">
            <h3 class="card__title">Dados do Comprador</h3>
            <div class="info-list">
              <div class="info-row-item"><span>Nome</span><span>{{ order.userName || '—' }}</span></div>
              <div class="info-row-item"><span>E-mail</span><span>{{ order.userEmail || '—' }}</span></div>
              <div class="info-row-item"><span>Telefone</span><span>{{ order.userPhone || '—' }}</span></div>
              <div class="info-row-item"><span>CPF / CNPJ</span><span>{{ order.userCpf || '—' }}</span></div>
            </div>
            <div class="section-divider"></div>
            <h4 class="section-subtitle">Endereço de Entrega</h4>
            <div class="info-list">
              <div class="info-row-item"><span>Rua</span><span>{{ order.addressStreet || '—' }}</span></div>
              <div class="info-row-item"><span>Cidade</span><span>{{ order.addressCity || '—' }}</span></div>
              <div class="info-row-item"><span>Estado</span><span>{{ order.addressState || '—' }}</span></div>
              <div class="info-row-item" style="border-bottom:none"><span>CEP</span><span>{{ order.addressZip || '—' }}</span></div>
            </div>
          </div>
        </div>
        <div class="card">
          <h3 class="card__title">Itens do Pedido</h3>
          <table class="table">
            <thead><tr><th>Produto</th><th>Qtd</th><th>Preço Unit.</th><th>Subtotal</th></tr></thead>
            <tbody>
              <tr *ngFor="let item of order.items">
                <td class="td--bold">{{ item.productName }}</td>
                <td>{{ item.quantity }}</td>
                <td>{{ item.price | currency:'BRL' }}</td>
                <td>{{ item.subtotal | currency:'BRL' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </ng-container>
    </div>

    <app-modal [open]="statusModal" title="Atualizar Status" [loading]="saving"
      (closeModal)="statusModal=false" (confirm)="updateStatus()">
      <div class="field">
        <label class="field__label">Novo Status</label>
        <select class="field__input" [(ngModel)]="newStatus">
          <option *ngFor="let s of statusOptions" [value]="s.value">{{ s.label }}</option>
        </select>
      </div>
    </app-modal>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 20px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .page__breadcrumb a { color: inherit; }
    .page__head { display: flex; align-items: flex-start; justify-content: space-between; }
    .page__title { font-size: 24px; font-weight: 800; }
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm); }
    .card__title { font-size: 16px; font-weight: 700; margin-bottom: 16px; }
    .detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .info-list { display: flex; flex-direction: column; gap: 10px; }
    .info-row-item { display: flex; justify-content: space-between; align-items: center; font-size: 13px; padding: 6px 0; border-bottom: 1px solid var(--color-border); }
    .info-row-item--total { font-weight: 700; font-size: 15px; border-bottom: none; }
    .section-divider { border: none; border-top: 1.5px solid var(--color-border); margin: 16px 0 12px; }
    .section-subtitle { font-size: 12px; font-weight: 700; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .6px; margin-bottom: 10px; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); }
    td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); }
    .td--bold { font-weight: 600; }
    .field { display: flex; flex-direction: column; gap: 5px; }
    .field__label { font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .field__input { padding: 10px 12px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 14px; outline: none; }

    /* Stepper */
    .stepper-card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 28px 32px; box-shadow: var(--shadow-sm); }
    .stepper-card--cancelled { display: flex; align-items: center; gap: 10px; font-size: 14px; font-weight: 600; color: #dc2626; background: #fef2f2; border: 1.5px solid #fecaca; border-radius: var(--radius-lg); padding: 16px 24px; }
    .stepper { display: flex; align-items: center; width: 100%; }
    .step { display: flex; flex-direction: column; align-items: center; gap: 8px; flex-shrink: 0; }
    .step__icon { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: var(--color-bg); border: 2px solid var(--color-border); color: var(--color-text-muted); transition: all .2s; }
    .step__icon svg { width: 18px; height: 18px; }
    .step--done .step__icon { background: var(--color-primary, #2563eb); border-color: var(--color-primary, #2563eb); color: #fff; }
    .step--active .step__icon { background: #fff; border-color: var(--color-primary, #2563eb); color: var(--color-primary, #2563eb); box-shadow: 0 0 0 4px rgba(37,99,235,.15); }
    .step__label { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .4px; white-space: nowrap; }
    .step--done .step__label, .step--active .step__label { color: var(--color-primary, #2563eb); }
    .step__line { flex: 1; height: 2px; background: var(--color-border); margin: 0 4px; margin-bottom: 26px; transition: background .2s; }
    .step__line--done { background: var(--color-primary, #2563eb); }
  `]
})
export class OrderDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private ordersHttp = inject(OrdersHttpService);
  private toast = inject(ToastService);
  private sanitizer = inject(DomSanitizer);
  order: any = null; loading = true; statusModal = false; saving = false; newStatus = '';
  statusOptions = Object.entries(STATUS_LABELS).map(([value, label]) => ({ value, label }));
  statusLabel = (s: string) => STATUS_LABELS[s] ?? s;
  steps: { key: string; label: string; icon: SafeHtml }[] = [];

  isStepDone(key: string): boolean {
    const current = STATUS_FLOW.indexOf(this.order?.status);
    const step = STATUS_FLOW.indexOf(key);
    return step < current;
  }
  isStepActive(key: string): boolean {
    return this.order?.status === key;
  }
  isCancelledOrRefunded(): boolean {
    return ['CANCELLED','REFUNDED','DEVOLUTION'].includes(this.order?.status);
  }

  ngOnInit(): void {
    this.steps = STATUS_FLOW.map(key => ({
      key,
      label: STATUS_LABELS[key],
      icon: this.sanitizer.bypassSecurityTrustHtml(
        `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">${STATUS_ICONS[key]}</svg>`
      )
    }));
    const id = this.route.snapshot.paramMap.get('id')!;
    this.ordersHttp.getById(id).subscribe({ next: o => { this.order = o?.data ?? o; this.newStatus = this.order?.status; this.loading = false; }, error: () => this.loading = false });
  }
  updateStatus(): void {
    this.saving = true;
    this.ordersHttp.updateStatus(this.order.id, this.newStatus).subscribe({ next: () => { this.order.status = this.newStatus; this.statusModal = false; this.saving = false; this.toast.success('Status atualizado!'); }, error: () => { this.saving = false; this.toast.error('Erro ao atualizar status.'); } });
  }
}
