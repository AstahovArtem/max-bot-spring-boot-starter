plugins {
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.vanniktech.maven.publish")
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

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("MAX Bot Spring Boot Starter")
        description.set("Spring Boot starter for building bots for MAX Messenger")
        url.set("https://github.com/AstahovArtem/max-bot-spring-boot-starter")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("AstahovArtem")
                name.set("Artem Astahov")
                email.set("astahov.artem.work@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/AstahovArtem/max-bot-spring-boot-starter.git")
            developerConnection.set("scm:git:ssh://github.com/AstahovArtem/max-bot-spring-boot-starter.git")
            url.set("https://github.com/AstahovArtem/max-bot-spring-boot-starter")
        }
    }
}
