
# Flight Admin Service

A Spring Boot microservice for administering airports and flights as part of a larger flight booking platform.

The current service is intentionally small. It provides the foundation for managing airports and scheduled flights, with SQL Server persistence, Flyway database migrations, OpenAPI/Swagger documentation, request logging, correlation IDs, and global exception handling.

Future services may include:

- `booking-service` — bookings, passengers, cancellations, rescheduling
- `auth-service` — authentication and authorization
- `payment-service` — payments and refunds
- `notification-service` — customer notifications

Eventually, services can communicate through events such as:

```text
Admin Service
    |
    | FlightCancelled event
    v
Message Broker
    |
    v
Booking Service
    |
    +--> Refund / reschedule
    |
    +--> Customer notification
```

The current focus is the **Admin Service only**.

---

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Gradle (Groovy DSL)
- Spring MVC
- Spring Data JPA
- Hibernate
- Microsoft SQL Server 2022
- Flyway
- Jakarta Bean Validation
- Spring Boot Actuator
- springdoc-openapi / Swagger UI
- SLF4J + Logback
- JUnit 5 (JUnit Jupiter)
- Mockito
- Testcontainers
- Docker / Docker Compose

---

## Current Features

The service currently supports:

### Airports

- Create an airport
- Get all airports
- Get an airport by ID

### Flights

- Create a flight
- Get all flights
- Get a flight by ID

### Infrastructure

- SQL Server running in Docker
- Flyway version-controlled schema migrations
- Hibernate schema validation
- Swagger UI / OpenAPI
- Global exception handling
- Correlation IDs
- Request audit logging to the `webservices` table
- Unexpected application error logging to the `application_errors` table
- Unit tests with JUnit 5 and Mockito
- SQL Server integration tests with Testcontainers
- API/controller integration tests against a real Spring Boot HTTP server

---

# Project Structure

The application is organized primarily by feature/domain rather than placing every controller, service, repository, and entity into large shared technical folders.

```text
flight-admin-service/
|
|-- build.gradle
|-- settings.gradle
|-- gradlew
|-- gradlew.bat
|-- compose.yaml
|-- .env                  # local only - DO NOT COMMIT
|-- .gitignore
|
`-- src/
    |-- main/
    |   |-- java/
    |   |   `-- com/maverick/flightadminservice/
    |   |       |
    |   |       |-- FlightAdminServiceApplication.java
    |   |       |
    |   |       |-- airport/
    |   |       |   |-- Airport.java
    |   |       |   |-- AirportRepository.java
    |   |       |   |-- AirportService.java
    |   |       |   |-- AirportServiceImpl.java
    |   |       |   |-- AirportController.java
    |   |       |   `-- dto/
    |   |       |       |-- CreateAirportRequest.java
    |   |       |       `-- AirportResponse.java
    |   |       |
    |   |       |-- flight/
    |   |       |   |-- Flight.java
    |   |       |   |-- FlightStatus.java
    |   |       |   |-- FlightRepository.java
    |   |       |   |-- FlightService.java
    |   |       |   |-- FlightServiceImpl.java
    |   |       |   |-- FlightController.java
    |   |       |   `-- dto/
    |   |       |       |-- CreateFlightRequest.java
    |   |       |       `-- FlightResponse.java
    |   |       |
    |   |       |-- commons/
    |   |       |   |-- config/
    |   |       |   |   `-- OpenApiConfig.java
    |   |       |   |
    |   |       |   |-- exception/
    |   |       |   |   |-- BadRequestException.java
    |   |       |   |   |-- ResourceNotFoundException.java
    |   |       |   |   |-- ConflictException.java
    |   |       |   |   `-- GlobalExceptionHandler.java
    |   |       |   |
    |   |       |   `-- logging/
    |   |       |       |-- CorrelationIdFilter.java
    |   |       |       `-- WebServiceLoggingFilter.java
    |   |       |
    |   |       |-- error/
    |   |       |   |-- ApplicationError.java
    |   |       |   |-- ApplicationErrorRepository.java
    |   |       |   `-- ErrorAuditService.java
    |   |       |
    |   |       `-- webservice/
    |   |           |-- WebServiceLog.java
    |   |           |-- WebServiceLogRepository.java
    |   |           `-- WebServiceLogService.java
    |   |
    |   `-- resources/
    |       |-- application.yml
    |       `-- db/
    |           `-- migration/
    |               |-- V1__create_airports_table.sql
    |               |-- V2__create_flights_table.sql
    |               |-- V3__create_application_errors_table.sql
    |               `-- V4__create_webservices_table.sql
    |
    |-- test/
    |   `-- java/
    |       `-- com/maverick/flightadminservice/
    |           |-- airport/
    |           |   `-- service/
    |           |       `-- AirportServiceImplTest.java
    |           `-- flight/
    |               `-- service/
    |                   `-- FlightServiceImplTest.java
    |
    `-- integrationTest/
        |-- java/
        |   `-- com/maverick/flightadminservice/
        |       |-- TestContainersConfiguration.java
        |       |-- airport/
        |       |   |-- AirportServiceIntegrationTest.java
        |       |   `-- AirportControllerIntegrationTest.java
        |       `-- flight/
        |           `-- FlightServiceIntegrationTest.java
        `-- resources/
```

---

# Architecture

The current request flow is:

```text
HTTP Request
    |
    v
CorrelationIdFilter
    |
    v
WebServiceLoggingFilter
    |
    v
Controller
    |
    v
Service Interface
    |
    v
Service Implementation
    |
    v
Repository
    |
    v
Hibernate / JPA
    |
    v
SQL Server
```

The service layer is split into an interface and implementation:

```text
FlightController
      |
      v
FlightService
      |
      v
FlightServiceImpl
      |
      v
FlightRepository
```

Controllers should depend on the interface:

```java
private final FlightService flightService;
```

rather than the implementation:

```java
private final FlightServiceImpl flightService;
```

---

# Prerequisites

Install the following before running the project:

- IntelliJ IDEA
- Java 21
- Docker Desktop
- Git
- A GitHub account if pushing the repository remotely

Verify Java:

```bash
java --version
```

Verify Docker:

```bash
docker --version
docker compose version
```

Verify Gradle through the project wrapper:

```bash
./gradlew --version
```

On Windows Command Prompt:

```cmd
gradlew.bat --version
```

The project should compile using Java 21.

---

# IntelliJ Setup

Open the project in IntelliJ.

Check:

```text
File
-> Project Structure
-> Project
```

Use:

```text
SDK: Java 21
Language level: 21
```

Then check:

```text
Settings
-> Build, Execution, Deployment
-> Build Tools
-> Gradle
```

Recommended:

```text
Gradle distribution: Wrapper
Gradle JVM: Project SDK / Java 21
```

---

# Gradle Dependencies

The project uses Gradle with the Groovy DSL.

A representative `build.gradle` is:

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.1.1'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.maverick'
version = '0.0.1-SNAPSHOT'
description = 'flight-admin-service'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-flyway'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'

    implementation 'org.flywaydb:flyway-sqlserver'

    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0'

    runtimeOnly 'com.microsoft.sqlserver:mssql-jdbc'

    testImplementation 'org.springframework.boot:spring-boot-starter-actuator-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-flyway-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-validation-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'

    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

Refresh Gradle in IntelliJ after changing dependencies.

The current project also defines a separate `integrationTest` source set and task. The important testing dependencies/configurations include:

```gradle
sourceSets {
    integrationTest {
        java.srcDir 'src/integrationTest/java'
        resources.srcDir 'src/integrationTest/resources'

        compileClasspath += sourceSets.main.output
        runtimeClasspath += sourceSets.main.output
    }
}

configurations {
    mockitoAgent

    integrationTestImplementation.extendsFrom implementation, testImplementation
    integrationTestRuntimeOnly.extendsFrom runtimeOnly, testRuntimeOnly
}

dependencies {
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    integrationTestImplementation 'org.springframework.boot:spring-boot-testcontainers'
    integrationTestImplementation 'org.testcontainers:testcontainers-mssqlserver'

    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    mockitoAgent('org.mockito:mockito-core') {
        transitive = false
    }
}

tasks.named('test') {
    useJUnitPlatform()

    jvmArgs "-javaagent:${configurations.mockitoAgent.asPath}"

    testLogging {
        events "passed", "failed", "skipped"
        showStandardStreams = true
    }
}

tasks.register('integrationTest', Test) {
    description = 'Runs integration tests.'
    group = 'verification'

    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath

    shouldRunAfter test
    useJUnitPlatform()

    jvmArgs "-javaagent:${configurations.mockitoAgent.asPath}"

    testLogging {
        events "passed", "failed", "skipped"
        showStandardStreams = true
    }
}

tasks.named('check') {
    dependsOn tasks.named('integrationTest')
}
```

Useful commands:

```text
./gradlew test             -> unit tests only
./gradlew integrationTest  -> integration tests only
./gradlew clean build      -> unit + integration tests + package
```

---

# SQL Server with Docker

SQL Server runs locally in a Docker container.

Create `compose.yaml` in the project root:

```yaml
services:
  sqlserver:
    image: mcr.microsoft.com/mssql/server:2022-latest
    container_name: flight-admin-sqlserver

    environment:
      ACCEPT_EULA: "Y"
      MSSQL_PID: "Developer"
      MSSQL_SA_PASSWORD: "${MSSQL_SA_PASSWORD}"

    ports:
      - "1433:1433"

    volumes:
      - sqlserver-data:/var/opt/mssql

    restart: unless-stopped

volumes:
  sqlserver-data:
```

The volume keeps SQL Server data when the container is recreated.

---

# Environment Variables

Create a local `.env` file in the project root:

```env
MSSQL_SA_PASSWORD=YourStrongLocalPasswordHere!
```

Do not commit this file.

Ensure `.gitignore` contains:

```gitignore
.env

.gradle/
build/

.idea/
*.iml

.DS_Store
Thumbs.db
```

The Docker container reads:

```text
MSSQL_SA_PASSWORD
```

The Spring Boot application reads:

```text
DB_PASSWORD
```

The two values should be the same for the current local setup.

In Git Bash:

```bash
export DB_PASSWORD='YourStrongLocalPasswordHere!'
```

When running from IntelliJ:

```text
Run
-> Edit Configurations
-> FlightAdminServiceApplication
-> Environment variables
```

Add:

```text
DB_PASSWORD=YourStrongLocalPasswordHere!
```

---

# Start SQL Server

From the project root:

```bash
docker compose up -d
```

Check status:

```bash
docker compose ps
```

Inspect logs:

```bash
docker compose logs sqlserver
```

Follow logs:

```bash
docker compose logs -f sqlserver
```

Stop following logs with `Ctrl + C`.

---

# Create the Database

The SQL Server container creates the SQL Server instance, but the application database must exist.

Create:

```text
flight_admin
```

Run:

```bash
docker exec flight-admin-sqlserver bash -c \
'/opt/mssql-tools18/bin/sqlcmd \
-S localhost \
-U sa \
-P "$MSSQL_SA_PASSWORD" \
-C \
-Q "CREATE DATABASE flight_admin"'
```

Verify:

```bash
docker exec flight-admin-sqlserver bash -c \
'/opt/mssql-tools18/bin/sqlcmd \
-S localhost \
-U sa \
-P "$MSSQL_SA_PASSWORD" \
-C \
-Q "SELECT name FROM sys.databases"'
```

You should see:

```text
master
tempdb
model
msdb
flight_admin
```

---

# Spring Boot Database Configuration

`src/main/resources/application.yml` should contain the SQL Server connection.

Example:

```yaml
spring:
  application:
    name: flight-admin-service

  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=flight_admin;encrypt=true;trustServerCertificate=true
    username: sa
    password: ${DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver

  jpa:
    hibernate:
      ddl-auto: validate

    open-in-view: false

  flyway:
    enabled: true
    locations: classpath:db/migration

  mvc:
    problemdetails:
      enabled: true

logging:
  level:
    root: INFO
    com.maverick.flightadminservice: INFO

  pattern:
    correlation: "[corr=%X{correlationId:-}] "
```

`trustServerCertificate=true` is convenient for local Docker development and should not be treated as a production TLS configuration.

---

# Why Hibernate Uses `ddl-auto: validate`

The application deliberately does not use:

```yaml
ddl-auto: update
```

Hibernate can automatically modify tables, but that is schema synchronization rather than reliable version-controlled database migration.

This project uses:

```text
JPA Entity
    +
Flyway Migration
```

The workflow is:

```text
Design/change entity
      |
      v
Create Flyway migration
      |
      v
Flyway applies SQL
      |
      v
Hibernate validates entity vs schema
```

This is different from EF Core, where migration generation is integrated into the normal ORM workflow.

Flyway migrations are explicit and version controlled.

---

# Flyway Migrations

Migrations live in:

```text
src/main/resources/db/migration/
```

Current migrations:

```text
V1__create_airports_table.sql
V2__create_flights_table.sql
V3__create_application_errors_table.sql
V4__create_webservices_table.sql
```

Flyway creates:

```text
flyway_schema_history
```

and records each migration that has been applied.

Never modify a migration that has already been applied to a shared environment.

Instead, create a new migration:

```text
V5__add_something.sql
```

---

# Database Model

## Airports

The `airports` table stores:

```text
id
iata_code
icao_code
name
city
country_code
timezone
active
created_at
updated_at
```

Important concepts:

- `id` — internal database primary key
- `iata_code` — 3-character passenger-facing airport code, e.g. `AKL`
- `icao_code` — 4-character aviation code, e.g. `NZAA`
- `timezone` — IANA timezone such as `Pacific/Auckland`
- `active` — allows airports to be deactivated without deleting historical references

## Flights

The `flights` table stores:

```text
id
flight_number
origin_airport_id
destination_airport_id
scheduled_departure
scheduled_arrival
status
created_at
updated_at
```

A flight references two airport rows:

```text
             airports
             /      \
            /        \
      origin          destination
          \            /
           \          /
              flight
```

The flight number alone is not the database primary key because the same commercial flight number can operate on multiple dates.

---

# Running the Application

Make sure SQL Server is running:

```bash
docker compose up -d
```

Then run:

```bash
./gradlew bootRun
```

A successful startup should include messages similar to:

```text
Successfully validated migrations
Tomcat started on port 8080
Started FlightAdminServiceApplication
```

`bootRun` remains active because the web server continues waiting for HTTP requests.

Stop the application with:

```text
Ctrl + C
```

---

# Building the Project

Run:

```bash
./gradlew clean build
```

A successful build ends with:

```text
BUILD SUCCESSFUL
```

The project is configured so `build` runs the unit-test task and the custom `integrationTest` task before packaging succeeds.

If either unit tests or integration tests fail, the build fails and a container image should not be published by CI.

---

# Testing

The project has two deliberately separate test layers.

```text
src/test/java
    |
    +--> JUnit 5 + Mockito unit tests
         No Spring context
         No SQL Server
         No Docker

src/integrationTest/java
    |
    +--> Spring Boot integration tests
         Real Spring ApplicationContext
         Real Hibernate/JPA repositories
         Flyway migrations
         Temporary SQL Server Testcontainer
         Optional real HTTP server for controller/API tests
```

## Unit Tests

Unit tests live in:

```text
src/test/java/
```

Examples:

```text
AirportServiceImplTest
FlightServiceImplTest
```

These tests use JUnit 5 to run assertions and Mockito to replace dependencies such as repositories.

A service unit test should normally follow:

```text
Arrange
    |
    +--> create request/test data
    +--> configure Mockito mocks

Act
    |
    +--> call the method being tested

Assert
    |
    +--> verify returned result or exception
    +--> verify important repository interactions
```

Unit tests should not start Spring, SQL Server, Flyway, Docker, or Testcontainers.

### Run all unit tests

```bash
./gradlew test
```

### Run one unit-test class

```bash
./gradlew test --tests "*AirportServiceImplTest"
```

```bash
./gradlew test --tests "*FlightServiceImplTest"
```

### Run one test method

```bash
./gradlew test \
  --tests "*AirportServiceImplTest.createAirport_shouldCreateAirport_whenRequestIsValid"
```

### Unit-test report

Gradle generates:

```text
build/reports/tests/test/index.html
```

On Windows/Git Bash:

```bash
start build/reports/tests/test/index.html
```

---

## Integration Tests

Integration tests live in:

```text
src/integrationTest/java/
```

Run them with:

```bash
./gradlew integrationTest
```

These tests require **Docker Desktop to be running** because Testcontainers starts a real temporary SQL Server instance.

You do not need to manually start the normal `flight-admin-sqlserver` Compose container for these tests.

The integration-test flow is:

```text
./gradlew integrationTest
        |
        v
Testcontainers connects to Docker
        |
        v
Temporary SQL Server starts
        |
        v
Spring Boot ApplicationContext starts
        |
        v
@ServiceConnection provides DB connection details
        |
        v
Flyway runs migrations
        |
        v
Hibernate validates schema
        |
        v
Integration tests execute
        |
        v
Temporary SQL Server container is removed
```

### Service/database integration tests

Example:

```text
AirportServiceIntegrationTest
```

These verify:

```text
AirportService
    |
    v
AirportServiceImpl
    |
    v
AirportRepository
    |
    v
Hibernate / JPA
    |
    v
SQL Server Testcontainer
```

A service integration-test class can use:

```java
@SpringBootTest
@Import(TestContainersConfiguration.class)
@Transactional
```

`@Transactional` is useful here because the service-level test transaction can be rolled back after the test.

### API/controller integration tests

Example:

```text
AirportControllerIntegrationTest
```

These start a real embedded web server:

```java
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
@Import(TestContainersConfiguration.class)
```

The real request path becomes:

```text
HTTP Request
    |
    v
Tomcat
    |
    v
CorrelationIdFilter
    |
    v
WebServiceLoggingFilter
    |
    v
Jackson / Bean Validation
    |
    v
AirportController
    |
    v
AirportService
    |
    v
AirportRepository
    |
    v
SQL Server Testcontainer
    |
    v
HTTP / JSON Response
```

Useful API integration tests include:

```text
POST airport              -> 201
POST duplicate airport    -> 409
POST invalid airport      -> 400
GET existing airport      -> 200
GET missing airport       -> 404
GET airports              -> 200 + JSON list
```

Each integration test should arrange its own data and be independently runnable. Do not rely on one test running before another.

For API tests, database cleanup can be performed with:

```java
@Autowired
private AirportRepository airportRepository;

@BeforeEach
void cleanDatabase() {
    airportRepository.deleteAll();
}
```

Once flights reference airports, delete dependent data first:

```text
flights first
airports second
```

Do not rely on `@Transactional` on a `RANDOM_PORT` HTTP test to roll back endpoint-created data because the HTTP request runs in a different server thread/transaction.

### Integration-test report

Gradle generates:

```text
build/reports/tests/integrationTest/index.html
```

On Windows/Git Bash:

```bash
start build/reports/tests/integrationTest/index.html
```

---

## Run Every Test

Run only the two test tasks:

```bash
./gradlew test integrationTest
```

Run the complete verification/build:

```bash
./gradlew clean build
```

Because:

```gradle
tasks.named('check') {
    dependsOn tasks.named('integrationTest')
}
```

the build runs both unit and integration tests.

---

## Test Troubleshooting

### Tests pass but Gradle does not show individual test names

Ensure the test task contains:

```gradle
testLogging {
    events "passed", "failed", "skipped"
    showStandardStreams = true
}
```

The same can be configured for `integrationTest`.

---

### Mockito self-attaching / dynamic Java agent warning

A warning such as:

```text
Mockito is currently self-attaching to enable the inline-mock-maker.
Dynamic loading of agents will be disallowed by default in a future release.
```

does not mean the tests failed.

On Java 21+, configure Mockito as a Java agent:

```gradle
configurations {
    mockitoAgent
}

dependencies {
    mockitoAgent('org.mockito:mockito-core') {
        transitive = false
    }
}

tasks.named('test') {
    jvmArgs "-javaagent:${configurations.mockitoAgent.asPath}"
}
```

---

### `Cannot resolve symbol 'ServiceConnection'`

Check:

```gradle
integrationTestImplementation 'org.springframework.boot:spring-boot-testcontainers'
```

Then run:

```bash
./gradlew clean integrationTestClasses
```

and reload the Gradle project in IntelliJ.

Verify the classpath with:

```bash
./gradlew dependencies --configuration integrationTestCompileClasspath
```

Look for:

```text
org.springframework.boot:spring-boot-testcontainers
```

---

### `Cannot resolve symbol 'MSSQLServerContainer'`

Check:

```gradle
integrationTestImplementation 'org.testcontainers:testcontainers-mssqlserver'
```

With Testcontainers 2.x use:

```java
import org.testcontainers.mssqlserver.MSSQLServerContainer;
```

Then:

```bash
./gradlew clean integrationTestClasses
```

and reload Gradle in IntelliJ.

---

### IntelliJ only offers `New -> Directory` under `integrationTest/java`

Ensure:

```gradle
sourceSets {
    integrationTest {
        java.srcDir 'src/integrationTest/java'
        resources.srcDir 'src/integrationTest/resources'
    }
}
```

Then:

```text
Gradle Tool Window
-> Reload All Gradle Projects
```

Prefer letting IntelliJ derive the custom source set from Gradle.

---

### `Autowired members must be defined in valid Spring bean`

Inside an integration-test class this may be an IntelliJ inspection/source-set issue.

First verify Gradle can compile the integration tests:

```bash
./gradlew integrationTestClasses
```

Constructor injection is supported:

```java
@SpringBootTest
@TestConstructor(
    autowireMode = TestConstructor.AutowireMode.ALL
)
class AirportServiceIntegrationTest {

    private final AirportService airportService;

    AirportServiceIntegrationTest(
            AirportService airportService
    ) {
        this.airportService = airportService;
    }
}
```

If Gradle compiles successfully but IntelliJ still warns, reload the Gradle project.

---

### `Could not find a valid Docker environment`

This is a Testcontainers/Docker issue.

Make sure Docker Desktop is running.

From the same terminal:

```bash
docker version
docker info
docker ps
```

Also try:

```bash
docker run --rm hello-world
```

Then:

```bash
./gradlew integrationTest
```

Inspect Docker/Testcontainers environment variables:

```bash
echo $DOCKER_HOST
```

```bash
env | grep -E 'DOCKER|TESTCONTAINERS'
```

A stale manually configured Docker endpoint can prevent Testcontainers from finding Docker.

---

### `Failed to load ApplicationContext`

This is usually a top-level symptom, not the root cause.

Run:

```bash
./gradlew integrationTest --stacktrace
```

Read down to the deepest:

```text
Caused by:
```

Typical root causes:

```text
Could not find a valid Docker environment
Flyway migration failure
SchemaManagementException
Database connection failure
Bean creation failure
Missing configuration
```

Fix the deepest/root cause first.

---

### `SchemaManagementException`

Hibernate found a mismatch between JPA entities and the Flyway-created schema.

Run:

```bash
./gradlew integrationTest --stacktrace
```

Look for:

```text
Schema-validation: missing table
Schema-validation: missing column
Schema-validation: wrong column type
```

Fix the entity/migration mismatch rather than disabling validation.

---

### Flyway migration fails only in integration tests

Testcontainers starts a clean SQL Server database, so every migration must work from scratch:

```text
V1
V2
V3
V4
...
```

Check:

```text
src/main/resources/db/migration/
```

and run:

```bash
./gradlew integrationTest --stacktrace
```

A failure here often means the migration depends on something that was manually created in the local development database.

---

### Duplicate airport / unique-constraint failures between API tests

Each API integration test should start with predictable data.

For airport-only tests:

```java
@BeforeEach
void cleanDatabase() {
    airportRepository.deleteAll();
}
```

Do not depend on test execution order.

Avoid:

```text
POST test creates AKL
GET test expects POST test to have already run
```

Instead, each GET test should create the data it needs in its own Arrange section.

---

### Database cleanup fails because of foreign keys

When flights reference airports:

```java
airportRepository.deleteAll();
```

may fail while flights still reference airport rows.

Use dependency order:

```java
flightRepository.deleteAll();
airportRepository.deleteAll();
```

As the schema grows, move cleanup into a shared integration-test helper.

---

### Integration tests are not being discovered

Confirm:

```text
src/integrationTest/java/
```

and the custom Gradle source set/task exist.

Then run:

```bash
./gradlew integrationTestClasses
```

followed by:

```bash
./gradlew integrationTest --info
```

Tests should use JUnit Jupiter:

```java
import org.junit.jupiter.api.Test;
```

and the task must contain:

```gradle
useJUnitPlatform()
```

---

### Run one failing integration test

One class:

```bash
./gradlew integrationTest \
  --tests "*AirportServiceIntegrationTest"
```

Controller class:

```bash
./gradlew integrationTest \
  --tests "*AirportControllerIntegrationTest"
```

One method:

```bash
./gradlew integrationTest \
  --tests "*AirportControllerIntegrationTest.getAirport_shouldReturn200_whenAirportExists"
```

---

### Useful diagnostic commands

```bash
./gradlew test --info
```

```bash
./gradlew integrationTest
```

```bash
./gradlew integrationTest --stacktrace
```

```bash
./gradlew integrationTestClasses
```

```bash
./gradlew dependencies --configuration integrationTestCompileClasspath
```

```bash
docker version
docker info
docker ps
```

Unit-test report:

```bash
start build/reports/tests/test/index.html
```

Integration-test report:

```bash
start build/reports/tests/integrationTest/index.html
```

---

# Swagger / OpenAPI

Swagger UI is provided by springdoc-openapi.

Start the application and open:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

OpenAPI YAML:

```text
http://localhost:8080/v3/api-docs.yaml
```

Swagger allows API calls to be executed directly from the browser.

---

# API Endpoints

## Airports

### Create Airport

```http
POST /api/v1/airports
```

Example:

```json
{
  "iataCode": "AKL",
  "icaoCode": "NZAA",
  "name": "Auckland Airport",
  "city": "Auckland",
  "countryCode": "NZ",
  "timezone": "Pacific/Auckland"
}
```

Another example:

```json
{
  "iataCode": "SYD",
  "icaoCode": "YSSY",
  "name": "Sydney Airport",
  "city": "Sydney",
  "countryCode": "AU",
  "timezone": "Australia/Sydney"
}
```

### Get All Airports

```http
GET /api/v1/airports
```

### Get Airport by ID

```http
GET /api/v1/airports/{id}
```

---

# Flights

Create airports before creating flights because flights reference airport IDs.

### Create Flight

```http
POST /api/v1/flights
```

Example:

```json
{
  "flightNumber": "NZ101",
  "originAirportId": 1,
  "destinationAirportId": 2,
  "scheduledDeparture": "2026-09-15T08:30:00+12:00",
  "scheduledArrival": "2026-09-15T10:10:00+10:00"
}
```

The application uses `OffsetDateTime`, so the UTC offset is considered when comparing departure and arrival.

A valid rule is:

```text
scheduledArrival > scheduledDeparture
```

### Get All Flights

```http
GET /api/v1/flights
```

### Get Flight by ID

```http
GET /api/v1/flights/{id}
```

---

# Validation and HTTP Status Codes

The service uses global exception handling instead of allowing every exception to become `500 Internal Server Error`.

Typical mapping:

| Situation | Status |
|---|---:|
| Invalid request DTO | 400 |
| Invalid business input | 400 |
| Resource not found | 404 |
| Duplicate/conflicting resource | 409 |
| Unexpected application failure | 500 |

Application exceptions include:

```text
BadRequestException
ResourceNotFoundException
ConflictException
```

The global handler uses:

```java
@RestControllerAdvice
```

and:

```java
@ExceptionHandler(...)
```

to translate exceptions into HTTP responses.

---

# Problem Details Error Responses

Error responses use Spring `ProblemDetail`.

Example validation response:

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "One or more request fields are invalid.",
  "instance": "/api/v1/flights",
  "timestamp": "2026-08-29T09:30:18.511Z",
  "correlationId": "7c2ff49a-c42e-43ca-88d3-08dd07d73a54",
  "errors": {
    "flightNumber": "must not be blank",
    "originAirportId": "must not be null"
  }
}
```

Unexpected errors return a generic message rather than exposing internal stack traces.

---

# Correlation IDs

Every API request receives a correlation ID.

Incoming clients may provide:

```http
X-Correlation-Id: abc-123
```

If one is not supplied, the application creates a UUID.

Example:

```text
7c2ff49a-c42e-43ca-88d3-08dd07d73a54
```

The correlation ID is:

- added to SLF4J MDC
- returned in the response header
- included in logs
- stored in the `webservices` table
- stored against unexpected errors in `application_errors`

Conceptually:

```text
              correlation_id
                    |
          +---------+---------+
          |         |         |
          v         v         v
     webservices   errors    logs
```

This allows a specific failing HTTP request to be traced across the application.

---

# Request Logging

`CorrelationIdFilter` runs first:

```text
Request
   |
   v
CorrelationIdFilter
   |
   v
WebServiceLoggingFilter
   |
   v
Controller
```

`CorrelationIdFilter`:

1. reads or creates an `X-Correlation-Id`
2. places it in MDC
3. adds it to the HTTP response
4. removes it from MDC when the request finishes

`WebServiceLoggingFilter` records API request metadata before/after the controller executes.

The filter records only paths beginning with:

```text
/api/
```

so Swagger and Actuator requests do not fill the audit table.

---

# `webservices` Audit Table

Every API request is recorded in:

```text
webservices
```

Typical columns:

```text
id
correlation_id
http_method
request_path
query_string
response_status
started_at
completed_at
duration_ms
client_ip
user_agent
```

Example query:

```sql
SELECT TOP 50
    id,
    correlation_id,
    http_method,
    request_path,
    response_status,
    duration_ms,
    started_at
FROM webservices
ORDER BY id DESC;
```

Search by correlation ID:

```sql
SELECT *
FROM webservices
WHERE correlation_id = 'YOUR-CORRELATION-ID';
```

The table stores request metadata, not request/response bodies.

This avoids unintentionally storing passwords, tokens, payment information, or large payloads.

---

# Logging

Spring Boot uses SLF4J with Logback by default.

Create loggers like:

```java
private static final Logger log =
        LoggerFactory.getLogger(FlightServiceImpl.class);
```

Use parameterized logging:

```java
log.info(
    "Creating flight flightNumber={} originAirportId={} destinationAirportId={}",
    flightNumber,
    originAirport.getId(),
    destinationAirport.getId()
);
```

Avoid:

```java
System.out.println(...)
```

and avoid logging sensitive data.

Typical levels:

```text
INFO  - normal business/application activity
WARN  - expected but undesirable client/business situations
ERROR - unexpected application/infrastructure failures
DEBUG - development diagnostics
TRACE - extremely detailed diagnostics
```

Do not log the same exception repeatedly in controller, service, repository, and global handler.

The global exception handler should generally be the main place unexpected exceptions are logged with a stack trace.

---

# Useful SQL Queries

### Airports

```sql
SELECT *
FROM airports
ORDER BY id;
```

### Flights

```sql
SELECT *
FROM flights
ORDER BY id;
```

### Flyway History

```sql
SELECT *
FROM flyway_schema_history
ORDER BY installed_rank;
```

### API Request Logs

```sql
SELECT TOP 50 *
FROM webservices
ORDER BY id DESC;
```

### Unexpected Errors

```sql
SELECT TOP 50 *
FROM application_errors
ORDER BY id DESC;
```

### Trace a Correlation ID

```sql
DECLARE @correlationId VARCHAR(100) =
    'YOUR-CORRELATION-ID';

SELECT *
FROM webservices
WHERE correlation_id = @correlationId;

SELECT *
FROM application_errors
WHERE correlation_id = @correlationId;
```

---

# Docker Commands

Start SQL Server:

```bash
docker compose up -d
```

Check status:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs sqlserver
```

Stop the container:

```bash
docker compose stop
```

Start it again:

```bash
docker compose start
```

Remove container/network but keep volume:

```bash
docker compose down
```

Remove container **and database volume**:

```bash
docker compose down -v
```

Use `-v` carefully because it removes persistent SQL Server data.

---

# Connecting to SQL Server Manually

Run a query inside the container:

```bash
docker exec flight-admin-sqlserver bash -c \
'/opt/mssql-tools18/bin/sqlcmd \
-S localhost \
-U sa \
-P "$MSSQL_SA_PASSWORD" \
-C \
-Q "SELECT name FROM sys.databases"'
```

The `-Q` option runs a query directly and exits, so `GO` is not required.

---

# IntelliJ Database Connection

If IntelliJ database tooling is available:

```text
View
-> Tool Windows
-> Database
-> +
-> Data Source
-> Microsoft SQL Server
```

Use:

```text
Host: localhost
Port: 1433
Database: flight_admin
User: sa
Password: your local password
```

For local development you may also need:

```text
encrypt = true
trustServerCertificate = true
```

---

# Testing Common Error Responses

Assume airport IDs `1` and `2` exist.

## 400 - Same Origin and Destination

```json
{
  "flightNumber": "NZ500",
  "originAirportId": 1,
  "destinationAirportId": 1,
  "scheduledDeparture": "2026-11-10T08:00:00+13:00",
  "scheduledArrival": "2026-11-10T09:00:00+13:00"
}
```

## 400 - Arrival Before Departure

```json
{
  "flightNumber": "NZ501",
  "originAirportId": 1,
  "destinationAirportId": 2,
  "scheduledDeparture": "2026-11-10T15:00:00Z",
  "scheduledArrival": "2026-11-10T14:00:00Z"
}
```

## 404 - Missing Airport

```json
{
  "flightNumber": "NZ503",
  "originAirportId": 999999,
  "destinationAirportId": 2,
  "scheduledDeparture": "2026-11-10T15:00:00Z",
  "scheduledArrival": "2026-11-10T18:00:00Z"
}
```

## 409 - Duplicate Flight

Create the same flight number + scheduled departure twice:

```json
{
  "flightNumber": "NZ600",
  "originAirportId": 1,
  "destinationAirportId": 2,
  "scheduledDeparture": "2026-11-20T08:00:00+13:00",
  "scheduledArrival": "2026-11-20T10:00:00+11:00"
}
```

---

# Troubleshooting

---

## `SchemaManagementException`

Hibernate validation found a mismatch between JPA entities and the Flyway-created schema.

Run:

```bash
./gradlew test --stacktrace
```

Look for:

```text
Schema-validation: missing table
Schema-validation: missing column
Schema-validation: wrong column type
```

Often this means that the sql table file for the migration hasnt been created yet.
Only the entity class was created, so flyway cant reconcile the entity classes structure and the sql schema.

---

## Swagger UI

Make sure the application started successfully.

Open:

```text
http://localhost:8080/swagger-ui.html
```

Also try - this is for the openapi specs

```text
http://localhost:8080/v3/api-docs
```

# Nexus Local Docker Registry Setup

This guide documents the working process for setting up Sonatype Nexus Repository Community Edition as a local Docker registry on Windows with Docker Desktop.

## 1. Install Nexus as a Windows Service

Extract Nexus to a permanent location, for example:

```text
C:\nexus\nexus-3.x.x
```

Open **Command Prompt as Administrator**:

```cmd
cd /d C:\nexus\nexus-3.x.x\bin
install-nexus-service.bat
```

Open Windows Services:

```text
Win + R
services.msc
```

Find:

```text
SonatypeNexusRepository
```

Start the service.

---

## 2. Open Nexus

Open Nexus in a browser:

```text
http://localhost:8081
```

Log in with the `admin` user.

The initial admin password is stored under the Nexus data directory in:

```text
sonatype-work\nexus3\admin.password
```

Complete the onboarding process:

- Change the initial admin password
- Accept the Nexus Community Edition EULA
- Complete the remaining setup steps

---

## 3. Create a Docker Hosted Repository

In Nexus, go to:

```text
Settings
→ Repository
→ Repositories
→ Create repository
→ docker (hosted)
```

Create a repository named:

```text
flight-admin-onprem
```

Enable **Path-based routing**.

Save the repository.

The image path will follow this pattern:

```text
<registry-host>/<repository-name>/<image-name>:<tag>
```

Example:

```text
host.docker.internal:8081/flight-admin-onprem/flight-admin-service:0.0.1
```

---

## 4. Enable Docker Authentication

In Nexus, go to:

```text
Settings
→ Security
→ Realms
```

Move:

```text
Docker Bearer Token Realm
```

into the list of active realms.

Save the configuration.

---

## 5. Configure Docker Desktop for the Local HTTP Registry

Because the local Nexus registry is using HTTP rather than HTTPS, Docker Desktop must treat it as an insecure registry.

Open:

```text
Docker Desktop
→ Settings
→ Docker Engine
```

Add:

```json
"insecure-registries": [
  "host.docker.internal:8081"
]
```

Example configuration:

```json
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
    }
  },
  "experimental": false,
  "insecure-registries": [
    "host.docker.internal:8081"
  ]
}
```

Click:

```text
Apply & restart
```

> For production environments, use HTTPS/TLS rather than an insecure HTTP registry.

---

## 6. Verify Docker Loaded the Registry Configuration

Run in PowerShell:

```powershell
docker info | Select-String -Pattern "Insecure Registries" -Context 0,8
```

You should see:

```text
host.docker.internal:8081
```

---

## 7. Verify the Nexus Docker Registry Endpoint

From Windows, run:

```powershell
curl.exe -v http://localhost:8081/v2/
```

A response similar to this is expected:

```text
HTTP/1.1 401 Unauthorized
Docker-Distribution-Api-Version: registry/2.0
```

The `401 Unauthorized` response is expected when authentication is enabled. It confirms that the Docker Registry v2 endpoint is available.

---

## 8. Login to Nexus from Docker

Run:

```powershell
docker login host.docker.internal:8081
```

Enter the Nexus username and password when prompted.

Expected result:

```text
Login Succeeded
```

Do not place passwords directly in commands or commit credentials to source control.

---

## 9. Test the Registry with Alpine

Pull a small public image:

```powershell
docker pull alpine:latest
```

Tag it for the Nexus repository:

```powershell
docker tag alpine:latest `
  host.docker.internal:8081/flight-admin-onprem/alpine:test
```

Verify the new tag:

```powershell
docker images
```

You should see:

```text
host.docker.internal:8081/flight-admin-onprem/alpine   test
```

---

## 10. Push the Image to Nexus

Push the image:

```powershell
docker push `
  host.docker.internal:8081/flight-admin-onprem/alpine:test
```

A successful push will end with output similar to:

```text
Pushed
test: digest: sha256:...
```

---

## 11. Verify the Image in Nexus

Open:

```text
http://localhost:8081
```

Then go to:

```text
Browse
→ flight-admin-onprem
```

The pushed Alpine image should now appear in the repository.

---

## 12. Verify Pulling from Nexus

Remove the local Nexus-tagged image:

```powershell
docker image rm `
  host.docker.internal:8081/flight-admin-onprem/alpine:test
```

Pull it back from Nexus:

```powershell
docker pull `
  host.docker.internal:8081/flight-admin-onprem/alpine:test
```

If the pull succeeds, the registry setup is working end-to-end.

---

## Important Hostname Difference

When accessing Nexus directly from Windows:

```text
http://localhost:8081
```

When Docker Desktop pushes or pulls images:

```text
host.docker.internal:8081
```

This is because Docker Desktop runs its engine in a Linux environment and uses `host.docker.internal` to reach services running on the Windows host.

---

## Image Naming Convention

Use:

```text
host.docker.internal:8081/flight-admin-onprem/<image-name>:<tag>
```

For the Admin API:

```text
host.docker.internal:8081/flight-admin-onprem/flight-admin-service:0.0.1
```

Later, Jenkins can generate tags automatically using a Git commit SHA or build number.

Example:

```text
host.docker.internal:8081/flight-admin-onprem/flight-admin-service:a1b2c3d
```

---

## Intended CI/CD Flow

```text
Git Repository
      ↓
Jenkins
      ↓
Unit Tests
      ↓
Integration Tests
      ↓
Build Spring Boot JAR
      ↓
Build Docker Image
      ↓
Tag Docker Image
      ↓
Push to Nexus
      ↓
Kubernetes Deployment
```

## Difference between the Compose and DockerFiles

For Jenkins, create a dedicated Nexus CI user with only the permissions required to push and pull images. Do not use the Nexus `admin` account for CI/CD.

The DockerFile describes how to build one image, in this case it is for the admin-service

So when you want to build a new image of your spring boot admin service
```text
docker build -t flight-admin-service:local .
```
Whereas the compose.yaml describes how to run multiple containers together, in this case it is sql server container and the admin service container, inside of a single private network.
In our case it is getting the admin api container from the registry.

Once kubernetes is setup, compose will no longer be needed as this part will be done by kubernetes. 

But can also have a local dev setup where compose calls dockerfile which will first build the admin api artifact and then compose combines it into the same vnet as the mysql container.