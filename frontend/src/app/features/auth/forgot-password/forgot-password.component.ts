import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthHttpService } from '../../../core/services/auth-http.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';

@Component({
  selector: 'app-forgot-password',
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
        <h2 class="auth-card__title">Esqueceu sua senha?</h2>
        <p class="auth-card__desc">Digite seu e-mail para que possamos verificá-lo.</p>

        <div *ngIf="!sent; else sentTpl">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="auth-form">
            <div class="field">
              <label class="field__label">E-mail</label>
              <div class="field__wrap">
                <span class="field__icon">✉️</span>
                <input class="field__input" type="email" formControlName="email" placeholder="mail@email.com" />
              </div>
            </div>
            <span class="auth-form__error" *ngIf="error">{{ error }}</span>
            <app-button type="submit" variant="primary" [loading]="loading" [fullWidth]="true">CONTINUAR</app-button>
          </form>
        </div>
        <ng-template #sentTpl>
          <div class="auth-card__success">
            <div class="auth-card__success-icon">📧</div>
            <p>E-mail enviado! Verifique sua caixa de entrada e siga as instruções para redefinir sua senha.</p>
          </div>
        </ng-template>

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
    .field__wrap { position: relative; display: flex; align-items: center; }
    .field__icon { position: absolute; left: 12px; font-size: 14px; }
    .field__input { width: 100%; padding: 10px 12px 10px 38px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 14px; outline: none; }
    .field__input:focus { border-color: var(--color-primary); }
    .auth-card__success { text-align: center; padding: 20px 0; }
    .auth-card__success-icon { font-size: 48px; margin-bottom: 12px; }
    .auth-card__back { display: block; text-align: center; margin-top: 24px; font-size: 13px; color: var(--color-text-muted); }
    .auth-card__back:hover { color: var(--color-accent); }
  `]
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private authHttp = inject(AuthHttpService);
  form = this.fb.group({ email: ['', [Validators.required, Validators.email]] });
  loading = false; error = ''; sent = false;

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.authHttp.forgotPassword(this.form.value.email!).subscribe({
      next: () => { this.sent = true; this.loading = false; },
      error: () => { this.error = 'Erro ao enviar e-mail. Tente novamente.'; this.loading = false; }
    });
  }
}
