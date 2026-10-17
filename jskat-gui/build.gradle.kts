import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("jskat.kotlin-library-conventions")

    alias(libs.plugins.javafx)
}

dependencies {
    implementation(project(":jskat-base"))
}

val javafxVersion = extensions.getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion("javafx")
    .get()
    .requiredVersion

javafx {
    modules = listOf("javafx.base", "javafx.fxml", "javafx.web")
    version = javafxVersion
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.FAIL
}
