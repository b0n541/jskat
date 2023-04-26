pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
}

rootProject.name = "JSkat"

include("jskat-base", "jskat-swing-gui", "jskat-javafx-gui", "app")