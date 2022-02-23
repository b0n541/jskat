plugins {
    application

    id("org.openjfx.javafxplugin") version "0.0.12"
}

buildscript {
    repositories {
        mavenCentral()
    }
}

version = "0.21.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    dependencies {
        implementation("org.slf4j", "slf4j-api", "1.7.32")
        implementation("ch.qos.logback", "logback-classic", "1.2.7")

        testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.8.2")
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.8.2")
        testImplementation("org.mockito", "mockito-core", "4.1.0")
        testImplementation("org.assertj", "assertj-core", "3.21.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withSourcesJar()
        withJavadocJar()
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(project("jskat-base"))
    implementation(project("jskat-swing-gui"))
    implementation(project("jskat-javafx-gui"))
}

var mainClassWithPackage = "org.jskat.Launcher"

application {
    mainClass.set(mainClassWithPackage)
}

tasks.startScripts {
    mainClassName = mainClassWithPackage
}

tasks.register("fatjar", Jar::class.java) {

    dependsOn("build")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Main-Class" to mainClassWithPackage
        )
    }

    from(configurations.runtimeClasspath.get()
        .onEach { println("add from dependencies: ${it.name}") }
        .map { if (it.isDirectory) it else zipTree(it) })

    archiveBaseName.set(rootProject.name)

    val os: OperatingSystem =
        org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
    if (os.isLinux) {
        archiveClassifier.set("linux")
    } else if (os.isMacOsX) {
        archiveClassifier.set("macos")
    } else if (os.isWindows) {
        archiveClassifier.set("windows")
    }
}
