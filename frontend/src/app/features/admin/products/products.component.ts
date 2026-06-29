import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, CurrencyPipe, CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductsHttpService } from '../../../core/services/products-http.service';
import { CategoriesHttpService } from '../../../core/services/categories-http.service';
import { ToastService } from '../../../core/services/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';
import { ModalComponent } from '../../../shared/components/modal/modal.component';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule, RouterLink, FormsModule, ReactiveFormsModule, CurrencyPipe, ButtonComponent, PaginationComponent, SpinnerComponent, ModalComponent],
  template: `
    <div class="page" (click)="closeMenu()">
      <div class="page__head">
        <div>
          <span class="page__breadcrumb">PRODUTOS</span>
          <h1 class="page__title">Produtos</h1>
        </div>
        <app-button variant="primary" (click)="openCreate(); $event.stopPropagation()">+ Novo Produto</app-button>
      </div>
      <div class="card">
        <div class="card__toolbar">
          <div class="search-box">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            <input [(ngModel)]="search" (input)="onSearch()" placeholder="Buscar produto..." />
          </div>
        </div>
        <app-spinner *ngIf="loading" />
        <div class="table-wrap" *ngIf="!loading">
          <table class="table">
            <thead><tr>
              <th style="width:48px"></th>
              <th>Nome</th><th>SKU</th><th>Preço</th><th>Estoque</th><th>Condição</th><th>Categoria</th><th>Status</th><th></th>
            </tr></thead>
            <tbody>
              <tr *ngFor="let p of products">
                <td>
                  <div class="thumb" [style.backgroundImage]="p.imageUrl ? 'url(' + p.imageUrl + ')' : 'none'">
                    <svg *ngIf="!p.imageUrl" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
                  </div>
                </td>
                <td class="td--bold">{{ p.name }}</td>
                <td class="td--mono">{{ p.sku || '—' }}</td>
                <td>{{ p.price | currency:'BRL' }}</td>
                <td>{{ p.stock }}</td>
                <td>
                  <span *ngIf="p.condition" class="badge-cond badge-cond--{{p.condition}}">{{ conditionLabel(p.condition) }}</span>
                  <span *ngIf="!p.condition">—</span>
                </td>
                <td>{{ p.category?.name || p.categoryName || '—' }}</td>
                <td><span class="badge" [class]="p.isActive ? 'badge--success' : 'badge--gray'">{{ p.isActive ? 'Ativo' : 'Inativo' }}</span></td>
                <td class="td--actions">
                  <div class="action-menu">
                    <button class="btn-icon btn-more" (click)="toggleMenu(p.id, $event)" title="Ações">
                      <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor" stroke="none">
                        <circle cx="12" cy="5" r="1.8"/><circle cx="12" cy="12" r="1.8"/><circle cx="12" cy="19" r="1.8"/>
                      </svg>
                    </button>
                    <div class="action-dropdown" *ngIf="menuOpenId === p.id" (click)="$event.stopPropagation()">
                      <a [routerLink]="['/admin/products', p.id]" class="action-item" (click)="closeMenu()">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                        Detalhes
                      </a>
                      <button class="action-item" (click)="toggleActive(p); closeMenu()">
                        <svg *ngIf="p.isActive" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="4" width="4" height="16"/><rect x="14" y="4" width="4" height="16"/></svg>
                        <svg *ngIf="!p.isActive" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polygon points="5 3 19 12 5 21 5 3"/></svg>
                        {{ p.isActive ? 'Desativar' : 'Ativar' }}
                      </button>
                      <button class="action-item action-item--danger" (click)="confirmDelete(p); closeMenu()">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6m3 0V4a1 1 0 011-1h4a1 1 0 011 1v2"/></svg>
                        Excluir
                      </button>
                    </div>
                  </div>
                </td>
              </tr>
              <tr *ngIf="!products.length"><td colspan="9" class="table__empty">Nenhum produto encontrado.</td></tr>
            </tbody>
          </table>
        </div>
        <app-pagination [currentPage]="page" [totalPages]="totalPages" (pageChange)="onPageChange($event)" />
      </div>
    </div>

    <!-- CREATE MODAL -->
    <app-modal [open]="createModal" [title]="'Novo Produto'" confirmLabel="Criar" [loading]="saving"
      (closeModal)="createModal=false" (confirm)="saveProduct()">
      <form [formGroup]="form" class="prod-form">

        <!-- Image upload -->
        <div class="img-upload" (click)="imgInput.click()">
          <img *ngIf="imagePreview" [src]="imagePreview" class="img-upload__preview" />
          <div *ngIf="!imagePreview" class="img-upload__placeholder">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
            <span>Clique para adicionar imagem</span>
          </div>
          <input #imgInput type="file" accept="image/*" style="display:none" (change)="onImageChange($event)" />
        </div>

        <!-- Fields grid -->
        <div class="form-grid">
          <div class="field field--full">
            <label class="field__label">Nome *</label>
            <input class="field__input" formControlName="name" placeholder="Ex: Camisa Polo Masculina" />
          </div>

          <div class="field">
            <label class="field__label">SKU / Código</label>
            <input class="field__input" formControlName="sku" placeholder="Ex: CAM-001" />
          </div>

          <div class="field">
            <label class="field__label">Condição</label>
            <select class="field__input" formControlName="condition">
              <option value="">Selecione...</option>
              <option value="novo">Novo</option>
              <option value="usado">Usado</option>
              <option value="recondicionado">Recondicionado</option>
            </select>
          </div>

          <div class="field">
            <label class="field__label">Preço *</label>
            <input class="field__input" type="number" formControlName="price" placeholder="0.00" />
          </div>

          <div class="field">
            <label class="field__label">Estoque</label>
            <input class="field__input" type="number" formControlName="stock" placeholder="0" />
          </div>

          <div class="field">
            <label class="field__label">Categoria</label>
            <select class="field__input" formControlName="categoryId" (change)="onCategoryChange()">
              <option value="">Selecione...</option>
              <option *ngFor="let c of categories" [value]="c.id">{{ c.name }}</option>
            </select>
          </div>

          <div class="field">
            <label class="field__label">Subcategoria</label>
            <select class="field__input" formControlName="subcategoryId" [disabled]="!form.get('categoryId')?.value">
              <option value="">Selecione...</option>
              <option *ngFor="let s of filteredSubcategories" [value]="s.id">{{ s.name }}</option>
            </select>
          </div>

          <div class="field field--full">
            <label class="field__label">Descrição</label>
            <textarea class="field__input" formControlName="description" rows="3" placeholder="Descreva o produto em detalhes..."></textarea>
          </div>
        </div>
      </form>
    </app-modal>

    <!-- DELETE MODAL -->
    <app-modal [open]="deleteModal" title="Confirmar exclusão" confirmLabel="Excluir" confirmVariant="danger"
      (closeModal)="deleteModal=false" (confirm)="deleteProduct()">
      <p>Tem certeza que deseja excluir <strong>{{ selectedProduct?.name }}</strong>?</p>
    </app-modal>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 24px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px; }
    .page__head { display: flex; align-items: flex-start; justify-content: space-between; }
    .page__title { font-size: 24px; font-weight: 800; margin-top: 4px; }
    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); }
    .card__toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
    .search-box { display: flex; align-items: center; gap: 8px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); padding: 8px 12px; flex: 1; max-width: 320px; }
    .search-box input { border: none; outline: none; font-size: 13px; width: 100%; background: transparent; }
    .table-wrap { overflow-x: auto; }
    .table { width: 100%; border-collapse: collapse; }
    th { padding: 10px 14px; font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; border-bottom: 2px solid var(--color-border); white-space: nowrap; }
    td { padding: 10px 14px; font-size: 13px; border-bottom: 1px solid var(--color-border); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .td--bold { font-weight: 600; }
    .td--mono { font-family: monospace; font-size: 12px; color: var(--color-text-muted); }
    .td--actions { width: 48px; text-align: right; }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
    .thumb { width: 36px; height: 36px; border-radius: var(--radius-sm); background: var(--color-bg); border: 1px solid var(--color-border); display: flex; align-items: center; justify-content: center; background-size: cover; background-position: center; overflow: hidden; flex-shrink: 0; }
    .badge-cond { display: inline-block; padding: 2px 8px; border-radius: 999px; font-size: 11px; font-weight: 600; text-transform: uppercase; }
    .badge-cond--novo { background: #dcfce7; color: #166534; }
    .badge-cond--usado { background: #fef9c3; color: #854d0e; }
    .badge-cond--recondicionado { background: #ede9fe; color: #5b21b6; }
    .btn-icon { background: none; border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 5px 8px; cursor: pointer; }
    .btn-icon:hover { background: var(--color-bg); }
    .btn-more { display: flex; align-items: center; justify-content: center; color: var(--color-text-muted); }
    .action-menu { position: relative; display: inline-block; }
    .action-dropdown { position: absolute; right: 0; top: calc(100% + 4px); background: var(--color-surface); border: 1.5px solid var(--color-border); border-radius: var(--radius-md); box-shadow: 0 4px 16px rgba(0,0,0,.10); min-width: 148px; z-index: 200; overflow: hidden; }
    .action-item { display: flex; align-items: center; gap: 9px; width: 100%; padding: 9px 14px; font-size: 13px; font-weight: 500; color: var(--color-text); background: none; border: none; cursor: pointer; text-decoration: none; transition: background .12s; white-space: nowrap; }
    .action-item:hover { background: var(--color-bg); }
    .action-item--danger { color: var(--color-error, #dc3545); }
    .action-item--danger:hover { background: #fef2f2; }

    /* Form */
    .prod-form { display: flex; flex-direction: column; gap: 16px; }
    .img-upload { width: 100%; height: 140px; border: 2px dashed var(--color-border); border-radius: var(--radius-md); cursor: pointer; overflow: hidden; transition: border-color .15s; display: flex; align-items: center; justify-content: center; }
    .img-upload:hover { border-color: var(--color-primary); }
    .img-upload__preview { width: 100%; height: 100%; object-fit: cover; }
    .img-upload__placeholder { display: flex; flex-direction: column; align-items: center; gap: 8px; color: var(--color-text-muted); font-size: 13px; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
    .field { display: flex; flex-direction: column; gap: 5px; }
    .field--full { grid-column: 1/-1; }
    .field__label { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .5px; }
    .field__input { padding: 10px 12px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 13px; outline: none; resize: vertical; background: transparent; color: var(--color-text); }
    .field__input:focus { border-color: var(--color-primary); }
    .field__input:disabled { opacity: .5; cursor: not-allowed; }
    select.field__input { cursor: pointer; }
  `]
})
export class ProductsComponent implements OnInit {
  private productsHttp = inject(ProductsHttpService);
  private categoriesHttp = inject(CategoriesHttpService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  products: any[] = [];
  loading = true; page = 1; totalPages = 1; search = '';
  createModal = false; deleteModal = false; selectedProduct: any = null; saving = false;
  menuOpenId: any = null;

  categories: any[] = [];
  allSubcategories: any[] = [];
  filteredSubcategories: any[] = [];
  imagePreview: string | null = null;
  imageBase64: string | null = null;

  form = this.fb.group({
    name: ['', Validators.required],
    sku: [''],
    condition: [''],
    price: [0, Validators.required],
    stock: [0],
    categoryId: [''],
    subcategoryId: [''],
    description: ['']
  });

  ngOnInit(): void {
    this.load();
    this.loadCategories();
  }

  load(): void {
    this.loading = true;
    this.productsHttp.getAll({ page: this.page, pageSize: 10, search: this.search || undefined }).subscribe({
      next: r => { this.products = Array.isArray(r) ? r : r?.data ?? []; this.totalPages = r?.totalPages ?? 1; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadCategories(): void {
    this.categoriesHttp.getAll().subscribe({ next: cs => this.categories = cs, error: () => {} });
    this.categoriesHttp.getSubcategories().subscribe({ next: ss => this.allSubcategories = ss, error: () => {} });
  }

  onCategoryChange(): void {
    const catId = this.form.get('categoryId')?.value;
    this.filteredSubcategories = catId
      ? this.allSubcategories.filter(s => String(s.categoryId ?? s.category?.id) === String(catId))
      : [];
    this.form.patchValue({ subcategoryId: '' });
  }

  onImageChange(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
      this.imageBase64 = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  conditionLabel(c: string): string {
    return { novo: 'Novo', usado: 'Usado', recondicionado: 'Recond.' }[c] ?? c;
  }

  onSearch(): void { this.page = 1; this.load(); }
  onPageChange(p: number): void { this.page = p; this.load(); }
  toggleMenu(id: any, event: Event): void { event.stopPropagation(); this.menuOpenId = this.menuOpenId === id ? null : id; }
  closeMenu(): void { this.menuOpenId = null; }

  toggleActive(p: any): void {
    const action = p.isActive ? this.productsHttp.deactivate(p.id) : this.productsHttp.activate(p.id);
    action.subscribe({
      next: () => { p.isActive = !p.isActive; this.toast.success('Produto atualizado.'); },
      error: () => this.toast.error('Erro ao atualizar produto.')
    });
  }

  openCreate(): void {
    this.form.reset({ price: 0, stock: 0 });
    this.imagePreview = null;
    this.imageBase64 = null;
    this.filteredSubcategories = [];
    this.createModal = true;
  }

  saveProduct(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const payload: any = { ...this.form.value };
    if (this.imageBase64) payload.imageUrl = this.imageBase64;
    if (!payload.categoryId) delete payload.categoryId;
    if (!payload.subcategoryId) delete payload.subcategoryId;
    if (!payload.condition) delete payload.condition;
    if (!payload.sku) delete payload.sku;

    this.productsHttp.create(payload).subscribe({
      next: () => { this.createModal = false; this.saving = false; this.toast.success('Produto criado!'); this.load(); },
      error: () => { this.saving = false; this.toast.error('Erro ao criar produto.'); }
    });
  }

  confirmDelete(p: any): void { this.selectedProduct = p; this.deleteModal = true; }
  deleteProduct(): void {
    this.productsHttp.delete(this.selectedProduct.id).subscribe({
      next: () => { this.deleteModal = false; this.toast.success('Produto excluído.'); this.load(); },
      error: () => this.toast.error('Erro ao excluir produto.')
    });
  }
}
