# Fast Trade

Marketplace de compra, venda e troca de produtos. Monorepo com três aplicações:

| App | Stack | Papel |
|-----|-------|-------|
| **backend/** | Java 21 · Spring Boot 3.2 · PostgreSQL · JWT · WebSocket | API REST + realtime |
| **frontend/** | Angular 17 · ApexCharts · STOMP/SockJS | Painel administrativo |
| **android/** | Kotlin · Jetpack Compose · Hilt · Retrofit | App do cliente |

---

## Demonstração

<!-- Salve os prints/GIF em docs/screenshots/ e referencie abaixo. Um GIF do fluxo
     de checkout vale mais que dez linhas de texto — é o que o recrutador vê primeiro. -->

| Cliente (Android) | Painel Admin (Angular) |
|---|---|
| ![App Android](docs/screenshots/android-home.png) | ![Painel admin](docs/screenshots/admin-dashboard.png) |

> **GIF do checkout:** `docs/screenshots/checkout.gif` · **Demo ao vivo:** _em breve_

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

> Enquanto o backend não estiver no ar, o Angular usa `MOCK_USERS` em
> `src/app/core/services/auth-http.service.ts`. Remova esse bloco ao integrar com a API real.

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
