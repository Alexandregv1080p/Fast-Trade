import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CategoriesHttpService } from '../../../core/services/categories-http.service';
import { ToastService } from '../../../core/services/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';
import { ModalComponent } from '../../../shared/components/modal/modal.component';

@Component({
  selector: 'app-subcategories',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule, FormsModule, ReactiveFormsModule, ButtonComponent, SpinnerComponent, ModalComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <div><span class="page__breadcrumb">SUBCATEGORIAS</span><h1 class="page__title">Subcategorias</h1></div>
        <app-button variant="primary" (click)="openCreate()">+ Nova Subcategoria</app-button>
      </div>
      <div class="card">
        <app-spinner *ngIf="loading" />
        <div class="table-wrap" *ngIf="!loading">
          <table class="table">
            <thead><tr><th>Nome</th><th>Categoria Pai</th><th></th></tr></thead>
            <tbody>
              <tr *ngFor="let s of subcategories" (click)="closeMenu()">
                <td class="td--bold">{{ s.name }}</td>
                <td>{{ getCatName(s.category?.id ?? s.categoryId) }}</td>
                <td class="td--actions">
                  <div class="action-menu">
                    <button class="btn-icon btn-more" (click)="toggleMenu(s.id, $event)">
                      <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor" stroke="none">
                        <circle cx="12" cy="5" r="1.8"/><circle cx="12" cy="12" r="1.8"/><circle cx="12" cy="19" r="1.8"/>
                      </svg>
                    </button>
                    <div class="action-dropdown" *ngIf="menuOpenId === s.id" (click)="$event.stopPropagation()">
                      <button class="action-item" (click)="openEdit(s); closeMenu()">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                        Editar
                      </button>
                      <button class="action-item action-item--danger" (click)="confirmDelete(s); closeMenu()">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6m3 0V4a1 1 0 011-1h4a1 1 0 011 1v2"/></svg>
                        Excluir
                      </button>
                    </div>
                  </div>
                </td>
              </tr>
              <tr *ngIf="!subcategories.length"><td colspan="3" class="table__empty">Nenhuma subcategoria.</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <app-modal [open]="formModal" [title]="editMode ? 'Editar' : 'Nova Subcategoria'" [loading]="saving"
      (closeModal)="formModal=false" (confirm)="save()">
      <form [formGroup]="form" class="form-col">
        <div class="field"><label class="field__label">Nome</label><input class="field__input" formControlName="name" /></div>
        <div class="field"><label class="field__label">Categoria</label>
          <select class="field__input" formControlName="categoryId">
            <option value="">Selecione...</option>
            <option *ngFor="let c of categories" [value]="c.id + ''">{{ c.name }}</option>
          </select>
        </div>
      </form>
    </app-modal>
    <app-modal [open]="deleteModal" title="Excluir" confirmLabel="Excluir" confirmVariant="danger"
      (closeModal)="deleteModal=false" (confirm)="deleteSub()">
      <p>Deseja excluir <strong>{{ selected?.name }}</strong>?</p>
    </app-modal>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 24px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px; }
    .page__head { display: flex; align-items: flex-start; justify-content: space-between; }
    .page__title { font-size: 24px; font-weight: 800; margin-top: 4px; }
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); }
    .table-wrap { overflow-x: auto; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); }
    td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .td--bold { font-weight: 600; }
    .td--actions { width: 48px; text-align: right; }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
    .btn-icon { background: none; border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 5px 8px; cursor: pointer; }
    .btn-icon:hover { background: var(--color-bg); }
    .btn-more { display: flex; align-items: center; justify-content: center; color: var(--color-text-muted); }
    .action-menu { position: relative; display: inline-block; }
    .action-dropdown { position: absolute; right: 0; top: calc(100% + 4px); background: var(--color-surface); border: 1.5px solid var(--color-border); border-radius: var(--radius-md); box-shadow: 0 4px 16px rgba(0,0,0,.10); min-width: 140px; z-index: 200; overflow: hidden; }
    .action-item { display: flex; align-items: center; gap: 9px; width: 100%; padding: 9px 14px; font-size: 13px; font-weight: 500; color: var(--color-text); background: none; border: none; cursor: pointer; transition: background .12s; white-space: nowrap; }
    .action-item:hover { background: var(--color-bg); }
    .action-item--danger { color: var(--color-error, #dc3545); }
    .action-item--danger:hover { background: #fef2f2; }
    .form-col { display: flex; flex-direction: column; gap: 16px; }
    .field { display: flex; flex-direction: column; gap: 5px; }
    .field__label { font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .field__input { padding: 10px 12px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 14px; outline: none; }
    .field__input:focus { border-color: var(--color-accent); box-shadow: 0 0 0 3px rgba(37,99,235,.12); }
  `]
})
export class SubcategoriesComponent implements OnInit {
  private catHttp = inject(CategoriesHttpService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  subcategories: any[] = []; categories: any[] = []; loading = true;
  formModal = false; deleteModal = false; selected: any = null; editMode = false; saving = false; menuOpenId: any = null;
  form = this.fb.group({ name: ['', Validators.required], categoryId: ['', Validators.required] });

  ngOnInit(): void {
    this.loading = true;
    this.catHttp.getAll().subscribe(c => { this.categories = c; });
    this.catHttp.getSubcategories().subscribe({ next: s => { this.subcategories = s; this.loading = false; }, error: () => this.loading = false });
  }
  toggleMenu(id: any, e: Event): void { e.stopPropagation(); this.menuOpenId = this.menuOpenId === id ? null : id; }
  closeMenu(): void { this.menuOpenId = null; }
  getCatName(id: any): string { return this.categories.find(c => String(c.id) === String(id))?.name ?? '—'; }
  openCreate(): void { this.editMode = false; this.form.reset(); this.formModal = true; }
  openEdit(s: any): void {
    this.editMode = true;
    this.selected = s;
    this.form.patchValue({ name: s.name, categoryId: String(s.category?.id ?? s.categoryId ?? '') });
    this.formModal = true;
  }
  save(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const action = this.editMode ? this.catHttp.updateSubcategory(this.selected.id, this.form.value) : this.catHttp.createSubcategory(this.form.value);
    action.subscribe({ next: () => { this.formModal = false; this.saving = false; this.toast.success('Salva!'); this.ngOnInit(); }, error: () => { this.saving = false; this.toast.error('Erro.'); } });
  }
  confirmDelete(s: any): void { this.selected = s; this.deleteModal = true; }
  deleteSub(): void { this.catHttp.deleteSubcategory(this.selected.id).subscribe({ next: () => { this.deleteModal = false; this.toast.success('Excluída!'); this.ngOnInit(); }, error: () => this.toast.error('Erro.') }); }
}
