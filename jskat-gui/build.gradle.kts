plugins {
    id("jskat.kotlin-library-conventions")

    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation(project(":jskat-base"))
}

javafx {
    modules = listOf("javafx.base", "javafx.fxml", "javafx.web")
    version = "26.0.2"
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
