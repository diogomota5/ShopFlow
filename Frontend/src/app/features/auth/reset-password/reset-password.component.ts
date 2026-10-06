import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <main class="reset-page">
      <section class="reset-card animate-fade-in">
        @if (success()) {
          <div class="reset-icon reset-icon--success">✓</div>
          <h1>Senha alterada</h1>
          <p>Sua senha foi redefinida com segurança.</p>
          <a routerLink="/login" class="btn btn-primary">Entrar na conta</a>
        } @else {
          <h1>Nova senha</h1>
          <p>Crie uma senha com pelo menos 8 caracteres.</p>
          @if (error()) { <div class="reset-error" role="alert">{{ error() }}</div> }
          <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
            <label for="reset-password">Nova senha</label>
            <input id="reset-password" type="password" formControlName="password" autocomplete="new-password" />
            @if (form.controls.password.invalid && form.controls.password.touched) { <small>A senha deve ter pelo menos 8 caracteres.</small> }
            <label for="reset-confirm">Confirmar senha</label>
            <input id="reset-confirm" type="password" formControlName="confirmation" autocomplete="new-password" />
            @if (form.controls.confirmation.touched && form.controls.password.value !== form.controls.confirmation.value) { <small>As senhas não coincidem.</small> }
            <button class="btn btn-primary" type="submit" [disabled]="form.invalid || form.controls.password.value !== form.controls.confirmation.value || loading()">
              {{ loading() ? 'Salvando...' : 'Redefinir senha' }}
            </button>
          </form>
        }
      </section>
    </main>
  `,
  styles: [`
    :host { display: block; min-height: 100dvh; }
    .reset-page { min-height: 100dvh; display: grid; place-items: center; padding: 1.5rem; background: linear-gradient(135deg, #f6f2fb, #ede1f7); }
    .reset-card { width: min(440px, 100%); padding: 2.25rem 2rem; background: var(--surface-card); border: 1px solid var(--border); border-radius: var(--radius-lg); box-shadow: var(--shadow-md); }
    h1 { margin: 0 0 0.5rem; color: var(--text-primary); font-family: var(--font-heading); }
    p { margin: 0 0 1.5rem; color: var(--text-secondary); line-height: 1.6; }
    form { display: grid; gap: 0.5rem; }
    label { color: var(--text-secondary); font-size: 0.8rem; font-weight: 700; text-transform: uppercase; }
    input { width: 100%; box-sizing: border-box; padding: 0.8rem 0.9rem; border: 1px solid var(--border); border-radius: var(--radius-sm); background: var(--surface-card); color: var(--text-primary); }
    small, .reset-error { color: var(--danger); font-size: 0.8rem; }
    button { margin-top: 0.65rem; width: 100%; }
    .reset-icon { width: 56px; height: 56px; margin-bottom: 1.2rem; display: grid; place-items: center; border-radius: 50%; font-size: 1.8rem; font-weight: 800; }
    .reset-icon--success { background: var(--success-alpha); color: var(--success); }
  `],
})
export class ResetPasswordComponent {
  private route = inject(ActivatedRoute);
  private auth = inject(AuthService);
  private fb = inject(FormBuilder);
  private token = this.route.snapshot.queryParamMap.get('token') ?? '';
  form = this.fb.nonNullable.group({
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmation: ['', Validators.required],
  });
  loading = signal(false);
  success = signal(false);
  error = signal('');

  submit(): void {
    const { password, confirmation } = this.form.getRawValue();
    if (!this.token || this.form.invalid || password !== confirmation || this.loading()) return;
    this.loading.set(true);
    this.auth.resetPassword(this.token, password).subscribe({
      next: () => { this.success.set(true); this.loading.set(false); },
      error: (err) => { this.error.set(err.error?.message ?? 'Token inválido ou expirado.'); this.loading.set(false); },
    });
  }
}
