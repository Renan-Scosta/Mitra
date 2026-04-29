# Mitra — Fitness Tracking API

> **Documento vivo.** Este arquivo é o onboarding doc da equipe (humanos + agentes de IA).
> Deve ser lido integralmente antes de qualquer interação com o projeto.
> **Última atualização**: 2026-04-29

---

## 1. Visão Geral

**Mitra** é uma API REST de acompanhamento fitness construída sobre **Spring Boot 4** com arquitetura **Hexagonal (Ports & Adapters)**.
O sistema permite registrar usuários, gerenciar perfis, montar rotinas de treino, executar sessões, registrar séries, calcular métricas fisiológicas (BMR, calorias via MET), consultar personal records, e visualizar dashboards de progresso.

### Core Values

| Princípio | Regra |
|---|---|
| **Small Releases** | Cada commit em `main` passa no CI. Sem exceção. Não existe "commit quebrando que vai ser consertado no próximo". Todo commit é **production-ready**. |
| **Refactoring Contínuo** | Código é melhorado constantemente. Se você toca num arquivo, melhore o que estiver ao redor. Nunca deixe tech debt proposital. |
| **Integração Contínua** | `./mvnw test` **DEVE** passar antes de qualquer commit. Isso é uma regra inviolável. |
| **Conventional Commits** | Formato: `type(scope): description`. Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`. |
| **Cobertura de Testes** | Para cada linha de código funcional, uma linha de teste. Cobertura máxima, sem exceção. |
| **TDD quando aplicável** | Ciclo Red → Green → Refactor para use cases e domain services. |

---

## 2. Stack Tecnológico

| Camada | Tecnologia | Versão |
|---|---|---|
| **Linguagem** | Java | 21 |
| **Framework** | Spring Boot | 4.0.5 |
| **Build Tool** | Maven (wrapper) | 3.x |
| **ORM** | Spring Data JPA / Hibernate | via Spring Boot BOM |
| **Migrations** | Flyway (spring-boot-starter-flyway) | via Spring Boot BOM |
| **Banco Produção** | PostgreSQL | 16+ |
| **Banco Testes** | H2 (in-memory) | via Spring Boot BOM |
| **Segurança** | Spring Security + JWT (auth0/java-jwt 4.4.0) | — |
| **OAuth2** | Google API Client (API-first Stateless OAuth2) | 2.4.0 |
| **Criptografia** | BCryptPasswordEncoder | — |
| **Documentação API** | Springdoc OpenAPI (Swagger UI) | 3.0.2 |
| **Observabilidade** | Spring Boot Actuator | via Spring Boot BOM |
| **Boilerplate** | Lombok | via Spring Boot BOM |
| **Testes** | JUnit 5 + Mockito + MockMvc | via Spring Boot BOM |

---

## 3. Variáveis de Ambiente

| Variável | Obrigatória | Default | Descrição |
|---|---|---|---|
| `DB_URL` | Não | `jdbc:postgresql://localhost:5432/mitra` | JDBC URL do PostgreSQL |
| `DB_USERNAME` | Não | `postgres` | Usuário do banco |
| `DB_PASSWORD` | **Sim** | — | Senha do banco |
| `GOOGLE_CLIENT_ID` | Sim* | — | Client ID do Google OAuth2 |
| `GOOGLE_CLIENT_SECRET` | Sim* | — | Client Secret do Google OAuth2 |
| `api.security.token.secret` | Não | `my-super-secret-mitra-key` | Secret do HMAC256 para JWT |

> \* OAuth2 funciona num modelo stateless: o cliente (mobile) obtém o idToken do Google e repassa ao Mitra na rota POST `/api/v1/auth/google`.

---

## 4. Estrutura do Projeto

```
Mitra-App/
├── PROJECT.md                         ← VOCÊ ESTÁ AQUI
├── .gitignore
└── mitra/mitra/                       ← Módulo Maven principal
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    └── src/
        ├── main/
        │   ├── java/com/mitra/
        │   │   ├── MitraApplication.java
        │   │   ├── domain/                    ← CAMADA DOMAIN (0 dependência Spring)
        │   │   │   ├── model/                 ← POJOs puros com Lombok
        │   │   │   │   ├── User.java
        │   │   │   │   ├── Exercise.java
        │   │   │   │   ├── WorkoutRoutine.java
        │   │   │   │   ├── RoutineExercise.java
        │   │   │   │   ├── WorkoutSession.java
        │   │   │   │   ├── SetRecord.java
        │   │   │   │   ├── BodyMeasurement.java
        │   │   │   │   └── enums/
        │   │   │   │       ├── Gender.java          (MALE, FEMALE)
        │   │   │   │       ├── Role.java            (ADMIN, USER)
        │   │   │   │       └── TrackingType.java     (WEIGHT_REPS, REPS_ONLY, TIME_ONLY)
        │   │   │   └── service/
        │   │   │       ├── BmrCalculator.java        ← Mifflin-St Jeor (puro, sem Spring)
        │   │   │       ├── CalorieCalculator.java    ← Cálculo MET (puro, sem Spring)
        │   │   │       └── CalorieResult.java        ← Retorno do cálculo
        │   │   │
        │   │   ├── application/               ← CAMADA APPLICATION (orquestração)
        │   │   │   ├── port/out/              ← Interfaces de saída (driven)
        │   │   │   │   ├── UserRepositoryPort.java
        │   │   │   │   ├── ExerciseRepositoryPort.java
        │   │   │   │   ├── WorkoutRoutineRepositoryPort.java
        │   │   │   │   ├── WorkoutSessionRepositoryPort.java
        │   │   │   │   ├── RoutineExerciseRepositoryPort.java
        │   │   │   │   ├── SetRecordRepositoryPort.java
        │   │   │   │   ├── BodyMeasurementRepositoryPort.java
        │   │   │   │   ├── GoogleTokenVerifierPort.java
        │   │   │   │   └── PasswordEncoderPort.java
        │   │   │   ├── exception/
        │   │   │   │   └── ResourceNotFoundException.java
        │   │   │   └── usecase/               ← Interfaces + implementations (23 use cases)
        │   │   │       ├── RegisterUserUseCase.java
        │   │   │       ├── GoogleLoginUseCase.java
        │   │   │       ├── CalculateBmrUseCase.java
        │   │   │       ├── UpdateUserProfileUseCase.java
        │   │   │       ├── UpdateUserPasswordUseCase.java
        │   │   │       ├── DeleteUserAccountUseCase.java
        │   │   │       ├── GetUserDashboardUseCase.java
        │   │   │       ├── GetUserVolumeSummaryUseCase.java
        │   │   │       ├── CreateExerciseUseCase.java
        │   │   │       ├── GetAllExercisesUseCase.java
        │   │   │       ├── GetExerciseHistoryUseCase.java
        │   │   │       ├── GetPersonalRecordsUseCase.java
        │   │   │       ├── CreateWorkoutRoutineUseCase.java
        │   │   │       ├── GetWorkoutRoutinesUseCase.java
        │   │   │       ├── AddRoutineExerciseUseCase.java
        │   │   │       ├── StartWorkoutSessionUseCase.java
        │   │   │       ├── LogSetRecordUseCase.java
        │   │   │       ├── FinishWorkoutSessionUseCase.java
        │   │   │       ├── GetWorkoutSessionUseCase.java
        │   │   │       ├── GetUserSessionsUseCase.java
        │   │   │       ├── CalculateSessionCaloriesUseCase.java
        │   │   │       ├── CreateBodyMeasurementUseCase.java
        │   │   │       ├── GetBodyMeasurementsUseCase.java
        │   │   │       └── impl/              ← 22 implementações concretas
        │   │   │
        │   │   ├── infrastructure/            ← CAMADA INFRASTRUCTURE (frameworks)
        │   │   │   ├── config/
        │   │   │   │   ├── SecurityConfig.java       @Profile("!test")
        │   │   │   │   ├── SwaggerConfig.java
        │   │   │   │   ├── UseCaseConfig.java        ← Wiring manual dos beans
        │   │   │   │   └── DatabaseSeeder.java       @Profile("!test")
        │   │   │   ├── security/
        │   │   │   │   ├── TokenService.java         ← JWT HMAC256
        │   │   │   │   ├── SecurityFilter.java       ← OncePerRequestFilter
        │   │   │   │   └── PasswordEncoderAdapter.java
        │   │   │   └── persistence/
        │   │   │       ├── entity/            ← JPA Entities (@Entity)
        │   │   │       ├── mapper/            ← Domain ↔ Entity mappers
        │   │   │       ├── repository/        ← Spring Data JPA interfaces
        │   │   │       └── adapter/           ← Implementações dos Ports
        │   │   │
        │   │   └── presentation/              ← CAMADA PRESENTATION (HTTP)
        │   │       ├── controller/
        │   │       │   ├── AuthController.java                POST /login, POST /google
        │   │       │   ├── UserController.java                users, profile, dashboard, volume
        │   │       │   ├── ExerciseController.java            CRUD exercises + history + PRs
        │   │       │   ├── RoutineController.java             CRUD routines (tenant-isolated)
        │   │       │   ├── WorkoutSessionController.java      sessions lifecycle + history
        │   │       │   └── BodyMeasurementController.java     POST+GET /api/v1/measurements
        │   │       ├── dto/
        │   │       │   ├── request/   (11 records — all with Bean Validation)
        │   │       │   └── response/  (15 records)
        │   │       └── exception/
        │   │           └── GlobalExceptionHandler.java
        │   │
        │   └── resources/
        │       ├── application.properties        ← Config produção (PostgreSQL)
        │       └── application-test.properties   ← Config teste (H2 in-memory)
        │
        └── test/
            └── java/com/mitra/
                ├── MitraApplicationTests.java             ← Context loads
                ├── domain/
                │   ├── model/UserTest, WorkoutSessionTest, BodyMeasurementTest
                │   └── service/BmrCalculatorTest.java, CalorieCalculatorTest.java
                ├── application/usecase/
                │   ├── CalculateBmrUseCaseTest.java
                │   └── impl/  (20 use case tests)
                ├── infrastructure/
                │   ├── config/TestSecurityConfig.java
                │   ├── security/GoogleTokenVerifierAdapterTest.java
                │   └── persistence/adapter/  (7 adapter tests @DataJpaTest)
                └── presentation/controller/  (6 controller tests @WebMvcTest)
```

---

## 5. Arquitetura

```
┌──────────────────────────────────────────────────────────────┐
│                      PRESENTATION                            │
│  Controllers (REST) → DTOs (request/response records)        │
│  @AuthenticationPrincipal User ← SecurityFilter injects      │
└────────────────────┬─────────────────────────────────────────┘
                     │ calls
┌────────────────────▼─────────────────────────────────────────┐
│                      APPLICATION                             │
│  UseCases (interfaces) → impl/ (business orchestration)      │
│  Ports (interfaces) — driven side                            │
└────────────────────┬─────────────────────────────────────────┘
                     │ implemented by
┌────────────────────▼─────────────────────────────────────────┐
│                    INFRASTRUCTURE                            │
│  Adapters → JPA Repositories → Entities → Mappers            │
│  SecurityFilter, TokenService, PasswordEncoderAdapter         │
│  Config beans (SecurityConfig, UseCaseConfig, DatabaseSeeder) │
└──────────────────────────────────────────────────────────────┘
                     │ pure, no dependencies
┌────────────────────▼─────────────────────────────────────────┐
│                       DOMAIN                                 │
│  Models (POJOs + Lombok) — User, Exercise, WorkoutSession... │
│  Services — BmrCalculator (Mifflin-St Jeor)                  │
│  Enums — Gender, TrackingType                                │
└──────────────────────────────────────────────────────────────┘
```

### Regra de Dependência

**Domain → nada** | **Application → Domain** | **Infrastructure → Application + Domain** | **Presentation → Application + Domain**

> A camada Domain **nunca** importa Spring, JPA, ou qualquer framework. `BmrCalculator` é a prova: zero annotations Spring.

---

## 6. Domain Models

### User
| Campo | Tipo | Regra |
|---|---|---|
| id | Long | PK auto-generated |
| email | String | unique, not null |
| name | String | not null |
| password | String | BCrypt encoded, not null |
| birthDate | LocalDate | not null |
| gender | Gender | MALE / FEMALE |
| heightCm | int | not null |
| role | Role | ADMIN / USER (default). Controla permissões. |
| `getAge()` | *derived* | `Period.between(birthDate, now)` |

### Exercise
| Campo | Tipo | Regra |
|---|---|---|
| id | Long | PK |
| name | String | ex: "Bench Press" |
| muscleGroup | String | ex: "Chest" |
| metFactor | BigDecimal | MET value for caloric calc |
| trackingType | TrackingType | WEIGHT_REPS, REPS_ONLY, TIME_ONLY |

### WorkoutRoutine
User → has many → WorkoutRoutine → has many → RoutineExercise → references → Exercise

### WorkoutSession
- `isActive()`: endTime == null
- `isAbandoned()`: ativa por mais de 3h
- `getEffectiveDuration()`: se abandonada, caps em 60min para cálculo calórico
- `finish()`: sets endTime, throws se já finalizada

### SetRecord
Adapta-se ao `TrackingType`: `weightKg` (null para REPS_ONLY/TIME_ONLY), `reps` (null para TIME_ONLY), `durationSeconds` (null para WEIGHT_REPS/REPS_ONLY)

### BodyMeasurement
- `getLeanMass()`: weight × (1 - bodyFat/100)
- `getFatMass()`: weight × (bodyFat/100)

---

## 7. Use Cases (Application)

| Use Case | Input | Output | Notas |
|---|---|---|---|
| `RegisterUserUseCase` | `CreateUserRequestDto` | `Long` (userId) | Encoda senha via `PasswordEncoderPort`, salva `BodyMeasurement` inicial |
| `CalculateBmrUseCase` | `Long` (userId) | `double` (kcal/day) | Busca user + último peso, delega para `BmrCalculator` |
| `UpdateUserProfileUseCase` | `Long userId, UpdateUserProfileRequestDto` | `UserProfileResponseDto` | Atualiza nome, birthDate, gender, height |
| `UpdateUserPasswordUseCase` | `Long userId, UpdateUserPasswordRequestDto` | `void` | Valida senha atual, bloqueia contas OAuth2 |
| `DeleteUserAccountUseCase` | `Long userId` | `void` | Deleta conta permanentemente via `UserRepositoryPort.deleteById` |
| `GetUserDashboardUseCase` | `Long userId` | `DashboardResponseDto` | Streak, workouts/week, calorias/week, último treino |
| `GetUserVolumeSummaryUseCase` | `Long userId, LocalDateTime start, LocalDateTime end` | `List<VolumeSummaryResponseDto>` | Volume (kg×reps) agrupado por grupo muscular |
| `CreateExerciseUseCase` | `CreateExerciseRequestDto` | `Long` | Exige `ROLE_ADMIN` |
| `GetAllExercisesUseCase` | `Pageable` | `Page<ExerciseResponseDto>` | Paginado |
| `GetExerciseHistoryUseCase` | `Long userId, Long exerciseId` | `ExerciseHistoryResponseDto` | Histórico cronológico de séries do exercício 🔒 |
| `GetPersonalRecordsUseCase` | `Long userId, Long exerciseId` | `PersonalRecordResponseDto` | PRs: maior peso, reps, volume, duração 🔒 |
| `CreateWorkoutRoutineUseCase` | `Long userId, CreateRoutineRequestDto` | `Long` | userId vem do `@AuthenticationPrincipal` |
| `GetWorkoutRoutinesUseCase` | `Long userId, Pageable pageable` | `Page<RoutineResponseDto>` | Inclui exercícios aninhados, paginado, `@Transactional(readOnly=true)` |
| `AddRoutineExerciseUseCase` | `Long userId, Long routineId, AddRoutineExerciseRequestDto` | `RoutineExerciseResponseDto` | 🔒 **Ownership**: valida `routine.userId == userId`, throws `SecurityException` |
| `StartWorkoutSessionUseCase` | `Long userId, StartSessionRequestDto` | `Long` | userId vem do `@AuthenticationPrincipal` |
| `LogSetRecordUseCase` | `Long userId, Long sessionId, LogSetRequestDto` | `SetRecordResponseDto` | 🔒 **Ownership**: valida `session.userId == userId`, throws `SecurityException` |
| `FinishWorkoutSessionUseCase` | `Long userId, Long sessionId` | `SessionSummaryResponseDto` | 🔒 **Ownership**: valida `session.userId == userId`, throws `SecurityException` |
| `GetWorkoutSessionUseCase` | `Long userId, Long sessionId` | `WorkoutSessionResponseDto` | 🔒 **Ownership**: valida `session.userId == userId`, throws `SecurityException` |
| `GetUserSessionsUseCase` | `Long userId, LocalDate start, LocalDate end, Pageable` | `Page<WorkoutSessionResponseDto>` | Paginado com filtro de data opcional |
| `CalculateSessionCaloriesUseCase` | `Long userId, Long sessionId` | `SessionCaloriesResponseDto` | 🔒 **Ownership**: valida, requer `BodyMeasurement` para retornar valor |
| `CreateBodyMeasurementUseCase` | `Long userId, CreateBodyMeasurementRequestDto` | `BodyMeasurementResponseDto` | Calcula `leanMassKg` e `fatMassKg` derivados |
| `GetBodyMeasurementsUseCase` | `Long userId, Pageable pageable` | `Page<BodyMeasurementResponseDto>` | Histórico paginado de medições do user autenticado |

#### UseCases Puros: Modelos de Inteligência e Cálculos
- `GoogleLoginUseCase`: Valida idToken (via Port), registra/encontra usuário e emite JWT.
- `CalculateBmrUseCase`: Usa fórmula Mifflin-St Jeor no `BmrCalculator`.
- `CalculateSessionCaloriesUseCase`: Calcula gasto calórico de uma sessão inteira via MET.
- `GetExerciseHistoryUseCase`: Busca histórico cronológico de séries do usuário para um exercício.
- `GetPersonalRecordsUseCase`: Calcula PRs (maior peso, reps, volume, duração).
- `GetUserDashboardUseCase`: Agrega streak, treinos/semana, calorias/semana e último treino.
- `GetUserVolumeSummaryUseCase`: Agrega volume total por grupo muscular em um período.

---

## 8. API Endpoints

### Auth — `/api/v1/auth`
| Método | Path | Auth | Descrição |
|---|---|---|---|
| POST | `/login` | ❌ público | Retorna JWT (`TokenResponseDto`) |
| POST | `/google` | ❌ público | Valida `idToken` Google e retorna JWT |

### Users — `/api/v1/users`
| Método | Path | Auth | Descrição |
|---|---|---|---|
| POST | `/` | ❌ público | Registra novo usuário (valida password == confirmPassword) |
| GET | `/me/bmr` | ✅ Bearer | Calcula BMR do usuário logado |
| PUT | `/me` | ✅ Bearer | Atualiza perfil (name, birthDate, gender, heightCm) |
| PUT | `/me/password` | ✅ Bearer | Altera senha (exige senha atual, bloqueia contas OAuth2) |
| DELETE | `/me` | ✅ Bearer | Deleta conta permanentemente |
| GET | `/me/dashboard` | ✅ Bearer | Dashboard: streak, treinos/semana, calorias, último treino |
| GET | `/me/volume-summary` | ✅ Bearer | Volume por grupo muscular (`?startDate=&endDate=`, default últimos 7 dias) |

### Exercises (Catálogo, Histórico e PRs) — `/api/v1/exercises`
| Método | Path | Auth | Descrição |
|---|---|---|---|
| POST | `/` | ✅ Bearer (`ADMIN`) | Registra exercício no catálogo global |
| GET | `/` | ✅ Bearer | Lista catálogo (suporta `Pageable`, default size 20) |
| GET | `/{exerciseId}/history` | ✅ Bearer 🔒 | Histórico cronológico de séries do exercício |
| GET | `/{exerciseId}/records` | ✅ Bearer 🔒 | Personal Records do exercício |

### Routines — `/api/v1/routines`
| Método | Path | Auth | Descrição |
|---|---|---|---|
| POST | `/` | ✅ Bearer | Cria rotina para o user autenticado |
| GET | `/` | ✅ Bearer | Lista rotinas do user autenticado (suporta `Pageable`) |
| POST | `/{routineId}/exercises` | ✅ Bearer 🔒 | Adiciona exercício à rotina (ownership enforced) |

### Sessions — `/api/v1/sessions`
| Método | Path | Auth | Descrição |
|---|---|---|---|
| POST | `/` | ✅ Bearer | Inicia sessão de treino |
| GET | `/` | ✅ Bearer | Lista histórico paginado (`?startDate=&endDate=`, suporta `Pageable`) |
| POST | `/{sessionId}/sets` | ✅ Bearer 🔒 | Registra uma série (ownership enforced) |
| POST | `/{sessionId}/finish` | ✅ Bearer 🔒 | Finaliza sessão (ownership enforced) |
| GET | `/{sessionId}` | ✅ Bearer 🔒 | Detalhes da sessão (ownership enforced) |
| GET | `/{sessionId}/calories` | ✅ Bearer 🔒 | Calcula perda calórica (ownership enforced) |

### Body Measurements — `/api/v1/measurements`
| Método | Path | Auth | Descrição |
|---|---|---|---|
| POST | `/` | ✅ Bearer | Registra nova medição corporal |
| GET | `/` | ✅ Bearer | Lista histórico paginado de medições do user autenticado (suporta `Pageable`) |

---

## 9. Segurança & Autenticação

### Fluxo JWT
1. `POST /api/v1/auth/login` com `{ email, password }` 
2. `AuthController` busca user por email, compara senha com `BCryptPasswordEncoder`
3. Gera JWT (HMAC256, issuer `mitra-api`, subject=email, claim userId, expira em 24h)
4. Cliente envia `Authorization: Bearer <token>` nas próximas requests
5. `SecurityFilter` intercepta, valida token, carrega `User` e injeta no `SecurityContextHolder`
6. Controllers recebem o user via `@AuthenticationPrincipal com.mitra.domain.model.User currentUser`

### Tenant Isolation
- Nenhum endpoint aceita `userId` como parâmetro explícito
- O userId é **sempre** derivado do token JWT
- Cada rotina/sessão é criada com o userId do token — um usuário nunca vê dados de outro

### Ownership Enforcement
- Endpoints que operam sobre recursos existentes (sessão, rotina, exercício) validam ownership **na camada de Use Case**
- Se `resource.userId != currentUser.id` → `SecurityException` → **403 Forbidden**
- Endpoints protegidos: `GET/POST /{sessionId}/*`, `POST /{routineId}/exercises`, `GET /exercises/{id}/history`, `GET /exercises/{id}/records`
- O `GlobalExceptionHandler` mapeia `SecurityException` → 403

### Credenciais de Dev
O `DatabaseSeeder` (ativo apenas fora do perfil `test`) cria automaticamente:
- **Email**: `dev@mitra.com`
- **Senha**: `123456`

---

## 10. Testes

### Princípio de Cobertura
> **Regra: 1:1.** Para cada linha de código funcional, deve existir uma linha de teste correspondente. Cobertura máxima é uma prioridade do projeto, não um nice-to-have.

### Estratégia por camada

| Tipo | Quant | Anotação | Banco | O que testa |
|---|---|---|---|---|
| Unit (Domain) | 21 | Nenhuma | Nenhum | `BmrCalculator`, `CalorieCalculator`, `User`, `WorkoutSession`, `BodyMeasurement` |
| Unit (UseCase) | ~57 | `@ExtendWith(MockitoExtension)` | Nenhum | Todos os 23 use cases + ownership violations |
| Unit (Security) | 2 | Nenhuma | Nenhum | `GoogleTokenVerifierAdapter` |
| Integration (Persistence) | 16 | `@DataJpaTest` | H2 | Todos os 7 Repository Adapters |
| WebMvc (Controller) | ~49 | `@WebMvcTest` | Nenhum | Auth, User, Exercise, Routine, Session, BodyMeasurement controllers |
| Context | 1 | `@SpringBootTest` | H2 | Verifica que o contexto Spring sobe |
| **Total** | **~146** | | |

### Configuração de teste
- Perfil `test` ativo via `@ActiveProfiles("test")`
- `TestSecurityConfig` (`@TestConfiguration @Profile("test")`) fornece `PasswordEncoder` bean e `SecurityFilterChain` permissivo
- `application-test.properties` exclui apenas OAuth2 auto-config
- Controllers WebMvc usam `@AutoConfigureMockMvc(addFilters = false)` + `SecurityContextHolder` com `User` mockado no `@BeforeEach`

### Rodando testes
```bash
cd mitra/mitra
./mvnw test          # Unix
.\mvnw.cmd test      # Windows
```

---

## 11. Common Hurdles & Soluções

### ❌ `NULL not allowed for column "PASSWORD"` nos testes de persistência
**Causa**: `UserEntity.password` é `@Column(nullable = false)`. Testes criados antes da feature de senha não incluíam `.password()` no builder.
**Solução**: Sempre incluir `.password("hashed_password")` no `User.builder()` em testes de integração.

### ❌ `@AuthenticationPrincipal` retorna `null` em testes WebMvc
**Causa**: `@AutoConfigureMockMvc(addFilters = false)` desativa filtros, então `SecurityFilter` não popula o contexto.
**Solução**: Injetar manualmente no `@BeforeEach`:
```java
User testUser = User.builder().id(1L).email("test@mitra.com").name("Test").password("x").build();
var auth = new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList());
SecurityContextHolder.getContext().setAuthentication(auth);
```

### ❌ `MitraApplicationTests.contextLoads()` falha
**Causa**: `SecurityConfig` tem `@Profile("!test")`, então não existia `PasswordEncoder` bean no perfil test. `PasswordEncoderAdapter` (que é `@Component` sem profile) precisa dele.
**Solução**: `TestSecurityConfig` com `@TestConfiguration @Profile("test")` fornece `PasswordEncoder` e `SecurityFilterChain`. `MitraApplicationTests` importa via `@Import(TestSecurityConfig.class)`.

### ❌ Springdoc OpenAPI não funciona com Spring Boot 4
**Causa**: Versões antigas do springdoc não suportam Spring Boot 4.
**Solução**: Usar `springdoc-openapi-starter-webmvc-ui` versão `3.0.2`.

### ❌ `&&` não funciona no PowerShell para encadear comandos Git
**Causa**: PowerShell não suporta `&&` como operador de encadeamento em todas as versões.
**Solução**: Separar em dois comandos: `git add -A` seguido de `git commit -m "..."`.

### ❌ Flyway não roda automaticamente no Spring Boot 4 com `flyway-core`
**Causa**: No Spring Boot 4, adicionar apenas `flyway-core` ao classpath **não** dispara auto-configuração. O pacote mudou para `org.springframework.boot.flyway.autoconfigure`.
**Solução**: Usar `spring-boot-starter-flyway` ao invés de `flyway-core`. Para PostgreSQL, adicionar também `flyway-database-postgresql`. Migrations ficam em `src/main/resources/db/migration/`.

---

## 12. Design Patterns

| Pattern | Onde | Por quê |
|---|---|---|
| **Hexagonal (Ports & Adapters)** | Toda a arquitetura | Isola domain de frameworks. Ports são interfaces na Application, Adapters vivem na Infrastructure. |
| **Use Case (Command)** | `application/usecase/` | Cada operação de negócio é uma classe com método `execute()`. Facilita teste e SRP. |
| **Repository (via Port)** | `application/port/out/` → `infrastructure/persistence/adapter/` | Abstração sobre JPA. Domain nunca vê `@Entity`. |
| **Mapper** | `infrastructure/persistence/mapper/` | Converte Domain ↔ Entity. Sem MapStruct, manual e explícito. |
| **DTO Records** | `presentation/dto/` | Java records imutáveis para request/response. Zero boilerplate. |
| **Filter Chain** | `SecurityFilter` | `OncePerRequestFilter` para validar JWT antes de chegar nos controllers. |
| **Seeder** | `DatabaseSeeder` | `CommandLineRunner` para popular dados iniciais em dev. `@Profile("!test")` para não contaminar testes. |
| **Builder** | Domain models | Lombok `@Builder` em todos os domain models para construção fluente. |

---

## 13. Checklist Pré-Commit (Inviolável)

```
[ ] 1. ./mvnw test passa com 0 falhas e 0 erros
[ ] 2. Código novo tem cobertura de teste correspondente (ratio 1:1)
[ ] 3. Nenhum TODO/FIXME novo sem issue associada
[ ] 4. Novos arquivos seguem a estrutura Hexagonal (domain puro, ports na application, adapters na infrastructure)
[ ] 5. Commit message segue Conventional Commits
[ ] 6. Se adicionou campo obrigatório em Entity → atualizou TODOS os testes de persistência
[ ] 7. Se alterou controller → verificou que @AuthenticationPrincipal está sendo mockado nos testes
[ ] 8. Se adicionou nova rota → verificou permissão no SecurityConfig (permitAll vs authenticated)
[ ] 9. PROJECT.md atualizado se houve mudança estrutural
```

---

## 14. Checklist Pós-Implementação

```
[ ] 1. Todos os 140+ testes passando
[ ] 2. Swagger UI acessível em http://localhost:8080/swagger-ui/index.html
[ ] 3. Login funciona com dev@mitra.com / 123456
[ ] 4. Bearer Token funciona no Swagger (botão Authorize)
[ ] 5. Novas rotas aparecem no Swagger com descrições OpenAPI
[ ] 6. Dados criados são isolados por tenant (não há vazamento entre users)
[ ] 7. git log --oneline mostra commits atômicos e legíveis
[ ] 8. PROJECT.md reflete o estado atual do código
```

---

## 15. Como Rodar Localmente

### Pré-requisitos
- Java 21+
- PostgreSQL 16+ rodando em `localhost:5432` com database `mitra`
- Variável `DB_PASSWORD` configurada

### Comandos
```bash
cd mitra/mitra
./mvnw spring-boot:run     # Sobe em http://localhost:8080
```

### Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

1. `POST /api/v1/auth/login` com `{"email":"dev@mitra.com","password":"123456"}`
2. Copiar o token da resposta
3. Clicar em "Authorize" no Swagger → Colar `Bearer <token>`
4. Agora todas as rotas autenticadas funcionam

---

*Este documento é mantido junto ao código. Se você mudou a arquitetura, **atualize o PROJECT.md no mesmo commit.***
