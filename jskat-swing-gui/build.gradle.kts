plugins {
    id("jskat.kotlin-library-conventions")

    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation(project(":jskat-base"))

    implementation("com.miglayout:miglayout-swing:11.2")
}

javafx {
    modules = listOf("javafx.controls")
    version = "21.0.+"
}
