<div align="center">

# 🛒 ShopFlow — E-commerce Full Stack

> Plataforma de e-commerce completa com autenticação JWT, painel administrativo e fluxo de pedidos.

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-DD0031?style=flat-square&logo=angular&logoColor=white)](https://angular.io)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com)

[**🌐 Demo ao vivo**](https://seu-projeto.vercel.app) · [**📖 Documentação da API**](https://seu-backend.railway.app/swagger-ui.html) · [**📋 Reportar bug**](https://github.com/seu-usuario/shopflow/issues)

![Demo do projeto](./docs/demo.gif)

</div>

---

## 📌 Sobre o projeto

O **ShopFlow** é uma aplicação full stack de e-commerce desenvolvida como projeto de portfólio, com foco em boas práticas de desenvolvimento, arquitetura limpa e recursos valorizados no mercado.

A aplicação conta com fluxo completo de compra — do cadastro de produtos à finalização do pedido — além de painel administrativo para gestão da loja.

### ✨ Funcionalidades principais

- **Autenticação** — Registro, login e refresh token com JWT + Spring Security
- **Catálogo de produtos** — Listagem, busca, filtros por categoria e faixa de preço, paginação
- **Carrinho de compras** — Persistido por usuário, atualização em tempo real
- **Fluxo de pedidos** — Estados: `PENDENTE → PAGO → ENVIADO → ENTREGUE`
- **Painel Admin** — Gerenciamento de produtos, pedidos e usuários (ROLE_ADMIN)
- **Avaliações** — Usuários podem avaliar produtos com nota e comentário
- **Upload de imagens** — Imagens de produtos via integração com Cloudinary
- **Documentação da API** — Swagger UI com todos os endpoints documentados

---

## 🧱 Arquitetura e tecnologias

### Backend
| Tecnologia | Uso |
|---|---|
| Java 17 + Spring Boot 3 | Framework principal |
| Spring Security + JWT | Autenticação e autorização |
| Spring Data JPA + Hibernate | Persistência de dados |
| PostgreSQL 15 | Banco de dados relacional |
| Flyway | Controle de versão do banco |
| MapStruct | Mapeamento DTO ↔ Entidade |
| Swagger / OpenAPI 3 | Documentação da API |
| JUnit 5 + Mockito | Testes unitários |
| Docker Compose | Ambiente de desenvolvimento |

### Frontend
| Tecnologia | Uso |
|---|---|
| Angular 17 | Framework SPA |
| TypeScript | Tipagem estática |
| Angular Material | Componentes de UI |
| RxJS | Programação reativa |
| HTTP Interceptor | Injeção automática do JWT |
| Auth Guard | Proteção de rotas |
| Chart.js / ng2-charts | Gráficos no painel admin |

---

## 🗂️ Estrutura do projeto

```
shopflow/
├── backend/                    # API Spring Boot
│   ├── src/main/java/com/shopflow/
│   │   ├── config/             # Configs de segurança, Swagger, CORS
│   │   ├── controller/         # Endpoints REST
│   │   ├── dto/                # Data Transfer Objects (request/response)
│   │   ├── entity/             # Entidades JPA
│   │   ├── exception/          # GlobalExceptionHandler + exceções customizadas
│   │   ├── mapper/             # MapStruct mappers
│   │   ├── repository/         # Spring Data JPA repositories
│   │   └── service/            # Regras de negócio
│   ├── src/main/resources/
│   │   ├── db/migration/       # Flyway migrations
│   │   └── application.yml     # Configurações da aplicação
│   └── src/test/               # Testes unitários e de integração
│
├── frontend/                   # SPA Angular
│   └── src/app/
│       ├── core/               # Guards, interceptors, services globais
│       ├── shared/             # Componentes e pipes reutilizáveis
│       └── features/
│           ├── auth/           # Login e registro
│           ├── products/       # Catálogo e detalhes do produto
│           ├── cart/           # Carrinho de compras
│           ├── orders/         # Histórico de pedidos
│           └── admin/          # Painel administrativo
│
├── docs/                       # Prints e GIF de demonstração
├── docker-compose.yml          # PostgreSQL + pgAdmin
└── README.md
```

---

## ⚙️ Como rodar localmente

### Pré-requisitos

- [Java 17+](https://adoptium.net/)
- [Node.js 18+](https://nodejs.org/)
- [Docker e Docker Compose](https://www.docker.com/)
- [Angular CLI](https://angular.io/cli): `npm install -g @angular/cli`

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/shopflow.git
cd shopflow
```

### 2. Suba o banco de dados

```bash
docker-compose up -d
```

> Isso inicia o PostgreSQL na porta `5432` e o pgAdmin na porta `5050`.

### 3. Configure as variáveis de ambiente do backend

Crie o arquivo `backend/src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shopflow
    username: postgres
    password: postgres

jwt:
  secret: SUA_CHAVE_SECRETA_AQUI_MINIMO_256_BITS
  expiration: 86400000 # 24 horas em ms

cloudinary:
  cloud-name: seu-cloud-name
  api-key: sua-api-key
  api-secret: seu-api-secret
```

### 4. Rode o backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

A API estará disponível em `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 5. Rode o frontend

```bash
cd frontend
npm install
ng serve
```

A aplicação estará disponível em `http://localhost:4200`

---

## 🔐 Credenciais para teste

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@shopflow.com | admin123 |
| Cliente | cliente@shopflow.com | cliente123 |

---

## 🗃️ Modelo do banco de dados

```
Usuario ──< Pedido ──< ItemPedido >── Produto >── Categoria
                                                     │
                                               Avaliacao
                                               (Usuario + Produto)
```

As migrations Flyway estão em `backend/src/main/resources/db/migration/` e são executadas automaticamente na inicialização.

---

## 🧪 Testes

```bash
# Rodar todos os testes
cd backend
./mvnw test

# Relatório de cobertura (JaCoCo)
./mvnw verify
# Relatório gerado em: target/site/jacoco/index.html
```

---

## 🚀 Deploy

| Serviço | Plataforma |
|---|---|
| Backend (API) | [Railway](https://railway.app) |
| Frontend | [Vercel](https://vercel.com) |
| Banco de dados | Railway PostgreSQL |
| Imagens | Cloudinary |

---

## 🔭 Próximos passos

- [ ] Integração com gateway de pagamento (Mercado Pago Sandbox)
- [ ] Notificação por e-mail ao confirmar pedido (JavaMailSender)
- [ ] CI/CD com GitHub Actions
- [ ] Cache com Redis para produtos mais acessados
- [ ] Testes de integração com Testcontainers

---

## 👨‍💻 Autor

Feito com 💙 por **Seu Nome**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-seu--perfil-0077B5?style=flat-square&logo=linkedin)](https://linkedin.com/in/diogomota05)
[![GitHub](https://img.shields.io/badge/GitHub-seu--usuario-181717?style=flat-square&logo=github)](https://github.com/diogomota5)

---

<div align="center">
  <sub>Projeto desenvolvido para fins de portfólio e aprendizado.</sub>
</div>
