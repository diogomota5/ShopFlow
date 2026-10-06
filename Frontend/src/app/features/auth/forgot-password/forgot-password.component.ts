import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <main class="recovery-page">
      <section class="recovery-card animate-fade-in">
        @if (sent()) {
          <div class="recovery-icon recovery-icon--success">✓</div>
          <h1>E-mail enviado</h1>
          <p>Se o endereço estiver cadastrado, você receberá as instruções em instantes.</p>
          <a routerLink="/login" class="btn btn-primary">Voltar para o login</a>
        } @else {
          <a routerLink="/login" class="back-link">← Voltar para o login</a>
          <h1>Recuperar senha</h1>
          <p>Informe seu e-mail para receber um link seguro de recuperação.</p>
          @if (error()) { <div class="recovery-error" role="alert">{{ error() }}</div> }
          <form [formGroup]="form" (ngSubmit)="submit(false)" novalidate>
            <label for="recovery-email">E-mail</label>
            <input id="recovery-email" type="email" formControlName="email" placeholder="seuemail@gmail.com" autocomplete="email" />
            @if (form.controls.email.invalid && form.controls.email.touched) { <small>Digite um e-mail válido.</small> }
            <button class="btn btn-primary" type="submit" [disabled]="form.invalid || loading()">
              {{ loading() ? 'Enviando...' : 'Enviar recuperação' }}
            </button>
          </form>
          <button class="secondary-action" type="button" (click)="submit(true)" [disabled]="form.invalid || loading()">
            Reenviar confirmação de cadastro
          </button>
        }
        <a class="home-link" routerLink="/">Quer voltar para a home?</a>
      </section>
    </main>
  `,
  styles: [`
    :host { display: block; min-height: 100dvh; }
    .recovery-page { min-height: 100dvh; display: grid; place-items: center; padding: clamp(0.4rem, 1.2vh, 1rem); background-color: var(--surface); background-image: radial-gradient(ellipse 70% 55% at 15% 20%, color-mix(in srgb, var(--primary) 18%, transparent), transparent 55%), radial-gradient(ellipse 60% 50% at 90% 80%, color-mix(in srgb, var(--accent) 22%, transparent), transparent 50%), linear-gradient(135deg, var(--surface) 0%, var(--surface-raised) 100%); position: relative; }
    .recovery-page::before { content: ''; position: absolute; inset: 0; background-image: radial-gradient(color-mix(in srgb, var(--primary) 8%, transparent) 1px, transparent 1px); background-size: 24px 24px; pointer-events: none; }
    .recovery-card { position: relative; z-index: 1; width: min(440px, 100%); padding: 2.25rem 2rem; background: linear-gradient(180deg, var(--surface-card) 0%, var(--surface-raised) 100%); border: 1px solid color-mix(in srgb, var(--primary) 16%, transparent); border-radius: var(--radius-lg); box-shadow: 0 24px 60px color-mix(in srgb, #1a1a1d 18%, transparent); }
    .recovery-card h1 { margin: 1.2rem 0 0.5rem; color: var(--text-primary); font-family: var(--font-heading); }
    .recovery-card p { margin: 0 0 1.5rem; color: var(--text-secondary); line-height: 1.6; }
    .back-link { color: var(--primary); font-size: 0.85rem; font-weight: 600; text-decoration: none; }
    form { display: grid; gap: 0.5rem; }
    label { color: var(--text-secondary); font-size: 0.8rem; font-weight: 700; text-transform: uppercase; }
    input { width: 100%; box-sizing: border-box; padding: 0.8rem 0.9rem; border: 1px solid var(--border); border-radius: var(--radius-sm); background: var(--surface-card); color: var(--text-primary); }
    small, .recovery-error { color: var(--danger); font-size: 0.8rem; }
    button { margin-top: 0.65rem; width: 100%; }
    .secondary-action { border: 0; background: transparent; color: var(--primary); font-weight: 600; cursor: pointer; }
    .home-link { display: block; margin-top: 1.25rem; padding-top: 1rem; border-top: 1px solid var(--border); color: var(--primary); font-size: 0.86rem; font-weight: 600; text-align: center; text-decoration: none; }
    .home-link:hover { text-decoration: underline; }
    .recovery-icon { width: 56px; height: 56px; display: grid; place-items: center; border-radius: 50%; font-size: 1.8rem; font-weight: 800; }
    .recovery-icon--success { background: var(--success-alpha); color: var(--success); }
    :host-context([data-theme='dark']) .recovery-page { background-color: #1a1a1d; background-image: radial-gradient(ellipse 70% 55% at 15% 20%, color-mix(in srgb, #a020f0 24%, transparent), transparent 55%), radial-gradient(ellipse 60% 50% at 90% 80%, color-mix(in srgb, #d8bfd8 14%, transparent), transparent 50%), linear-gradient(135deg, #1a1a1d 0%, #2f2f35 100%); }
  `],
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  form = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]] });
  loading = signal(false);
  sent = signal(false);
  error = signal('');

  submit(resendVerification: boolean): void {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);
    this.error.set('');
    const request = resendVerification
      ? this.auth.resendVerification(this.form.controls.email.value)
      : this.auth.forgotPassword(this.form.controls.email.value);
    request.subscribe({
      next: () => { this.sent.set(true); this.loading.set(false); },
      error: (err) => { this.error.set(err.error?.message ?? 'Não foi possível enviar o e-mail.'); this.loading.set(false); },
    });
  }
}
