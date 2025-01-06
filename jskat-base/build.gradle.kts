plugins {
    id("jskat.kotlin-library-conventions")
}

dependencies {
    implementation(platform("ai.djl:bom:0.31.1"))
    implementation("ai.djl:api")
    implementation("ai.djl:basicdataset")
    runtimeOnly("ai.djl.mxnet:mxnet-engine")
}