plugins {
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":jskat-base"))
    implementation("com.google.guava:guava:30.0-jre")
    implementation("com.miglayout:miglayout-swing:5.2")
    implementation("org.slf4j:slf4j-api:1.7.30")
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "11.0.+"
}
