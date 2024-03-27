plugins {
    id("jskat.kotlin-library-conventions")
}

dependencies {
    implementation(platform("ai.djl:bom:0.26.0"))
    implementation("ai.djl:api")
    implementation("ai.djl:model-zoo")
    implementation("ai.djl:basicdataset")
    runtimeOnly("ai.djl.mxnet:mxnet-engine")
}