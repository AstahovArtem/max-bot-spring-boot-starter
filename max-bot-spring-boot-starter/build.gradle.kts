plugins {
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    api(project(":max-bot-core"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("io.micrometer:micrometer-core")
    implementation("com.github.max-messenger:max-bot-api-client-java:2c2a05e")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
