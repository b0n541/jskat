plugins {
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":jskat-base"))
    implementation(project(":jskat-swing-gui"))
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.google.guava:guava:28.2-jre")
}

//sourceSets {
//    main {
//        resources {
//            srcDirs = listOf("src/main/resources")
//            includes = listOf("**/*.fxml", "**/*.properties", "**/*.css")
//        }
//    }
//}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "11.0.+"
}