plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

kotlin {
    jvmToolchain(23)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.springdoc.openapi)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.kotlin.test)
    testImplementation("com.h2database:h2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName = "posting.jar"
}

tasks.register<Copy>("stage") {
    dependsOn(tasks.bootJar)
    from(tasks.bootJar.flatMap { it.archiveFile })
    into(layout.buildDirectory.dir("stage"))
}
