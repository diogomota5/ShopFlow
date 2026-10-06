# 🛒 ShopFlow

Um sistema **Full Stack** de **e-commerce**, desenvolvido para meu portfólio com foco em **boas práticas**, **arquitetura organizada** e **experiência real de projeto** (frontend + backend + banco).

---

## ✨ Visão geral

O ShopFlow permite que o usuário:

- 🔐 **Crie sua conta e faça login** com autenticação JWT
- 🛍️ **Navegue pelo catálogo** de produtos com busca e filtros por categoria e preço
- 🛒 **Monte seu carrinho**, ajustando quantidades em tempo real
- 📦 **Finalize pedidos** e acompanhe o status da compra
- ⭐ **Avalie produtos** que já comprou, com nota e comentário
- 🎟️ **Aplique cupons de desconto** no checkout

E o administrador pode:

- 🗂️ Gerenciar categorias e produtos, com upload de imagens
- 📦 Acompanhar e atualizar o status de todos os pedidos da loja
- 🎟️ Criar e controlar cupons de desconto

---

## 🎯 Objetivo do projeto

Este projeto foi criado para demonstrar, na prática, minha capacidade de:

- Construir uma **API REST** completa e bem estruturada
- Integrar **Frontend (Angular)** com **Backend (Spring Boot)**
- Modelar um banco de dados relacional com relacionamentos reais de um e-commerce
- Persistir dados com **JPA/Hibernate** e versionar o schema com **Flyway**
- Aplicar padrões comuns do mercado (services, guards, interceptors, DTOs, mappers, specifications)
- Integrar serviços externos (upload de imagens com **Cloudinary**)

---

## 🧠 O que eu implementei (destaques)

- ✅ **Arquitetura em camadas** (Controller → Service → Repository)
- ✅ **DTOs** para padronizar requests e responses (ex.: `ProductRequest`, `OrderResponse`)
- ✅ **Mappers com MapStruct** para conversão entre Entity e DTO
- ✅ **Tratamento global de erros** (Global Exception Handler) com resposta padronizada
- ✅ **Autenticação com JWT** + Spring Security, com perfis `USER` e `ADMIN`
- ✅ **Filtros dinâmicos** com Specification (busca por nome, categoria e faixa de preço)
- ✅ **Snapshot de dados históricos** — preço e nome do produto preservados em cada pedido, mesmo que o produto mude depois
- ✅ **Controle de estoque** automático a cada pedido criado ou cancelado
- ✅ **Upload de imagens** via integração com Cloudinary
- ✅ **Rotas protegidas** no Angular (Auth Guard)
- ✅ **Interceptor HTTP** para enviar o token JWT automaticamente
- ✅ Integração completa **Front ↔ API ↔ Banco**

---

## 🛠️ Tecnologias utilizadas

### 🔙 Backend

- ☕ **Java 17**
- 🌱 **Spring Boot 3**
- 🌐 **Spring Web (API REST)**
- 🔐 **Spring Security + JWT**
- 🗄️ **Spring Data JPA / Hibernate**
- 🐘 **PostgreSQL**
- 🔄 **Flyway** (versionamento do banco)
- 🧩 **MapStruct** (mapeamento DTO ↔ Entity)
- ⚙️ **Gradle**
- 🧯 **Global Exception Handler**
- ☁️ **Cloudinary** (upload de imagens)

### 🔜 Frontend

- 🅰️ **Angular**
- 🔁 **HTTP Client**
- 🛡️ **Auth Guard**
- 🧩 **HTTP Interceptor**
- 🧱 Estrutura por módulos/pastas: `core/` e `features/`

---

## 🗄️ Como funciona a comunicação com o banco

O backend utiliza **Spring Data JPA**, onde:

- As **Entities** representam as tabelas (ex.: `Product`, `Order`, `Cart`)
- Os **Repositories** fazem o acesso ao banco (CRUD, consultas e Specifications)
- O **Hibernate** faz o mapeamento objeto-relacional automaticamente
- O **Flyway** versiona e aplica as alterações do schema automaticamente na inicialização
- A configuração de conexão fica no arquivo:

📌 `src/main/resources/application.properties`

---

## 🧭 Estrutura do projeto (organização)

- `src/main/java/...`
  - `controller/` → endpoints REST
  - `service/` → regras de negócio
  - `repository/` → persistência com JPA
  - `specification/` → filtros dinâmicos de busca
  - `entity/` → entidades do banco
  - `dto/request/` e `dto/response/` → objetos de entrada e saída da API
  - `mapper/` → conversão entre Entity e DTO
  - `exception/` → tratamento global de erros
  - `config/` → segurança, JWT e Cloudinary
- `src/main/resources/db/migration/` → migrations versionadas com Flyway
- `frontend/src/app/`
  - `core/` → auth, services, interceptors, guards
  - `features/` → páginas (auth, products, cart, orders, admin)

---

## 🗺️ Modelo do banco de dados

```
User ──< Order ──< OrderItem >── Product >── Category
  │                                  │
  ├──< Cart ──< CartItem             │
  │                                  │
  └──< Assessment >──────────────────┘
              (avaliação de produto)

Coupon ──< Order (cupom aplicado no pedido)
```

---

## 🚀 Como rodar o projeto localmente

### ✅ Pré-requisitos

- ☕ **Java 17+**
- 🟢 **Node.js 18+**
- 🅰️ **Angular CLI**
- 🐳 **Docker e Docker Compose**

### 1️⃣ Suba o banco de dados

Na raiz do projeto:

```bash
docker compose up -d
```

> Isso inicia o PostgreSQL na porta `5432` e o pgAdmin na porta `5050`.

### 2️⃣ Configure as variáveis de ambiente

Crie o arquivo `.env` na raiz do backend com suas credenciais (banco, JWT e Cloudinary). Veja o `.env.example` como referência.

### 3️⃣ Backend (Spring Boot)

Na pasta `backend/`:

```bash
./gradlew bootRun
```

A API estará disponível em `http://localhost:8080`

### 4️⃣ Frontend (Angular)

Na pasta `frontend/`:

```bash
cd frontend
npm install
npm start
```

A aplicação estará disponível em `http://localhost:4200`

---

## 📸 Imagens do Projeto

<figure>
  <img width="1912" height="847" alt="tela de login" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Tela de login</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1912" height="843" alt="catálogo de produtos" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Catálogo de produtos</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1886" height="805" alt="carrinho de compras" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Carrinho de compras</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1888" height="810" alt="checkout" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Checkout - Finalização do pedido</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1878" height="822" alt="histórico de pedidos" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Histórico de pedidos</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1892" height="841" alt="painel admin produtos" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Painel Admin - Produtos</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1886" height="761" alt="painel admin pedidos" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Painel Admin - Pedidos</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1872" height="688" alt="avaliações de produto" src="COLE_AQUI_O_LINK_DA_IMAGEM" />
  <figcaption>Avaliações de produto</figcaption>
</figure>
<br> <br>

---

## 🔭 Próximos passos

- [ ] Integração com gateway de pagamento (Mercado Pago Sandbox)
- [ ] Notificação por e-mail ao confirmar pedido
- [ ] CI/CD com GitHub Actions
- [ ] Deploy em produção (Railway + Vercel)
- [ ] Testes unitários com JUnit 5 + Mockito

---

<div align="center">
  <sub>Projeto desenvolvido para fins de portfólio e aprendizado.</sub>
</div>
