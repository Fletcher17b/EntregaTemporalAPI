# AppBackend — Architecture & Flow Documentation

## 1. Overview

AppBackend is a Spring Boot service that manages **quiz/test templates**, **test sessions**, and **scoring**. It does **not** own user credentials — authentication (login, password, token issuance) lives in a separate **Auth API** (default `http://localhost:8081`). AppBackend trusts JWTs issued by that Auth API and keeps its own local copy of each user (id, username, role) once they've been registered.

Two client types talk to this service:

- **End users / admins**, authenticated via a bearer JWT from the Auth API.
- **The Auth API itself**, authenticated via a shared internal API key, for service-to-service calls under `/api/internal/**`.

## 2. Core Domain Model

|Entity|Purpose|
|---|---|
|`User`|Local copy of a provisioned user: `id` (UUID, matches Auth API's `user_id`), `username`, `role` (`USER`/`ADMIN`).|
|`Test`|A test template: `id`, `version`, `title`, list of `Question`s.|
|`Question`|Belongs to a `Test`; has `id`, `text`, list of `Option`s.|
|`Option`|Belongs to a `Question`; has `id`, `text`, `weight` (score contribution).|
|`TestSession`|Records that a user started/completed a specific test (`userId`, `testId`, `startedAt`, `completedAt`).|
|`TestResult`|The scored outcome of a session: `totalScore`, `interpretation`, `categoryScores`.|

**ID scheme** (`util/EntityIds.java`) — IDs are derived deterministically from the test ID, not randomly generated:

- Question ID: `{testId}.q{questionNumber}` (e.g. `1.q2`)
- Option ID: `{testId}.q{questionNumber}.o{optionNumber}` (e.g. `1.q2.o3`)

This lets scoring do an O(1) map lookup of `questionId:optionId → weight` instead of a DB join per answer.

## 3. Request Flows

### 3.1 Registration — `POST /api/register` (public)

```
Client → RegistrationController → RegistrationService
                                        │
                                        ├─► POST {authApiBaseUrl}/register_poo
                                        │     (email, password) → Auth API
                                        │     Auth API returns { user_id, role }
                                        │
                                        ├─► Check local UserRepository for existsById(userId)
                                        │     → 409 Conflict if already present
                                        │
                                        └─► Save local User(id, username, role)
                                              → 201 Created, returns userId/username/role
```

> Note: the outbound call currently targets `/register_poo` on the Auth API rather than the configured `registerEndpoint` (`/register`) — likely worth double-checking this is intentional.

### 3.2 Authentication on every other request

1. Client sends `Authorization: Bearer <JWT>` (obtained from the Auth API's login/refresh, out of scope for this service).
2. `JwtDecoderConfig` verifies the RS256 signature using the Auth API's **public key** (configured directly or loaded from a PEM file/classpath resource) — no network round-trip needed per request.
3. `JwtRoleConverter` (runs as part of the OAuth2 resource server pipeline):
    - Reads the JWT `sub` claim as a UUID.
    - Looks the user up in the local `UserRepository`.
    - **Rejects the token** if no local user exists for that id ("not provisioned").
    - Grants a Spring Security authority (`ROLE_USER` or `ROLE_ADMIN`) based on the **local DB role**, not any role claim in the token itself.
4. `CurrentUserService` is used later by services to fetch the authenticated user's id, and to assert that a request's `userId` field matches the token's subject (used when submitting a test, to stop User A submitting on behalf of User B).

### 3.3 Internal service calls — `/api/internal/**`

A separate, higher-priority (`@Order(1)`) security filter chain. `InternalApiKeyFilter` compares the `X-Internal-Api-Key` header against a configured shared secret (`auth.api.internal-api-key`). On match, it installs an `InternalServiceAuthentication` with `ROLE_INTERNAL`. No JWT is involved on this path.

### 3.4 Creating a test — `POST /api/tests` (ADMIN only)

```
CreateTestRequest { testId, version, title, questions[ { text, options[ {text, weight} ] } ] }
        │
        ▼
TestService.createTest
  ├─ 409 if testId already exists
  ├─ For each question (1-indexed) → build Question with id = EntityIds.questionId(testId, n)
  │     For each option (1-indexed) → build Option with id = EntityIds.optionId(testId, n, m)
  └─ Save Test (cascades Questions & Options), return the freshly loaded blueprint
```

### 3.5 Fetching a test — `GET /api/tests/{testId}` (any authenticated user)

Loads the `Test` with its `Question`s and `Option`s eagerly fetched (`findByIdWithQuestions`, a `LEFT JOIN FETCH` query) and returns the full structure for the client to render as a quiz form. 404 if not found.

### 3.6 Submitting a test — `POST /api/tests/submissions`

```
SubmitTestRequest { testId, userId, answers[ {questionId, optionId} ], startedAt, completedAt }
        │
        ▼
TestService.submitTest
  ├─ currentUserService.requireMatchingUserId(userId)   → 403 if JWT subject ≠ request.userId
  ├─ Load Test blueprint (404 if missing)
  ├─ Create + save a TestSession (new random UUID id)
  ├─ Build a {questionId}:{optionId} → weight lookup map from the blueprint
  ├─ For each submitted answer:
  │     look up its weight; if found, add to totalScore and record it in categoryScores
  ├─ interpretation = totalScore < 5 ? "Low" : "High"   (fixed threshold, not per-test)
  └─ Save + return TestResult { totalScore, interpretation, categoryScores }
```

> Note: `categoryScores` is keyed by `questionId`, so each entry just holds that question's own weight rather than an aggregated "category" total — there's no grouping of multiple questions into a shared category today.

## 4. Error Handling

`GlobalExceptionHandler` maps exceptions to RFC-7807 `ProblemDetail` responses:

|Exception|HTTP Status|
|---|---|
|`ResourceNotFoundException`|404|
|`ResourceConflictException`|409|
|`AccessDeniedException`|403|
|`MethodArgumentNotValidException` (bean validation)|400|

## 5. Configuration Reference (`AuthProperties`, prefix `auth.*`)

|Property|Default|Purpose|
|---|---|---|
|`auth.api.base-url`|`http://localhost:8081`|Auth API host used for the registration call and shown in Swagger docs.|
|`auth.api.register-endpoint`|`/register`|Configured but currently unused (registration call is hardcoded to `/register_poo`).|
|`auth.api.internal-api-key`|_(empty)_|Shared secret expected on `X-Internal-Api-Key` for `/api/internal/**`.|
|`auth.jwt.public-key`|_(empty)_|Inline PEM public key to verify JWTs.|
|`auth.jwt.public-key-location`|`classpath:jwt-public.pem`|Fallback location to load the public key from if not set inline.|

## 6. Endpoint Summary

|Method|Path|Auth|Notes|
|---|---|---|---|
|POST|`/api/register`|Public|Forwards to Auth API, creates local user|
|POST|`/api/tests`|JWT, `ROLE_ADMIN`|Create test template|
|GET|`/api/tests/{testId}`|JWT, any user|Fetch test template|
|POST|`/api/tests/submissions`|JWT, matching user|Submit & score a test|
|*|`/api/internal/**`|`X-Internal-Api-Key` header|Reserved for Auth API → AppBackend calls|
|*|`/swagger-ui/**`, `/v3/api-docs/**`|Public|API docs|

## 7. Things Worth Double-Checking

- `RegistrationService` posts to `{baseUrl}/register_poo` instead of the configured `registerEndpoint` (`/register`).
- `submitTest`'s `categoryScores` map doesn't actually aggregate by category — it's one entry per question, overwritten if a question id repeats.
- The `"Low"`/`"High"` scoring threshold (`< 5`) is hardcoded globally rather than being per-test or configurable.