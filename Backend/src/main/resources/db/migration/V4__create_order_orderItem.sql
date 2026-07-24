-- Garante que a extensão de UUID está ativa no banco
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Criação da tabela de Pedidos (Orders)
CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        user_id UUID NOT NULL,
                        address_id UUID NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        subtotal NUMERIC(10, 2) NOT NULL,
                        discount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
                        total NUMERIC(10, 2) NOT NULL,
                        shipping_zip_code VARCHAR(9) NOT NULL,
                        shipping_street VARCHAR(200) NOT NULL,
                        shipping_number VARCHAR(10) NOT NULL,
                        shipping_complement VARCHAR(100),
                        shipping_neighborhood VARCHAR(100) NOT NULL,
                        shipping_city VARCHAR(100) NOT NULL,
                        shipping_state VARCHAR(2) NOT NULL,
                        tracking_code VARCHAR(50),
                        notes TEXT,
                        created_at TIMESTAMP WITHOUT TIME ZONE,
                        updated_at TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- 2. Criação da tabela de Itens do Pedido (Order Items)
CREATE TABLE order_items (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             order_id UUID NOT NULL,
                             product_id UUID NOT NULL,
                             product_name VARCHAR(200) NOT NULL,
                             unit_price NUMERIC(10, 2) NOT NULL,
                             image_url VARCHAR(500),
                             quantity INTEGER NOT NULL,
                             subtotal NUMERIC(10, 2) NOT NULL,
                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);
