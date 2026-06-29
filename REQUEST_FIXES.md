# Request Fixes - API Gateway Routing Issues

## Problem Summary

The dashboard, accounts, cards, and transactions pages are not making any requests to the account service. The gateway terminal shows no activity when these pages are accessed, indicating that requests are not reaching the gateway at all.

## Root Cause Analysis

After analyzing the codebase, the following issues have been identified:

### 1. **Gateway Routing Pattern Mismatch** (CRITICAL)

**Location:** `acabou-mony-gateway/src/main/java/com/exemplo/gateway/controller/GatewayController.java`

**Current Pattern:**
```java
@RequestMapping("/{service}/{path:^(?!api).*$}/**")
```

**Issue:** The gateway expects URLs in the format `/api/{service}/{path}/**`, but the frontend is making requests to `/api/{endpoint}` directly.

**Examples of URL Mismatch:**
- Frontend sends: `GET /api/accounts?page=0&size=10`
- Gateway expects: `GET /api/accounts/accounts?page=0&size=10` (with service name duplicated)

**Evidence:**
- Frontend API calls use paths like `/accounts`, `/cards`, `/transactions`
- Gateway routing requires a `{service}` path variable that should match: `auth`, `accounts`, `cards`, `transactions`, or `auditing`
- The current regex pattern `^(?!api).*$` prevents matching paths that contain "api"

### 2. **Path Variable Extraction Issue**

**Location:** `acabou-mony-gateway/src/main/java/com/exemplo/gateway/controller/GatewayController.java`

**Current Code:**
```java
@RequestMapping("/{service}/{path:^(?!api).*$}/**")
public Mono<ResponseEntity<String>> proxy(
    @PathVariable String service,
    @PathVariable String path,
    ...
)
```

**Issue:** The `{path}` variable is defined but not properly used. The gateway reconstructs the full path using:
```java
String fullPath = request.getURI().getRawPath().replace("/api", "");
```

This means:
- Request: `/api/accounts` → fullPath: `/accounts`
- Gateway tries to route to: `http://localhost:8080/accounts` ✓ (This would work)
- BUT the `@RequestMapping` pattern doesn't match `/api/accounts` because it expects `/api/{service}/{path}`

### 3. **Frontend API Configuration**

**Location:** `acabou-mony-frontend/src/lib/constants.ts`

**Current Configuration:**
```typescript
export const API_BASE_URL = "/api"
```

**Vite Proxy Configuration:** `acabou-mony-frontend/vite.config.ts`
```typescript
server: {
  proxy: {
    "/api": {
      target: "http://localhost:8088",
      changeOrigin: true,
    },
  },
}
```

**Status:** This configuration is correct. Vite will proxy all `/api/*` requests to `http://localhost:8088/api/*`.

### 4. **Service Endpoint Mapping**

**Current Microservice Endpoints:**
- Account Service (port 8080): `/accounts`, `/accounts/balance/{id}`
- Card Service (port 8085): `/cards`, `/cards/account/{id}`
- Transaction Service (port 8083): `/transactions`, `/transactions/conta/{id}`

**Frontend API Calls:**
- `GET /api/accounts?page=0&size=10`
- `GET /api/accounts/balance/{contaId}`
- `GET /api/cards/account/{contaId}`
- `GET /api/transactions/conta/{contaId}`

## Action Plan

### Option A: Modify Gateway to Match Frontend URLs (RECOMMENDED)

This approach requires minimal changes and maintains RESTful URL patterns.

#### Changes Required:

1. **Update Gateway Controller Routing Pattern**
   - File: `acabou-mony-gateway/src/main/java/com/exemplo/gateway/controller/GatewayController.java`
   - Change the `@RequestMapping` pattern to match direct endpoint calls
   - Remove the `{service}` path variable requirement
   - Implement service detection based on the first path segment

2. **Implementation Details:**
   ```java
   @RequestMapping("/**")
   public Mono<ResponseEntity<String>> proxy(
       @RequestHeader HttpHeaders headers,
       @RequestParam(required = false) MultiValueMap<String, String> queryParams,
       @RequestBody(required = false) Mono<String> body,
       ServerHttpRequest request
   ) {
       String fullPath = request.getURI().getRawPath().replace("/api", "");
       String[] pathSegments = fullPath.split("/");
       String service = pathSegments.length > 1 ? pathSegments[1] : "";
       
       String baseUrl = switch (service) {
           case "auth" -> authUrl;
           case "accounts" -> accountUrl;
           case "cards" -> cardUrl;
           case "transactions" -> transactionUrl;
           case "auditing" -> auditingUrl;
           default -> null;
       };
       
       if (baseUrl == null) {
           return Mono.just(ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body("Service not found: " + service));
       }
       
       return webClient.method(request.getMethod())
           .uri(baseUrl + fullPath)
           .headers(httpHeaders -> httpHeaders.addAll(headers))
           .body(body == null ? Mono.empty() : body, String.class)
           .retrieve()
           .toEntity(String.class);
   }
   ```

### Option B: Modify Frontend to Match Gateway Pattern

This approach requires changes to all API calls in the frontend.

#### Changes Required:

1. **Update API Client Base URLs**
   - File: `acabou-mony-frontend/src/api/accounts.api.ts`
   - Change: `/accounts` → `/accounts/accounts`
   - Change: `/accounts/balance/{id}` → `/accounts/accounts/balance/{id}`

2. **Update API Client Base URLs**
   - File: `acabou-mony-frontend/src/api/cards.api.ts`
   - Change: `/cards` → `/cards/cards`
   - Change: `/cards/account/{id}` → `/cards/cards/account/{id}`

3. **Update API Client Base URLs**
   - File: `acabou-mony-frontend/src/api/transactions.api.ts`
   - Change: `/transactions` → `/transactions/transactions`
   - Change: `/transactions/conta/{id}` → `/transactions/transactions/conta/{id}`

**Note:** This approach is NOT recommended as it creates redundant URL patterns and violates RESTful conventions.

### Option C: Implement Path Rewriting in Gateway

Add intelligent path rewriting to maintain clean URLs while routing correctly.

#### Changes Required:

1. **Add Path Mapping Logic**
   - File: `acabou-mony-gateway/src/main/java/com/exemplo/gateway/controller/GatewayController.java`
   - Implement a path mapping strategy that:
     - Detects the service from the first path segment
     - Preserves the original path for forwarding
     - Handles query parameters correctly

2. **Example Implementation:**
   ```java
   private String extractServiceFromPath(String path) {
       if (path.startsWith("/accounts")) return "accounts";
       if (path.startsWith("/cards")) return "cards";
       if (path.startsWith("/transactions")) return "transactions";
       if (path.startsWith("/auth")) return "auth";
       if (path.startsWith("/auditing")) return "auditing";
       return null;
   }
   ```

## Additional Issues Found

### 1. **React Query Configuration**

**Location:** `acabou-mony-frontend/src/App.tsx`

**Current Configuration:**
```typescript
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 30_000,
    },
  },
})
```

**Status:** Configuration is appropriate. Queries will retry once on failure and cache for 30 seconds.

### 2. **Missing Error Handling in Hooks**

**Location:** All hooks in `acabou-mony-frontend/src/hooks/`

**Issue:** Hooks use `useQuery` but don't expose error states to components.

**Recommendation:** Components should handle `isError` and `error` states from queries.

### 3. **CORS Configuration**

**Status:** CORS is properly configured in:
- Gateway: `acabou-mony-gateway/src/main/java/com/exemplo/gateway/config/WebConfig.java`
- Account Service: `acabou-mony-account/src/main/java/com/example/acabou_mony_account/config/WebConfig.java`
- Card Service: `acabou-mony-card/src/main/java/com/example/acabou_mony_card/config/WebConfig.java`

All allow `http://localhost:5173` as origin.

### 4. **Security Configuration**

**Location:** `acabou-mony-account/src/main/java/com/example/acabou_mony_account/security/SecurityConfig.java`

**Current Configuration:**
```java
.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
.csrf(csrf -> csrf.disable());
```

**Status:** Security is disabled (permitAll). This is acceptable for development but should be addressed for production.

## Testing Strategy

After implementing the fixes, test the following scenarios:

### 1. **Dashboard Page**
- [ ] Verify `GET /api/accounts?page=0&size=10` is called
- [ ] Verify `GET /api/accounts/balance/{contaId}` is called
- [ ] Verify `GET /api/transactions/conta/{contaId}` is called
- [ ] Check gateway logs for incoming requests
- [ ] Check account service logs for processed requests

### 2. **Accounts Page**
- [ ] Verify `GET /api/accounts?page=0&size=10` is called
- [ ] Verify pagination works correctly
- [ ] Check data is displayed in the table

### 3. **Cards Page**
- [ ] Verify `GET /api/accounts?page=0&size=10` is called (for account dropdown)
- [ ] Verify `GET /api/cards/account/{contaId}` is called when account is selected
- [ ] Verify `POST /api/cards` works for creating cards
- [ ] Verify `PUT /api/cards/{id}/toggle-status` works

### 4. **Transactions Page**
- [ ] Verify `GET /api/accounts?page=0&size=10` is called (for account dropdown)
- [ ] Verify `GET /api/transactions/conta/{contaId}` is called when account is selected
- [ ] Check transaction data is displayed correctly

## Recommended Implementation Order

1. **Implement Option A (Gateway Modification)** - Highest Priority
   - Modify `GatewayController.java` to accept direct endpoint paths
   - Test with curl or Postman to verify routing works
   - Deploy and test with frontend

2. **Add Request/Response Logging** - Medium Priority
   - Add detailed logging in gateway to track all incoming requests
   - Add logging in microservices to confirm request receipt
   - This will help debug any remaining issues

3. **Enhance Error Handling** - Medium Priority
   - Update frontend components to display error states
   - Add user-friendly error messages
   - Implement retry logic for failed requests

4. **Add Integration Tests** - Low Priority
   - Create tests that verify end-to-end request flow
   - Test gateway routing for all services
   - Test frontend API calls with mock server

## Environment Verification Checklist

Before implementing fixes, verify:

- [ ] Gateway is running on port 8088
- [ ] Account service is running on port 8080
- [ ] Card service is running on port 8085
- [ ] Transaction service is running on port 8083
- [ ] Frontend dev server is running on port 5173
- [ ] MySQL database is running and accessible
- [ ] RabbitMQ is running (if needed for events)
- [ ] All services can connect to the database
- [ ] No firewall blocking localhost connections

## Expected Behavior After Fix

1. User navigates to `/dashboard`
2. Frontend makes requests:
   - `GET http://localhost:5173/api/accounts?page=0&size=10`
3. Vite proxy forwards to:
   - `GET http://localhost:8088/api/accounts?page=0&size=10`
4. Gateway logs show:
   - "FULL PATH: /accounts"
   - "BASE URL: http://127.0.0.1:8080"
5. Gateway forwards to:
   - `GET http://127.0.0.1:8080/accounts?page=0&size=10`
6. Account service processes request and returns data
7. Gateway returns response to frontend
8. Frontend displays data in dashboard

## Conclusion

The primary issue is a **routing pattern mismatch** between the frontend API calls and the gateway's expected URL structure. The recommended solution is **Option A**: modify the gateway to accept direct endpoint paths without requiring service name duplication in the URL.

This approach:
- Requires minimal code changes (only gateway)
- Maintains RESTful URL conventions
- Preserves existing frontend code
- Aligns with standard API gateway patterns
- Is easier to maintain and understand

After implementing the fix, the system should work as expected with proper request routing from frontend through gateway to microservices.
