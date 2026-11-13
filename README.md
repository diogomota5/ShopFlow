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
  <img width="1917" height="861" alt="TelaLogin" src="https://github.com/user-attachments/assets/4b187099-6d5a-4505-8e85-3022a9449325" />
  <figcaption>Tela de Login</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1917" height="862" alt="TelaCadastro_Shopflow" src="https://github.com/user-attachments/assets/b1555f37-ed04-4e20-843a-c34e06fa02d6" />          
  <figcaption>Tela de Cadastro</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1917" height="855" alt="PaginaInicial" src="https://github.com/user-attachments/assets/3d908552-79be-4322-a307-510f08e2fc81" />
  <figcaption>Página Inicial</figcaption>
</figure>
<br> <br>

<figure>
   <img width="1906" height="487" alt="PaginaInicial1" src="https://github.com/user-attachments/assets/5f0e3452-6ca1-41fc-9445-f45a41c4af3d" />
   <figcapition>Página Inicial 1</figcaption>
</figure>
<br> <br>

<figure>
  <img width="1912" height="856" alt="PaginaInicial2" src="https://github.com/user-attachments/assets/c4d5f552-0ad4-4ec1-b0c1-6e4bcd31d104" />
  <figcapition>Página Inicial 2</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1906" height="607" alt="PaginaInicial3" src="https://github.com/user-attachments/assets/bf2322af-e41e-480b-8918-a651a2f6c064" />
  <figcapition>Página Inicial 3</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1915" height="847" alt="CatalogoDeProdutos" src="https://github.com/user-attachments/assets/48c3b88c-7744-4996-97ed-6a0b40884746" />
  <figcaption>Catálogo de Produtos</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1916" height="826" alt="CarrinhoDeCompras" src="https://github.com/user-attachments/assets/27a668f8-a915-4a8e-8cae-7f98157c52dc" />
  <figcapition>Carrinho de Compras</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1912" height="847" alt="FinalizacaoDeCompra" src="https://github.com/user-attachments/assets/fbe0809c-aba6-4cc5-a8d7-259c66257c86" />
  <figcapition>Finalização de Compra</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1397" height="842" alt="HistoricoDePedidos" src="https://github.com/user-attachments/assets/501c551f-c162-4e50-a021-d540a32ad98c" />
  <figcapition>Histórico de Pedidos</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1917" height="851" alt="PainelAdm_VisaoGeral" src="https://github.com/user-attachments/assets/99b67c73-5924-465a-bdd6-e96045a4c332" />
  <figcapition>Painel Adm Visão Geral</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1912" height="867" alt="CadastroProduto" src="https://github.com/user-attachments/assets/e675b29a-bc34-4236-b04c-c1d9d75f8f5b" />
  <figcapition>Cadastro de Produto</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1917" height="861" alt="PesquisaProduto" src="https://github.com/user-attachments/assets/f8e1af39-99da-4de9-a267-dfce39966459" />
  <figcapition>Pesquisa de Produto</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1916" height="861" alt="CadastroCategoria" src="https://github.com/user-attachments/assets/200419bd-0eaf-4b3f-bb4a-016547ff8e29" />
  <figcapition>Cadastro de Categoria</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1917" height="866" alt="PesquisaCategoria" src="https://github.com/user-attachments/assets/b529994e-6cd8-4f4e-88bf-5b1ffee6804b" />
  <figcapition>Pesquisa de Categoria</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1917" height="862" alt="CadastroCupom" src="https://github.com/user-attachments/assets/78bfbf0a-5abb-4d0d-9377-026518aab6f5" />
  <figcapition>Cadastro de Cupom</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1907" height="857" alt="GestãoDePedidos" src="https://github.com/user-attachments/assets/0b439058-f15f-41d5-8a88-ccd299b628cd" />
  <figcapition>Gestão de Pedidos</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1917" height="862" alt="GestãoDePedidos1" src="https://github.com/user-attachments/assets/0342ce46-5cef-4b6b-9931-c30fdaf72cae" />
  <figcapition>Gestão de Pedidos 1</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1917" height="862" alt="GestãoUsuários" src="https://github.com/user-attachments/assets/4fdddd62-27c0-47c1-b32c-5ac068590eca" />
  <figcapition>Gestão de Usuários</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1917" height="851" alt="GestãoUsuários1" src="https://github.com/user-attachments/assets/90fa9bb1-6206-4e9d-86f5-e70692c1c848" />
  <figcapition>Gestão de Usuários 1</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1901" height="857" alt="GestãoUsuários2" src="https://github.com/user-attachments/assets/cd69f043-7af8-458b-8596-50bdc37bc83f" />
  <figcapition>Gestão de Usuários 2</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1731" height="840" alt="GestãoDePedidos_usuario" src="https://github.com/user-attachments/assets/ed3be4cc-1d72-46d4-8018-099aa66c819a" />
  <figcapition>Gestão de Pedidos User</figcapition>
</figure>
<br> <br>

<figure>
  <img width="1357" height="857" alt="EdiçãoPerfil_usuarios" src="https://github.com/user-attachments/assets/cfe3f4f1-00d6-45ee-b641-d74edd2b9b39" />
  <figcapition>Edição de Usuários User</figcapition>
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
