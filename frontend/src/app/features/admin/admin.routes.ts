import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard',     loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'users',         loadComponent: () => import('./users/users.component').then(m => m.UsersComponent) },
  { path: 'users/:id',     loadComponent: () => import('./users/user-detail/user-detail.component').then(m => m.UserDetailComponent) },
  { path: 'products',      loadComponent: () => import('./products/products.component').then(m => m.ProductsComponent) },
  { path: 'products/:id',  loadComponent: () => import('./products/product-detail/product-detail.component').then(m => m.ProductDetailComponent) },
  { path: 'orders',        loadComponent: () => import('./orders/orders.component').then(m => m.OrdersComponent) },
  { path: 'orders/returns',loadComponent: () => import('./orders/returns/returns.component').then(m => m.ReturnsComponent) },
  { path: 'orders/:id',    loadComponent: () => import('./orders/order-detail/order-detail.component').then(m => m.OrderDetailComponent) },
  { path: 'categories',    loadComponent: () => import('./categories/categories.component').then(m => m.CategoriesComponent) },
  { path: 'subcategories', loadComponent: () => import('./subcategories/subcategories.component').then(m => m.SubcategoriesComponent) },
  { path: 'financial',     loadComponent: () => import('./financial/financial.component').then(m => m.FinancialComponent) },
  { path: 'access',        loadComponent: () => import('./access/access.component').then(m => m.AccessComponent) },
  { path: 'chat',          loadComponent: () => import('./chat/chat.component').then(m => m.ChatComponent) },
];
