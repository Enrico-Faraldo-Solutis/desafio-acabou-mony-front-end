# Acabou o Mony - Banking Microservices Platform

## Core Concept

**Acabou o Mony** is a distributed banking system built with microservices architecture. It provides core banking functionality including user authentication with 2FA, account management, card issuance, transaction processing, and comprehensive audit logging. The system uses asynchronous messaging for notifications and implements circuit breaker patterns for resilience.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│  NGINX (port 80) → Load Balancer                                │
│    └── API Gateway (port 8088) → JWT Validation & Routing       │
│         ├── Auth Service (8081)         → Authentication & 2FA  │
│         ├── Account Service (8080)      → Users & Accounts      │
│         ├── Card Service (8085)         → Card Management       │
│         ├── Transaction Service (8083)  → Payments & Transfers  │
│         ├── Auditing Service (8084)     → Audit Logs           │
│         └── Notification Service (8087) → Email/SMS via Queue   │
└─────────────────────────────────────────────────────────────────┘
                           ↓                    ↓
                    MySQL (3307)          RabbitMQ (5672)
                  db_acabou_mony         Message Queues
```

**Tech Stack:** Spring Boot, MySQL 8.0, RabbitMQ 3.13, Docker Compose, React + TypeScript (Frontend)

## Infrastructure Components

### Shared Services
- **MySQL Database** (`db_acabou_mony`) - Single shared database for all microservices
- **RabbitMQ** - Asynchronous messaging for notifications and events
- **NGINX** - Load balancer and reverse proxy
- **API Gateway** - Centralized routing, JWT validation, service discovery

### Microservices Ports
| Service | Port | Purpose |
|---------|------|---------|
| NGINX | 80 | External entry point |
| Gateway | 8088 | API routing hub |
| Auth | 8081 | Authentication & 2FA |
| Account | 8080 | User & account management |
| Transaction | 8083 | Payment processing |
| Auditing | 8084 | Audit trail |
| Card | 8085 | Card issuance & management |
| Notification | 8087 | Email/SMS delivery |

## API Endpoints

### Authentication Flow (`/api/auth/*`)

**POST /api/auth/login**
```json
Request:  { "email": "user@example.com", "senha": "password123" }
Response: { "mensagem": "Código 2FA enviado.", "usuarioId": 1 }
```

**POST /api/auth/verify-2fa**
```json
Request:  { "usuarioId": 1, "codigo": "123456" }
Response: { "token": "eyJhbGc...", "tipo": "Bearer" }
```

### User Management (`/api/accounts/users/*`)

**POST /api/accounts/users** - Register new user
```json
Request: { "nome": "John Doe", "email": "john@example.com", "cpf": "12345678900", "senha": "pass123" }
```

**GET /api/accounts/users** - List users (paginated)  
**GET /api/accounts/users/{id}** - Get user by ID

### Account Operations (`/api/accounts/*`)

**GET /api/accounts** - List all accounts (paginated)  
**GET /api/accounts/user/{usuarioId}** - List accounts by user (paginated)  
**GET /api/accounts/balance/{contaId}** - Get account balance & details
```json
Response: { "id": 1, "usuarioId": 1, "saldo": 1000.50, "status": "ATIVA", "dataCriacao": "2024-01-15T10:30:00" }
```

**PUT /api/accounts/balance** - Update balance (internal use)

### Card Management (`/api/cards/*`)

**POST /api/cards** - Issue new card
```json
Request:  { "contaId": 1, "nomeImpresso": "JOHN DOE" }
Response: { "id": 1, "numeroCartao": "4532015112890367", "dataValidade": "2027-06-01", "ativo": true }
```

**GET /api/cards/account/{contaId}** - List cards for account  
**PUT /api/cards/{id}/toggle-status** - Block/unblock card

### Transaction Processing (`/api/transactions/*`)

**POST /api/transactions** - Create transaction
```json
Request: { "contaOrigemId": 1, "contaDestinoId": 2, "valor": 100.50, "tipo": "TRANSFERENCIA" }
Types: "DEBITO" | "CREDITO" | "TRANSFERENCIA"
Status: "PENDENTE" | "CONCLUIDA" | "FALHA"
```

**GET /api/transactions** - List all transactions  
**GET /api/transactions/{id}** - Get transaction by ID  
**GET /api/transactions/conta/{contaId}** - Get account transaction history

### Audit Logging (`/api/auditing/*`)

**POST /api/auditing** - Create audit log (internal)  
**GET /api/auditing** - List all audit logs (paginated)  
**GET /api/auditing/{id}** - Get audit log by ID  
**GET /api/auditing/usuario/{usuarioId}** - Filter by user  
**GET /api/auditing/acao/{acao}** - Filter by action  
**GET /api/auditing/entidade?nome={nome}&id={id}** - Filter by entity

## Data Model

### Core Tables
- **usuarios** - User credentials (email, cpf, senha_hash)
- **codigos_2fa** - Time-limited 2FA codes
- **contas** - Bank accounts (saldo, status: ATIVA/BLOQUEADA/ENCERRADA)
- **cartoes** - Credit/debit cards (encrypted number & CVC)
- **transacoes** - Financial transactions with status tracking
- **audit_logs** - Comprehensive audit trail (JSON details)
- **notificacoes** - User notifications (email/SMS queue)

## Asynchronous Events (RabbitMQ)

| Queue | Producer | Consumer | Trigger |
|-------|----------|----------|---------|
| `am.notificacao.usuario-criado` | Account Service | Notification Service | User registration |
| `am.notificacao.2fa-solicitado` | Auth Service | Notification Service | Login attempt |
| `am.notificacao.transacao-concluida` | Transaction Service | Notification Service | Transaction success |

## Resilience Patterns

### Circuit Breaker (Transaction Service)
- **Target:** Account Service balance validation
- **Configuration:** 10-request sliding window, 30% failure threshold, 30s open state
- **Retry Policy:** Exponential backoff (100ms initial, 2× multiplier, max 4 attempts)
- **Fallback:** Returns 503 Service Unavailable

### Health Checks
- MySQL: `mysqladmin ping` every 10s (5 retries)
- RabbitMQ: `rabbitmq-diagnostics ping` every 15s (5 retries)
- Services: Depend on healthy database before starting

## Security

### JWT Authentication
- **Secret:** Base64-encoded 256-bit key (configurable via env)
- **Expiration:** 24 hours (86400000ms)
- **Flow:** Login → 2FA verification → JWT token → Bearer header on all requests
- **Claims:** `sub` (usuarioId), `exp` (expiration timestamp)

### Gateway Validation
- All requests through `/api/*` are routed via Gateway
- JWT validation happens at Gateway level
- Invalid/expired tokens return 401 Unauthorized

## Frontend Integration

### Tech Stack
- **Framework:** React 18 + TypeScript + Vite
- **Routing:** React Router v6
- **State:** Zustand (auth store) + React Query (server state)
- **HTTP:** Axios with JWT interceptor
- **UI:** Tailwind CSS + shadcn/ui components
- **Forms:** React Hook Form + Zod validation

### API Client Configuration
```typescript
baseURL: "/api"  // Proxied through NGINX to Gateway
Authorization: `Bearer ${token}`  // Auto-injected via interceptor
```

### Key Frontend Routes
- `/login` → Login form → `POST /api/auth/login`
- `/login/2fa` → 2FA verification → `POST /api/auth/verify-2fa`
- `/register` → User registration → `POST /api/accounts/users`
- `/dashboard` → Account overview → `GET /api/accounts/balance/{id}`
- `/transactions` → Transaction history → `GET /api/transactions/conta/{id}`
- `/cards` → Card management → `GET /api/cards/account/{id}`
- `/auditing` → Audit logs (admin) → `GET /api/auditing`

## Deployment

### Docker Compose
```bash
docker-compose up -d  # Start all services
docker-compose logs -f [service-name]  # View logs
docker-compose down  # Stop all services
```

### Environment Variables
- `DB_USER`, `DB_PASS` - MySQL credentials (default: root/102102)
- `RABBIT_USER`, `RABBIT_PASS` - RabbitMQ credentials (default: guest/guest)
- `JWT_SECRET` - JWT signing key (base64-encoded)
- `JWT_EXPIRATION` - Token lifetime in milliseconds

### Service Dependencies
1. MySQL & RabbitMQ start first (with health checks)
2. Auth Service starts (depends on MySQL)
3. Other services start (depend on MySQL, some on RabbitMQ)
4. Gateway starts (depends on Auth Service)
5. NGINX starts (depends on Gateway)

## Development Notes

- **Database:** All services share `db_acabou_mony` (no schema isolation)
- **DDL Mode:** `update` on all services except Auditing (`validate`)
- **Logging:** SQL logging enabled on all services (`show-sql: true`)
- **CORS:** Handled at Gateway level
- **Pagination:** Spring Data Page format (content, totalPages, totalElements, etc.)
