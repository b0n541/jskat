plugins {
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":jskat-base"))
    implementation(project(":jskat-swing-gui"))
    implementation("com.google.guava:guava:30.1.1-jre")
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "11.0.+"
}
