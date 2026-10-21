package org.jskat.gui.javafx.tournament

import javafx.application.Platform
import javafx.beans.property.ReadOnlyStringWrapper
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import org.jskat.tournament.*
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.Executor

/** A disposable monitor/editor; all expensive work is owned outside the JavaFX thread. */
class TournamentPanel(
    val controller: TournamentController,
    private val backend: TournamentBackend = TournamentBackend(),
    private val inspectInTab: ((Path) -> Unit)? = null,
    private val openDirectory: (Path) -> Unit = {},
    private var monitorInvocation: Boolean = true
) : BorderPane() {
    val editor = TournamentEditor()
    val issuesView = ListView<ValidationIssue>()
    private val detail = TextArea().apply { isEditable = false; prefRowCount = 7 }
    val status = Label("Ready").apply { id = "tournament-status" }
    private val progress = Label().apply { isWrapText = true; maxWidth = Double.MAX_VALUE; minWidth = 0.0 }
    private val fallback = Label().apply { isWrapText = true; minWidth = 0.0 }
    private val ratings = Label("Uncertainty unavailable until published analysis")
    data class LeaderboardRow(val rank: String, val name: String, val implementation: String, val rating: String, val uncertainty: String)
    private val leaderboardTable = TableView<LeaderboardRow>().apply {
        id = "tournament-leaderboard"
        fun column(title: String, value: (LeaderboardRow) -> String) = TableColumn<LeaderboardRow, String>(title).apply {
            setCellValueFactory { ReadOnlyStringWrapper(value(it.value)) }
            isSortable = false
            setCellFactory { object : TableCell<LeaderboardRow, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty) null else item
                    tooltip = if (empty || item == null) null else Tooltip(item)
                }
            } }
            prefWidth = when (title) { "Rank" -> 50.0; "Rating" -> 90.0; "Implementation" -> 135.0; "Uncertainty (95%)" -> 260.0; else -> 135.0 }
        }
        columns.addAll(column("Rank") { it.rank }, column("Competitor") { it.name }, column("Implementation") { it.implementation },
            column("Rating") { it.rating }, column("Uncertainty (95%)") { it.uncertainty })
        placeholder = Label("No complete epochs yet — no rated evidence")
        prefHeight = 150.0
        minHeight = 90.0
    }
    private var displayedSummary: String? = null
    private val summary = TextArea().apply { isEditable = false }
    private val leaderboard = TextArea().apply { isEditable = false }
    private val scores = TextArea().apply { isEditable = false }
    private val audit = TextArea().apply { isEditable = false }
    private val generation = Label("No published report generation").apply { isWrapText = true; minWidth = 0.0 }
    private val workers = TextField("1").apply { prefColumnCount = 4 }
    private val stall = TextField("60").apply { prefColumnCount = 5 }
    private val verbose = CheckBox("Verbose")
    private val editingActions = mutableListOf<Control>()
    private val start = Button("Start new").apply { id = "tournament-start" }
    private val verify = Button("Verify retained run").apply { id = "tournament-verify" }
    private val resume = Button("Resume").apply { id = "tournament-resume" }
    private val recovery = Button("Recover stale ownership and resume…").apply { isVisible = false; isManaged = false }
    private val validate = Button("Validate").apply { id = "tournament-validate" }
    private val cancel = Button("Cancel after admitted games")
    private var followController = true
    private var boundConfiguration: TournamentConfiguration? = null
    val monitorsActiveInvocation: Boolean get() = monitorInvocation && controller.active
    private var inspectionMode = false
    private var lastRenderedProgress: TournamentProgress? = null
    private var retained: TournamentConfiguration? = null
    private var openedDirectory: Path? = null
    private var verified = false
    private var completed = false
    private var busy = false
    private var nonReproducible = false
    private var subscription: AutoCloseable? = null
    private val background = Executor { Thread(it, "tournament-inspection").start() }

    init {
        padding = Insets(8.0)
        val new = Button("New run").apply { setOnAction { if (!controller.active && !busy) newConfiguration() } }
        val import = Button("Import JSON…").apply { setOnAction {
            FileChooser().showOpenDialog(scene?.window)?.let { importConfiguration(it.toPath()) }
        } }
        val export = Button("Export JSON…").apply { setOnAction {
            FileChooser().showSaveDialog(scene?.window)?.let { exportConfiguration(it.toPath()) }
        } }
        val open = Button("Open run…").apply { setOnAction {
            DirectoryChooser().showDialog(scene?.window)?.let { if (inspectInTab != null) inspectInTab.invoke(it.toPath()) else openRun(it.toPath()) }
        } }
        val discover = Button("Discover models…").apply { setOnAction { discover(PlayerImplementation.ML); discover(PlayerImplementation.ML_PRO) } }
        editingActions.addAll(listOf(new, import, discover))
        val directory = Button("Run directory").apply { setOnAction { openedDirectory?.let(openDirectory) } }
        val refresh = Button("Refresh reports / audit").apply { setOnAction { openedDirectory?.let { loadEvidence(it) } } }
        top = VBox(8.0, FlowPane(8.0, 4.0, new, import, export, open, discover, directory, refresh),
            FlowPane(8.0, 4.0, Label("Session workers"), workers, verbose, Label("Stall warning seconds"), stall),
            FlowPane(8.0, 4.0, validate, start, verify, resume, recovery, cancel), status, progress, ratings, fallback)
        val reports = TabPane().apply {
            tabs.addAll(Tab("Summary JSON", summary), Tab("Leaderboard CSV", leaderboard), Tab("Game scores CSV", scores), Tab("Audit history", audit))
            tabs.forEach { it.isClosable = false }
        }
        val configuration = ScrollPane(editor).apply { isFitToWidth = true }
        center = SplitPane(configuration, VBox(6.0, Label("Validation / identities / diagnostics"), issuesView, detail, generation, leaderboardTable, reports).apply {
            VBox.setVgrow(reports, Priority.ALWAYS)
        }).apply { setDividerPositions(0.52) }
        issuesView.prefHeight = 110.0
        issuesView.setCellFactory { object : ListCell<ValidationIssue>() {
            override fun updateItem(item: ValidationIssue?, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (empty || item == null) null else "${item.severity()} ${item.code()} ${item.path()}: ${item.message()}"
                isWrapText = true
            }
        } }
        issuesView.selectionModel.selectedItemProperty().addListener { _, _, issue -> issue?.let(editor::focusIssue) }
        validate.setOnAction { validateConfiguration() }
        start.setOnAction { execute(false) }
        verify.setOnAction { verifyRun() }
        resume.setOnAction { execute(true) }
        cancel.setOnAction { controller.cancel() }
        recovery.setOnAction { offerRecovery() }
        editor.onModelSelection = ::discover
        attach()
    }

    fun attach() {
        if (subscription == null) subscription = controller.subscribe(::render)
    }
    fun detach() { subscription?.close(); subscription = null }

    private fun render() {
        if (monitorInvocation && followController && controller.configuration != null && boundConfiguration !== controller.configuration) {
            boundConfiguration = controller.configuration
            retained = boundConfiguration
            openedDirectory = retained!!.output().absolute()
            editor.load(retained!!, true)
            inspectionMode = true
            nonReproducible = controller.nonReproducible
        }
        editor.isDisable = controller.active || busy || (inspectionMode && retained == null)
        editingActions.forEach { it.isDisable = controller.active || busy || controller.inputBusy }
        editor.isVisible = !(inspectionMode && retained == null)
        if (monitorInvocation && controller.active && controller.progress == null) status.text = controller.diagnostic
        start.isDisable = controller.active || busy || controller.inputBusy || inspectionMode
        validate.isDisable = busy || controller.active || controller.inputBusy || inspectionMode
        verify.isDisable = busy || retained == null
        resume.isDisable = busy || controller.active || controller.inputBusy || retained == null || !verified || completed
        cancel.isDisable = !controller.active || !monitorInvocation
        recovery.isDisable = busy || controller.active
        workers.isDisable = controller.active; stall.isDisable = controller.active; verbose.isDisable = controller.active
        if (!controller.active && monitorInvocation && lastRenderedProgress === controller.progress && controller.diagnostic.contains("Error")) status.text = controller.diagnostic
        controller.progress?.takeIf { monitorInvocation && it !== lastRenderedProgress }?.let { p ->
            lastRenderedProgress = p
            status.text = "${p.state()}: ${p.message()}"
            val epochs = retained?.epochs() ?: editor.epochs.text.toLongOrNull()
            progress.text = "Committed ${p.committedGames()} / ${p.totalGames()} · Session admissions ${p.admittedGames()} · Failed attempts ${p.failedAttempts()}\n" +
                "Complete epochs ${p.completeEpochs()} / ${epochs ?: "?"} · Committed games outside complete epochs: ${p.committedGames() - (if (epochs != null && epochs > 0) p.completeEpochs() * (p.totalGames() / epochs) else 0)}\n" +
                "Elapsed ${p.metrics().elapsed()} · ${"%.2f".format(p.metrics().gamesPerSecond())} committed games/s · ETA ${p.metrics().estimatedRemaining().map { it.toString() }.orElse("unavailable")} (rating/publication may pause play)\n" +
                p.metrics().runningAttempts().joinToString("\n") { "Game ${it.gameId()} · attempt ${it.attemptId()} · ${it.elapsed()} · competitors ${it.competitors()}${if (it.stallWarned()) " · STALL WARNING" else ""}" }
            nonReproducible = nonReproducible || controller.nonReproducible || !p.reproducible()
            p.metrics().summaryJson().ifPresent { showSummary(it, p.state() != TournamentProgress.State.COMPLETED) }
        }
        fallback.text = if (nonReproducible) "NON-REPRODUCIBLE: inference fallback occurred. See retained diagnostics and audit history." else ""
    }

    private fun issues(list: List<ValidationIssue>) { issuesView.items.setAll(list); editor.showIssues(list) }
    private fun <T> task(work: () -> T, input: Boolean = false, done: (T) -> Unit) {
        val lease = if (input) controller.beginInputWork() ?: return else null
        busy = true; render()
        background.execute {
            try { val result = work(); Platform.runLater { busy = false; lease?.close(); done(result); render() } }
            catch (e: Throwable) { Platform.runLater { busy = false; lease?.close(); status.text = e.toString(); detail.text = e.toString(); updateRecovery(e.toString()); render() } }
        }
    }

    private fun discover(implementation: PlayerImplementation) {
        val lease = controller.beginInputWork() ?: return
        background.execute {
            try {
                val found = backend.discover(implementation)
                Platform.runLater {
                    lease.close()
                    editor.discovery(implementation, found)
                    detail.text = "$implementation: " + found.joinToString("\n") { "${it.source()}: ${it.label()} ${it.directory()} ${it.issues().joinToString { issue -> issue.message() }}" }
                }
            } catch (e: Throwable) { Platform.runLater { lease.close(); detail.text = "Discovery failed: $e" } }
        }
    }

    fun importConfiguration(file: Path) {
        if (controller.active || busy) return
        followController = false
        task({ backend.read(file) }) { parsed ->
            issues(parsed.issues())
            if (parsed.valid()) { inspectionMode = false; retained = null; openedDirectory = null; verified = false; completed = false; editor.load(parsed.value(), false); status.text = "Imported $file" }
        }
    }

    fun exportConfiguration(file: Path) {
        val json = editor.exportJson(file)
        task({ Files.writeString(file, json) }) { status.text = "Exported $it; configuration base remains ${editor.configurationFile}" }
    }

    fun validateConfiguration() {
        val json = editor.json(); val file = editor.configurationFile
        task({
            val parsed = backend.read(json, file)
            if (!parsed.valid()) Pair(parsed.issues(), "")
            else backend.preflight(parsed.value(), CancellationToken.NONE).let { preflight ->
                Pair(preflight.issues(), "Build: ${preflight.build().map { it.identity() }.orElse("unavailable")}\n" +
                    preflight.competitors().joinToString("\n") { "${it.competitorId()} · ${it.concreteClass()} · ${it.releaseLabel()} · ${it.fingerprint()}" })
            }
        }, input = true) { result -> issues(result.first); detail.text = result.second; status.text = if (result.first.any { it.severity() == ValidationIssue.Severity.ERROR }) "Validation failed" else "Validation passed; Start repeats checks under ownership" }
    }

    fun openRun(directory: Path) {
        followController = false
        inspectionMode = true; editor.isDisable = true
        openedDirectory = directory.toAbsolutePath().normalize(); verified = false; completed = false
        recovery.isVisible = false; recovery.isManaged = false
        task({ Pair(backend.open(directory), backend.audit(directory)) }) { (opened, history) ->
            retained = opened.inspection().requested().orElse(null)
            retained?.let { editor.load(it, true) }
            updateRecovery(opened.diagnostics().joinToString("\n"))
            detail.text = "Run ${opened.inspection().runId().orElse(null)}\nDirectory: $openedDirectory\n" + opened.diagnostics().joinToString("\n")
            audit.text = history; opened.reports().ifPresent(::showReports)
            status.text = "Read-only retained inspection. Verify before explicit Resume."
        }
    }

    fun verifyRun() {
        val configuration = retained ?: return
        verified = false
        task({ backend.verify(configuration, CancellationToken.NONE) }) { result ->
            recovery.isVisible = false; recovery.isManaged = false
            verified = true; completed = result.successfullyCompleted()
            verify.text = if (completed) "Verify completed run" else "Verify retained run"
            detail.text = "Run ${result.runId()} · committed ${result.committedSet().games().size}\n" +
                "Build: ${result.configuration().build().identity()}\nEffective seed: ${java.lang.Long.toUnsignedString(result.configuration().effectiveSeed())}\n" +
                result.configuration().competitors().joinToString("\n") { "${it.requested().id()} · ${it.requested().name()} · ${it.concreteClass()} · release ${it.releaseLabel() ?: "none"} · fingerprint ${it.fingerprint() ?: "none"} · snapshot ${it.snapshot() ?: "none"}" } +
                "\n${result.diagnostics().joinToString("\n")}"
            result.reports().ifPresent(::showReports)
            status.text = if (completed) "Verified completed run; no games played" else "Verified retained snapshots. Resume missing games or publication explicitly."
        }
    }

    /** Explicit authorization is passed only by the recovery confirmation action. */
    fun resumeWithRecovery(confirm: () -> Boolean) { if (confirm() && retained != null) execute(true, true) }

    private fun execute(resuming: Boolean, recover: Boolean = false) {
        if (controller.active || busy || (resuming && ((!verified && !recover) || completed))) return
        val json = editor.json(); val file = editor.configurationFile
        val configuration = retained
        val options = try { TournamentSessionOptions(workers.text.toInt(), verbose.isSelected, Duration.ofSeconds(stall.text.toLong())) }
        catch (e: Exception) { status.text = "Session options: ${e.message}"; return }
        monitorInvocation = true
        followController = true
        controller.execute(if (resuming) configuration else null) { cancellation, observer ->
            val parsed = if (resuming && configuration != null) ValidationResult(configuration, emptyList()) else backend.read(json, file)
            if (!parsed.valid()) Platform.runLater { issues(parsed.issues()); status.text = "Configuration rejected" }
            else {
                val requested = parsed.value()
                Platform.runLater { controller.bindConfiguration(requested) }
                val result = backend.run(requested, TournamentInvocation(if (resuming) TournamentInvocation.Action.RESUME else TournamentInvocation.Action.NEW, recover), options, cancellation, observer)
                Platform.runLater {
                    if (result.disposition() == TournamentService.Disposition.CONFIGURATION_ERROR) {
                        inspectionMode = false; retained = null; editor.load(requested, false)
                    }
                    issues(result.configurationIssues()); detail.text = result.diagnostics().joinToString("\n")
                    openedDirectory = result.output(); inspectionMode = result.disposition() != TournamentService.Disposition.CONFIGURATION_ERROR
                    if (inspectionMode) { retained = requested; editor.load(requested, true) }
                    verified = false; completed = result.disposition() == TournamentService.Disposition.COMPLETED
                    result.reports().ifPresent(::showReports)
                    status.text = result.disposition().toString()
                    // Recovery is never inferred from inspection. A fresh strict verify will report stale metadata.
                    updateRecovery(result.diagnostics().joinToString("\n"))
                    if (result.disposition() != TournamentService.Disposition.CONFIGURATION_ERROR) loadEvidence(result.output())
                }
            }
        }
    }

    private fun updateRecovery(diagnostic: String) {
        val stale = diagnostic.contains("stale", true) && retained != null && !completed
        recovery.isVisible = stale; recovery.isManaged = stale
    }

    private fun offerRecovery() {
        val recover = ButtonType("Authorize stale-lock recovery")
        val stay = ButtonType("Keep inspecting", ButtonBar.ButtonData.CANCEL_CLOSE)
        val dialog = Alert(Alert.AlertType.WARNING, "Only a free OS lock and valid retained inputs permit recovery. The shared service preserves previous metadata and audits recovery.", recover, stay)
        dialog.headerText = "Stale tournament ownership metadata"
        dialog.initOwner(scene?.window)
        if (dialog.showAndWait().orElse(stay) == recover) execute(true, true)
    }

    private fun loadEvidence(directory: Path) = task({ Pair(backend.open(directory), backend.audit(directory)) }) { (opened, history) ->
        opened.reports().ifPresent(::showReports); audit.text = history
        if (opened.diagnostics().isNotEmpty()) detail.appendText("\n${opened.diagnostics().joinToString("\n")}")
    }

    private fun showReports(report: DurableTournamentRunStore.ReportGeneration) {
        val exports = report.exports() // One immutable generation for every visible report.
        generation.text = "Report generation: ${report.generationId()} · set ${report.setDigest()}"
        summary.text = exports["summary.json"]?.toString(Charsets.UTF_8).orEmpty()
        leaderboard.text = exports["leaderboard.csv"]?.toString(Charsets.UTF_8).orEmpty()
        scores.text = exports["game-scores.csv"]?.toString(Charsets.UTF_8).orEmpty()
        showSummary(summary.text, !completed)
    }

    private fun showSummary(json: String, provisional: Boolean) {
        summary.text = json
        if (displayedSummary == json) return
        displayedSummary = json
        background.execute {
            try {
                val tree = ObjectMapper().readTree(json)
                val rows = tree.path("leaderboard").map { row ->
                    val interval = row.path("interval")
                    LeaderboardRow(row.path("rank").asText(), row.path("name").asText(), row.path("implementation").asText(),
                        if (row.path("rating").isNumber) "%.2f".format(java.util.Locale.ROOT, row.path("rating").asDouble()) else "No rated evidence",
                        if (interval.isArray && interval.size() == 2) "${interval[0].asText()} – ${interval[1].asText()}"
                        else "Unavailable: ${row.path("intervalUnavailableReason").asText("not calculated")}")
                }
                Platform.runLater {
                    if (displayedSummary == json) {
                        leaderboardTable.items.setAll(rows)
                        val unfinished = tree.path("provisional").asBoolean(provisional)
                        ratings.text = if (unfinished) "PROVISIONAL leaderboard · complete epochs only" else "Completed leaderboard · complete epochs only"
                        if (!tree.path("hasRatedEvidence").asBoolean(false)) ratings.text += " · no rated evidence"
                        if (!tree.path("reproducible").asBoolean(true)) nonReproducible = true
                        fallback.text = if (nonReproducible) "NON-REPRODUCIBLE: inference fallback occurred. See retained diagnostics and audit history." else ""
                    }
                }
            } catch (e: Exception) {
                Platform.runLater { if (displayedSummary == json) ratings.text = "Published analysis unavailable: $e" }
            }
        }
    }

    private fun newConfiguration() {
        followController = false
        inspectionMode = false; editor.isDisable = false; retained = null; openedDirectory = null; verified = false; completed = false
        recovery.isVisible = false; recovery.isManaged = false
        lastRenderedProgress = controller.progress; nonReproducible = false
        editor.reset(); issues(emptyList()); status.text = "New run"; render()
    }
}
