plugins {
    id("jskat.java-library-conventions")

    id("org.openjfx.javafxplugin") version "0.0.13"
}

dependencies {
    implementation(project(":jskat-base"))

    implementation("com.miglayout", "miglayout-swing", "11.0")
}

javafx {
    modules = listOf("javafx.controls")
    version = "19.0.+"
}
