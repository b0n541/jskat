import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    // Apply the java Plugin to add support for Java.
    java
}

val dependencyCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(dependencyCatalog.findLibrary("slf4j-api").get())
    implementation(dependencyCatalog.findLibrary("logback-classic").get())
    implementation(dependencyCatalog.findLibrary("guava").get())

    testImplementation(dependencyCatalog.findLibrary("junit-jupiter-api").get())
    testImplementation(dependencyCatalog.findLibrary("junit-jupiter-params").get())
    testRuntimeOnly(dependencyCatalog.findLibrary("junit-jupiter-engine").get())
    testRuntimeOnly(dependencyCatalog.findLibrary("junit-platform-launcher").get())
    testImplementation(dependencyCatalog.findLibrary("mockito-core").get())
    testImplementation(dependencyCatalog.findLibrary("assertj-core").get())
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

    withSourcesJar()
    withJavadocJar()
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
