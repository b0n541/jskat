plugins {
    id("jskat.kotlin-library-conventions")
}

dependencies {
    implementation(platform("ai.djl:bom:0.27.0"))
    implementation("ai.djl:api")
    implementation("ai.djl:basicdataset")
    runtimeOnly("ai.djl.mxnet:mxnet-engine")

    implementation("org.jetbrains.kotlinx:dataframe:0.13.1")
}