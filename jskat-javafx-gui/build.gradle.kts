plugins {
    id("jskat.kotlin-library-conventions")

    id("org.openjfx.javafxplugin") version "0.0.14"
}

dependencies {
    implementation(project(":jskat-base"))
    implementation(project(":jskat-swing-gui"))
}

javafx {
    modules = listOf("javafx.base", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "20.0.+"
}
