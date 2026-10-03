# SchoolOS Backend

SchoolOS Backend is a Java REST API for the SchoolOS school-administration frontend. It uses Spring MVC, Spring Data JPA/Hibernate, PostgreSQL, and JWT-based login code.

## Technology and prerequisites

- Java 21 JDK
- PostgreSQL (12 or newer recommended)
- Windows PowerShell for the commands below; the Maven Wrapper is included
- Internet access on the first Maven Wrapper/dependency download

The backend uses Spring Boot 4.0.6. No separately installed Maven is required when using `mvnw.cmd`/`./mvnw`.

## Configure PostgreSQL

By default, `src/main/resources/application.properties` connects to:

| Setting | Default |
| --- | --- |
| Database | `school_os` |
| Schema | `schoolos` |
| Username | `postgres` |
| Password | `postgres` |
| HTTP port | `8080` |

Create the database and schema before first startup. Hibernate is configured with `ddl-auto=update` and will create/update tables in the existing schema. For local installations with different database settings, set Spring Boot environment variables instead of editing tracked application properties:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/school_os?currentSchema=schoolos'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = '<your-local-postgres-password>'
```

The root [startup guide](../../startup.md) includes database creation and frontend startup instructions.

## Run the API

From this directory:

```powershell
.\mvnw.cmd spring-boot:run
```

The API listens on `http://localhost:8080` and its main API prefix is `/api`.

Build and test:

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

On macOS/Linux, use `./mvnw` instead of the Windows `mvnw.cmd` script.

## Local demo data (opt-in)

The `local-demo` Spring profile creates missing local sample records for school ID `1`, classes/sections, parents/students, today's and yesterday's attendance, fee balances/payment history, local notification history, and one administrator account. It is idempotent: records matching its documented demo keys are reused, not deleted or reset. It never runs unless explicitly enabled. Seeded notifications are database examples only; no external messages are sent.

Before starting with the profile, provide credentials through environment variables. Choose a local-only password yourself; do not commit it:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'local-demo'
$env:SCHOOLOS_DEMO_ADMIN_USERNAME = 'admin'
$env:SCHOOLOS_DEMO_ADMIN_PASSWORD = '<choose-a-local-demo-password>'
.\mvnw.cmd spring-boot:run
```

The account is created only if that username is absent. If the username already exists, the initializer will not overwrite its password. The frontend's school-scoped API calls currently target school ID `1`.

The demo initializer is intended for a fresh local database. It does not run SQL scripts or erase/update existing records. No real credentials or personal data should be used in this seed.

## Main API areas

Most application APIs are school-scoped under `/api/schools/{schoolId}`:

- `/master/classes` and `/master/sections` — class/section lookups
- `/students` — student management
- `/attendance` — daily attendance and summaries
- `/fees` — student fees and payments
- `/dashboard` — summary metrics
- `/notifications` — local notification records/workflows
- `/parents` — parent records
- `/auth/login` — login (`POST /api/auth/login`, not school-scoped)

Example smoke check after starting the local demo profile:

```powershell
Invoke-RestMethod http://localhost:8080/api/schools/1/master/classes
```

## Code layout

- `src/main/java/com/school/controller/` — REST controllers
- `src/main/java/com/school/service/` — business services
- `src/main/java/com/school/repository/` — Spring Data repositories
- `src/main/java/com/school/model/` — JPA entities
- `src/main/java/com/school/dto/` — request/response types
- `src/main/resources/application.properties` — default local configuration
- `src/main/java/com/school/config/LocalDemoDataInitializer.java` — profile-gated sample data initializer

## Database initialization warning

Spring SQL script initialization is not enabled in the checked-in configuration. In particular, `src/main/resources/schema.sql` begins with `DROP TABLE` statements. **Do not execute that script against a database containing data.** For local development, create an empty database/schema, let Hibernate create tables, and optionally enable the `local-demo` profile. The separate root-level `db-schema/` files are reference scripts, not an automatic migration system.

## Development security warning

This repository's current security configuration is development-only: API authorization is configured to permit requests, and `JwtFilter` currently bypasses token validation. The JWT secret is also present in tracked development properties, and there is an auth controller endpoint that returns an encoded password. Do not expose this backend to an untrusted network or deploy it to production without replacing these settings and reviewing/removing unsafe development endpoints.

## Troubleshooting

- **Connection refused / database missing:** Start PostgreSQL; verify the database name, port, credentials, and `schoolos` schema.
- **Schema does not exist:** Create the schema before launch; Hibernate's `update` setting does not provision the PostgreSQL schema itself.
- **Port 8080 occupied:** Stop the other process or override `SERVER_PORT` for the backend. Update `VITE_API_BASE_URL` and CORS configuration if using a non-default port/origin.
- **Local profile complains about credentials:** Set both `SCHOOLOS_DEMO_ADMIN_USERNAME` and `SCHOOLOS_DEMO_ADMIN_PASSWORD` before startup.
- **Demo login fails after a previous run:** The initializer intentionally does not replace an existing account's password. Use that account's current password or a fresh local database.