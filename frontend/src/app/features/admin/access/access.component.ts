import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault, SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminHttpService } from '../../../core/services/admin-http.service';
import { ModalComponent } from '../../../shared/components/modal/modal.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';

type Action = 'view' | 'create' | 'edit' | 'delete';
type RoleKey = 'ADMIN' | 'MANAGER' | 'SUPPORT';

interface ModulePerms { view: boolean; create: boolean; edit: boolean; delete: boolean; }
interface RolePerms { [module: string]: ModulePerms; }

const MODULES = [
  { key: 'dashboard',     label: 'Dashboard'    },
  { key: 'users',         label: 'Usuários'     },
  { key: 'products',      label: 'Produtos'     },
  { key: 'orders',        label: 'Pedidos'      },
  { key: 'categories',    label: 'Categorias'   },
  { key: 'financial',     label: 'Financeiro'   },
  { key: 'access',        label: 'Acessos'      },
];

const full = (): ModulePerms => ({ view: true, create: true, edit: true, delete: true });
const none = (): ModulePerms => ({ view: false, create: false, edit: false, delete: false });
const view = (): ModulePerms => ({ view: true, create: false, edit: false, delete: false });

const DEFAULT_PERMS: Record<RoleKey, RolePerms> = {
  ADMIN: Object.fromEntries(MODULES.map(m => [m.key, full()])),
  MANAGER: {
    dashboard:  view(),
    users:      none(),
    products:   full(),
    orders:     { view: true, create: true, edit: true, delete: false },
    categories: full(),
    financial:  view(),
    access:     none(),
  },
  SUPPORT: {
    dashboard:  view(),
    users:      none(),
    products:   view(),
    orders:     { view: true, create: false, edit: true, delete: false },
    categories: view(),
    financial:  none(),
    access:     none(),
  },
};

@Component({
  selector: 'app-access',
  standalone: true,
  imports: [NgFor, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault, SlicePipe, FormsModule, ModalComponent, ButtonComponent, SpinnerComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <div>
          <span class="page__breadcrumb">CONFIGURAÇÕES</span>
          <h1 class="page__title">Controle de Acessos</h1>
        </div>
        <app-button variant="accent" (click)="openCreate()">+ Novo Colaborador</app-button>
      </div>

      <!-- Tabs -->
      <div class="tabs">
        <button class="tab" [class.tab--active]="activeTab === 'team'" (click)="activeTab = 'team'">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
          Equipe <span class="tab__badge">{{ collaborators.length }}</span>
        </button>
        <button class="tab" [class.tab--active]="activeTab === 'perms'" (click)="activeTab = 'perms'">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><circle cx="12" cy="16" r="1"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
          Permissões por Perfil
        </button>
      </div>

      <!-- EQUIPE TAB -->
      <div *ngIf="activeTab === 'team'" class="card">
        <app-spinner *ngIf="loading" />
        <table class="table" *ngIf="!loading">
          <thead>
            <tr>
              <th>Colaborador</th>
              <th>E-mail</th>
              <th>Perfil</th>
              <th>Criado em</th>
              <th style="width:80px;text-align:center;">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let c of collaborators">
              <td class="td--name">
                <div class="avatar-cell">
                  <div class="avatar">{{ c.name?.charAt(0) || 'U' }}</div>
                  {{ c.name }}
                </div>
              </td>
              <td class="td--email">{{ c.email }}</td>
              <td><span class="role-badge" [class]="'role-badge--' + (c.role || 'ADMIN').toLowerCase()">{{ roleLabel(c.role) }}</span></td>
              <td class="td--date">{{ c.createdAt | slice:0:10 }}</td>
              <td class="td--actions">
                <button class="btn-icon" (click)="openEdit(c)" title="Editar">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                </button>
                <button class="btn-icon btn-icon--danger" (click)="openDelete(c)" title="Remover">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 011-1h4a1 1 0 011 1v2"/></svg>
                </button>
              </td>
            </tr>
            <tr *ngIf="!collaborators.length">
              <td colspan="5" class="table__empty">Nenhum colaborador encontrado.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- PERMISSÕES TAB -->
      <div *ngIf="activeTab === 'perms'" class="perms-section">

        <div class="role-cards">
          <div *ngFor="let role of roles" class="role-card" [class.role-card--active]="selectedRole === role.key" (click)="selectedRole = role.key">
            <div class="role-card__icon" [class]="'role-card__icon--' + role.key.toLowerCase()">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <div class="role-card__info">
              <span class="role-card__name">{{ role.label }}</span>
              <span class="role-card__desc">{{ role.desc }}</span>
            </div>
            <div class="role-card__check" *ngIf="selectedRole === role.key">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
          </div>
        </div>

        <div class="perm-matrix">
          <div class="perm-matrix__header">
            <div class="perm-matrix__role-label">
              <span class="role-badge" [class]="'role-badge--' + selectedRole.toLowerCase()">{{ roleLabel(selectedRole) }}</span>
              <span class="perm-matrix__lock-note" *ngIf="selectedRole === 'ADMIN'">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
                Acesso total — não editável
              </span>
              <span class="perm-matrix__lock-note" *ngIf="selectedRole !== 'ADMIN'">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
                Clique nos itens para editar
              </span>
            </div>
            <div class="perm-matrix__actions-head">
              <span>Visualizar</span>
              <span>Criar</span>
              <span>Editar</span>
              <span>Excluir</span>
            </div>
          </div>

          <div *ngFor="let mod of modules" class="perm-row">
            <div class="perm-row__module">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <ng-container [ngSwitch]="mod.key">
                  <ng-container *ngSwitchCase="'dashboard'"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></ng-container>
                  <ng-container *ngSwitchCase="'users'"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></ng-container>
                  <ng-container *ngSwitchCase="'products'"><path d="M21 7.5l-9-5.25L3 7.5m18 0l-9 5.25m9-5.25v9l-9 5.25M3 7.5l9 5.25M3 7.5v9l9 5.25m0-9v9"/></ng-container>
                  <ng-container *ngSwitchCase="'orders'"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></ng-container>
                  <ng-container *ngSwitchCase="'categories'"><path d="M4 6h16M4 12h8M4 18h4"/></ng-container>
                  <ng-container *ngSwitchCase="'financial'"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></ng-container>
                  <ng-container *ngSwitchDefault><rect x="3" y="11" width="18" height="11" rx="2"/><circle cx="12" cy="16" r="1"/><path d="M7 11V7a5 5 0 0110 0v4"/></ng-container>
                </ng-container>
              </svg>
              {{ mod.label }}
            </div>
            <div class="perm-row__checks">
              <button *ngFor="let action of actions"
                class="perm-check"
                [class.perm-check--on]="getPermValue(selectedRole, mod.key, action)"
                [class.perm-check--locked]="selectedRole === 'ADMIN'"
                [disabled]="selectedRole === 'ADMIN'"
                (click)="togglePerm(selectedRole, mod.key, action)"
                [title]="actionLabel(action)">
                <svg *ngIf="getPermValue(selectedRole, mod.key, action)" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
              </button>
            </div>
          </div>
        </div>

        <div class="perm-legend">
          <div *ngFor="let a of actions" class="legend-item">
            <div class="perm-check perm-check--on perm-check--sm">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
            {{ actionLabel(a) }}
          </div>
        </div>
      </div>
    </div>

    <!-- CREATE / EDIT MODAL -->
    <app-modal *ngIf="showForm" [open]="showForm" [title]="editing ? 'Editar Colaborador' : 'Novo Colaborador'"
      (closeModal)="closeForm()" (confirm)="saveCollaborator()" [confirmLabel]="saving ? 'Salvando…' : 'Salvar'" [confirmDisabled]="saving">
      <div class="form">
        <div class="form__field">
          <label class="form__label">Nome</label>
          <input class="form__input" [(ngModel)]="form.name" placeholder="Nome completo" />
        </div>
        <div class="form__field">
          <label class="form__label">E-mail</label>
          <input class="form__input" type="email" [(ngModel)]="form.email" placeholder="email@empresa.com" [disabled]="editing" />
        </div>
        <div class="form__field">
          <label class="form__label">Senha</label>
          <input class="form__input" type="password" [(ngModel)]="form.password" [placeholder]="editing ? 'Deixe em branco para manter' : 'Senha'" />
        </div>
        <div class="form__field">
          <label class="form__label">Perfil de Acesso</label>
          <div class="role-picker">
            <button *ngFor="let r of roles" type="button"
              class="role-option"
              [class.role-option--active]="form.role === r.key"
              (click)="form.role = r.key">
              <div class="role-option__icon" [class]="'role-card__icon--' + r.key.toLowerCase()">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
              <div>
                <div class="role-option__name">{{ r.label }}</div>
                <div class="role-option__desc">{{ r.desc }}</div>
              </div>
            </button>
          </div>
        </div>
        <div class="form__preview" *ngIf="form.role">
          <div class="form__preview-title">Permissões do perfil selecionado</div>
          <div class="mini-perm-grid">
            <div *ngFor="let mod of modules" class="mini-perm-row">
              <span>{{ mod.label }}</span>
              <div class="mini-perm-dots">
                <span *ngFor="let a of actions" class="mini-dot"
                  [class.mini-dot--on]="getPermValue(form.role, mod.key, a)"
                  [title]="actionLabel(a)"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </app-modal>

    <!-- DELETE MODAL -->
    <app-modal *ngIf="showDelete" [open]="showDelete" title="Remover Colaborador"
      (closeModal)="showDelete=false" (confirm)="deleteCollaborator()" confirmLabel="Remover" confirmVariant="danger">
      <p style="font-size:14px;line-height:1.6;">Tem certeza que deseja remover <strong>{{ selected?.name }}</strong>?<br>Esta ação não pode ser desfeita.</p>
    </app-modal>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 20px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px; }
    .page__head { display: flex; align-items: center; justify-content: space-between; }
    .page__title { font-size: 24px; font-weight: 800; margin-top: 4px; }

    /* Tabs */
    .tabs { display: flex; gap: 4px; border-bottom: 2px solid var(--color-border); padding-bottom: 0; }
    .tab { display: flex; align-items: center; gap: 7px; padding: 10px 16px; font-size: 13px; font-weight: 600; color: var(--color-text-muted); border: none; background: none; cursor: pointer; border-bottom: 2px solid transparent; margin-bottom: -2px; border-radius: var(--radius-md) var(--radius-md) 0 0; transition: all .15s; }
    .tab:hover { color: var(--color-text); background: var(--color-bg); }
    .tab--active { color: var(--color-accent); border-bottom-color: var(--color-accent); background: none; }
    .tab__badge { background: var(--color-bg); border: 1px solid var(--color-border); border-radius: 20px; padding: 1px 7px; font-size: 11px; font-weight: 700; }
    .tab--active .tab__badge { background: #eff6ff; border-color: #bfdbfe; color: var(--color-accent); }

    /* Table */
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); overflow-x: auto; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .5px; border-bottom: 2px solid var(--color-border); text-align: left; }
    td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .avatar-cell { display: flex; align-items: center; gap: 10px; }
    .avatar { width: 32px; height: 32px; border-radius: 50%; background: var(--color-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; flex-shrink: 0; }
    .td--name { font-weight: 600; }
    .td--email { color: var(--color-text-muted); font-size: 12px; }
    .td--date { color: var(--color-text-muted); font-size: 12px; }
    .td--actions { display: flex; gap: 4px; align-items: center; justify-content: center; }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
    .btn-icon { border: 1px solid var(--color-border); background: var(--color-surface); cursor: pointer; padding: 6px; border-radius: 6px; display: flex; align-items: center; color: var(--color-text-muted); transition: all .15s; }
    .btn-icon:hover { background: var(--color-bg); color: var(--color-text); border-color: var(--color-text-muted); }
    .btn-icon--danger:hover { background: #fef2f2; color: var(--color-error); border-color: #fca5a5; }

    /* Role badge */
    .role-badge { display: inline-flex; align-items: center; padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: .5px; }
    .role-badge--admin   { background: #fef2f2; color: #dc2626; border: 1px solid #fca5a5; }
    .role-badge--manager { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; }
    .role-badge--support { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }

    /* Role cards */
    .perms-section { display: flex; flex-direction: column; gap: 16px; }
    .role-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
    .role-card { display: flex; align-items: center; gap: 14px; background: var(--color-surface); border: 2px solid var(--color-border); border-radius: var(--radius-lg); padding: 16px 18px; cursor: pointer; transition: all .15s; position: relative; }
    .role-card:hover { border-color: var(--color-accent); background: #fafbff; }
    .role-card--active { border-color: var(--color-accent); background: #eff6ff; }
    .role-card__icon { width: 40px; height: 40px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .role-card__icon--admin   { background: #fef2f2; color: #dc2626; }
    .role-card__icon--manager { background: #eff6ff; color: #2563eb; }
    .role-card__icon--support { background: #f0fdf4; color: #16a34a; }
    .role-card__info { display: flex; flex-direction: column; gap: 2px; flex: 1; }
    .role-card__name { font-size: 14px; font-weight: 700; color: var(--color-text); }
    .role-card__desc { font-size: 12px; color: var(--color-text-muted); }
    .role-card__check { color: var(--color-accent); }

    /* Permission matrix */
    .perm-matrix { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); }
    .perm-matrix__header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 2px solid var(--color-border); }
    .perm-matrix__role-label { display: flex; align-items: center; gap: 10px; }
    .perm-matrix__lock-note { display: flex; align-items: center; gap: 5px; font-size: 11px; color: var(--color-text-muted); }
    .perm-matrix__actions-head { display: flex; gap: 8px; }
    .perm-matrix__actions-head span { width: 40px; text-align: center; font-size: 10px; font-weight: 700; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .4px; }

    .perm-row { display: flex; align-items: center; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid var(--color-border); }
    .perm-row:last-child { border-bottom: none; }
    .perm-row__module { display: flex; align-items: center; gap: 9px; font-size: 13px; font-weight: 600; color: var(--color-text); flex: 1; }
    .perm-row__checks { display: flex; gap: 8px; }

    .perm-check { width: 40px; height: 28px; border-radius: 6px; border: 1.5px solid var(--color-border); background: var(--color-bg); cursor: pointer; display: flex; align-items: center; justify-content: center; color: transparent; transition: all .15s; }
    .perm-check--on { background: #eff6ff; border-color: #bfdbfe; color: #2563eb; }
    .perm-check--locked { cursor: not-allowed; }
    .perm-check--locked.perm-check--on { background: #fef2f2; border-color: #fca5a5; color: #dc2626; }
    .perm-check--sm { width: 22px; height: 22px; border-radius: 5px; }

    .perm-legend { display: flex; align-items: center; gap: 16px; justify-content: flex-end; }
    .legend-item { display: flex; align-items: center; gap: 6px; font-size: 11px; color: var(--color-text-muted); }

    /* Modal form */
    .form { display: flex; flex-direction: column; gap: 16px; }
    .form__field { display: flex; flex-direction: column; gap: 6px; }
    .form__label { font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .5px; }
    .form__input { border: 1.5px solid var(--color-border); border-radius: var(--radius-md); padding: 10px 12px; font-size: 14px; outline: none; color: var(--color-text); background: var(--color-surface); }
    .form__input:focus { border-color: var(--color-accent); box-shadow: 0 0 0 3px rgba(37,99,235,.12); }
    .form__input:disabled { background: var(--color-bg); cursor: not-allowed; }

    .role-picker { display: flex; flex-direction: column; gap: 8px; }
    .role-option { display: flex; align-items: center; gap: 12px; padding: 12px 14px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface); cursor: pointer; text-align: left; transition: all .15s; }
    .role-option:hover { border-color: var(--color-accent); background: #fafbff; }
    .role-option--active { border-color: var(--color-accent); background: #eff6ff; }
    .role-option__icon { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .role-option__name { font-size: 13px; font-weight: 700; color: var(--color-text); }
    .role-option__desc { font-size: 11px; color: var(--color-text-muted); margin-top: 1px; }

    .form__preview { background: var(--color-bg); border-radius: var(--radius-md); padding: 12px 14px; border: 1px solid var(--color-border); }
    .form__preview-title { font-size: 11px; font-weight: 700; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .5px; margin-bottom: 10px; }
    .mini-perm-grid { display: flex; flex-direction: column; gap: 6px; }
    .mini-perm-row { display: flex; align-items: center; justify-content: space-between; font-size: 12px; color: var(--color-text); }
    .mini-perm-dots { display: flex; gap: 4px; }
    .mini-dot { width: 10px; height: 10px; border-radius: 50%; background: var(--color-border); }
    .mini-dot--on { background: var(--color-accent); }
  `]
})
export class AccessComponent implements OnInit {
  private adminHttp = inject(AdminHttpService);

  collaborators: any[] = [];
  loading = true;
  showForm = false;
  showDelete = false;
  editing = false;
  saving = false;
  selected: any = null;
  activeTab: 'team' | 'perms' = 'team';
  selectedRole: RoleKey = 'ADMIN';

  form = { name: '', email: '', password: '', role: 'ADMIN' };

  modules = MODULES;
  actions: Action[] = ['view', 'create', 'edit', 'delete'];
  roles = [
    { key: 'ADMIN'   as RoleKey, label: 'Admin',   desc: 'Acesso total ao sistema' },
    { key: 'MANAGER' as RoleKey, label: 'Gerente',  desc: 'Gestão de produtos, pedidos e categorias' },
    { key: 'SUPPORT' as RoleKey, label: 'Suporte',  desc: 'Visualização e atualização de pedidos' },
  ];

  // Local copy of permissions (editable for non-Admin roles)
  perms: Record<RoleKey, RolePerms> = {
    ADMIN:   JSON.parse(JSON.stringify(DEFAULT_PERMS.ADMIN)),
    MANAGER: JSON.parse(JSON.stringify(DEFAULT_PERMS.MANAGER)),
    SUPPORT: JSON.parse(JSON.stringify(DEFAULT_PERMS.SUPPORT)),
  };

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.adminHttp.getCollaborators().subscribe({
      next: r => { this.collaborators = r; this.loading = false; },
      error: () => this.loading = false,
    });
  }

  getPermValue(role: string, module: string, action: Action): boolean {
    const r = role as RoleKey;
    return this.perms[r]?.[module]?.[action] ?? false;
  }

  togglePerm(role: string, module: string, action: Action): void {
    if (role === 'ADMIN') return;
    const r = role as RoleKey;
    this.perms[r][module][action] = !this.perms[r][module][action];
    // If enabling create/edit/delete, ensure view is also enabled
    if (action !== 'view' && this.perms[r][module][action]) {
      this.perms[r][module]['view'] = true;
    }
  }

  roleLabel(role: string): string {
    return role === 'ADMIN' ? 'Admin' : role === 'MANAGER' ? 'Gerente' : role === 'SUPPORT' ? 'Suporte' : role;
  }

  actionLabel(action: Action): string {
    const map: Record<Action, string> = { view: 'Visualizar', create: 'Criar', edit: 'Editar', delete: 'Excluir' };
    return map[action];
  }

  openCreate(): void {
    this.editing = false;
    this.form = { name: '', email: '', password: '', role: 'ADMIN' };
    this.showForm = true;
  }

  openEdit(c: any): void {
    this.editing = true;
    this.selected = c;
    this.form = { name: c.name, email: c.email, password: '', role: c.role || 'ADMIN' };
    this.showForm = true;
  }

  openDelete(c: any): void {
    this.selected = c;
    this.showDelete = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.selected = null;
  }

  saveCollaborator(): void {
    this.saving = true;
    const call = this.editing
      ? this.adminHttp.updateCollaborator(this.selected.id, this.form)
      : this.adminHttp.createCollaborator(this.form);
    call.subscribe({
      next: () => { this.saving = false; this.closeForm(); this.load(); },
      error: () => { this.saving = false; },
    });
  }

  deleteCollaborator(): void {
    this.adminHttp.deleteCollaborator(this.selected.id).subscribe({
      next: () => { this.showDelete = false; this.load(); },
      error: () => {},
    });
  }
}
