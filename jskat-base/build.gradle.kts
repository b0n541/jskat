import java.net.URI

plugins {
    id("jskat.kotlin-library-conventions")
}

dependencies {
    // ONNX Runtime for ML model inference (1.17+ required for IR version 10)
    implementation("com.microsoft.onnxruntime:onnxruntime:1.27.0")

    // Jackson for JSON parsing in tests
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.21.3")
}

// ML Models download configuration
val mlModelsVersion = "1.4.0"
val mlModelsDir = rootProject.file(".jskat/models")
val mlModelsBaseUrl = "https://github.com/avaskys/skat-ml-models/releases/download/v$mlModelsVersion"

val mlModelFiles = listOf(
    "bidding_dense.onnx",
    "bidding_dense.onnx.data",
    "bidding_transformer.onnx",
    "bidding_transformer.onnx.data",
    "card_play_transformer.onnx",
    "game_eval_dense.onnx",
    "game_eval_dense.onnx.data",
    "game_eval_transformer.onnx",
    "game_eval_transformer.onnx.data"
)

tasks.register("downloadMlModels") {
    description = "Downloads ML models from skat-ml-models releases"
    group = "build setup"

    outputs.dir(mlModelsDir)

    doLast {
        val versionFile = File(mlModelsDir, ".version")
        val needsDownload = !versionFile.exists() || versionFile.readText().trim() != mlModelsVersion

        if (needsDownload) {
            logger.lifecycle("Models version changed or missing, clearing old models...")
            mlModelsDir.deleteRecursively()
        }

        mlModelsDir.mkdirs()

        mlModelFiles.forEach { fileName ->
            val targetFile = File(mlModelsDir, fileName)
            if (!targetFile.exists()) {
                val url = "$mlModelsBaseUrl/$fileName"
                logger.lifecycle("Downloading $fileName...")
                try {
                    URI(url).toURL().openStream().use { input ->
                        targetFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    logger.lifecycle("  -> ${targetFile.absolutePath}")
                } catch (e: Exception) {
                    logger.warn("Failed to download $fileName: ${e.message}")
                }
            } else {
                logger.lifecycle("$fileName already exists, skipping")
            }
        }

        versionFile.writeText(mlModelsVersion)
    }
}

// Ensure models are downloaded before tests run
tasks.named("test") {
    dependsOn("downloadMlModels")
}
