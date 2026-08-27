-- Cupom aplicado pelo usuário no carrinho (validado no servidor).
ALTER TABLE users ADD COLUMN IF NOT EXISTS coupon_code VARCHAR(40);
