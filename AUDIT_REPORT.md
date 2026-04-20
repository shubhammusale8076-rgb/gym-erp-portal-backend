# Backend Architecture & Security Audit Report

## 1. FLOW CORRECTNESS
*   **Logical Break in Payment Flow**: In `PaymentService.java`, the payment status is hardcoded to `"PAID"` upon creation. The system assumes payment success before receiving confirmation from the gateway.
*   **Premature Subscription Trigger**: Subscription creation/renewal logic is triggered immediately after creating a payment record. If the actual payment fails at the gateway, the user still gets the subscription benefits.
*   **Broken Webhook Processing**: `RazorpayEventProcessor.java` does not compile. It refers to non-existent methods in `EventProcessor` and `WebhookEvent`, indicating a broken or incomplete integration flow.

## 2. ARCHITECTURE VALIDATION
*   **Layering**: The project follows a standard Controller-Service-Repository pattern. However, the integration layer is poorly decoupled.
*   **Tight Coupling**: Services are tightly coupled to the `IntegrationClient`, and the event processing logic is fragmented.
*   **Broken Multi-tenancy**: Registration flow (`RegistrationService`) fails to assign a `tenant_id` to new users, effectively breaking the multi-tenant architecture for all new registrations.

## 3. INTEGRATION FLOW
*   **Security Vulnerability**: No signature verification for webhooks (e.g., Razorpay). Anyone can spoof a `payment.captured` event to gain free access.
*   **Reliability Issues**: `IntegrationClient` uses `@Async` for external calls but lacks a retry mechanism or a persistent outbox. If the external service is down, events are lost forever.
*   **Resource Exhaustion**: `RestTemplate` is used without configured timeouts. A slow external API can hang threads and eventually crash the application.

## 4. ERROR HANDLING & EDGE CASES
*   **Generic Exceptions**: Widespread use of `catch (Exception e)` without re-throwing or proper logging.
*   **Missing Global Handler**: No `@ControllerAdvice` exists to handle exceptions globally, leading to inconsistent error responses and potential leakage of stack traces.
*   **Inconsistent Logic**: Mixed use of `ResponseDto` return values and throwing `RuntimeException`. Many services return `null` or use `assert` instead of proper validation.

## 5. DATA FLOW & CONSISTENCY
*   **Cross-Tenant Data Leakage**: Critical service methods (e.g., `AttendanceService.recordEvent`) accept IDs (like `memberId`) and use them directly without verifying if the resource belongs to the authenticated tenant. A user from Tenant A could potentially record attendance for a member in Tenant B.
*   **Lombok Misuse**: Several entities use `@Builder` on classes with field initializers without `@Builder.Default`, causing initializers (like `new ArrayList<>()`) to be ignored by the builder.

## 6. SECURITY CHECK
*   **Major Exposure**: The endpoint `/api/auth/**` is marked as public in `WebSecurityConfig.java`. This exposes sensitive endpoints like `/api/auth/addrole` and `/api/auth/get-roles` to the public internet, allowing anyone to modify roles.
*   **Weak JWT Management**: `JWTTokenHelper` generates a new random secret key on every application restart. This invalidates all existing user tokens whenever the server restarts, forcing all users to log in again.
*   **Sensitive Data Exposure**: `RegistrationService` returns the hashed password in the response DTO, which is an unnecessary security risk.

## 7. SCALABILITY & PERFORMANCE
*   **Blocking Calls**: While some events are async, many operations perform multiple synchronous DB hits that could be optimized.
*   **Inefficient Queries**: Heatmap generation in `AttendanceService` performs a DB query followed by manual date filling. This is acceptable for now but could be optimized at the DB level for larger datasets.

## 8. CODE SMELLS & IMPROVEMENTS
*   **Anti-pattern**: Use of `System.out.println` instead of a logging framework (SLF4J).
*   **Hardcoded Logic**: Values like `SESSION_GAP_MINUTES = 90` should be configurable per tenant.
*   **Null Safety**: Frequent use of `.orElse(null)` followed by `assert` or unchecked access.

## 9. FINAL VERDICT: NO
**The current codebase is NOT production-ready.** It fails to compile, contains critical security holes (public role management, no webhook verification), and has broken multi-tenancy logic.

### Priority Fixes:
1.  **Fix Build**: Correct `RazorpayEventProcessor.java` and `WebhookEvent.java`.
2.  **Secure Auth**: Restrict `/api/auth/addrole` and use a persistent JWT secret.
3.  **Fix Multi-tenancy**: Assign tenants during registration and validate resource ownership in all services.
4.  **Webhook Security**: Implement signature verification for all third-party webhooks.

### Suggested Architecture:
```text
[Client] -> [Spring Security (JWT + Tenant Filter)]
             |
             v
[Controllers] -> [Services (Transactional + Ownership Check)]
             |      |
             |      +-> [Repositories (Tenant-Scoped Queries)]
             |
             +-> [Event Bus / Outbox] -> [Async Integration Handlers]
                                            |
                                            +-> [Third-Party APIs]
```
