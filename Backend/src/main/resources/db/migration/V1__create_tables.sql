CREATE TABLE usuarios
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    nome       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha      VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    telefone   VARCHAR(20),
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ           DEFAULT NOW(),
    updated_at TIMESTAMPTZ           DEFAULT NOW()
);

CREATE TABLE categorias
(
    id        UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    nome      VARCHAR(100) NOT NULL,
    descricao TEXT         NOT NULL,
    slug      VARCHAR(120) NOT NULL UNIQUE,
    image_url VARCHAR(500),
    ativo     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE produtos
(
    id             UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    nome           VARCHAR(100)   NOT NULL,
    descricao      TEXT           NOT NULL,
    preco          DECIMAL(10, 2) NOT NULL CHECK ( preco > 0 ),
    preco_original DECIMAL(10, 2) NOT NULL CHECK ( preco_original > 0),
    estoque        INTEGER        NOT NULL check ( estoque >= 0 ),
    sku            VARCHAR(50)    NOT NULL UNIQUE,
    image_url      VARCHAR(500),
    ativo          BOOLEAN        NOT NULL DEFAULT TRUE,
    categoria_id   UUID           NOT NULL,
    created_at     TIMESTAMPTZ             DEFAULT NOW(),
    updated_at     TIMESTAMPTZ             DEFAULT NOW(),

    CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id) ON DELETE RESTRICT
);

CREATE TABLE carrinhos
(
    id         BIGSERIAL PRIMARY KEY,
    usuario_id UUID NOT NULL UNIQUE,
    updated_at TIMESTAMPTZ,

    CONSTRAINT fk_carrinho_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE item_carrinho
(
    id          BIGSERIAL PRIMARY KEY,
    carrinho_id BIGINT      NOT NULL,
    produto_id  UUID        NOT NULL,
    quantidade  INTEGER     NOT NULL CHECK ( quantidade > 0 ),
    add_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (carrinho_id, produto_id),

    CONSTRAINT fk_item_carrinho FOREIGN KEY (carrinho_id) REFERENCES carrinhos (id) ON DELETE CASCADE,

    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produtos (id) ON DELETE RESTRICT
);

CREATE TABLE enderecos
(
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  UUID         NOT NULL,
    apelido     VARCHAR(50)  NOT NULL,
    cep         VARCHAR(9)   NOT NULL,
    logradouro  VARCHAR(200) NOT NULL,
    numero      VARCHAR(10)  NOT NULL,
    complemento VARCHAR(50),
    bairro      VARCHAR(100) NOT NULL,
    cidade      VARCHAR(100) NOT NULL,
    estado      CHAR(2)      NOT NULL,
    principal   BOOLEAN      NOT NULL,

    CONSTRAINT fk_endereco_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE RESTRICT
);

CREATE TYPE pedido_status AS ENUM (
'PENDENTE',
'PAGO',
'ENVIADO',
'ENTREGUE',
'CANCELADO'
);

CREATE TABLE pedidos
(
    id              BIGSERIAL       PRIMARY KEY,
    usuario_id      UUID            NOT NULL,
    status          pedido_status   NOT NULL DEFAULT 'PENDENTE',
    total           DECIMAL(10, 2)  NOT NULL CHECK (total >= 0),
    subtotal        DECIMAL(10, 2)  NOT NULL CHECK (subtotal >= 0),
    desconto        DECIMAL(10, 2)           DEFAULT 0.00 CHECK (desconto >= 0),
    codigo_rastreio VARCHAR(50),
    observacao      TEXT,
    endereco_id     BIGINT          NOT NULL,
    created_at      TIMESTAMPTZ              DEFAULT NOW(),
    updated_at      TIMESTAMPTZ              DEFAULT NOW(),

    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id),

    CONSTRAINT fk_pedido_endereco
        FOREIGN KEY (endereco_id) REFERENCES enderecos (id)
);

CREATE TABLE itens_pedido
(
    id             BIGSERIAL PRIMARY KEY,
    pedido_id      BIGINT         NOT NULL,
    produto_id     UUID           NOT NULL,
    nome_produto   VARCHAR(100)   NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL CHECK (preco_unitario >= 0),
    image_url      VARCHAR(500),
    quantidade     INTEGER        NOT NULL CHECK ( quantidade > 0 ),
    subtotal       DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0 ),

    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id) ON DELETE CASCADE,

    CONSTRAINT fk_item_pedido_produto FOREIGN KEY (produto_id) REFERENCES produtos (id) ON DELETE RESTRICT
);

CREATE TABLE avaliacoes
(
    id         BIGSERIAL PRIMARY KEY,
    usuario_id UUID        NOT NULL,
    produto_id UUID        NOT NULL,
    nota       INTEGER     NOT NULL CHECK ( nota >= 1 AND nota <= 5),
    titulo     VARCHAR(100),
    comentario TEXT,
    verificado BOOLEAN              DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (usuario_id, produto_id),

    CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE RESTRICT,

    CONSTRAINT fk_avaliacao_produto FOREIGN KEY (produto_id) REFERENCES produtos (id) ON DELETE RESTRICT
);

CREATE TYPE tipo_desconto AS ENUM (
'PERCENTUAL',
'FIXO'
);

CREATE TABLE cupons
(
    id                  BIGSERIAL PRIMARY KEY,
    codigo              VARCHAR(30)    NOT NULL UNIQUE,
    tipo_desconto       tipo_desconto  NOT NULL DEFAULT 'FIXO',
    valor               DECIMAL(10, 2) NOT NULL CHECK ( valor >= 0 ),
    valor_minimo_pedido DECIMAL(10, 2) NOT NULL CHECK (valor_minimo_pedido >= 0),
    limite_usos         INTEGER CHECK (limite_usos IS NULL OR limite_usos >= 0),
    usos_atuais         INTEGER                 DEFAULT 0 CHECK (usos_atuais >= 0),
    data_inicio         DATE           NOT NULL,
    data_fim            DATE           NOT NULL CHECK (data_fim >= data_inicio),
    ativo               BOOLEAN        NOT NULL DEFAULT FALSE
);