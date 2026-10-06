import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [RouterLink],
  template: `
    <main class="verify-page">
      <section class="verify-card animate-fade-in">
        @if (loading()) {
          <div class="verify-icon verify-icon--loading">...</div>
          <h1>Confirmando seu e-mail</h1>
          <p>Aguarde enquanto validamos seu cadastro.</p>
        } @else if (success()) {
          <div class="verify-icon verify-icon--success">✓</div>
          <h1>E-mail confirmado</h1>
          <p>Seu cadastro está ativo. Você já pode entrar na sua conta.</p>
          <a routerLink="/login" class="btn btn-primary">Ir para o login</a>
        } @else {
          <div class="verify-icon verify-icon--error">!</div>
          <h1>Não foi possível confirmar</h1>
          <p>{{ error() }}</p>
          <a routerLink="/register" class="btn btn-outline">Voltar ao cadastro</a>
        }
      </section>
    </main>
  `,
  styles: [`
    :host { display: block; min-height: 100dvh; }
    .verify-page { min-height: 100dvh; display: grid; place-items: center; padding: 1.5rem; background: linear-gradient(135deg, #f6f2fb, #ede1f7); }
    .verify-card { width: min(440px, 100%); padding: 2.5rem 2rem; text-align: center; background: var(--surface-card); border: 1px solid var(--border); border-radius: var(--radius-lg); box-shadow: var(--shadow-md); }
    .verify-icon { width: 64px; height: 64px; margin: 0 auto 1.25rem; display: grid; place-items: center; border-radius: 50%; font-size: 2rem; font-weight: 800; }
    .verify-icon--loading { background: var(--primary-alpha); color: var(--primary); }
    .verify-icon--success { background: var(--success-alpha); color: var(--success); }
    .verify-icon--error { background: var(--danger-alpha); color: var(--danger); }
    h1 { margin: 0 0 0.6rem; color: var(--text-primary); font-family: var(--font-heading); }
    p { margin: 0 0 1.5rem; color: var(--text-secondary); line-height: 1.6; }
  `],
})
export class VerifyEmailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private auth = inject(AuthService);
  loading = signal(true);
  success = signal(false);
  error = signal('O link pode ser inválido ou ter expirado.');

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.loading.set(false);
      return;
    }
    this.auth.verifyEmail(token).subscribe({
      next: () => { this.success.set(true); this.loading.set(false); },
      error: (err) => { this.error.set(err.error?.message ?? this.error()); this.loading.set(false); },
    });
  }
}
