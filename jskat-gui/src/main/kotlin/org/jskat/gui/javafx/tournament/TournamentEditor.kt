package org.jskat.gui.javafx.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.DirectoryChooser
import org.jskat.tournament.*
import java.nio.file.Path
import java.util.UUID

/** Native requested-input editor. Relative paths always retain this explicit configuration base. */
class TournamentEditor(initialFile: Path = Path.of("tournament.json").toAbsolutePath()) : VBox(8.0) {
    var configurationFile: Path = initialFile.toAbsolutePath().normalize()
        private set
    val output = TextField(".tournaments/tournament-run").apply { prefColumnCount = 30 }
    val epochs = TextField("1")
    val seed = TextField().apply { prefColumnCount = 25 }
    val ruleset = Label("standard-skat-v1")
    val count = Label()
    val rows = mutableListOf<CompetitorRow>()
    private val roster = VBox(8.0)
    private val base = Label().apply { isWrapText = true; minWidth = 0.0; maxWidth = Double.MAX_VALUE }
    private val add = Button("Add competitor")
    private val fields = linkedMapOf<String, Node>()
    private var retained = false
    var onModelSelection: ((PlayerImplementation) -> Unit)? = null
    private val candidates = mutableMapOf<PlayerImplementation, List<DefaultTournamentInputResolver.Candidate>>()

    init {
        padding = Insets(10.0)
        output.id = "tournament-output"
        epochs.id = "tournament-epochs"
        seed.id = "tournament-seed"
        seed.promptText = "Optional unsigned 64-bit seed"
        children.addAll(Label("Tournament configuration"), base,
            field("Fresh output directory", output, Button("Choose parent…").apply {
                setOnAction { DirectoryChooser().showDialog(scene?.window)?.let {
                    output.text = it.toPath().resolve(".tournaments/tournament-run").toString()
                } }
            }), field("Whole epochs", epochs), field("Root seed", seed),
            field("Fixed ruleset", ruleset), count, roster, add)
        fields["/output"] = output; fields["/epochs"] = epochs; fields["/seed"] = seed
        epochs.textProperty().addListener { _, _, _ -> updateCount() }
        add.setOnAction { addRow() }
        addRow(PlayerImplementation.RANDOM, "Random")
        addRow(PlayerImplementation.ALGORITHMIC, "Algorithmic")
        updateBase()
    }

    private fun field(title: String, vararg nodes: Node) = VBox(4.0, Label(title), FlowPane(8.0, 4.0, *nodes))
    private fun updateBase() { base.text = "Configuration base: $configurationFile" }
    private fun updateCount() {
        count.text = try { "Derived games: ${TournamentScheduleSize.gameCount(rows.size, epochs.text.toLong())}" }
        catch (_: Exception) { "Derived games: unavailable — correct roster / epochs" }
    }

    fun addRow(implementation: PlayerImplementation = PlayerImplementation.ML, name: String = "Competitor ${rows.size + 1}"): CompetitorRow {
        val row = CompetitorRow(UUID.randomUUID(), name, implementation)
        rows.add(row); roster.children.add(row); updateCount()
        if (implementation.modelRequired()) onModelSelection?.invoke(implementation)
        return row
    }

    inner class CompetitorRow(val uuid: UUID, initialName: String, implementation: PlayerImplementation) : VBox(4.0) {
        val name = TextField(initialName)
        val player = ComboBox<PlayerImplementation>().apply { items.setAll(PlayerImplementation.entries); value = implementation }
        val source = ComboBox<String>().apply { items.setAll("Active", "Installed", "Development"); value = "Active" }
        val modelPath = TextField().apply { prefColumnCount = 30 }
        val installed = ComboBox<DefaultTournamentInputResolver.Candidate>()
        private val modelControls = FlowPane(8.0, 4.0)
        private var updatingCandidates = false
        private var lastValid: DefaultTournamentInputResolver.Candidate? = null
        fun selectConfiguredCandidate(candidate: DefaultTournamentInputResolver.Candidate) {
            updatingCandidates = true
            if (!installed.items.contains(candidate)) installed.items.add(candidate)
            installed.value = candidate
            updatingCandidates = false
        }
        fun applyCandidates() {
            val found = candidates[player.value].orEmpty()
            val selected = installed.value
            updatingCandidates = true
            installed.items.setAll(found.filter { it.source() is ModelSource.Installed })
            if (selected != null) {
                val replacement = installed.items.firstOrNull { it.source() == selected.source() } ?: selected.also { installed.items.add(it) }
                installed.value = replacement
            }
            updatingCandidates = false
            source.setCellFactory { object : ListCell<String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    val active = found.firstOrNull { it.source() is ModelSource.Active }
                    val invalid = item == "Active" && active != null && active.issues().isNotEmpty()
                    text = if (empty) null else item + if (invalid) " (unavailable — see discovery diagnostics)" else ""
                    isDisable = invalid
                }
            } }
        }
        init {
            val remove = Button("Remove").apply { setOnAction { rows.remove(this@CompetitorRow); roster.children.remove(this@CompetitorRow); updateCount() } }
            val up = Button("Move up").apply { setOnAction {
                val i = rows.indexOf(this@CompetitorRow)
                if (i > 0) { java.util.Collections.swap(rows, i, i - 1); roster.children.setAll(rows) }
            } }
            installed.setCellFactory { object : ListCell<DefaultTournamentInputResolver.Candidate>() {
                override fun updateItem(item: DefaultTournamentInputResolver.Candidate?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) null else "${item.label()} ${item.issues().joinToString { it.message() }}"
                    isDisable = item != null && item.issues().any { it.severity() == ValidationIssue.Severity.ERROR }
                }
            } }
            installed.buttonCell = object : ListCell<DefaultTournamentInputResolver.Candidate>() {
                override fun updateItem(item: DefaultTournamentInputResolver.Candidate?, empty: Boolean) {
                    super.updateItem(item, empty); text = item?.label()
                }
            }
            installed.valueProperty().addListener { _, _, value ->
                if (!updatingCandidates && value != null && value.issues().any { it.severity() == ValidationIssue.Severity.ERROR }) installed.value = lastValid
                else lastValid = value
            }
            modelPath.promptText = "Development directory (relative to configuration base)"
            val choose = Button("Choose model directory…").apply { setOnAction {
                DirectoryChooser().showDialog(scene?.window)?.let { modelPath.text = it.absolutePath }
            } }
            modelControls.children.addAll(source, installed, modelPath, choose)
            fun visibility() {
                modelControls.isVisible = player.value.modelRequired(); modelControls.isManaged = modelControls.isVisible
                installed.isVisible = source.value == "Installed"; installed.isManaged = installed.isVisible
                modelPath.isVisible = source.value == "Development"; modelPath.isManaged = modelPath.isVisible
                choose.isVisible = modelPath.isVisible; choose.isManaged = choose.isVisible
            }
            player.valueProperty().addListener { _, _, _ -> applyCandidates(); visibility(); if (player.value.modelRequired()) onModelSelection?.invoke(player.value) }
            source.valueProperty().addListener { _, old, value ->
                val active = candidates[player.value].orEmpty().firstOrNull { it.source() is ModelSource.Active }
                if (value == "Active" && old != "Active" && active != null && active.issues().any { it.severity() == ValidationIssue.Severity.ERROR }) source.value = old
                visibility()
            }
            children.addAll(Label("UUID: $uuid"), name, FlowPane(8.0, 4.0, player, up, remove), modelControls)
            style = "-fx-border-color: -fx-box-border; -fx-padding: 8;"
            applyCandidates()
            visibility()
        }
    }

    fun discovery(implementation: PlayerImplementation, found: List<DefaultTournamentInputResolver.Candidate>) {
        candidates[implementation] = found
        rows.filter { it.player.value == implementation }.forEach { it.applyCandidates() }
    }

    fun reset() {
        configurationFile = Path.of("tournament.json").toAbsolutePath(); updateBase()
        output.text = ".tournaments/tournament-run"; epochs.text = "1"; seed.clear()
        rows.clear(); roster.children.clear()
        addRow(PlayerImplementation.RANDOM, "Random"); addRow(PlayerImplementation.ALGORITHMIC, "Algorithmic")
        retained = false; isDisable = false
        output.isDisable = false; epochs.isDisable = false; seed.isDisable = false; roster.isDisable = false; add.isDisable = false
    }

    fun load(configuration: TournamentConfiguration, readOnly: Boolean) {
        configurationFile = configuration.configurationFile(); updateBase()
        output.text = configuration.output().configured(); epochs.text = configuration.epochs().toString()
        seed.text = configuration.seed().map { java.lang.Long.toUnsignedString(it) }.orElse("")
        rows.clear(); roster.children.clear()
        configuration.competitors().forEach { c ->
            val row = CompetitorRow(c.id(), c.name(), c.implementation())
            when (val model = c.model()) {
                is ModelSource.Development -> { row.source.value = "Development"; row.modelPath.text = model.path().configured() }
                is ModelSource.Installed -> {
                    row.source.value = "Installed"
                    val candidate = candidates[row.player.value]?.firstOrNull { it.source() == model }
                        ?: DefaultTournamentInputResolver.Candidate(model, configurationFile.parent, model.version(), emptyList())
                    row.selectConfiguredCandidate(candidate)
                }
                else -> row.source.value = "Active"
            }
            rows.add(row); roster.children.add(row)
        }
        retained = readOnly
        output.isDisable = retained; epochs.isDisable = retained; seed.isDisable = retained
        roster.isDisable = retained; add.isDisable = retained
        updateCount()
        if (!readOnly) rows.map { it.player.value }.filter { it.modelRequired() }.distinct().forEach { onModelSelection?.invoke(it) }
    }

    fun json(): String = exportJson(configurationFile)

    /** Export rebases path text, never the editor's original base or retained identity. */
    fun exportJson(destination: Path): String {
        val absolute = destination.toAbsolutePath().normalize().parent != configurationFile.parent
        fun path(value: String) = if (absolute && value.isNotBlank()) configurationFile.parent.resolve(value).normalize().toString() else value
        val data = linkedMapOf<String, Any>("schemaVersion" to 1, "ruleset" to "standard-skat-v1",
            "epochs" to (epochs.text.toLongOrNull() ?: epochs.text), "output" to path(output.text))
        if (seed.text.isNotEmpty()) data["seed"] = seed.text
        data["competitors"] = rows.map { row ->
            val c = linkedMapOf<String, Any>("id" to row.uuid.toString(), "name" to row.name.text, "implementation" to row.player.value.id())
            if (row.player.value.modelRequired()) c["model"] = when (row.source.value) {
                "Installed" -> mapOf("source" to "installed", "version" to (row.installed.value?.source() as? ModelSource.Installed)?.version().orEmpty())
                "Development" -> mapOf("source" to "development", "path" to path(row.modelPath.text))
                else -> mapOf("source" to "active")
            }
            c
        }
        return ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data)
    }

    fun showIssues(issues: List<ValidationIssue>) {
        fields.entries.removeIf { it.key.startsWith("/competitors") }
        rows.forEachIndexed { i, row ->
            fields["/competitors/$i/name"] = row.name; fields["/competitors/$i/id"] = row
            fields["/competitors/$i/implementation"] = row.player
            fields["/competitors/$i/model"] = row.source
            fields["/competitors/$i/model/path"] = row.modelPath
            fields["/competitors/$i/model/version"] = row.installed
        }
        fields.forEach { (path, node) ->
            val matches = issues.filter { it.path() == path }
            node.style = if (matches.isEmpty()) "" else "-fx-border-color: #c74b41;"
            if (node is Control) node.tooltip = if (matches.isEmpty()) null else Tooltip(matches.joinToString("\n") { it.message() })
        }
    }

    fun focusIssue(issue: ValidationIssue) {
        val target = fields[issue.path()] ?: fields.entries.firstOrNull { issue.path().startsWith(it.key) }?.value
        target?.requestFocus()
    }
}
