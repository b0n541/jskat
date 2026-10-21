package org.jskat.gui.javafx.tournament

import javafx.scene.control.*
import java.nio.file.Path

/** Existing main-window tabs are presentation only; the application retains the controller. */
class TournamentTabHost(
    private val tabs: TabPane,
    private val controller: TournamentController,
    private val confirmClose: (() -> Boolean)? = null,
    private val openDirectory: (Path) -> Unit = {},
    private val backend: TournamentBackend = TournamentBackend()
) {
    private var monitor: TournamentPanel? = null

    fun openMonitor() {
        tabs.tabs.firstOrNull { it.id == "TOURNAMENT" }?.let { tabs.selectionModel.select(it); return }
        val panel = monitor ?: TournamentPanel(controller, backend, inspectInTab = ::openInspection, openDirectory = openDirectory).also { monitor = it }
        panel.attach()
        add("Tournament", "TOURNAMENT", panel)
    }

    fun openInspection(directory: Path) {
        val panel = TournamentPanel(controller, backend, inspectInTab = ::openInspection, openDirectory = openDirectory, monitorInvocation = false)
        add("Tournament: ${directory.fileName}", "TOURNAMENT_INSPECTION", panel)
        panel.openRun(directory)
    }

    private fun add(title: String, id: String, panel: TournamentPanel) {
        val tab = Tab(title, panel).apply {
            this.id = id
            setOnCloseRequest { event -> event.consume(); close(this) }
        }
        tabs.tabs.add(tab); tabs.selectionModel.select(tab)
    }

    fun close(tab: Tab) {
        val panel = tab.content as? TournamentPanel ?: return
        val detach = { panel.detach(); tabs.tabs.remove(tab); Unit }
        if (panel.monitorsActiveInvocation) controller.closeMonitor(confirmClose ?: { confirm() }, detach)
        else detach()
    }

    private fun confirm(): Boolean {
        val cancel = ButtonType("Cancel tournament and close tab", ButtonBar.ButtonData.OK_DONE)
        val keep = ButtonType("Continue running", ButtonBar.ButtonData.CANCEL_CLOSE)
        val dialog = Alert(Alert.AlertType.WARNING,
            "Admitted games, report publication and resource cleanup continue in this process after the tab closes. Reopen Tournament to monitor them.", cancel, keep)
        dialog.title = "Tournament is active"
        dialog.headerText = "Close tournament monitoring?"
        dialog.initOwner(tabs.scene?.window)
        return dialog.showAndWait().orElse(keep) == cancel
    }
}
