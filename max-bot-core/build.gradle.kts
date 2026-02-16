plugins {
    `java-library`
    id("com.vanniktech.maven.publish")
}

dependencies {
    implementation("com.github.max-messenger:max-bot-api-client-java:2c2a05e")
    implementation("org.slf4j:slf4j-api:2.0.16")

    testImplementation("org.junit.jupiter:junit-jupiter:5.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.2")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("MAX Bot Core")
        description.set("Core library for building bots for MAX Messenger")
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
