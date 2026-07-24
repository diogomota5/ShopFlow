CREATE TABLE coupons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_value NUMERIC(10, 2) NOT NULL,
    discount_type VARCHAR(20) NOT NULL,
    min_purchase_value NUMERIC(10, 2) DEFAULT 0.00,
    expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
    max_uses INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);