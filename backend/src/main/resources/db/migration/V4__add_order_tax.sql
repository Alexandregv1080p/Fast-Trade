-- Taxa de troca e frete cobrados no pedido (antes eram só exibição/implícitos no app).
ALTER TABLE orders ADD COLUMN IF NOT EXISTS tax NUMERIC(10,2) NOT NULL DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivery_fee NUMERIC(10,2) NOT NULL DEFAULT 0;
