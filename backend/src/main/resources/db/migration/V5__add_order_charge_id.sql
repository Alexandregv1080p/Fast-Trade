-- Referência da cobrança no PSP (PagBank) — preenchida quando a Fase 3.1 for ligada.
ALTER TABLE orders ADD COLUMN IF NOT EXISTS charge_id VARCHAR(64);
