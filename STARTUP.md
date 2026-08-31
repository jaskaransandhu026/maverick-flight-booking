# Startup Guide

This guide explains how to run the project in two ways:

1. Run SQL Server with Docker Compose and run Spring Boot locally with Gradle.
2. Run the full application stack with Docker Compose.

---

## Local Development

Use this workflow when developing and debugging the Spring Boot application from IntelliJ or Git Bash.

### 1. Start SQL Server

From the project root:

```bash
docker compose up -d sqlserver
```

Check that SQL Server is running:

```bash
docker compose ps
```

You should see the `flight-admin-sqlserver` container running.

---

### 2. Load Environment Variables into Git Bash

Docker Compose automatically reads the `.env` file, but `./gradlew bootRun` does not.

Load the variables from `.env` into the current Git Bash shell:

```bash
set -a
source .env
set +a
```

You can verify that the SQL Server password variable is loaded without printing the password:

```bash
if [ -n "$MSSQL_SA_PASSWORD" ]; then
  echo "MSSQL_SA_PASSWORD is set"
else
  echo "MSSQL_SA_PASSWORD is not set"
fi
```

Expected output:

```text
MSSQL_SA_PASSWORD is set
```

---

### 3. Run Spring Boot Locally

Run:

```bash
./gradlew bootRun
```

The local Spring Boot application connects to SQL Server through:

```text
localhost:1433
```

The datasource configuration should use:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=flight_admin;encrypt=true;trustServerCertificate=true
    username: sa
    password: ${MSSQL_SA_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

---

## Local Development Startup Flow

```text
Git Bash
   |
   | docker compose up -d sqlserver
   v
SQL Server container
   |
   | localhost:1433
   v
Spring Boot running locally
   ^
   |
   | ./gradlew bootRun
   |
Environment variables loaded from .env
```

The usual startup commands are:

```bash
docker compose up -d sqlserver

set -a
source .env
set +a

./gradlew bootRun
```

---

## Run the Full Stack with Docker Compose

Use this workflow when testing the containerized version of the application.

Run:

```bash
docker compose up -d
```

This starts both:

```text
flight-admin-sqlserver
flight-admin-service
```

Check container status:

```bash
docker compose ps
```

View Spring Boot logs:

```bash
docker compose logs -f flight-admin-service
```

Press `Ctrl+C` to stop following the logs. This does not stop the containers.

---

## Test the Application

If the Spring Boot service is mapped to port `8080`, test the health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

---

## Stop the Environment

Stop all Compose services:

```bash
docker compose down
```

This removes the containers but keeps the SQL Server volume and database data.

Do not use this unless you intentionally want to delete local database data:

```bash
docker compose down -v
```

The `-v` option removes the persistent SQL Server volume.

---

## Useful Commands

```bash
# Start only SQL Server
docker compose up -d sqlserver

# Start the full stack
docker compose up -d

# Check running services
docker compose ps

# Follow Spring Boot logs
docker compose logs -f flight-admin-service

# Follow SQL Server logs
docker compose logs -f sqlserver

# Restart Spring Boot container
docker compose restart flight-admin-service

# Stop all services and keep database data
docker compose down
```

---

## Environment Variable Notes

The `.env` file should contain:

```env
MSSQL_SA_PASSWORD=your-password-here
```

Make sure `.env` is ignored by Git:

```gitignore
.env
```

Do not commit credentials to source control.

Docker Compose automatically reads `.env`.

For a locally running Spring Boot application, Git Bash must load the variables first:

```bash
set -a
source .env
set +a
```

After that, Gradle and Spring Boot inherit the variables from the shell.

---
## Nexus
Download Nexus Respository community edition and install the community edition 

Login via: 
```
Start-Service SonatypeNexusRepository
```

Check its status:
```
Get-Service SonatypeNexusRepository
```

open at
```
http://localhost:8081
```

For Docker push/pull operations, also make sure Docker Desktop is running. Your registry image address remains:
```
host.docker.internal:8081/flight-admin-onprem/...
```

Login via cli 
```powershell
docker login host.docker.internal:8081
```

To stop Nexus later:
```powershell
Stop-Service SonatypeNexusRepository
```


