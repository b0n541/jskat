plugins {
    id("jskat.kotlin-library-conventions")
}

dependencies {
    // ONNX Runtime for ML model inference (1.17+ required for IR version 10)
    implementation("com.microsoft.onnxruntime:onnxruntime:1.31.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.3")

    // Jackson for JSON parsing in tests
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.22.3")
}
