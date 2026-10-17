package org.jskat.gui.javafx.model

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.Window
import org.jskat.ai.ml.*
import org.jskat.util.JSkatResourceBundle
import java.text.NumberFormat
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean


/** JavaFX adapter for the startup-only model lifecycle. */
class ModelStartupLifecycle(
    private val manager: ModelManager,
    private val owner: Window,
    private val publishInspection: (ModelInspection) -> Unit
) {

    private val strings = JSkatResourceBundle.INSTANCE
    private val megabyteFormat = NumberFormat.getNumberInstance(strings.locale).apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }

    private fun formatMegabytes(bytes: Long): String = megabyteFormat.format(
        bytes.toDouble() / (1024.0 * 1024.0)
    )

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
                    val updateNow = ButtonType(strings.getString("modelUpdateNow"))
                    val later = ButtonType(strings.getString("modelLater"))
                    val choice = Alert(Alert.AlertType.CONFIRMATION).apply {
                        initOwner(owner)
                        title = strings.getString("modelUpdateTitle")
                        headerText = strings.getString("modelUpdateAvailable", candidate.manifest.version())
                        contentText = strings.getString(
                            "modelUpdatePrompt",
                            local.installedVersion(),
                            formatMegabytes(candidate.manifest.totalBytes())
                        )
                        buttonTypes.setAll(updateNow, later)
                    }.also { centerAlertOnOwner(it) }
                        .showAndWait().orElse(later)
                    if (choice == updateNow) {
                        download(candidate, local)
                    }
                } else {
                    val download = ButtonType(strings.getString("modelDownload"))
                    val abortButton = ButtonType(strings.getString("modelAbort"))
                    val choice = Alert(Alert.AlertType.CONFIRMATION).apply {
                        initOwner(owner)
                        title = strings.getString("modelDataTitle")
                        headerText = strings.getString("modelDataNotInstalled")
                        contentText = strings.getString(
                            "modelDataDownloadPrompt",
                            formatMegabytes(candidate.manifest.totalBytes())
                        )
                        buttonTypes.setAll(download, abortButton)
                    }.also { centerAlertOnOwner(it) }
                        .showAndWait().orElse(abortButton)
                    if (choice == download) {
                        download(candidate, local)
                    }
                }
            }

            ModelUpdateResult.Status.UP_TO_DATE -> publishInspection(manager.inspectLocalAvailability())
            ModelUpdateResult.Status.UNSUPPORTED,
            ModelUpdateResult.Status.FAILED -> showCheckFailure(local, result)
        }
    }

    private fun showCheckFailure(local: ModelInspection, result: ModelUpdateResult) {
        val installed = if (local.isAvailable) {
            strings.getString("modelInstalledReleaseUsable", local.installedVersion())
        } else {
            strings.getString("modelDataUnavailableSession")
        }
        val retryButton = ButtonType(strings.getString("modelRetry"))
        val abortButton = ButtonType(strings.getString("modelAbort"))
        val retry = Alert(Alert.AlertType.ERROR).apply {
            initOwner(owner)
            title = strings.getString("modelCheckFailedTitle")
            headerText = unavailableMessage(result.reason())
            contentText = strings.getString("modelCheckFailurePrompt", installed)
            buttonTypes.setAll(retryButton, abortButton)
        }.also { centerAlertOnOwner(it) }
            .showAndWait().orElse(abortButton)
        if (retry == retryButton) {
            checkAndPresent(manager.inspectLocalAvailability())
        } else {
            publishInspection(manager.inspectLocalAvailability())
        }
    }

    private fun download(candidate: ModelReleaseCandidate, previous: ModelInspection) {
        val progress = DownloadProgressDialog(owner)
        progress.show()
        val cancelled = AtomicBoolean(false)
        CompletableFuture.supplyAsync {
            manager.install(
                candidate, { update -> Platform.runLater { progress.update(update) } },
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
                            title = strings.getString("modelInstallationFailedTitle")
                            headerText = strings.getString("modelInstallationFailedTitle")
                            contentText = if (previous.isAvailable) {
                                strings.getString("modelInstalledReleaseUsable", previous.installedVersion())
                            } else {
                                strings.getString("modelDataUnavailableSession")
                            }
                        }.also { centerAlertOnOwner(it) }
                            .showAndWait()
                        publishInspection(result.inspection() ?: previous)
                    }
                }
            }
        }
        progress.onCancel = { cancelled.set(true) }
    }

    private fun centerAlertOnOwner(alert: Alert) {
        val ownerScene = owner.scene ?: return
        alert.dialogPane.applyCss()
        alert.dialogPane.layout()

        val alertWidth = alert.dialogPane.prefWidth(-1.0)
        val alertHeight = alert.dialogPane.prefHeight(alertWidth)
        alert.x = owner.x + (ownerScene.width - alertWidth) / 2.0
        alert.y = owner.y + ownerScene.y / 2.0 + (ownerScene.height - alertHeight) / 2.0
    }

    private fun unavailableMessage(reason: ModelUnavailableReason?): String = strings.getString(
        when (reason) {
            ModelUnavailableReason.NO_ACTIVE_RELEASE -> "modelUnavailableNoActiveRelease"
            ModelUnavailableReason.ACTIVE_MARKER_INVALID -> "modelUnavailableActiveMarkerInvalid"
            ModelUnavailableReason.ACTIVE_RELEASE_MISSING -> "modelUnavailableActiveReleaseMissing"
            ModelUnavailableReason.MANIFEST_MISSING -> "modelUnavailableManifestMissing"
            ModelUnavailableReason.MANIFEST_INVALID -> "modelUnavailableManifestInvalid"
            ModelUnavailableReason.UNSUPPORTED_VERSION -> "modelUnavailableUnsupportedVersion"
            ModelUnavailableReason.RELEASE_TAG_MISMATCH -> "modelUnavailableReleaseTagMismatch"
            ModelUnavailableReason.REQUIRED_FILE_MISSING -> "modelUnavailableRequiredFileMissing"
            ModelUnavailableReason.FILE_SIZE_MISMATCH -> "modelUnavailableFileSizeMismatch"
            ModelUnavailableReason.FILE_HASH_MISMATCH -> "modelUnavailableFileHashMismatch"
            null -> "modelCheckUnknownFailure"
        }
    )

    private inner class DownloadProgressDialog(owner: Window) {
        private val dialog = Dialog<Void>()
        private val progressBar = ProgressBar()
        private val currentFile = Label(strings.getString("modelContactingService"))
        private val totals = Label("")
        var onCancel: (() -> Unit)? = null

        init {
            dialog.initOwner(owner)
            dialog.initModality(Modality.APPLICATION_MODAL)
            dialog.title = strings.getString("modelDownloadTitle")
            dialog.dialogPane.minWidth = 500.0
            progressBar.progress = -1.0
            val content = GridPane().apply {
                hgap = 10.0
                vgap = 10.0
                padding = Insets(20.0)
                columnConstraints.add(ColumnConstraints().apply { hgrow = Priority.ALWAYS })
                progressBar.maxWidth = Double.MAX_VALUE
                add(currentFile, 0, 0)
                add(progressBar, 0, 1)
                add(totals, 0, 2)
            }
            dialog.dialogPane.content = content
            val cancelButton = ButtonType(strings.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE)
            dialog.dialogPane.buttonTypes.add(cancelButton)
            dialog.setOnCloseRequest { onCancel?.invoke() }
            dialog.dialogPane.lookupButton(cancelButton).setOnMouseClicked { onCancel?.invoke() }
        }

        fun show() = dialog.show()

        fun close() = dialog.close()

        fun update(progress: ModelDownloadProgress) {
            if (progress.indeterminate()) {
                progressBar.progress = -1.0
            } else if (progress.totalBytes() > 0) {
                progressBar.progress = progress.downloadedBytes().toDouble() / progress.totalBytes()
            }
            currentFile.text = progress.currentFile() ?: strings.getString("modelContactingService")
            totals.text = strings.getString(
                "modelDownloadTotals",
                formatMegabytes(progress.downloadedBytes()),
                formatMegabytes(progress.totalBytes()),
                progress.completedFiles(),
                progress.totalFiles()
            )
        }
    }
}
