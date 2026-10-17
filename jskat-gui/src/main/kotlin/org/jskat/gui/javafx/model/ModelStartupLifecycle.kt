package org.jskat.gui.javafx.model

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Window
import org.jskat.ai.ml.ModelCancellation
import org.jskat.ai.ml.ModelDownloadProgress
import org.jskat.ai.ml.ModelInspection
import org.jskat.ai.ml.ModelInstallResult
import org.jskat.ai.ml.ModelManager
import org.jskat.ai.ml.ModelUpdateResult
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

/** JavaFX adapter for the startup-only model lifecycle. */
class ModelStartupLifecycle(
    private val manager: ModelManager,
    private val owner: Window,
    private val publishInspection: (ModelInspection) -> Unit
) {

    fun start() {
        val local = manager.inspectLocalAvailability()
        publishInspection(local)
        checkAndPresent(local)
    }

    private fun checkAndPresent(local: ModelInspection) {
        CompletableFuture.supplyAsync { manager.checkForUpdates() }
            .thenAccept { result -> Platform.runLater { presentResult(local, result) } }
    }

    private fun presentResult(local: ModelInspection, result: ModelUpdateResult) {
        when (result.status()) {
            ModelUpdateResult.Status.AVAILABLE -> {
                val candidate = result.candidate() ?: return
                if (local.isAvailable) {
                    val choice = Alert(Alert.AlertType.CONFIRMATION).apply {
                        initOwner(owner)
                        title = "JSkat model update"
                        headerText = "Model release ${candidate.manifest.version()} is available"
                        contentText = "Update now or keep the installed release ${local.installedVersion()}?"
                        buttonTypes.setAll(ButtonType("Update now"), ButtonType("Later"))
                    }.showAndWait().orElse(ButtonType("Later"))
                    if (choice.text == "Update now") {
                        download(candidate, local)
                    }
                } else {
                    val choice = Alert(Alert.AlertType.CONFIRMATION).apply {
                        initOwner(owner)
                        title = "JSkat model data"
                        headerText = "ML model data is not installed"
                        contentText = "Download the model data now? ML player types stay disabled if you continue."
                        buttonTypes.setAll(ButtonType("Download"), ButtonType("Continue"))
                    }.showAndWait().orElse(ButtonType("Continue"))
                    if (choice.text == "Download") {
                        download(candidate, local)
                    }
                }
            }
            ModelUpdateResult.Status.UP_TO_DATE -> publishInspection(manager.inspectLocalAvailability())
            ModelUpdateResult.Status.UNSUPPORTED,
            ModelUpdateResult.Status.FAILED -> showCheckFailure(local, result.message())
        }
    }

    private fun showCheckFailure(local: ModelInspection, message: String) {
        val installed = if (local.isAvailable) {
            "Installed model release ${local.installedVersion()} remains usable."
        } else {
            "Model data and ML players are unavailable for this session."
        }
        val retry = Alert(Alert.AlertType.ERROR).apply {
            initOwner(owner)
            title = "JSkat model check failed"
            headerText = message
            contentText = "$installed\nChoose Retry to repeat the startup check or Continue to open JSkat."
            buttonTypes.setAll(ButtonType("Retry"), ButtonType("Continue"))
        }.showAndWait().orElse(ButtonType("Continue"))
        if (retry.text == "Retry") {
            checkAndPresent(manager.inspectLocalAvailability())
        } else {
            publishInspection(manager.inspectLocalAvailability())
        }
    }

    private fun download(candidate: org.jskat.ai.ml.ModelReleaseCandidate, previous: ModelInspection) {
        val progress = DownloadProgressDialog(owner)
        progress.show()
        val cancelled = AtomicBoolean(false)
        CompletableFuture.supplyAsync {
            manager.install(candidate, { update -> Platform.runLater { progress.update(update) } },
                ModelCancellation { cancelled.get() })
        }.thenAccept { result ->
            Platform.runLater {
                progress.close()
                when (result.status()) {
                    ModelInstallResult.Status.INSTALLED -> publishInspection(result.inspection())
                    ModelInstallResult.Status.CANCELLED -> publishInspection(
                        result.inspection() ?: previous
                    )
                    ModelInstallResult.Status.FAILED -> {
                        Alert(Alert.AlertType.ERROR).apply {
                            initOwner(owner)
                            title = "JSkat model installation failed"
                            headerText = result.message()
                            contentText = if (previous.isAvailable) {
                                "Installed model release ${previous.installedVersion()} remains usable."
                            } else {
                                "Model data and ML players are unavailable for this session."
                            }
                        }.showAndWait()
                        publishInspection(result.inspection() ?: previous)
                    }
                }
            }
        }
        progress.onCancel = { cancelled.set(true) }
    }

    private class DownloadProgressDialog(owner: Window) {
        private val dialog = Dialog<Void>()
        private val progressBar = ProgressBar()
        private val currentFile = Label("Contacting model service…")
        private val totals = Label("")
        var onCancel: (() -> Unit)? = null

        init {
            dialog.initOwner(owner)
            dialog.title = "Downloading JSkat model data"
            progressBar.progress = -1.0
            val content = GridPane().apply {
                hgap = 10.0
                vgap = 10.0
                padding = Insets(20.0)
                add(currentFile, 0, 0)
                add(progressBar, 0, 1)
                add(totals, 0, 2)
            }
            dialog.dialogPane.content = content
            dialog.dialogPane.buttonTypes.add(ButtonType.CANCEL)
            dialog.setOnCloseRequest { onCancel?.invoke() }
            dialog.dialogPane.lookupButton(ButtonType.CANCEL).setOnMouseClicked { onCancel?.invoke() }
        }

        fun show() = dialog.show()

        fun close() = dialog.close()

        fun update(progress: ModelDownloadProgress) {
            if (progress.indeterminate()) {
                progressBar.progress = -1.0
            } else if (progress.totalBytes() > 0) {
                progressBar.progress = progress.downloadedBytes().toDouble() / progress.totalBytes()
            }
            currentFile.text = progress.currentFile() ?: "Contacting model service…"
            totals.text = "${progress.downloadedBytes()} / ${progress.totalBytes()} bytes; " +
                "${progress.completedFiles()} / ${progress.totalFiles()} files"
        }
    }
}
