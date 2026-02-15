plugins {
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    api(project(":max-bot-core"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("com.github.max-messenger:max-bot-api-client-java:main")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}