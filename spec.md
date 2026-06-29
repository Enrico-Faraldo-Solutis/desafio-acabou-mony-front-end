# Acabou o Mony — Frontend Application Spec

## 1. Architecture Overview

### Backend: Microservices Ecosystem

```
NGINX (port 80)
 └── API Gateway (port 8088)
      ├── Auth Service       (port 8081)  → /api/auth/*
      ├── Account Service    (port 8080)  → /api/accounts/*
      ├── Card Service       (port 8085)  → /api/cards/*
      ├── Transaction Service (port 8083)  → /api/transactions/*
      ├── Auditing Service   (port 8084)  → /api/auditing/*
      └── Notification Service (port 8087) → RabbitMQ consumers only
```

**Infrastructure:** MySQL (single DB `db_acabou_mony`), RabbitMQ, Docker Compose

### Frontend: Single Page Application

Target: React / Next.js consuming the Gateway at `http://localhost:8088/api/` (or through NGINX at `http://localhost/api/`).

---

## 2. Complete API Endpoint Reference

### 2.1 Auth Service — `POST /api/auth/login`

Authenticates user credentials and triggers 2FA code delivery.

**Request:**
```json
{
  "email": "user@example.com",
  "senha": "password123"
}
```

**Response (200):**
```json
{
  "mensagem": "Credenciais válidas. Código 2FA enviado.",
  "usuarioId": 1
}
```

**Errors:** 401 (invalid credentials)

---

### 2.2 Auth Service — `POST /api/auth/verify-2fa`

Validates the 2FA code and returns a JWT token.

**Request:**
```json
{
  "usuarioId": 1,
  "codigo": "123456"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer"
}
```

**Errors:** 401 (invalid/expired code)

---

### 2.3 Account Service — `POST /api/accounts/users`

Creates a new user. The Account service emits a `UsuarioCriadoEvent` to RabbitMQ (consumed by Notification service to send welcome email).

**Request:**
```json
{
  "nome": "John Doe",
  "email": "john@example.com",
  "cpf": "123.456.789-00",
  "senha": "password123"
}
```

**Response (201):**
```json
{
  "id": 1,
  "nome": "John Doe",
  "email": "john@example.com",
  "cpf": "12345678900",
  "dataCriacao": "2024-01-15T10:30:00"
}
```

---

### 2.4 Account Service — `GET /api/accounts/users`

Lists all users, paginated. Default: page=0, size=10, sort=id.

**Query params:** `?page=0&size=10&sort=id`

**Response (200) — Spring Page envelope:**
```json
{
  "content": [
    {
      "id": 1,
      "nome": "John Doe",
      "email": "john@example.com",
      "cpf": "12345678900",
      "dataCriacao": "2024-01-15T10:30:00"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 10 },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "empty": false
}
```

---

### 2.5 Account Service — `GET /api/accounts/users/{id}`

**Response (200):** Single `UsuarioResponseDto` (same shape as above). **404** if not found.

---

### 2.6 Account Service — `GET /api/accounts/balance/{contaId}`

Get account details by ID.

**Response (200):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "saldo": 1000.50,
  "status": "ATIVA",
  "dataCriacao": "2024-01-15T10:30:00"
}
```

**Status enum:** `ATIVA`, `BLOQUEADA`, `ENCERRADA`

---

### 2.7 Account Service — `PUT /api/accounts/balance`

Updates account balance (internal use by Transaction service).

**Request:**
```json
{
  "contaId": 1,
  "valor": 1500.00
}
```

**Response (200):** Updated `ContaResponseDto`

---

### 2.8 Account Service — `GET /api/accounts`

Lists all accounts, paginated. Default: page=0, size=10, sort=id.

---

### 2.9 Card Service — `POST /api/cards`

Generates a new card for an account.

**Request:**
```json
{
  "contaId": 1,
  "nomeImpresso": "JOHN DOE"
}
```

**Response (201):**
```json
{
  "id": 1,
  "contaId": 1,
  "numeroCartao": "4532015112890367",
  "nomeImpresso": "JOHN DOE",
  "dataValidade": "2027-06-01",
  "ativo": true
}
```

---

### 2.10 Card Service — `GET /api/cards/account/{contaId}`

Lists all cards for a given account.

**Response (200):** Array of `CardResponseDTO`

---

### 2.11 Card Service — `PUT /api/cards/{id}/toggle-status`

Toggles the active/inactive status of a card.

**Response (200):** Updated `CardResponseDTO`

---

### 2.12 Transaction Service — `POST /api/transactions`

Process a financial transaction. Validates balance (via AccountClient circuit-breaker), updates both account balances, and registers an audit log.

**Request:**
```json
{
  "contaOrigemId": 1,
  "contaDestinoId": 2,
  "valor": 100.50,
  "tipo": "TRANSFERENCIA"
}
```

**Possible `tipo` values:** `DEBITO`, `CREDITO`, `TRANSFERENCIA`

**Response (201):**
```json
{
  "id": 1,
  "contaOrigemId": 1,
  "contaDestinoId": 2,
  "valor": 100.50,
  "status": "CONCLUIDA",
  "tipo": "TRANSFERENCIA",
  "dataTransacao": "2024-01-15T10:30:00"
}
```

**Status values:** `PENDENTE`, `CONCLUIDA`, `FALHA`

**Errors:** 400 (insufficient balance / invalid input), 503 (account service unavailable)

---

### 2.13 Transaction Service — `GET /api/transactions`

Lists all transactions.

**Response (200):** Array of `TransacaoResponseDto`

---

### 2.14 Transaction Service — `GET /api/transactions/{id}`

**Response (200):** Single `TransacaoResponseDto`. **404** if not found.

---

### 2.15 Transaction Service — `GET /api/transactions/conta/{contaId}`

Returns all transactions (incoming and outgoing) for a specific account.

**Response (200):** Array of `TransacaoResponseDto`

---

### 2.16 Auditing Service — `POST /api/auditing`

Registers a new audit log entry (internal use).

**Request:**
```json
{
  "usuarioId": 1,
  "acao": "CRIAR_TRANSACAO",
  "entidadeNome": "Transacao",
  "entidadeId": 1,
  "detalhes": { "valor": 100.50 },
  "ipOrigem": "192.168.1.1"
}
```

**Response (201):** `AuditLogResponseDto`

---

### 2.17 Auditing Service — `GET /api/auditing`

Paginated list of all audit logs.

---

### 2.18 Auditing Service — `GET /api/auditing/{id}`

Single audit log by ID.

---

### 2.19 Auditing Service — `GET /api/auditing/usuario/{usuarioId}`

Paginated audit logs filtered by user.

---

### 2.20 Auditing Service — `GET /api/auditing/acao/{acao}`

Paginated audit logs filtered by action string.

---

### 2.21 Auditing Service — `GET /api/auditing/entidade?nome={nome}&id={id}`

Paginated audit logs filtered by entity name and entity ID.

---

### 2.22 Gateway — `GET /api/teste`

**Response (200):** `"Gateway funcionando!"`

---

## 3. Data Model (Entities)

| Table | Key Fields |
|---|---|
| `usuarios` | id, nome, email (unique), cpf (unique), senha_hash, data_criacao |
| `codigos_2fa` | id, usuario_id (FK), codigo, data_expiracao, usado |
| `contas` | id, usuario_id (FK), saldo (DECIMAL 15,2), status (ATIVA/BLOQUEADA/ENCERRADA), data_criacao |
| `cartoes` | id, conta_id (FK), numero_criptografado, cvc_criptografado, data_validade, ativo |
| `transacoes` | id, conta_origem_id (FK), conta_destino_id (FK), valor (DECIMAL 15,2), status (PENDENTE/CONCLUIDA/FALHA), tipo (DEBITO/CREDITO/TRANSFERENCIA), data_transacao |
| `audit_logs` | id, usuario_id (FK), acao, entidade_nome, entidade_id, detalhes (JSON), ip_origem, timestamp |
| `notificacoes` | id, usuario_id (FK), mensagem, lida, data_criacao |

---

## 4. Asynchronous Events (RabbitMQ)

| Queue | Producer | Consumer | Payload |
|---|---|---|---|
| `am.notificacao.usuario-criado` | Account Service | Notification Service | `{ id, nome, email, cpf }` |
| `am.notificacao.2fa-solicitado` | Auth Service | Notification Service | `{ email, nome, codigo }` |
| `am.notificacao.transacao-concluida` | Transaction Service | Notification Service | `{ id, contaOrigemId, contaDestinoId, valor }` |

---

## 5. Cross-Cutting Concerns

### Authentication
- JWT-based (Bearer token in `Authorization` header)
- Flow: Login → receives `usuarioId` → verify 2FA → receives JWT
- Token includes `sub` (usuarioId) and `exp` claims
- JWT secret is configured per-environment

### Circuit Breaker (Transaction Service)
- `accountService` circuit breaker: sliding window 10, failure threshold 30%, wait 30s in open state
- Retry: exponential backoff (100ms initial, 2× multiplier), max 4 attempts
- Catches `RestClientException` and `TimeoutException`

### Resilience
- `ddl-auto: update` on all services except Auditing (`validate`)
- All services share a single MySQL database (`db_acabou_mony`)
- Gateway performs naive proxy routing via `WebClient`

---

## 6. Frontend Routes & Feature Mapping

| Frontend Route | Feature | Backend API(s) |
|---|---|---|
| `/login` | Login form (email + password) | `POST /api/auth/login` |
| `/login/2fa` | 2FA code verification form | `POST /api/auth/verify-2fa` |
| `/register` | User registration form | `POST /api/accounts/users` |
| `/dashboard` | Dashboard / Account overview | `GET /api/accounts/balance/{id}`, `GET /api/transactions/conta/{id}` |
| `/accounts` | Account management | `GET /api/accounts`, `GET /api/accounts/balance/{id}` |
| `/users` | User management (admin) | `GET /api/accounts/users`, `GET /api/accounts/users/{id}` |
| `/cards` | Card management | `POST /api/cards`, `GET /api/cards/account/{id}`, `PUT /api/cards/{id}/toggle-status` |
| `/transactions` | Transaction list / history | `GET /api/transactions/conta/{id}`, `GET /api/transactions` |
| `/transactions/new` | Create new transaction | `POST /api/transactions` |
| `/auditing` | Audit log viewer (admin) | `GET /api/auditing`, `GET /api/auditing/usuario/{id}`, `GET /api/auditing/acao/{acao}`, `GET /api/auditing/entidade` |

---

## 7. TypeScript Type Definitions (DTO→Frontend)

```typescript
// Auth
interface LoginRequest { email: string; senha: string }
interface LoginResponse { mensagem: string; usuarioId: number }
interface Verify2FARequest { usuarioId: number; codigo: string }
interface TokenResponse { token: string; tipo: "Bearer" }

// Users
interface UserCreateRequest { nome: string; email: string; cpf: string; senha: string }
interface UserResponse { id: number; nome: string; email: string; cpf: string; dataCriacao: string }

// Accounts
interface AccountResponse { id: number; usuarioId: number; saldo: number; status: AccountStatus; dataCriacao: string }
type AccountStatus = "ATIVA" | "BLOQUEADA" | "ENCERRADA"
interface UpdateBalanceRequest { contaId: number; valor: number }

// Cards
interface CardCreateRequest { contaId: number; nomeImpresso: string }
interface CardResponse { id: number; contaId: number; numeroCartao: string; nomeImpresso: string; dataValidade: string; ativo: boolean }

// Transactions
interface TransactionCreateRequest { contaOrigemId: number; contaDestinoId: number; valor: number; tipo: TransactionType }
type TransactionType = "DEBITO" | "CREDITO" | "TRANSFERENCIA"
interface TransactionResponse { id: number; contaOrigemId: number; contaDestinoId: number; valor: number; status: TransactionStatus; tipo: TransactionType; dataTransacao: string }
type TransactionStatus = "PENDENTE" | "CONCLUIDA" | "FALHA"

// Auditing
interface AuditLogCreateRequest { usuarioId?: number; acao: string; entidadeNome: string; entidadeId: number; detalhes: Record<string, unknown>; ipOrigem?: string }
interface AuditLogResponse { id: number; usuarioId?: number; acao: string; entidadeNome: string; entidadeId: number; detalhes: Record<string, unknown>; ipOrigem?: string; timestamp: string }

// Pagination (Spring Page envelope)
interface PageResponse<T> {
  content: T[]
  pageable: { pageNumber: number; pageSize: number }
  totalPages: number
  totalElements: number
  last: boolean
  first: boolean
  empty: boolean
}
```

---

## 8. Auth Flow (Frontend Implementation)

1. User enters email + password → `POST /api/auth/login`
2. Store `usuarioId` in local state → redirect to `/login/2fa`
3. User enters 6-digit code → `POST /api/auth/verify-2fa`
4. Store returned JWT token (`token`, `tipo: "Bearer"`)
5. All subsequent API calls include header: `Authorization: Bearer <token>`
6. Token expiry → redirect to `/login`

---

## 9. Frontend Tech Stack Recommendations

| Concern | Recommendation |
|---|---|
| Framework | React 18 + TypeScript |
| Routing | React Router v6 |
| State | React Query (TanStack Query) for server state |
| HTTP | Axios or fetch with interceptor for JWT |
| Forms | React Hook Form + Zod validation |
| UI | Tailwind CSS + shadcn/ui or Ant Design |
| Pagination | React Query's `keepPreviousData` + Spring Page shape |
| Container | Docker + Nginx to serve static build |
