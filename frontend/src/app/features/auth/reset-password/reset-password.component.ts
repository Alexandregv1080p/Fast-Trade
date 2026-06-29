import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthHttpService } from '../../../core/services/auth-http.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgIf, ButtonComponent],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <div class="auth-card__brand">
          <div class="auth-card__logo">
            <svg width="16" height="16" viewBox="0 0 20 20" fill="none">
              <path d="M3 6l7-3 7 3-7 3-7-3z" fill="white" opacity=".9"/>
              <path d="M3 10l7 3 7-3" stroke="white" stroke-width="1.5" fill="none" opacity=".7"/>
            </svg>
          </div>
          <span>Fast Trade</span>
        </div>
        <h2 class="auth-card__title">Redefinir senha</h2>
        <p class="auth-card__desc">Insira o código recebido por e-mail e crie sua nova senha.</p>
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="auth-form">
          <div class="field">
            <label class="field__label">Código</label>
            <input class="field__input" formControlName="code" placeholder="000000" />
          </div>
          <div class="field">
            <label class="field__label">Nova senha</label>
            <input class="field__input" type="password" formControlName="password" placeholder="••••••••" />
          </div>
          <span class="auth-form__error" *ngIf="error">{{ error }}</span>
          <app-button type="submit" variant="primary" [loading]="loading" [fullWidth]="true">REDEFINIR SENHA</app-button>
        </form>
        <a routerLink="/auth/login" class="auth-card__back">← Voltar ao login</a>
      </div>
    </div>`,
  styles: [`
    .auth-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: var(--color-bg); }
    .auth-card { background: var(--color-surface); border-radius: var(--radius-xl); padding: 40px; width: 100%; max-width: 400px; box-shadow: var(--shadow-lg); }
    .auth-card__brand { display: flex; align-items: center; gap: 10px; margin-bottom: 28px; font-size: 15px; font-weight: 700; color: var(--color-text); letter-spacing: .3px; }
    .auth-card__logo { width: 32px; height: 32px; background: var(--color-accent); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
    .auth-card__title { font-size: 22px; font-weight: 800; color: var(--color-text); margin-bottom: 8px; }
    .auth-card__desc { font-size: 13px; color: var(--color-text-muted); margin-bottom: 28px; }
    .auth-form { display: flex; flex-direction: column; gap: 16px; }
    .auth-form__error { font-size: 13px; color: var(--color-error); }
    .field { display: flex; flex-direction: column; gap: 5px; }
    .field__label { font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; }
    .field__input { width: 100%; padding: 10px 12px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 14px; outline: none; }
    .field__input:focus { border-color: var(--color-accent); box-shadow: 0 0 0 3px rgba(37,99,235,.12); }
    .auth-card__back { display: block; text-align: center; margin-top: 24px; font-size: 13px; color: var(--color-text-muted); }
    .auth-card__back:hover { color: var(--color-accent); }
  `]
})
export class ResetPasswordComponent {
  private fb = inject(FormBuilder);
  private authHttp = inject(AuthHttpService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  form = this.fb.group({ code: ['', Validators.required], password: ['', [Validators.required, Validators.minLength(6)]] });
  loading = false; error = '';
  private email = this.route.snapshot.queryParamMap.get('email') ?? '';

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const { code, password } = this.form.value;
    this.loading = true;
    this.authHttp.resetPassword(this.email, code!, password!).subscribe({
      next: () => this.router.navigate(['/auth/login']),
      error: () => { this.error = 'Código inválido ou expirado.'; this.loading = false; }
    });
  }
}
