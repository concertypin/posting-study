# AGENTS.md

## Project-specific rules

### Kotlin vs Java

- Kotlin은 편의상 쓰는 언어입니다.
- **결과물은 Java로 있어야 합니다.**
- 새 기능은 Kotlin으로 먼저 작성하고, 내가 컨펌하면 그때 Java로 포팅합니다.
- 즉, PR 전에 무조건 Java로 포팅하는 게 아니라, 컨펌 후에 Java 버전을 만듭니다.
- 단, `src/main/kotlin/`에 있는 Kotlin 파일들은 아직 구현되지 않은 TODO 상태로 남아 있을 수 있으며, Java 포팅 전까지 동작하지 않는 코드로 간주합니다.

### 일반 규칙

- 현재 이 프로젝트는 Spring Boot 3.4.4 + JDK 21 + Gradle 8.13 기반입니다.
- Kotlin 플러그인이 build.gradle.kts에 포함되어 있으므로 Kotlin/JVM 호환 코드는 컴파일됩니다.
- 단, `InMemoryPostRepository`, `InMemoryUserRepository`, `UserService` 등의 Kotlin 구현체는 아직 TODO 상태입니다.
- `PostService`, `PostController`, `UserController` 등 REST API 컨트롤러가 아직 작성되지 않았습니다.
