import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "Kotlin version of the Spring Petclinic REST application"
group = "org.springframework.samples"
// Align with Spring Version
version = "3.1.0"

java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    val kotlinVersion = "1.8.21"
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.1.4"
    id("org.openapi.generator") version "6.6.0"
    id("com.coditory.integration-test") version "1.4.5"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}
tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    implementation("org.glassfish.jaxb:jaxb-runtime")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:5.0.0-beta-04")

    testImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
    testImplementation("com.marcinziolo:kotlin-wiremock:2.0.2")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey:0.5.9")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-kotlin:0.5.9")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation:0.5.9")
    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.6.2")
    testImplementation("io.kotest:kotest-property:5.6.2")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.6.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")


    integrationImplementation("io.rest-assured:kotlin-extensions:5.3.1")
    integrationImplementation("io.rest-assured:spring-mock-mvc-kotlin-extensions:5.3.1")
    integrationImplementation("io.rest-assured:spring-web-test-client:5.3.1")

    runtimeOnly("com.h2database:h2:2.2.220")
    runtimeOnly("org.hsqldb:hsqldb")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation(kotlin("stdlib-jdk8"))
}
