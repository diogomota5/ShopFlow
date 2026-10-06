# Checklist de evolução do ShopFlow

## Status atual

- [x] Catálogo com busca, filtros e categorias
- [x] Carrinho com atualização de quantidades
- [x] Checkout com endereço e cupons
- [x] Criação e acompanhamento de pedidos
- [x] Gestão administrativa de produtos, categorias, cupons e usuários
- [x] Gestão de status, rastreio, cancelamento e reabertura de pedidos
- [x] Avaliações de produtos
- [x] Autenticação com JWT
- [x] Validação de e-mail no cadastro
- [x] Upload de imagens com Cloudinary
- [x] Versionamento do banco com Flyway

## Etapa 1: pagamentos

- [ ] Escolher o gateway: Mercado Pago ou Stripe
- [ ] Criar credenciais sandbox
- [ ] Criar entidade e estados de pagamento
- [ ] Criar checkout de pagamento
- [ ] Implementar webhook assinado
- [ ] Atualizar pedido após pagamento aprovado
- [ ] Tratar pagamento recusado, expirado e estornado
- [ ] Impedir que o frontend trate pedido pendente como pago
- [ ] Testar fluxo aprovado, recusado e duplicado

## Etapa 2: conta e segurança

### Recuperação e confiabilidade de e-mail: concluída

- [x] Criar recuperação de senha por e-mail
- [x] Criar expiração e uso único dos tokens
- [x] Adicionar reenvio de confirmação de e-mail
- [x] Corrigir falha de cadastro quando o SMTP estiver indisponível

### Preparação de produção: pendente de configuração externa

- [ ] Adicionar limite de tentativas no backend
- [ ] Rotacionar credenciais que foram usadas no desenvolvimento
- [ ] Configurar CORS para o domínio de produção
- [ ] Remover valores padrão sensíveis das configurações

## Etapa 3: estoque e cupons

- [x] Proteger reserva de estoque contra compras simultâneas
- [x] Adicionar lock otimista ou atualização atômica
- [x] Definir política de reserva durante pagamento pendente
- [x] Evitar uso concorrente acima do limite de cupons
- [x] Registrar movimentações de estoque

## Etapa 4: operação de pedidos

- [x] Permitir cancelamento pelo cliente antes do envio
- [x] Criar histórico de alterações de status
- [x] Enviar e-mails de pedido criado, pago, enviado e entregue
- [ ] Adicionar cálculo de frete e prazo
- [x] Adicionar busca e paginação avançada no painel admin
- [ ] Integrar transportadora ou rastreio externo

## Etapa 5: conformidade e experiência

- [x] Criar política de privacidade
- [x] Criar termos de uso
- [x] Adicionar consentimento de cookies quando necessário
- [x] Revisar acessibilidade e navegação por teclado
- [x] Revisar estados de erro, vazio e carregamento
- [x] Melhorar SEO e metadados das páginas de produto

## Etapa 6: qualidade e entrega

- [x] Criar testes de autenticação e confirmação de e-mail
- [x] Criar testes de carrinho e checkout
- [x] Criar testes de autenticação e confirmação de e-mail
- [x] Criar testes de carrinho e checkout
- [x] Criar testes de estoque e cupons
- [x] Criar testes de transição de pedidos
- [x] Criar testes Angular dos fluxos principais
- [x] Corrigir o orçamento de CSS do frontend
- [x] Configurar CI com build e testes
- [x] Preparar deploy de backend, frontend e banco
- [x] Criar ambiente sandbox e produção separados
- [x] Atualizar README com configuração real e screenshots

## Ordem recomendada

1. Pagamentos em sandbox
2. Recuperação e confiabilidade de e-mail
3. Estoque concorrente
4. Histórico e notificações de pedidos
5. Segurança de produção e LGPD
6. Testes automatizados e deploy
