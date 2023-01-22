plugins {
    // Apply the java Plugin to add support for Java.
    java
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation("org.slf4j", "slf4j-api", "1.7.36")
    implementation("ch.qos.logback", "logback-classic", "1.2.11")
    implementation("com.google.guava", "guava", "31.1-jre")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.9.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.9.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.9.0")
    testImplementation("org.mockito", "mockito-core", "4.6.1")
    testImplementation("org.assertj", "assertj-core", "3.23.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
