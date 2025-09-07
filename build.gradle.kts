plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "uz.consorgroup"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

extra["springCloudVersion"] = "2024.0.1"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    // Gmail API
    implementation("com.google.api-client:google-api-client:2.5.0")
    implementation("com.google.apis:google-api-services-gmail:v1-rev20240506-2.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.27.0")

    // Аутентификация через OAuth2 (HttpCredentialsAdapter)
    implementation("com.google.auth:google-auth-library-oauth2-http:1.27.0")
    implementation("com.google.apis:google-api-services-gmail:v1-rev20250630-2.0.0")

    implementation("com.google.http-client:google-http-client-gson:1.44.1")


    implementation("org.springframework.boot:spring-boot-starter-mail")


    //Spring Boot Mail
    implementation ("org.springframework.boot:spring-boot-starter-mail")

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Liquibase for database migrations
    implementation("org.liquibase:liquibase-core")

    // core-api-dto
    implementation("uz.consortgroup:core-api-dto:0.0.1")

    // Lombok (compile-time annotations)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    //Google Guava
    implementation ("com.google.guava:guava:32.1.2-jre")

    //Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    //JWT
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Database driver
    runtimeOnly("org.postgresql:postgresql")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Apache Kafka
    implementation("org.springframework.kafka:spring-kafka:3.2.0")

    //Google Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-admin:9.4.0")

    //Feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("io.github.openfeign.form:feign-form-spring:3.8.0")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Тестирование
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
