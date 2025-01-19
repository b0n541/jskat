plugins {
    id("jskat.kotlin-library-conventions")

    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation(project(":jskat-base"))

    implementation("com.miglayout:miglayout-swing:11.4.2")
}

javafx {
    modules = listOf("javafx.base", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "21.0.+"
}
