plugins {
    application

    id("org.openjfx.javafxplugin") version "0.0.10"
}

buildscript {
    repositories {
        mavenCentral()
    }
}

version = "0.20.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    dependencies {
        implementation("ch.qos.logback:logback-classic:1.2.7")
        implementation("org.slf4j:slf4j-api:1.7.32")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        testImplementation("org.mockito:mockito-core:4.1.0")
        testImplementation("org.assertj:assertj-core:3.21.0")
    }

    java {
        modularity.inferModulePath.set(true)

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
    mainClass.set("org.jskat.Launcher")
}
