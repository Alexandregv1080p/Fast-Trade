-- Idempotência do fechamento de pedido (Fase 5.7): evita pedido/cobrança duplicada
-- em double-tap ou retry. UNIQUE (no Postgres, múltiplos NULL são permitidos).
ALTER TABLE orders ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(64);
ALTER TABLE orders ADD CONSTRAINT uq_orders_idempotency_key UNIQUE (idempotency_key);
