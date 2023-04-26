plugins {
    id("jskat.kotlin-library-conventions")

    id("org.openjfx.javafxplugin") version "0.0.13"
}

dependencies {
    implementation(project(":jskat-base"))

    implementation("com.miglayout:miglayout-swing:11.1")
}

javafx {
    modules = listOf("javafx.controls")
    version = "20.0.+"
}
