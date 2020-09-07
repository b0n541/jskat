plugins {
    application

    id("com.github.hierynomus.license") version "0.15.0"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

buildscript {
    repositories {
        mavenCentral()
    }
}

version = "0.18.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    //apply(plugin = "license")

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
        testImplementation("org.mockito:mockito-core:3.5.10")
        testImplementation("org.assertj:assertj-core:3.17.2")
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

//        license {
//            include("**/*.java")
//            header = project.file("LICENSE.header")
//            //ext.year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
//        }
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
}

application {
    mainClassName = "org.jskat.Launcher"
    applicationDefaultJvmArgs = listOf("-Djdk.gtk.version=2")
}
