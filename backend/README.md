# Fast Trade API — Backend Spring Boot

API REST do painel admin Fast Trade.  
**Stack:** Java 21 · Spring Boot 3.2 · PostgreSQL · JWT

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker + Docker Compose

---

## Subindo o banco

```bash
cd backend
docker-compose up -d
```

Isso sobe o PostgreSQL na porta **5432** com:
- DB: `fasttrade`
- User: `fasttrade`
- Password: `fasttrade123`

---

## Rodando a API

```bash
cd backend
./mvnw spring-boot:run
```

A API sobe em **http://localhost:8080**

---

## Credenciais padrão

| Campo | Valor |
|-------|-------|
| E-mail | `admin@fasttrade.com` |
| Senha | `admin123` |

---

## Endpoints principais

| Método | URL | Descrição |
|--------|-----|-----------|
| POST | `/api/auth/login/adm` | Login admin |
| GET | `/api/user` | Listar usuários |
| GET | `/api/product` | Listar produtos |
| GET | `/api/order` | Listar pedidos |
| GET | `/api/category` | Listar categorias |
| GET | `/api/financial/transactions` | Transações |
| GET | `/api/admin/dashboard` | Dashboard stats |
| GET | `/api/admin/collaborators` | Colaboradores |

Todos os endpoints (exceto login/recovery) exigem header:
```
Authorization: Bearer <token>
```

---

## Removendo o mock do Angular

Quando o backend estiver rodando, remova o bloco mock de  
`src/app/core/services/auth-http.service.ts` (linhas com `MOCK_USERS`).
