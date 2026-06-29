import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgIf, NgFor } from '@angular/common';
import { AuthHttpService } from '../../../core/services/auth-http.service';
import { AuthService } from '../../../core/services/auth.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { timeout, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgIf, NgFor, ButtonComponent],
  template: `
    <div class="login">
      <div class="login__form-side">
        <div class="login__inner">
          <div class="login__brand">
            <div class="login__logo-mark">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M3 6l7-3 7 3-7 3-7-3z" fill="white" opacity=".9"/>
                <path d="M3 10l7 3 7-3" stroke="white" stroke-width="1.5" fill="none" opacity=".7"/>
                <path d="M3 14l7 3 7-3" stroke="white" stroke-width="1.5" fill="none" opacity=".5"/>
              </svg>
            </div>
            <div class="login__brand-text">
              <span class="login__brand-name">Fast Trade</span>
              <span class="login__brand-sub">Admin Panel</span>
            </div>
          </div>

          <h1 class="login__title">Seja bem-vindo(a)!</h1>
          <p class="login__subtitle">Entre com suas credenciais para acessar o painel.</p>

          <div class="seed-box">
            <div class="seed-box__label">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              Credenciais de acesso (seed)
            </div>
            <div class="seed-box__rows">
              <div *ngFor="let c of seedCredentials" class="seed-row">
                <span class="seed-row__role">{{ c.role }}</span>
                <button class="seed-row__field" type="button" (click)="fill(c)" title="Clique para preencher">
                  <span>{{ c.email }}</span>
                  <span class="seed-row__sep">Â·</span>
                  <span>{{ c.password }}</span>
                  <svg class="seed-row__copy" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2"/><path d="M5 15H4a2 2 0 01-2-2V4a2 2 0 012-2h9a2 2 0 012 2v1"/></svg>
                </button>
              </div>
            </div>
          </div>

          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="login__fields">
            <div class="field">
              <label class="field__label">E-mail</label>
              <div class="field__wrap">
                <svg class="field__icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 01-2.06 0L2 7"/></svg>
                <input class="field__input" type="email" formControlName="email" placeholder="admin@fasttrade.com" autocomplete="email" />
              </div>
              <span class="field__error" *ngIf="form.get('email')?.touched && form.get('email')?.invalid">E-mail invalido</span>
            </div>

            <div class="field">
              <label class="field__label">Senha</label>
              <div class="field__wrap">
                <svg class="field__icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><circle cx="12" cy="16" r="1"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
                <input class="field__input" [type]="showPass ? 'text' : 'password'" formControlName="password" placeholder="..." autocomplete="current-password" />
                <button type="button" class="field__toggle" (click)="showPass = !showPass">
                  <svg *ngIf="!showPass" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  <svg *ngIf="showPass" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                </button>
              </div>
              <span class="field__error" *ngIf="form.get('password')?.touched && form.get('password')?.invalid">Senha obrigatoria</span>
            </div>

            <a routerLink="/auth/forgot-password" class="login__forgot">Esqueceu a senha? <strong>Clique aqui!</strong></a>
            <span class="login__error" *ngIf="error">{{ error }}</span>
            <app-button type="submit" variant="primary" size="lg" [loading]="loading" [fullWidth]="true">Entrar</app-button>
          </form>

          <footer class="login__footer">
            <a href="#">Termos de uso</a>
            <a href="#">Politicas de privacidade</a>
            <a href="#">Contato</a>
          </footer>
        </div>
      </div>

      <div class="login__image-side">
        <div class="login__image-overlay"></div>
        <div class="login__image-text">
          <h2>Gerencie seu negocio<br>com eficiencia</h2>
          <p>A plataforma completa para administrar produtos, pedidos e usuarios.</p>
        </div>
      </div>
    </div>`,
  styles: [`
    .login { display: flex; min-height: 100vh; }
    .login__form-side { width: 460px; flex-shrink: 0; display: flex; align-items: center; justify-content: center; background: var(--color-surface); padding: 48px 32px; border-right: 1px solid var(--color-border); }
    .login__inner { width: 100%; max-width: 380px; }
    .login__brand { display: flex; align-items: center; gap: 12px; margin-bottom: 32px; }
    .login__logo-mark { width: 38px; height: 38px; background: var(--color-accent); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 2px 8px rgba(37,99,235,.35); }
    .login__brand-text { display: flex; flex-direction: column; gap: 1px; }
    .login__brand-name { font-size: 15px; font-weight: 700; color: var(--color-text); letter-spacing: .3px; }
    .login__brand-sub { font-size: 10px; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .8px; }
    .login__title { font-size: 26px; font-weight: 800; color: var(--color-text); margin-bottom: 6px; }
    .login__subtitle { font-size: 13px; color: var(--color-text-muted); margin-bottom: 16px; line-height: 1.5; }
    .seed-box { background: #f8faff; border: 1.5px solid #dbeafe; border-radius: var(--radius-md); padding: 12px 14px; margin-bottom: 20px; }
    .seed-box__label { display: flex; align-items: center; gap: 6px; font-size: 10px; font-weight: 700; color: #2563eb; text-transform: uppercase; letter-spacing: .6px; margin-bottom: 10px; }
    .seed-box__rows { display: flex; flex-direction: column; gap: 6px; }
    .seed-row { display: flex; align-items: center; gap: 8px; }
    .seed-row__role { font-size: 10px; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: .5px; width: 56px; flex-shrink: 0; }
    .seed-row__field { display: flex; align-items: center; gap: 6px; flex: 1; background: #fff; border: 1px solid #e2e8f0; border-radius: 6px; padding: 6px 10px; font-size: 12px; color: #334155; cursor: pointer; transition: all .15s; text-align: left; font-family: inherit; }
    .seed-row__field:hover { border-color: #2563eb; background: #f0f6ff; color: #1d4ed8; }
    .seed-row__sep { color: #cbd5e1; }
    .seed-row__copy { color: #94a3b8; flex-shrink: 0; margin-left: auto; }
    .seed-row__field:hover .seed-row__copy { color: #2563eb; }
    .login__fields { display: flex; flex-direction: column; gap: 18px; }
    .field { display: flex; flex-direction: column; gap: 6px; }
    .field__label { font-size: 12px; font-weight: 600; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .5px; }
    .field__wrap { position: relative; display: flex; align-items: center; }
    .field__icon { position: absolute; left: 12px; color: var(--color-text-light); pointer-events: none; flex-shrink: 0; }
    .field__input { width: 100%; padding: 11px 12px 11px 40px; border: 1.5px solid var(--color-border); border-radius: var(--radius-md); font-size: 14px; background: var(--color-surface); transition: border-color var(--transition), box-shadow var(--transition); outline: none; color: var(--color-text); }
    .field__input:focus { border-color: var(--color-accent); box-shadow: 0 0 0 3px rgba(37,99,235,.12); }
    .field__toggle { position: absolute; right: 10px; background: none; border: none; color: var(--color-text-light); padding: 4px; cursor: pointer; display: flex; align-items: center; }
    .field__toggle:hover { color: var(--color-text-muted); }
    .field__error { font-size: 11px; color: var(--color-error); }
    .login__forgot { font-size: 13px; color: var(--color-text-muted); align-self: flex-start; }
    .login__forgot strong { color: var(--color-accent); font-weight: 600; }
    .login__error { font-size: 13px; color: var(--color-error); text-align: center; background: #fef2f2; padding: 10px 14px; border-radius: var(--radius-md); border: 1px solid #fecaca; }
    .login__footer { display: flex; justify-content: center; gap: 16px; margin-top: 32px; font-size: 11px; color: var(--color-text-light); }
    .login__footer a:hover { color: var(--color-accent); }
    .login__image-side { flex: 1; position: relative; background: var(--color-primary); background-image: url('https://images.unsplash.com/photo-1520333789090-1afc82db536a?w=1400&q=80'); background-size: cover; background-position: center; display: flex; align-items: flex-end; padding: 56px; }
    .login__image-overlay { position: absolute; inset: 0; background: linear-gradient(135deg, rgba(15,23,42,.85) 0%, rgba(37,99,235,.25) 100%); }
    .login__image-text { position: relative; color: #fff; max-width: 480px; }
    .login__image-text h2 { font-size: 36px; font-weight: 800; line-height: 1.2; margin-bottom: 14px; }
    .login__image-text p { font-size: 16px; opacity: .8; line-height: 1.6; }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authHttp = inject(AuthHttpService);
  private auth = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });
  loading = false; error = ''; showPass = false;

  seedCredentials = [
    { role: 'Admin',   email: 'admin@fasttrade.com',  password: 'admin123' },
    { role: 'Gerente', email: 'carlos@fasttrade.com', password: 'admin123' },
    { role: 'Suporte', email: 'ana@fasttrade.com',    password: 'admin123' },
  ];

  fill(c: { email: string; password: string }): void {
    this.form.patchValue({ email: c.email, password: c.password });
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true; this.error = '';
    this.authHttp.login(this.form.value as any).pipe(
      timeout(8000),
      catchError(() => throwError(() => new Error('timeout')))
    ).subscribe({
      next: s => {
        this.auth.setSession(s);
        this.loading = false;
        this.router.navigate(['/admin/dashboard']).catch(err => {
          this.error = 'Erro ao carregar painel: ' + (err?.message ?? String(err));
        });
      },
      error: (e) => { this.error = 'E-mail ou senha invalidos. ' + (e?.error?.message ?? ''); this.loading = false; }
    });
  }
}