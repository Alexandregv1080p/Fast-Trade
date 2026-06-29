import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, DatePipe, CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UsersHttpService } from '../../../core/services/users-http.service';
import { ToastService } from '../../../core/services/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';
import { ModalComponent } from '../../../shared/components/modal/modal.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule, DatePipe, RouterLink, FormsModule, ButtonComponent, PaginationComponent, SpinnerComponent, ModalComponent],
  template: `
    <div class="page" (click)="closeMenu()">
      <div class="page__head">
        <div>
          <span class="page__breadcrumb">USUÁRIOS</span>
          <h1 class="page__title">Detalhes Usuários</h1>
        </div>
      </div>

      <div class="card">
        <div class="card__toolbar">
          <div class="search-box">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            <input [(ngModel)]="search" (input)="onSearch()" placeholder="Buscar usuário..." />
          </div>
        </div>

        <app-spinner *ngIf="loading" />

        <div class="table-wrap" *ngIf="!loading">
          <table class="table">
            <thead><tr>
              <th>Data</th><th>Nome</th><th>E-mail</th><th>CPF/CNPJ</th><th>TRADEs</th><th>Score</th><th>Status</th><th></th>
            </tr></thead>
            <tbody>
              <tr *ngFor="let u of users">
                <td>{{ u.createdAt | date:'dd/MM/yy' }}</td>
                <td class="td--bold">{{ u.name }}</td>
                <td>{{ u.email }}</td>
                <td>{{ u.cpfCnpj || '—' }}</td>
                <td>{{ u.trades ?? 0 }}</td>
                <td>{{ u.score ?? 0 }}</td>
                <td><span class="badge" [class]="u.blocked ? 'badge--error' : 'badge--success'">{{ u.blocked ? 'Bloqueado' : 'Ativo' }}</span></td>
                <td class="td--actions">
                  <div class="action-menu">
                    <button class="btn-icon btn-more" (click)="toggleMenu(u.id, $event)" title="Ações">
                      <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor" stroke="none">
                        <circle cx="12" cy="5" r="1.8"/><circle cx="12" cy="12" r="1.8"/><circle cx="12" cy="19" r="1.8"/>
                      </svg>
                    </button>
                    <div class="action-dropdown" *ngIf="menuOpenId === u.id" (click)="$event.stopPropagation()">
                      <a [routerLink]="['/admin/users', u.id]" class="action-item" (click)="closeMenu()">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                        Detalhes
                      </a>
                      <button class="action-item" (click)="openTradesModal(u); closeMenu()">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                        Dar TRADEs
                      </button>
                      <button class="action-item" (click)="toggleBlock(u); closeMenu()">
                        <svg *ngIf="!u.blocked" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
                        <svg *ngIf="u.blocked" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 019.9-1"/></svg>
                        {{ u.blocked ? 'Desbloquear' : 'Bloquear' }}
                      </button>
                    </div>
                  </div>
                </td>
              </tr>
              <tr *ngIf="!users.length"><td colspan="8" class="table__empty">Nenhum usuário encontrado.</td></tr>
            </tbody>
          </table>
        </div>

        <app-pagination [currentPage]="page" [totalPages]="totalPages" (pageChange)="onPageChange($event)" />
      </div>
    </div>

    <app-modal [open]="tradesModal" title="Dar TRADEs" confirmLabel="Salvar" [loading]="savingTrades"
      (closeModal)="tradesModal=false" (confirm)="saveTrades()">
      <div class="field">
        <label class="field__label">Valor</label>
        <input class="field__input" type="number" [(ngModel)]="tradesValue" placeholder="Ex: 50" />
      </div>
    </app-modal>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 24px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px; }
    .page__head { display: flex; align-items: flex-start; justify-content: space-between; }
    .page__title { font-size: 24px; font-weight: 800; margin-top: 4px; }
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); }
    .card__toolbar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
    .search-box { display: flex; align-items: center; gap: 8px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); padding: 8px 12px; flex: 1; max-width: 320px; }
    .search-box input { border: none; outline: none; font-size: 13px; width: 100%; background: transparent; }
    .table-wrap { overflow-x: auto; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); white-space: nowrap; }
    td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .td--bold { font-weight: 600; }
    .td--actions { width: 48px; text-align: right; }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
    .btn-icon { background: none; border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 5px 8px; cursor: pointer; }
    .btn-icon:hover { background: var(--color-bg); }
    .btn-more { display: flex; align-items: center; justify-content: center; color: var(--color-text-muted); }
    .action-menu { position: relative; display: inline-block; }
    .action-dropdown { position: absolute; right: 0; top: calc(100% + 4px); background: var(--color-surface); border: 1.5px solid var(--color-border); border-radius: var(--radius-md); box-shadow: 0 4px 16px rgba(0,0,0,.10); min-width: 148px; z-index: 200; overflow: hidden; }
    .action-item { display: flex; align-items: center; gap: 9px; width: 100%; padding: 9px 14px; font-size: 13px; font-weight: 500; color: var(--color-text); background: none; border: none; cursor: pointer; text-decoration: none; transition: background .12s; white-space: nowrap; }
    .action-item:hover { background: var(--color-bg); }
    .field { display: flex; flex-direction: column; gap: 5px; }
    .field__label { font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .field__input { padding: 10px 12px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 14px; outline: none; background: transparent; }
    .field__input:focus { border-color: var(--color-accent); box-shadow: 0 0 0 3px rgba(37,99,235,.12); }
  `]
})
export class UsersComponent implements OnInit {
  private usersHttp = inject(UsersHttpService);
  private toast = inject(ToastService);

  users: any[] = []; loading = true; page = 1; totalPages = 1; search = '';
  tradesModal = false; selectedUser: any = null; tradesValue = 0; savingTrades = false;
  menuOpenId: any = null;

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.usersHttp.getAll({ page: this.page, pageSize: 10, search: this.search || undefined }).subscribe({
      next: r => { this.users = Array.isArray(r) ? r : r?.data ?? []; this.totalPages = r?.totalPages ?? 1; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onSearch(): void { this.page = 1; this.load(); }
  onPageChange(p: number): void { this.page = p; this.load(); }
  toggleMenu(id: any, event: Event): void { event.stopPropagation(); this.menuOpenId = this.menuOpenId === id ? null : id; }
  closeMenu(): void { this.menuOpenId = null; }

  toggleBlock(u: any): void {
    const action = u.blocked ? this.usersHttp.unblock(u.id) : this.usersHttp.block(u.id);
    action.subscribe({ next: () => { u.blocked = !u.blocked; this.toast.success(u.blocked ? 'Usuário bloqueado.' : 'Usuário desbloqueado.'); }, error: () => this.toast.error('Erro ao atualizar usuário.') });
  }

  openTradesModal(u: any): void { this.selectedUser = u; this.tradesValue = 0; this.tradesModal = true; }

  saveTrades(): void {
    if (!this.selectedUser || !this.tradesValue) return;
    this.savingTrades = true;
    this.usersHttp.giveTrades(this.selectedUser.id, this.tradesValue).subscribe({
      next: () => { this.tradesModal = false; this.savingTrades = false; this.toast.success('TRADEs atribuídos com sucesso!'); this.load(); },
      error: () => { this.savingTrades = false; this.toast.error('Erro ao atribuir TRADEs.'); }
    });
  }
}
