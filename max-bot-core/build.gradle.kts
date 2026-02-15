plugins {
    `java-library`
}

dependencies {
    implementation("com.github.max-messenger:max-bot-api-client-java:main")

    testImplementation("org.junit.jupiter:junit-jupiter:5.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.2")
}
