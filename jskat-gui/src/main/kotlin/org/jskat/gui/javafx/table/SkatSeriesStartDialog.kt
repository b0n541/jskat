package org.jskat.gui.javafx.table

import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.util.Callback
import org.jskat.control.JSkatMaster
import org.jskat.data.JSkatOptions
import org.jskat.player.JSkatPlayerResolver
import org.jskat.util.JSkatResourceBundle

/**
 * A dialog for configuring and starting a new Skat series.
 * This is a JavaFX and Kotlin replacement for the original Swing dialog.
 */
// TODO: review whether a nullable owner makes sense, this dialog must be modal
class SkatSeriesStartDialog(owner: Node?) {

    /**
     * Data class to hold the configuration result from the dialog.
     */
    private data class SeriesConfiguration(
        val playerTypes: List<String>,
        val playerNames: List<String>,
        val numberOfRounds: Int,
        val unlimited: Boolean,
        val onlyPlayRamsch: Boolean
    )

    private val strings = JSkatResourceBundle.INSTANCE
    private val options = JSkatOptions.instance()

    private val dialog = Dialog<SeriesConfiguration>()

    // UI Controls
    private lateinit var player1NameField: TextField
    private lateinit var player2NameField: TextField
    private lateinit var player3NameField: TextField
    private lateinit var player1TypeCombo: ComboBox<String>
    private lateinit var player2TypeCombo: ComboBox<String>
    private lateinit var player3TypeCombo: ComboBox<String>
    private lateinit var roundsSpinner: Spinner<Int>
    private lateinit var unlimitedCheck: CheckBox
    private lateinit var ramschCheck: CheckBox

    init {
        dialog.title = strings.getString("start_series")
        // TODO: add a different string for the dialog header
        dialog.headerText = strings.getString("start_series")
        // Use a safe call to handle the nullable owner
        dialog.initOwner(owner?.scene?.window)

        dialog.dialogPane.content = createContentPane()
        setupButtons()
        setupResultConverter()
        setupValidation()
        setupInitialState()
    }

    private fun createContentPane(): GridPane = GridPane().apply {
        hgap = 10.0
        vgap = 10.0
        padding = Insets(20.0, 20.0, 10.0, 10.0)

        val playerTypes = JSkatPlayerResolver.getAllAIPlayerImplementations().sorted()

        // Player 1
        add(Label(strings.getString("player") + " 1"), 0, 0)
        player1NameField = TextField("Jan")
        add(player1NameField, 1, 0)
        player1TypeCombo = createPlayerTypeComboBox(playerTypes)
        add(player1TypeCombo, 2, 0)

        // Player 2
        add(Label(strings.getString("player") + " 2"), 0, 1)
        player2NameField = TextField("Markus")
        add(player2NameField, 1, 1)
        player2TypeCombo = createPlayerTypeComboBox(playerTypes)
        add(player2TypeCombo, 2, 1)

        // Player 3
        add(Label(strings.getString("player") + " 3"), 0, 2)
        player3NameField = TextField(System.getProperty("user.name"))
        add(player3NameField, 1, 2)
        val player3Types = playerTypes + JSkatPlayerResolver.HUMAN_PLAYER_CLASS
        player3TypeCombo = createPlayerTypeComboBox(player3Types)
        player3TypeCombo.selectionModel.selectLast()
        add(player3TypeCombo, 2, 2)

        // Rounds
        add(Label(strings.getString("number_of_rounds")), 0, 3)
        roundsSpinner = Spinner(1, 48, 12)
        add(roundsSpinner, 1, 3)
        unlimitedCheck = CheckBox(strings.getString("unlimited"))
        roundsSpinner.disableProperty().bind(unlimitedCheck.selectedProperty())
        add(unlimitedCheck, 2, 3)

        // Ramsch
        add(Label(strings.getString("ramsch")), 0, 4)
        ramschCheck = CheckBox(strings.getString("only_play_ramsch"))
        add(ramschCheck, 1, 4, 2, 1)
    }

    private fun createPlayerTypeComboBox(playerTypes: List<String>): ComboBox<String> =
        ComboBox<String>().apply {
            items.addAll(playerTypes)
            val cellFactory = Callback<ListView<String>, ListCell<String>> { PlayerTypeCell() }
            this.cellFactory = cellFactory
            this.buttonCell = PlayerTypeCell()
            selectionModel.selectFirst()
        }

    private fun setupButtons() {
        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
    }

    private fun setupValidation() {
        val okButton = dialog.dialogPane.lookupButton(ButtonType.OK)
        okButton.addEventFilter(ActionEvent.ACTION) { event ->
            if (player1NameField.text.isBlank() || player2NameField.text.isBlank() || player3NameField.text.isBlank()) {
                // In a full JavaFX app, this should be a JavaFX Alert.
                JSkatMaster.showEmptyInputNameMessage()
                event.consume() // Prevents the dialog from closing
            }
        }
    }

    private fun setupResultConverter() {
        dialog.resultConverter = Callback { buttonType ->
            if (buttonType == ButtonType.OK) {
                SeriesConfiguration(
                    playerTypes = listOf(player1TypeCombo.value, player2TypeCombo.value, player3TypeCombo.value),
                    playerNames = listOf(player1NameField.text, player2NameField.text, player3NameField.text),
                    numberOfRounds = roundsSpinner.value,
                    unlimited = unlimitedCheck.isSelected,
                    onlyPlayRamsch = ramschCheck.isSelected
                )
            } else null
        }
    }

    private fun setupInitialState() {
        ramschCheck.isDisable = !options.isPlayRamsch(true)
        if (ramschCheck.isDisable) {
            ramschCheck.isSelected = false
        }
    }

    /**
     * Shows the dialog, waits for user input, and starts the series if confirmed.
     */
    fun showAndWaitAndStartSeries() {
        dialog.showAndWait().ifPresent { config ->
            JSkatMaster.INSTANCE.startSeries(
                config.playerTypes,
                config.playerNames,
                config.numberOfRounds,
                config.unlimited,
                config.onlyPlayRamsch,
                100 // Default bid value, as in the original
            )
        }
    }

    /**
     * Custom ListCell to display human-readable player type names.
     */
    private inner class PlayerTypeCell : ListCell<String>() {
        override fun updateItem(item: String?, empty: Boolean) {
            super.updateItem(item, empty)
            text = if (empty || item == null) {
                null
            } else when (item) {
                "org.jskat.ai.newalgorithm.AlgorithmAI" -> strings.getString("algorithmic_nextgen_player")
                "org.jskat.ai.mjl.AIPlayerMJL" -> strings.getString("algorithmic_player")
                "org.jskat.ai.rnd.AIPlayerRND" -> strings.getString("random_player")
                "org.jskat.ai.nn.AIPlayerNN" -> strings.getString("neural_network_player")
                "org.jskat.ai.ml.MLPlayer" -> strings.getString("ml_player")
                "org.jskat.ai.ml.MLPlayerPro" -> strings.getString("ml_player_pro")
                JSkatPlayerResolver.HUMAN_PLAYER_CLASS -> strings.getString("human_player")
                else -> item
            }
        }
    }
}
