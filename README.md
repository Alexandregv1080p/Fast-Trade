<div align="center">

# 🛒 Fast Trade

**Marketplace full-stack de compra, venda e troca — app Android, painel web e API REST.**

[![CI](https://github.com/Alexandregv1080p/Fast-Trade/actions/workflows/ci.yml/badge.svg)](https://github.com/Alexandregv1080p/Fast-Trade/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=springboot&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-Compose-7F52FF?logo=kotlin&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-17-DD0031?logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Flyway-4169E1?logo=postgresql&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Deploy-Helm%20%2F%20K8s-326CE5?logo=kubernetes&logoColor=white)

</div>

Monorepo com três aplicações sobre a mesma API:

| App | Stack | Papel |
|-----|-------|-------|
| **backend/** | Java 21 · Spring Boot 3.2 · PostgreSQL · JWT · WebSocket | API REST + realtime |
| **frontend/** | Angular 17 · ApexCharts · STOMP/SockJS | Painel administrativo |
| **android/** | Kotlin · Jetpack Compose · Hilt · Retrofit | App do cliente |

---

## Demonstração

<!--
  SHOT LIST — capture na sua máquina e salve em docs/screenshots/ com ESTES nomes.
  (Android Studio: Emulator → ícone de câmera p/ print; ícone de vídeo p/ o GIF.)

  Android:    android-home.png · android-product.png · android-cart.png ·
              android-checkout.png · android-payment.png · android-orders.png ·
              android-chat.png · android-profile.png
  Admin (web): admin-dashboard.png · admin-products.png
  Engenharia:  swagger.png (Swagger UI) · ci.png (CI verde no GitHub Actions)
  Hero:        checkout.gif (fluxo carrinho → pagamento)

  Dica: enquanto não colar um arquivo, a imagem aparece quebrada — cole todos antes de commitar,
  ou remova as linhas dos que não for usar.
-->

<p align="center">
  <img src="docs/screenshots/checkout.gif" alt="Fluxo de checkout" width="280">
</p>

### App do cliente (Android)

| Início | Produto | Carrinho |
|---|---|---|
| ![Início](docs/screenshots/android-home.png) | ![Produto](docs/screenshots/android-product.png) | ![Carrinho](docs/screenshots/android-cart.png) |
| **Pagamento** | **Pedido** | **Perfil** |
| ![Pagamento](docs/screenshots/android-payment.png) | ![Pedido](docs/screenshots/android-orders.png) | ![Perfil](docs/screenshots/android-profile.png) |

### Painel administrativo (Angular)

| Dashboard | Produtos |
|---|---|
| ![Dashboard](docs/screenshots/admin-dashboard.png) | ![Produtos](docs/screenshots/admin-products.png) |

### Engenharia

| Swagger / OpenAPI | CI (GitHub Actions) |
|---|---|
| ![Swagger UI](docs/screenshots/swagger.png) | ![CI verde](docs/screenshots/ci.png) |

---

## Arquitetura

```mermaid
flowchart TB
    subgraph Clientes
        AND["App Android<br/>Kotlin · Compose · Hilt"]
        WEB["Painel Admin<br/>Angular 17"]
    end

    API["API REST + WebSocket<br/>Spring Boot 3.2 · Java 21<br/>JWT · Flyway"]
    DB[("PostgreSQL")]
    MP{{"Mercado Pago<br/>(pagamento simulado — arquitetado)"}}

    AND -->|"HTTPS · JWT"| API
    WEB -->|"HTTPS · JWT"| API
    AND -.->|"STOMP/WS · chat"| API
    WEB -.->|"STOMP/WS · chat"| API
    API -->|"JPA · migrations Flyway"| DB
    API -.->|"opcional · liga com token"| MP
```

**Qualidade & operação:** CI multi-job (build + testes + migrations num Postgres real + secret scanning) · OpenAPI/Swagger · métricas Prometheus · rate limiting no login · idempotência no checkout · deploy via Helm/Kubernetes.

---

## Funcionalidades

- **Catálogo** — produtos, categorias e subcategorias
- **Carrinho e pedidos** — carrinho, cupom, itens e histórico de pedidos
- **Checkout** — PIX, boleto e cartão em **modo simulado** (ver nota abaixo)
- **Carteira e financeiro** — saldo (Wallet) e transações
- **Chat em tempo real** — mensagens via WebSocket (STOMP/SockJS)
- **Autenticação** — login por JWT, com fluxo separado de admin
- **Painel admin** — dashboard com métricas, usuários, produtos, pedidos, colaboradores e suporte

> **Pagamento é simulado** (projeto não-comercial): a aprovação é mockada e o QR/boleto são
> ilustrativos. A integração real com o **Mercado Pago** já está **arquitetada** em
> `backend/.../api/payment/` (`MercadoPagoConfig`, `PaymentService`, webhook) e o app envia
> `Idempotency-Key` no checkout — basta definir `MP_ACCESS_TOKEN`/`MP_PUBLIC_KEY` para ativar.

---

## Estrutura

```
Fast Trade/
├── backend/    API Spring Boot (Maven)
├── frontend/   Painel admin Angular
├── android/    App Android (Gradle KTS)
└── k8s/        Helm chart para deploy em Kubernetes
```

Módulos do backend: `auth` · `admin` · `cart` · `category` · `chat` · `financial` · `order` · `product` · `user`.

---

## Rodando o projeto

### Backend

```bash
cd backend
docker-compose up -d        # PostgreSQL em :5432 (db/user: fasttrade, senha: fasttrade123)
./mvnw spring-boot:run      # API em http://localhost:8080
```

Credenciais admin padrão: `admin@fasttrade.com` / `admin123`.

Detalhes e lista de endpoints em [backend/README.md](backend/README.md).

### Frontend (painel admin)

```bash
cd frontend
npm install
npm start                   # http://localhost:4200
```

> Requer o backend no ar (API em `http://localhost:8080`) — o painel consome a API real.

### Android

Abra a pasta `android/` no Android Studio e rode no emulador/dispositivo (minSdk 26).
Ajuste a URL base da API em `local.properties` / config do Retrofit conforme o ambiente.

### Kubernetes

Backend, frontend e PostgreSQL sobem juntos via Helm em um cluster local (kind):

```bash
docker build -t fast-trade-backend:local ./backend
docker build -t fast-trade-frontend:local ./frontend
kind load docker-image fast-trade-backend:local fast-trade-frontend:local --name fasttrade

helm install ft ./k8s/fast-trade -f ./k8s/fast-trade/values-local.yaml \
  --namespace fasttrade --create-namespace
```

Arquitetura, migrations e opções do chart em [k8s/README.md](k8s/README.md).
Roteiro comentado com o que esperar em cada etapa em [k8s/RODANDO-LOCAL.md](k8s/RODANDO-LOCAL.md).

> O schema é versionado com Flyway (`backend/src/main/resources/db/migration`).
> O Hibernate roda em `validate` — quem altera tabela é sempre uma migration.

---

## Requisitos

- Java 21+ e Maven 3.9+ (backend)
- Docker + Docker Compose (banco)
- Node 18+ e Angular CLI 17 (frontend)
- Android Studio + JDK 17 (app)
- kubectl, Helm 3 e kind (deploy em Kubernetes — opcional)
