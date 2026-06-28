-- ============================================================
--  FAST TRADE — SEED DE DADOS
-- ============================================================

-- ── ADMIN USERS ──────────────────────────────────────────────
INSERT INTO admin_users (name, email, password, role, created_at, updated_at)
VALUES
  ('Administrador',  'admin@fasttrade.com',  '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', 'ADMIN',   NOW(), NOW()),
  ('Carlos Gerente', 'carlos@fasttrade.com', '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', 'MANAGER', NOW(), NOW()),
  ('Ana Suporte',    'ana@fasttrade.com',    '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', 'SUPPORT', NOW(), NOW())
ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password;

-- ── USERS ─────────────────────────────────────────────────────
INSERT INTO users (name, email, password, cpf_cnpj, phone, birth_date, gender, score, trades, blocked,
                   address_street, address_city, address_state, address_zip, created_at, updated_at)
VALUES
  ('Joao Silva',      'joao@email.com',     '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '111.222.333-44', '(11) 91111-1111', '1990-05-15', 'M', 95, 520,  false, 'Rua das Flores, 100 - Vila Madalena',  'São Paulo',       'SP', '01310-100', NOW() - INTERVAL '120 days', NOW()),
  ('Maria Oliveira',  'maria@email.com',    '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '222.333.444-55', '(21) 92222-2222', '1988-09-22', 'F', 92, 200,  false, 'Av. Brasil, 450 - Centro',             'Rio de Janeiro',  'RJ', '20040-020', NOW() - INTERVAL '95 days',  NOW()),
  ('Pedro Santos',    'pedro@email.com',    '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '333.444.555-66', '(31) 93333-3333', '1995-02-10', 'M', 60,  50,  false, 'Rua Minas Gerais, 77 - Savassi',       'Belo Horizonte',  'MG', '30130-110', NOW() - INTERVAL '80 days',  NOW()),
  ('Fernanda Costa',  'fernanda@email.com', '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '444.555.666-77', '(41) 94444-4444', '1993-11-30', 'F', 78,  90,  false, 'Rua XV de Nov., 200 - Centro',         'Curitiba',        'PR', '80020-310', NOW() - INTERVAL '60 days',  NOW()),
  ('Lucas Mendes',    'lucas@email.com',    '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '555.666.777-88', '(51) 95555-5555', '1997-07-07', 'M', 45,  30,  false, 'Av. Ipiranga, 1200 - Azenha',          'Porto Alegre',    'RS', '90160-091', NOW() - INTERVAL '45 days',  NOW()),
  ('Beatriz Lima',    'beatriz@email.com',  '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '666.777.888-99', '(71) 96666-6666', '1991-03-18', 'F', 55,  60,  false, 'Rua Chile, 310 - Comércio',            'Salvador',        'BA', '40020-900', NOW() - INTERVAL '30 days',  NOW()),
  ('Rafael Rocha',    'rafael@email.com',   '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '777.888.999-00', '(85) 97777-7777', '1994-08-25', 'M', 30,  10,  true,  'Av. Beira Mar, 500 - Meireles',        'Fortaleza',       'CE', '60165-121', NOW() - INTERVAL '20 days',  NOW()),
  ('Camila Alves',    'camila@email.com',   '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '888.999.000-11', '(62) 98888-8888', '1996-12-05', 'F', 70,  80,  false, 'Rua 44, 900 - Setor Bueno',            'Goiânia',         'GO', '74055-110', NOW() - INTERVAL '15 days',  NOW()),
  ('Thiago Ferreira', 'thiago@email.com',   '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '999.000.111-22', '(11) 99999-9999', '1992-04-20', 'M', 88, 310,  false, 'Rua Augusta, 2000 - Consolação',       'São Paulo',       'SP', '01305-100', NOW() - INTERVAL '10 days',  NOW()),
  ('Juliana Pereira', 'juliana@email.com',  '$2b$10$KhRnv9SS9EgKl4.Czy/lUOgQNSvlTrb15w0734Cr38SJY44mCp4W.', '000.111.222-33', '(11) 90000-0000', '1999-08-12', 'F', 40,  15,  false, 'Al. Santos, 800 - Jardins',            'São Paulo',       'SP', '01418-100', NOW() - INTERVAL '5 days',   NOW())
ON CONFLICT (email) DO UPDATE SET
  password       = EXCLUDED.password,
  address_street = EXCLUDED.address_street,
  trades         = EXCLUDED.trades,
  score          = EXCLUDED.score;

-- ── CATEGORIES ────────────────────────────────────────────────
INSERT INTO categories (name, slug) SELECT 'Eletrônicos',    'eletronicos'    WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='eletronicos');
INSERT INTO categories (name, slug) SELECT 'Roupas',         'roupas'         WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='roupas');
INSERT INTO categories (name, slug) SELECT 'Casa e Jardim',  'casa-e-jardim'  WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='casa-e-jardim');
INSERT INTO categories (name, slug) SELECT 'Esportes',       'esportes'       WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='esportes');
INSERT INTO categories (name, slug) SELECT 'Livros',         'livros'         WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='livros');
INSERT INTO categories (name, slug) SELECT 'Beleza',         'beleza'         WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='beleza');
INSERT INTO categories (name, slug) SELECT 'Games',          'games'          WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='games');
INSERT INTO categories (name, slug) SELECT 'Alimentos',      'alimentos'      WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug='alimentos');

-- ── SUBCATEGORIES ─────────────────────────────────────────────
INSERT INTO subcategories (name, slug, category_id) SELECT 'Smartphones',       'smartphones',    (SELECT id FROM categories WHERE slug='eletronicos'   LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='smartphones');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Notebooks',         'notebooks',      (SELECT id FROM categories WHERE slug='eletronicos'   LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='notebooks');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Fones de Ouvido',   'fones-ouvido',   (SELECT id FROM categories WHERE slug='eletronicos'   LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='fones-ouvido');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Tablets',           'tablets',        (SELECT id FROM categories WHERE slug='eletronicos'   LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='tablets');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Smart TVs',         'smart-tvs',      (SELECT id FROM categories WHERE slug='eletronicos'   LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='smart-tvs');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Smartwatches',      'smartwatches',   (SELECT id FROM categories WHERE slug='eletronicos'   LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='smartwatches');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Câmeras',           'cameras',        (SELECT id FROM categories WHERE slug='eletronicos'   LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='cameras');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Camisetas',         'camisetas',      (SELECT id FROM categories WHERE slug='roupas'        LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='camisetas');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Calças',            'calcas',         (SELECT id FROM categories WHERE slug='roupas'        LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='calcas');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Tênis',             'tenis',          (SELECT id FROM categories WHERE slug='roupas'        LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='tenis');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Moletons',          'moletons',       (SELECT id FROM categories WHERE slug='roupas'        LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='moletons');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Sofás',             'sofas',          (SELECT id FROM categories WHERE slug='casa-e-jardim' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='sofas');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Iluminação',        'iluminacao',     (SELECT id FROM categories WHERE slug='casa-e-jardim' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='iluminacao');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Cadeiras',          'cadeiras',       (SELECT id FROM categories WHERE slug='casa-e-jardim' LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='cadeiras');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Suplementos',       'suplementos',    (SELECT id FROM categories WHERE slug='esportes'      LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='suplementos');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Bicicletas',        'bicicletas',     (SELECT id FROM categories WHERE slug='esportes'      LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='bicicletas');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Equipamentos',      'equipamentos',   (SELECT id FROM categories WHERE slug='esportes'      LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='equipamentos');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Ficção',            'ficcao',         (SELECT id FROM categories WHERE slug='livros'        LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='ficcao');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Técnicos',          'tecnicos',       (SELECT id FROM categories WHERE slug='livros'        LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='tecnicos');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Consoles',          'consoles',       (SELECT id FROM categories WHERE slug='games'         LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='consoles');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Jogos',             'jogos',          (SELECT id FROM categories WHERE slug='games'         LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='jogos');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Perfumaria',        'perfumaria',     (SELECT id FROM categories WHERE slug='beleza'        LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='perfumaria');
INSERT INTO subcategories (name, slug, category_id) SELECT 'Orgânicos',         'organicos',      (SELECT id FROM categories WHERE slug='alimentos'     LIMIT 1) WHERE NOT EXISTS (SELECT 1 FROM subcategories WHERE slug='organicos');

-- ── PRODUCTS ─────────────────────────────────────────────────
-- Eletrônicos — Smartphones
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'iPhone 15 128GB', 'Smartphone Apple com chip A16 Bionic, câmera 48MP, Dynamic Island e USB-C.', 5999.00, 15, true,
  'https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=400&h=400&fit=crop',
  'APL-IP15-128', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='smartphones' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='iPhone 15 128GB');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Samsung Galaxy S24', 'Android flagship com câmera 200MP, Galaxy AI e tela AMOLED 120Hz.', 4299.00, 20, true,
  'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400&h=400&fit=crop',
  'SAM-GS24-256', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='smartphones' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Samsung Galaxy S24');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Motorola Edge 40 Pro', 'Snapdragon 8 Gen 2, carregamento 125W, câmera 50MP OIS.', 2799.00, 25, true,
  'https://images.unsplash.com/photo-1567581935884-3349723552ca?w=400&h=400&fit=crop',
  'MOT-E40P-256', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='smartphones' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Motorola Edge 40 Pro');

-- Eletrônicos — Notebooks
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Notebook Dell XPS 15', 'Intel Core i7-13700H, 16GB DDR5, SSD 512GB, tela OLED 3.5K.', 7499.00, 8, true,
  'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop',
  'DEL-XPS15-512', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='notebooks' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Notebook Dell XPS 15');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'MacBook Air M3', 'Apple M3, 8GB RAM unificada, SSD 256GB, tela Liquid Retina 13".', 9199.00, 5, true,
  'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop',
  'APL-MBA-M3-256', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='notebooks' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='MacBook Air M3');

-- Eletrônicos — Fones
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'AirPods Pro 2', 'Cancelamento ativo de ruído, modo Transparência adaptativo, chip H2.', 1899.00, 30, true,
  'https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=400&h=400&fit=crop',
  'APL-APP2', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='fones-ouvido' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='AirPods Pro 2');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Sony WH-1000XM5', 'Fone over-ear com melhor cancelamento de ruído da categoria, 30h de bateria.', 1699.00, 18, true,
  'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop',
  'SNY-WH1000XM5', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='fones-ouvido' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Sony WH-1000XM5');

-- Eletrônicos — Tablets
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'iPad Air M2', 'Apple M2, tela 11" Liquid Retina, suporte Apple Pencil Pro.', 5499.00, 10, true,
  'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop',
  'APL-IPAD-AIR-M2', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='tablets' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='iPad Air M2');

-- Eletrônicos — Smart TVs
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Smart TV Samsung 55" QLED', '4K, 120Hz, Quantum Processor, HDR10+, Gaming Hub.', 3299.00, 12, true,
  'https://images.unsplash.com/photo-1593359677879-a4bb92f4834c?w=400&h=400&fit=crop',
  'SAM-TV55-QLED', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='smart-tvs' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Smart TV Samsung 55" QLED');

-- Eletrônicos — Smartwatches
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Apple Watch Series 9', 'GPS, tela Always-On Retina, monitoramento de saúde avançado, chip S9.', 2799.00, 22, true,
  'https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400&h=400&fit=crop',
  'APL-AWS9-45MM', 'NOVO',
  (SELECT id FROM categories WHERE slug='eletronicos' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='smartwatches' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Apple Watch Series 9');

-- Roupas — Camisetas
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Camiseta Polo Básica', 'Algodão 100% pima, slim fit, disponível em 8 cores.', 89.90, 100, true,
  'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400&h=400&fit=crop',
  'ROU-CAM-POLO-M', 'NOVO',
  (SELECT id FROM categories WHERE slug='roupas' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='camisetas' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Camiseta Polo Básica');

-- Roupas — Calças
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Calça Jeans Slim', 'Denim premium lavagem stone, corte slim, elastano 2%.', 199.90, 60, true,
  'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=400&fit=crop',
  'ROU-CAL-JEANS-42', 'NOVO',
  (SELECT id FROM categories WHERE slug='roupas' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='calcas' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Calça Jeans Slim');

-- Roupas — Tênis
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Tênis Nike Air Max 270', 'Amortecimento Air de 270°, solado EVA, cabedal em mesh respirável.', 599.00, 40, true,
  'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',
  'NIK-AM270-42', 'NOVO',
  (SELECT id FROM categories WHERE slug='roupas' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='tenis' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Tênis Nike Air Max 270');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Tênis Adidas Ultraboost 23', 'Tecnologia BOOST, cabedal Primeknit+, Torsion System.', 699.00, 35, true,
  'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400&h=400&fit=crop',
  'ADI-UB23-43', 'NOVO',
  (SELECT id FROM categories WHERE slug='roupas' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='tenis' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Tênis Adidas Ultraboost 23');

-- Roupas — Moletons
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Moletom Básico Premium', 'Fleece 320g, forro felpudo, ribana na barra e punhos.', 249.90, 80, true,
  'https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=400&h=400&fit=crop',
  'ROU-MOL-PREM-G', 'NOVO',
  (SELECT id FROM categories WHERE slug='roupas' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='moletons' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Moletom Básico Premium');

-- Casa e Jardim — Sofás
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Sofá 3 Lugares Veludo', 'Estrutura em MDF eucalipto, tecido veludo premium, pés madeira.', 1499.00, 7, true,
  'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop',
  'MOV-SOF3-VEL', 'NOVO',
  (SELECT id FROM categories WHERE slug='casa-e-jardim' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='sofas' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Sofá 3 Lugares Veludo');

-- Casa e Jardim — Iluminação
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Luminária LED Smart', 'Compatível com Alexa e Google Assistant, 16M cores, 1200 lúmens.', 249.00, 50, true,
  'https://images.unsplash.com/photo-1524484485831-a92ffc0de03f?w=400&h=400&fit=crop',
  'ILU-LED-SMART', 'NOVO',
  (SELECT id FROM categories WHERE slug='casa-e-jardim' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='iluminacao' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Luminária LED Smart');

-- Casa e Jardim — Cadeiras
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Cadeira Gamer RGB', 'Apoio lombar ajustável, braços 4D, inclinação 135°, capacidade 150kg.', 899.00, 15, true,
  'https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=400&h=400&fit=crop',
  'MOV-CAD-GAMER', 'NOVO',
  (SELECT id FROM categories WHERE slug='casa-e-jardim' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='cadeiras' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Cadeira Gamer RGB');

-- Esportes — Suplementos
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Whey Protein 1kg', 'Proteína concentrada 80%, sabor baunilha, 33 porções. Sem lactose.', 129.90, 80, true,
  'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400&h=400&fit=crop',
  'SUP-WHEY-1KG-BAN', 'NOVO',
  (SELECT id FROM categories WHERE slug='esportes' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='suplementos' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Whey Protein 1kg');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Creatina Monohidratada 300g', 'Creatina pura 99,9%, sem adição de açúcar, 60 doses.', 79.90, 60, true,
  'https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=400&h=400&fit=crop',
  'SUP-CREAT-300G', 'NOVO',
  (SELECT id FROM categories WHERE slug='esportes' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='suplementos' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Creatina Monohidratada 300g');

-- Esportes — Bicicletas
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Bicicleta Speed 21v', 'Quadro alumínio 700C, câmbio Shimano Tourney, freios V-Brake.', 1299.00, 12, true,
  'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop',
  'ESP-BIKE-21V', 'NOVO',
  (SELECT id FROM categories WHERE slug='esportes' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='bicicletas' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Bicicleta Speed 21v');

-- Games — Consoles
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'PlayStation 5 Slim', 'Console Sony PS5 Slim, SSD 1TB, leitor de disco, DualSense incluso.', 3799.00, 10, true,
  'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=400&h=400&fit=crop',
  'SNY-PS5-SLIM', 'NOVO',
  (SELECT id FROM categories WHERE slug='games' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='consoles' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='PlayStation 5 Slim');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Xbox Series X', 'Console Microsoft 4K, 120fps, SSD 1TB, retrocompatível com 4 gerações.', 3499.00, 8, true,
  'https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=400&h=400&fit=crop',
  'MSF-XSX-1TB', 'NOVO',
  (SELECT id FROM categories WHERE slug='games' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='consoles' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Xbox Series X');

-- Livros — Técnicos
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Clean Code', 'Robert C. Martin. Boas práticas de programação para código limpo e manutenível.', 79.90, 35, true,
  'https://images.unsplash.com/photo-1589998059171-988d887df646?w=400&h=400&fit=crop',
  'LIV-CC-MARTIN', 'USADO_BOM',
  (SELECT id FROM categories WHERE slug='livros' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='tecnicos' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Clean Code');

INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'Design Patterns', 'Gang of Four. Os 23 padrões de projeto essenciais para desenvolvimento.', 99.90, 20, true,
  'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=400&fit=crop',
  'LIV-DP-GOF', 'USADO_BOM',
  (SELECT id FROM categories WHERE slug='livros' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='tecnicos' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Design Patterns');

-- Livros — Ficção
INSERT INTO products (name, description, price, stock, is_active, image_url, sku, condition, category_id, subcategory_id, created_at, updated_at)
SELECT 'O Poder do Hábito', 'Charles Duhigg. Como hábitos funcionam e como mudá-los. Bestseller do NYT.', 49.90, 45, true,
  'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=400&fit=crop',
  'LIV-POH-DUHIGG', 'NOVO',
  (SELECT id FROM categories WHERE slug='livros' LIMIT 1),
  (SELECT id FROM subcategories WHERE slug='ficcao' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='O Poder do Hábito');

-- ── ATUALIZA image_url de produtos existentes (sem imagem) ───
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=400&h=400&fit=crop'
  WHERE name = 'iPhone 15 128GB'   AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400&h=400&fit=crop'
  WHERE name = 'Samsung Galaxy S24' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop'
  WHERE name = 'Notebook Dell XPS 15' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop'
  WHERE name = 'MacBook Air M3' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=400&h=400&fit=crop'
  WHERE name = 'AirPods Pro 2' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400&h=400&fit=crop'
  WHERE name = 'Camiseta Polo Basica' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=400&fit=crop'
  WHERE name = 'Calca Jeans Slim' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop'
  WHERE name = 'Tenis Nike Air Max 270' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop'
  WHERE name = 'Sofa 3 Lugares Veludo' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1524484485831-a92ffc0de03f?w=400&h=400&fit=crop'
  WHERE name = 'Luminaria LED Smart' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400&h=400&fit=crop'
  WHERE name = 'Whey Protein 1kg' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop'
  WHERE name = 'Bicicleta Speed 21v' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1589998059171-988d887df646?w=400&h=400&fit=crop'
  WHERE name = 'Clean Code' AND (image_url IS NULL OR image_url = '');
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=400&fit=crop'
  WHERE name = 'O Poder do Habito' AND (image_url IS NULL OR image_url = '');

-- ── ORDERS (joao@email.com — histórico completo) ──────────────
-- Pedidos existentes
INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0001', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 5999.00, 'DELIVERED',  'PIX',         NOW() - INTERVAL '90 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0001');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0008', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 129.90, 'DELIVERED',  'PIX',         NOW() - INTERVAL '60 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0008');

-- Novos pedidos para joao
INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0020', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 9199.00, 'DELIVERED',  'CREDIT_CARD', NOW() - INTERVAL '45 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0020');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0021', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 1699.00, 'DELIVERED',  'PIX',         NOW() - INTERVAL '30 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0021');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0022', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 699.00,  'TRANSIT',    'PIX',         NOW() - INTERVAL '5 days',  NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0022');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0023', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 249.00,  'SEPARATING', 'CREDIT_CARD', NOW() - INTERVAL '2 days',  NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0023');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0024', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 4299.00, 'CONFIRMED',  'PIX',         NOW() - INTERVAL '1 days',  NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0024');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0025', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 3799.00, 'PENDING',    'BOLETO',      NOW() - INTERVAL '12 hours',NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0025');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0026', (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1), 'Joao Silva', 599.00,  'CANCELLED',  'PIX',         NOW() - INTERVAL '20 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0026');

-- Outros usuários
INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0002', (SELECT id FROM users WHERE email='maria@email.com' LIMIT 1),    'Maria Oliveira', 4299.00, 'DELIVERED',  'CREDIT_CARD', NOW() - INTERVAL '85 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0002');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0003', (SELECT id FROM users WHERE email='pedro@email.com' LIMIT 1),    'Pedro Santos',    599.00, 'TRANSIT',    'PIX',         NOW() - INTERVAL '10 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0003');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0004', (SELECT id FROM users WHERE email='fernanda@email.com' LIMIT 1), 'Fernanda Costa', 1899.00, 'CONFIRMED',  'CREDIT_CARD', NOW() - INTERVAL '5 days',  NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0004');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0005', (SELECT id FROM users WHERE email='lucas@email.com' LIMIT 1),    'Lucas Mendes',    199.90, 'PENDING',    'BOLETO',      NOW() - INTERVAL '2 days',  NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0005');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0006', (SELECT id FROM users WHERE email='beatriz@email.com' LIMIT 1),  'Beatriz Lima',    249.00, 'DELIVERED',  'PIX',         NOW() - INTERVAL '20 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0006');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0007', (SELECT id FROM users WHERE email='camila@email.com' LIMIT 1),   'Camila Alves',   7499.00, 'SEPARATING', 'CREDIT_CARD', NOW() - INTERVAL '3 days',  NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0007');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0009', (SELECT id FROM users WHERE email='maria@email.com' LIMIT 1),    'Maria Oliveira', 1299.00, 'CANCELLED',  'CREDIT_CARD', NOW() - INTERVAL '40 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0009');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0010', (SELECT id FROM users WHERE email='pedro@email.com' LIMIT 1),    'Pedro Santos',     79.90, 'REFUNDED',   'PIX',         NOW() - INTERVAL '30 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0010');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0011', (SELECT id FROM users WHERE email='fernanda@email.com' LIMIT 1), 'Fernanda Costa',   89.90, 'DEVOLUTION', 'CREDIT_CARD', NOW() - INTERVAL '25 days', NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0011');

INSERT INTO orders (order_number, user_id, user_name, total, status, payment_method, created_at, updated_at)
SELECT 'FT-2024-0012', (SELECT id FROM users WHERE email='camila@email.com' LIMIT 1),   'Camila Alves',   1499.00, 'DELIVERED',  'BOLETO',      NOW() - INTERVAL '1 days',  NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE order_number='FT-2024-0012');

-- ── ORDER ITEMS ───────────────────────────────────────────────
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0001' LIMIT 1), (SELECT id FROM products WHERE name='iPhone 15 128GB'      LIMIT 1), 'iPhone 15 128GB',      1, 5999.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0001' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0008' LIMIT 1), (SELECT id FROM products WHERE name='Whey Protein 1kg'      LIMIT 1), 'Whey Protein 1kg',     1,  129.90 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0008' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0020' LIMIT 1), (SELECT id FROM products WHERE name='MacBook Air M3'        LIMIT 1), 'MacBook Air M3',       1, 9199.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0020' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0021' LIMIT 1), (SELECT id FROM products WHERE name='Sony WH-1000XM5'       LIMIT 1), 'Sony WH-1000XM5',      1, 1699.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0021' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0022' LIMIT 1), (SELECT id FROM products WHERE name='Tênis Adidas Ultraboost 23' LIMIT 1), 'Tênis Adidas Ultraboost 23', 1, 699.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0022' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0023' LIMIT 1), (SELECT id FROM products WHERE name='Luminária LED Smart'   LIMIT 1), 'Luminária LED Smart',  1,  249.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0023' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0024' LIMIT 1), (SELECT id FROM products WHERE name='Samsung Galaxy S24'    LIMIT 1), 'Samsung Galaxy S24',   1, 4299.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0024' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0025' LIMIT 1), (SELECT id FROM products WHERE name='PlayStation 5 Slim'    LIMIT 1), 'PlayStation 5 Slim',   1, 3799.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0025' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0026' LIMIT 1), (SELECT id FROM products WHERE name='Tênis Nike Air Max 270' LIMIT 1), 'Tênis Nike Air Max 270', 1, 599.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0026' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0002' LIMIT 1), (SELECT id FROM products WHERE name='Samsung Galaxy S24'    LIMIT 1), 'Samsung Galaxy S24',   1, 4299.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0002' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0003' LIMIT 1), (SELECT id FROM products WHERE name='Tênis Nike Air Max 270' LIMIT 1), 'Tênis Nike Air Max 270', 1, 599.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0003' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0004' LIMIT 1), (SELECT id FROM products WHERE name='AirPods Pro 2'         LIMIT 1), 'AirPods Pro 2',        1, 1899.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0004' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0005' LIMIT 1), (SELECT id FROM products WHERE name='Calça Jeans Slim'      LIMIT 1), 'Calça Jeans Slim',     1,  199.90 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0005' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0006' LIMIT 1), (SELECT id FROM products WHERE name='Luminária LED Smart'   LIMIT 1), 'Luminária LED Smart',  1,  249.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0006' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0007' LIMIT 1), (SELECT id FROM products WHERE name='Notebook Dell XPS 15'  LIMIT 1), 'Notebook Dell XPS 15', 1, 7499.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0007' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0009' LIMIT 1), (SELECT id FROM products WHERE name='Bicicleta Speed 21v'   LIMIT 1), 'Bicicleta Speed 21v',  1, 1299.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0009' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0010' LIMIT 1), (SELECT id FROM products WHERE name='Clean Code'            LIMIT 1), 'Clean Code',           1,   79.90 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0010' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0011' LIMIT 1), (SELECT id FROM products WHERE name='Camiseta Polo Básica'  LIMIT 1), 'Camiseta Polo Básica', 1,   89.90 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0011' LIMIT 1));
INSERT INTO order_items (order_id, product_id, product_name, quantity, price)
SELECT (SELECT id FROM orders WHERE order_number='FT-2024-0012' LIMIT 1), (SELECT id FROM products WHERE name='Sofá 3 Lugares Veludo' LIMIT 1), 'Sofá 3 Lugares Veludo', 1, 1499.00 WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id=(SELECT id FROM orders WHERE order_number='FT-2024-0012' LIMIT 1));

-- ── WALLETS ───────────────────────────────────────────────────
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1),     'Joao Silva',      520.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='maria@email.com' LIMIT 1),    'Maria Oliveira',  820.50, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='maria@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='pedro@email.com' LIMIT 1),    'Pedro Santos',     45.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='pedro@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='fernanda@email.com' LIMIT 1), 'Fernanda Costa',  200.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='fernanda@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='lucas@email.com' LIMIT 1),    'Lucas Mendes',     12.90, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='lucas@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='beatriz@email.com' LIMIT 1),  'Beatriz Lima',    640.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='beatriz@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='rafael@email.com' LIMIT 1),   'Rafael Rocha',      0.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='rafael@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='camila@email.com' LIMIT 1),   'Camila Alves',    175.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='camila@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='thiago@email.com' LIMIT 1),   'Thiago Ferreira', 310.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='thiago@email.com' LIMIT 1));
INSERT INTO wallets (user_id, user_name, balance, updated_at)
SELECT (SELECT id FROM users WHERE email='juliana@email.com' LIMIT 1),  'Juliana Pereira',  15.00, NOW() WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id=(SELECT id FROM users WHERE email='juliana@email.com' LIMIT 1));

-- Atualiza saldo de joao
UPDATE wallets SET balance = 520.00 WHERE user_id = (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1);

-- ── SELLERS → atribui vendedores reais aos produtos ─────────
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='joao@email.com'     LIMIT 1) WHERE name IN ('iPhone 15 128GB','AirPods Pro 2','Apple Watch Series 9','iPad Air M2') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='thiago@email.com'   LIMIT 1) WHERE name IN ('Samsung Galaxy S24','Smart TV Samsung 55" QLED','PlayStation 5 Slim','Xbox Series X') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='maria@email.com'    LIMIT 1) WHERE name IN ('MacBook Air M3','Notebook Dell XPS 15','Sony WH-1000XM5') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='fernanda@email.com' LIMIT 1) WHERE name IN ('Camiseta Polo Básica','Calça Jeans Slim','Moletom Básico Premium') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='lucas@email.com'    LIMIT 1) WHERE name IN ('Tênis Nike Air Max 270','Tênis Adidas Ultraboost 23') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='beatriz@email.com'  LIMIT 1) WHERE name IN ('Sofá 3 Lugares Veludo','Luminária LED Smart','Cadeira Gamer RGB') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='camila@email.com'   LIMIT 1) WHERE name IN ('Whey Protein 1kg','Creatina Monohidratada 300g','Bicicleta Speed 21v') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='pedro@email.com'    LIMIT 1) WHERE name IN ('Clean Code','Design Patterns','O Poder do Hábito') AND seller_id IS NULL;
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='motorola@email.com' LIMIT 1) WHERE name IN ('Motorola Edge 40 Pro') AND seller_id IS NULL;
-- fallback: produtos sem seller -> joao
UPDATE products SET seller_id = (SELECT id FROM users WHERE email='joao@email.com'     LIMIT 1) WHERE seller_id IS NULL;

-- ── TRANSACTIONS (joao@email.com) ────────────────────────────
-- user_id é VARCHAR na entidade — cast ::text obrigatório
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'COMPLETED',5999.00,'Compra #FT-2024-0001','PIX',        NOW()-INTERVAL '90 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0001');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','CREDIT','COMPLETED', 350.00,'Recarga de saldo v1',     'PIX',        NOW()-INTERVAL '85 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Recarga de saldo v1');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'COMPLETED', 129.90,'Compra #FT-2024-0008','PIX',        NOW()-INTERVAL '60 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0008');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'COMPLETED',9199.00,'Compra #FT-2024-0020','CREDIT_CARD',NOW()-INTERVAL '45 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0020');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','CREDIT','COMPLETED', 600.00,'Recarga de saldo v2',     'PIX',        NOW()-INTERVAL '35 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Recarga de saldo v2');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'COMPLETED',1699.00,'Compra #FT-2024-0021','PIX',        NOW()-INTERVAL '30 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0021');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'CANCELLED',  599.00,'Compra #FT-2024-0026','PIX',        NOW()-INTERVAL '20 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0026');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','CREDIT','COMPLETED',  599.00,'Estorno #FT-2024-0026','PIX',       NOW()-INTERVAL '18 days' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Estorno #FT-2024-0026');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'PENDING',    699.00,'Compra #FT-2024-0022','PIX',        NOW()-INTERVAL '5 days'  WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0022');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'PENDING',    249.00,'Compra #FT-2024-0023','CREDIT_CARD',NOW()-INTERVAL '2 days'  WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0023');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'PENDING',   4299.00,'Compra #FT-2024-0024','PIX',        NOW()-INTERVAL '1 days'  WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0024');
INSERT INTO transactions (user_id, user_name, type, status, amount, description, payment_method, created_at)
SELECT (SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text,'Joao Silva','DEBIT', 'PENDING',   3799.00,'Compra #FT-2024-0025','BOLETO',     NOW()-INTERVAL '12 hours' WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE user_id=(SELECT id FROM users WHERE email='joao@email.com' LIMIT 1)::text AND description='Compra #FT-2024-0025');
