# AGENTS.md

## Cursor Cloud specific instructions

### Overview
This workspace contains 10 independent git repositories under `/agent/repos/`. Most are Java/Maven projects; one is Node.js.

### JDK Requirements
- **Java 11** (`/usr/lib/jvm/java-11-openjdk-amd64`): Used by `rss-analyzer`, `task-manager`, `SimpleMVC`, `spring5webapp`, `spring-core-spring-mvc`, `spring-boot-mongodb`
- **Java 17** (`/usr/lib/jvm/java-17-openjdk-amd64`): Used by `uml-reverse-mapper`
- Set `JAVA_HOME` before running Maven commands, e.g. `export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64`

### Running Services
| Repository | Command | Port | Notes |
|---|---|---|---|
| rss-analyzer | `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./mvnw spring-boot:run` | 8080 | Self-contained (H2). Swagger at `/swagger-ui.html` |
| task-manager | `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./mvnw spring-boot:run` | 8080 | Requires PostgreSQL running (`sudo pg_ctlcluster 16 main start`) |
| nodejs-ex | `PORT=3000 npm start` | 3000 | Works without MongoDB (page count = -1) |
| spring5webapp | `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./mvnw spring-boot:run` | 8080 | Self-contained (H2) |

### PostgreSQL Setup (for task-manager)
PostgreSQL 16 is installed. To start:
```
sudo pg_ctlcluster 16 main start
```
Database `sample` with schema `Tasks` and table `Tasks.Tasks` is pre-created. Connection: `localhost:5432`, user `postgres`, password `postgres`.

### Known Issues (pre-existing in code)
- **spring-core-spring-mvc**: Tests fail on Java 11+ due to old Hibernate Validator incompatibility. Build succeeds; would need JDK 8 for tests to pass.
- **JsonLd-test**: Build fails due to missing Maven dependency (`org.eclipse.rdf4j:eclipse-rdf4j:jar:onejar:3.2.0` doesn't exist in Maven Central).
- **spring-boot-mongodb**: Requires a running MongoDB instance on `localhost:27017` to start.
- **spring-core-spring-mvc** and **spring-boot-mongodb**: Maven wrappers (`mvnw`) are broken (missing wrapper jar); use system `mvn` instead.

### Testing
- `rss-analyzer`: `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./mvnw test` (6 tests)
- `task-manager`: `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./mvnw test` (1 test, needs PostgreSQL)
- `spring5webapp`: `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./mvnw test` (1 test)
- `uml-reverse-mapper`: `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./mvnw test` (2 tests per module)
- `nodejs-ex`: `npm test` (2 tests)
