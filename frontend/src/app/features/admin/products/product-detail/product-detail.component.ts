import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NgIf, CurrencyPipe } from '@angular/common';
import { ProductsHttpService } from '../../../../core/services/products-http.service';
import { SpinnerComponent } from '../../../../shared/components/spinner/spinner.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [NgIf, RouterLink, CurrencyPipe, SpinnerComponent],
  template: `
    <div class="page">
      <div class="page__head">
        <span class="page__breadcrumb"><a routerLink="/admin/products">PRODUTOS</a> / DETALHE</span>
        <h1 class="page__title">Detalhe do Produto</h1>
      </div>
      <app-spinner *ngIf="loading" />
      <ng-container *ngIf="!loading && product">
        <div class="card product-detail">
          <div class="product-detail__image">
            <img *ngIf="product.images?.[0]?.url" [src]="product.images[0].url" alt="Produto" />
            <div *ngIf="!product.images?.[0]?.url" class="product-detail__image-placeholder">📦</div>
          </div>
          <div class="product-detail__info">
            <h2>{{ product.name }}</h2>
            <p class="product-detail__desc">{{ product.description || 'Sem descrição.' }}</p>
            <div class="info-row">
              <div class="info-field"><span class="info-field__label">Preço</span><span class="info-field__value--price">{{ product.price | currency:'BRL' }}</span></div>
              <div class="info-field"><span class="info-field__label">Estoque</span><span>{{ product.stock }}</span></div>
              <div class="info-field"><span class="info-field__label">Categoria</span><span>{{ product.category?.name || '—' }}</span></div>
              <div class="info-field"><span class="info-field__label">Status</span>
                <span class="badge" [class]="product.isActive ? 'badge--success' : 'badge--gray'">{{ product.isActive ? 'Ativo' : 'Inativo' }}</span>
              </div>
            </div>
          </div>
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
    .product-detail { display: flex; gap: 32px; }
    .product-detail__image { width: 200px; height: 200px; border-radius: var(--radius-md); overflow: hidden; background: var(--color-bg); flex-shrink: 0; display: flex; align-items: center; justify-content: center; }
    .product-detail__image img { width: 100%; height: 100%; object-fit: cover; }
    .product-detail__image-placeholder { font-size: 64px; }
    .product-detail__info { flex: 1; }
    .product-detail__info h2 { font-size: 22px; font-weight: 800; margin-bottom: 8px; }
    .product-detail__desc { font-size: 14px; color: var(--color-text-muted); margin-bottom: 20px; }
    .info-row { display: flex; flex-wrap: wrap; gap: 24px; }
    .info-field { display: flex; flex-direction: column; gap: 4px; }
    .info-field__label { font-size: 11px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .info-field__value--price { font-size: 20px; font-weight: 800; color: var(--color-primary); }
  `]
})
export class ProductDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productsHttp = inject(ProductsHttpService);
  product: any = null; loading = true;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.productsHttp.getById(id).subscribe({ next: p => { this.product = p?.data ?? p; this.loading = false; }, error: () => this.loading = false });
  }
}
