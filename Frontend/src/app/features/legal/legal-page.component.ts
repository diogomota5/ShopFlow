import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-legal-page',
  standalone: true,
  imports: [RouterLink],
  template: `
    <main class="legal-page">
      <article class="legal-card">
        <a routerLink="/" class="back-link">← Voltar para a loja</a>
        <p class="legal-eyebrow">ShopFlow</p>
        <h1>{{ title }}</h1>
        <p class="legal-updated">Última atualização: 19 de agosto de 2026</p>

        @if (privacy) {
          <h2>1. Dados coletados</h2>
          <p>Coletamos os dados necessários para criar sua conta, processar pedidos, entregar compras e oferecer atendimento, como nome, e-mail, telefone, endereço e histórico de pedidos.</p>
          <h2>2. Uso dos dados</h2>
          <p>Os dados são usados para autenticação, processamento de compras, comunicação sobre pedidos, prevenção de fraude e melhoria da experiência da loja.</p>
          <h2>3. Compartilhamento</h2>
          <p>Compartilhamos somente o necessário com serviços envolvidos na operação, como pagamentos, entrega, hospedagem e envio de e-mails. Não vendemos dados pessoais.</p>
          <h2>4. Segurança e direitos</h2>
          <p>Adotamos controles técnicos razoáveis e respeitamos os direitos previstos na LGPD. Para solicitar acesso, correção ou exclusão, entre em contato pelos canais oficiais da loja.</p>
        } @else {
          <h2>1. Uso da plataforma</h2>
          <p>Você deve fornecer informações verdadeiras e utilizar a ShopFlow de forma lícita, respeitando as regras de compra, pagamento e entrega apresentadas no checkout.</p>
          <h2>2. Produtos e pedidos</h2>
          <p>Preços, estoque, prazos e condições podem ser atualizados. O pedido é confirmado conforme a disponibilidade e a validação do pagamento.</p>
          <h2>3. Conta</h2>
          <p>Você é responsável por manter seus dados de acesso protegidos e por comunicar qualquer uso não autorizado da sua conta.</p>
          <h2>4. Atendimento</h2>
          <p>Em caso de dúvidas, cancelamentos ou problemas com a compra, utilize os canais de atendimento disponibilizados pela ShopFlow.</p>
        }
      </article>
    </main>
  `,
  styles: [`
    :host { display: block; min-height: 100dvh; }
    .legal-page { min-height: 100dvh; padding: 3rem 1.5rem; background: var(--surface); }
    .legal-card { width: min(820px, 100%); margin: 0 auto; padding: clamp(1.5rem, 4vw, 3rem); box-sizing: border-box; background: var(--surface-card); border: 1px solid var(--border); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); }
    .back-link { color: var(--primary); font-size: .85rem; font-weight: 700; text-decoration: none; }
    .legal-eyebrow { margin: 2rem 0 .35rem; color: var(--primary); font-size: .75rem; font-weight: 800; letter-spacing: .12em; text-transform: uppercase; }
    h1 { margin: 0; color: var(--text-primary); font-family: var(--font-heading); }
    h2 { margin: 2rem 0 .5rem; color: var(--text-primary); font-size: 1.1rem; }
    p { color: var(--text-secondary); line-height: 1.75; }
    .legal-updated { margin-top: .5rem; font-size: .85rem; }
  `],
})
export class LegalPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  title = 'Política de Privacidade';
  privacy = true;

  ngOnInit(): void {
    this.privacy = this.route.snapshot.data['type'] !== 'terms';
    this.title = this.privacy ? 'Política de Privacidade' : 'Termos de Uso';
  }
}
