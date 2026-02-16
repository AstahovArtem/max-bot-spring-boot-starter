plugins {
    `java-library`
}

dependencies {
    implementation("com.github.max-messenger:max-bot-api-client-java:2c2a05e")
    implementation("org.slf4j:slf4j-api:2.0.16")

    testImplementation("org.junit.jupiter:junit-jupiter:5.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.2")
}
