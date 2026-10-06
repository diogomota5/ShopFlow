import { Component, inject, signal } from '@angular/core';

@Component({
  selector: 'app-cookie-consent',
  standalone: true,
  template: `
    @if (visible()) {
      <aside class="cookie-banner" role="dialog" aria-label="Preferências de cookies">
        <div>
          <strong>Privacidade e cookies</strong>
          <p>Usamos apenas cookies essenciais para manter sua sessão e o funcionamento da loja.</p>
        </div>
        <button type="button" class="btn btn-primary btn-sm" (click)="accept()">Entendi</button>
      </aside>
    }
  `,
  styles: [`
    .cookie-banner { position: fixed; right: 1rem; bottom: 1rem; z-index: 1000; width: min(420px, calc(100% - 2rem)); display: flex; align-items: center; gap: 1rem; padding: 1rem 1.1rem; box-sizing: border-box; background: var(--surface-card); border: 1px solid var(--border); border-radius: var(--radius-md); box-shadow: var(--shadow-lg); }
    strong { display: block; color: var(--text-primary); font-size: .9rem; }
    p { margin: .3rem 0 0; color: var(--text-secondary); font-size: .78rem; line-height: 1.45; }
    button { flex-shrink: 0; }
    @media (max-width: 560px) { .cookie-banner { align-items: flex-start; flex-direction: column; } button { width: 100%; } }
  `],
})
export class CookieConsentComponent {
  visible = signal(!localStorage.getItem('sf_cookie_consent'));

  accept(): void {
    localStorage.setItem('sf_cookie_consent', 'accepted');
    this.visible.set(false);
  }
}
