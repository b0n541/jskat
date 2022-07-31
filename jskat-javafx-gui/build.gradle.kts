plugins {
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":jskat-base"))
    implementation(project(":jskat-swing-gui"))
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "17.0.+"
}
