plugins {
    application

    id("org.openjfx.javafxplugin") version "0.0.9"
}

buildscript {
    repositories {
        mavenCentral()
    }
}

version = "0.19.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
        testImplementation("org.mockito:mockito-core:3.7.7")
        testImplementation("org.assertj:assertj-core:3.19.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

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

tasks {
    startScripts {
        mainClassName = "org.jskat.Launcher"
        defaultJvmOpts = listOf("-Djdk.gtk.version=2")
    }

    jar {
        manifest {
            attributes(
                "Main-Class" to "org.jskat.Launcher"
            )
        }
    }

    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to "org.jskat.Launcher")
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}

application {
    mainClassName = "org.jskat.Launcher"
    applicationDefaultJvmArgs = listOf("-Djdk.gtk.version=2")
}
