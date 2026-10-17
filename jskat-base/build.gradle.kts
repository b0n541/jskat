plugins {
    id("jskat.kotlin-library-conventions")
}

dependencies {
    // ONNX Runtime for ML model inference (1.17+ required for IR version 10)
    implementation(libs.onnxruntime)
    implementation(libs.jackson.databind)

    // Jackson for JSON parsing in tests
    testImplementation(libs.jackson.databind)
}
