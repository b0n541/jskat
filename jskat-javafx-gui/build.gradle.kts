plugins {
    id("jskat.java-application-conventions")

    id("org.openjfx.javafxplugin") version "0.0.13"
}

dependencies {
    implementation(project(":jskat-base"))
    implementation(project(":jskat-swing-gui"))
}

javafx {
    modules = listOf("javafx.base", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "19.0.+"
}

version = "0.22.0-SNAPSHOT"

var mainClassWithPackage = "org.jskat.Launcher"

application {
    mainClass.set(mainClassWithPackage)
}

tasks.startScripts {
    mainClassName = mainClassWithPackage
}

tasks.register("fatjar", Jar::class.java) {

    manifest {
        attributes("Main-Class" to mainClassWithPackage)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

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

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get()
        .onEach { println("add from dependencies : ${it.name}") }
        .map { if (it.isDirectory) it else zipTree(it) })
}

//tasks.register("fatjar", Jar::class.java) {
//
//    dependsOn("build")
//
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//
//    manifest {
//        attributes(
//            "Main-Class" to mainClassWithPackage
//        )
//    }
//
//    from(configurations.runtimeClasspath.get()
//        .onEach { println("add from dependencies: ${it.name}") }
//        .map { if (it.isDirectory) it else zipTree(it) })
//
//    archiveBaseName.set(rootProject.name)
//
//    val os: OperatingSystem =
//        org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
//    if (os.isLinux) {
//        archiveClassifier.set("linux")
//    } else if (os.isMacOsX) {
//        archiveClassifier.set("macos")
//    } else if (os.isWindows) {
//        archiveClassifier.set("windows")
//    }
//}
