var deeplearning4jVersion = "1.0.0-M1.1"

dependencies {
    implementation("com.google.guava", "guava", "31.0.1-jre")
    implementation("org.apache.commons", "commons-math3", "3.6.1")

    implementation("org.deeplearning4j", "deeplearning4j-core", "${deeplearning4jVersion}")
    implementation("org.deeplearning4j", "deeplearning4j-ui", "${deeplearning4jVersion}")
    implementation("org.nd4j", "nd4j-native-platform", "${deeplearning4jVersion}")
    implementation("org.datavec", "datavec-local", "${deeplearning4jVersion}") {
        // incompatible with Deeplearning4J UI
        exclude("org.datavec", "datavec-arrow")
    }
}
