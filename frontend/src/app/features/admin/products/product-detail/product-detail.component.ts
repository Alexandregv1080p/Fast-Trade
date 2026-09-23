import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NgIf, NgFor, CurrencyPipe, DatePipe } from '@angular/common';
import { ProductsHttpService } from '../../../../core/services/products-http.service';
import { SpinnerComponent } from '../../../../shared/components/spinner/spinner.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink, CurrencyPipe, DatePipe, SpinnerComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <div>
          <span class="page__breadcrumb"><a routerLink="/admin/products">PRODUTOS</a> / DETALHE</span>
          <h1 class="page__title">Detalhe do Produto</h1>
        </div>
        <a routerLink="/admin/products" class="btn btn--ghost">← Voltar ao catálogo</a>
      </div>

      <app-spinner *ngIf="loading" />

      <ng-container *ngIf="!loading && product">
        <div class="card pd">
          <!-- Mídia -->
          <div class="pd__media">
            <div class="pd__image">
              <img *ngIf="mainImage" [src]="mainImage" [alt]="product.name" />
              <div *ngIf="!mainImage" class="pd__image-ph">📦</div>
            </div>
            <div class="pd__thumbs" *ngIf="product.images?.length > 1">
              <div class="pd__thumb" *ngFor="let img of product.images">
                <img [src]="img.url" [alt]="product.name" />
              </div>
            </div>
          </div>

          <!-- Info -->
          <div class="pd__info">
            <div class="pd__tags">
              <span class="chip" *ngIf="product.category?.name">{{ product.category.name }}</span>
              <span class="chip chip--soft" *ngIf="product.subcategory?.name">{{ product.subcategory.name }}</span>
              <span class="badge" [class]="product.isActive ? 'badge--success' : 'badge--gray'">
                {{ product.isActive ? 'Ativo' : 'Inativo' }}
              </span>
            </div>

            <h2 class="pd__name">{{ product.name }}</h2>

            <div class="pd__price-block">
              <span class="pd__price">{{ product.price | currency:'BRL' }}</span>
              <span class="pd__commission" *ngIf="product.commission != null">
                Comissão da plataforma: {{ product.commission | currency:'BRL' }}
              </span>
            </div>

            <div class="pd__stats">
              <div class="stat">
                <span class="stat__label">Estoque</span>
                <span class="stat__value" [class]="stockClass">{{ product.stock }} un.</span>
              </div>
              <div class="stat">
                <span class="stat__label">SKU</span>
                <span class="stat__value stat__value--mono">{{ product.sku || '—' }}</span>
              </div>
              <div class="stat">
                <span class="stat__label">Condição</span>
                <span class="stat__value">{{ product.condition || '—' }}</span>
              </div>
              <div class="stat">
                <span class="stat__label">ID</span>
                <span class="stat__value stat__value--mono">#{{ product.id }}</span>
              </div>
            </div>

            <div class="pd__section">
              <span class="pd__section-label">Descrição</span>
              <p class="pd__desc">{{ product.description || 'Sem descrição cadastrada.' }}</p>
            </div>

            <div class="pd__meta" *ngIf="product.createdAt || product.updatedAt">
              <span *ngIf="product.createdAt">Criado em {{ product.createdAt | date:'dd/MM/yyyy' }}</span>
              <span *ngIf="product.updatedAt">· Atualizado em {{ product.updatedAt | date:'dd/MM/yyyy' }}</span>
            </div>
          </div>
        </div>
      </ng-container>

      <div class="card empty" *ngIf="!loading && !product">
        Produto não encontrado.
      </div>
    </div>`,
  styles: [`
    .page { display: flex; flex-direction: column; gap: 20px; }
    .page__head { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; }
    .page__breadcrumb { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .05em; }
    .page__breadcrumb a { color: inherit; text-decoration: none; }
    .page__breadcrumb a:hover { color: var(--color-primary); }
    .page__title { font-size: 24px; font-weight: 800; margin-top: 4px; }

    .btn { display: inline-flex; align-items: center; padding: 8px 14px; border-radius: var(--radius-md); font-size: 13px; font-weight: 600; text-decoration: none; cursor: pointer; }
    .btn--ghost { background: var(--color-surface); color: var(--color-text-muted); box-shadow: var(--shadow-sm); }
    .btn--ghost:hover { color: var(--color-primary); }

    .card { background: var(--color-surface); border-radius: var(--radius-lg); padding: 28px; box-shadow: var(--shadow-sm); }
    .empty { color: var(--color-text-muted); text-align: center; padding: 48px; }

    .pd { display: grid; grid-template-columns: 300px 1fr; gap: 36px; }
    @media (max-width: 760px) { .pd { grid-template-columns: 1fr; gap: 24px; } }

    .pd__media { display: flex; flex-direction: column; gap: 12px; }
    .pd__image { aspect-ratio: 1; border-radius: var(--radius-md); overflow: hidden; background: var(--color-bg); display: flex; align-items: center; justify-content: center; }
    .pd__image img { width: 100%; height: 100%; object-fit: cover; }
    .pd__image-ph { font-size: 80px; opacity: .5; }
    .pd__thumbs { display: flex; gap: 8px; flex-wrap: wrap; }
    .pd__thumb { width: 56px; height: 56px; border-radius: var(--radius-sm, 8px); overflow: hidden; background: var(--color-bg); }
    .pd__thumb img { width: 100%; height: 100%; object-fit: cover; }

    .pd__info { display: flex; flex-direction: column; }
    .pd__tags { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-bottom: 12px; }
    .chip { font-size: 12px; font-weight: 600; padding: 4px 12px; border-radius: 999px; background: color-mix(in srgb, var(--color-primary) 12%, transparent); color: var(--color-primary); }
    .chip--soft { background: var(--color-bg); color: var(--color-text-muted); }

    .pd__name { font-size: 26px; font-weight: 800; line-height: 1.2; margin-bottom: 16px; }

    .pd__price-block { display: flex; flex-direction: column; gap: 2px; margin-bottom: 24px; }
    .pd__price { font-size: 34px; font-weight: 800; color: var(--color-primary); line-height: 1; }
    .pd__commission { font-size: 12.5px; color: var(--color-text-muted); }

    .pd__stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 12px; margin-bottom: 24px; }
    .stat { background: var(--color-bg); border-radius: var(--radius-md); padding: 12px 14px; display: flex; flex-direction: column; gap: 4px; }
    .stat__label { font-size: 10.5px; font-weight: 700; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .06em; }
    .stat__value { font-size: 16px; font-weight: 700; }
    .stat__value--mono { font-family: ui-monospace, "Cascadia Code", monospace; font-size: 14px; }
    .stat__value--ok { color: #16a34a; }
    .stat__value--warn { color: #d97706; }
    .stat__value--out { color: #dc2626; }

    .pd__section { border-top: 1px solid var(--color-bg); padding-top: 18px; margin-bottom: 18px; }
    .pd__section-label { font-size: 11px; font-weight: 700; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .06em; }
    .pd__desc { font-size: 14px; line-height: 1.65; margin-top: 8px; }

    .pd__meta { font-size: 12px; color: var(--color-text-muted); display: flex; gap: 6px; flex-wrap: wrap; }
  `]
})
export class ProductDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productsHttp = inject(ProductsHttpService);
  product: any = null; loading = true;

  get mainImage(): string | null {
    return this.product?.images?.[0]?.url ?? this.product?.imageUrl ?? null;
  }

  get stockClass(): string {
    const s = this.product?.stock ?? 0;
    return s <= 0 ? 'stat__value--out' : s <= 10 ? 'stat__value--warn' : 'stat__value--ok';
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.productsHttp.getById(id).subscribe({ next: p => { this.product = p?.data ?? p; this.loading = false; }, error: () => this.loading = false });
  }
}
