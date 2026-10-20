import java.security.MessageDigest

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

// Tournament identity is packaged at build time; runtime execution never requires Git.
val tournamentIdentityDirectory = layout.buildDirectory.dir("generated/tournament-identity")
val generateTournamentBuildIdentity = tasks.register("generateTournamentBuildIdentity") {
    outputs.dir(tournamentIdentityDirectory)
    outputs.upToDateWhen { false } // A commit or relevant dirty input may change independently.
    doLast {
        fun git(vararg arguments: String): String {
            val process = ProcessBuilder(listOf("git") + arguments).directory(rootProject.projectDir)
                .redirectErrorStream(true).start()
            val text = process.inputStream.bufferedReader().readText()
            if (process.waitFor() != 0) throw GradleException("Tournament build identity requires Git metadata: $text")
            return text.trimEnd('\n', '\r')
        }
        fun relevant(path: String): Boolean =
            path.startsWith("jskat-base/src/main/") || path.startsWith("jskat-gui/src/main/") ||
            path.startsWith("app/src/main/") || path.startsWith("build-logic/src/main/") ||
            path.endsWith(".gradle.kts") || path == "gradle/libs.versions.toml" ||
            path.startsWith("gradle/wrapper/") || path == "gradlew" || path == "gradlew.bat"
        fun digest(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it.toInt() and 255) }
        val commit = git("rev-parse", "HEAD")
        if (!Regex("[0-9a-f]{40}|[0-9a-f]{64}").matches(commit)) throw GradleException("Full build commit unavailable")
        val files = git("ls-files", "-z", "--cached", "--others", "--exclude-standard")
            .split('\u0000').filter { it.isNotEmpty() && relevant(it) }.distinct().sorted()
        // Length-prefixed UTF-8 path + decimal byte length + lowercase content digest, LF separators.
        val inventory = buildString {
            append("jskat-build-inputs-v1\n")
            for (path in files) {
                val file = rootProject.file(path)
                if (file.isFile) {
                    val bytes = file.readBytes()
                    append(path.toByteArray(Charsets.UTF_8).size).append(':').append(path).append('\n')
                    append(bytes.size).append(':').append(digest(bytes)).append('\n')
                }
            }
        }
        val dirty = git("diff", "--name-only", "-z", "HEAD").split('\u0000').any(::relevant) ||
            git("ls-files", "-z", "--others", "--exclude-standard").split('\u0000').any(::relevant)
        val target = tournamentIdentityDirectory.get().file("org/jskat/tournament/build-identity.properties").asFile
        target.parentFile.mkdirs()
        target.writeText("encoding=jskat-build-inputs-v1\ncommit=$commit\n" +
            if (dirty) "sourceDigest=${digest(inventory.toByteArray(Charsets.UTF_8))}\n" else "")
    }
}
sourceSets.main { resources.srcDir(tournamentIdentityDirectory) }
tasks.named("processResources") { dependsOn(generateTournamentBuildIdentity) }
