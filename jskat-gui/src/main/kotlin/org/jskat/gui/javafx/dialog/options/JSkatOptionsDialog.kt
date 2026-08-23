package org.jskat.gui.javafx.dialog.options

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Window
import javafx.util.Callback
import org.jskat.control.JSkatEventBus
import org.jskat.control.command.general.HideToolbarCommand
import org.jskat.control.command.general.ShowToolbarCommand
import org.jskat.control.gui.img.CardSet
import org.jskat.data.JSkatOptions
import org.jskat.data.SkatTableOptions
import org.jskat.util.JSkatResourceBundle

class JSkatOptionsDialog(owner: Window? = null) : Dialog<ButtonType>() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val options = JSkatOptions.instance()

    // General options
    private val showTipsAtStartUpProperty = SimpleBooleanProperty(options.isShowTipsAtStartUp)
    private val checkForNewVersionAtStartUpProperty = SimpleBooleanProperty(options.isCheckForNewVersionAtStartUp)
    private val hideToolbarProperty = SimpleBooleanProperty(options.isHideToolbar)
    private val languageProperty = SimpleObjectProperty(options.language)
    private val savePathProperty = SimpleStringProperty(options.savePath)
    private val waitTimeAfterTrickProperty = SimpleIntegerProperty(options.waitTimeAfterTrick)

    // Card set options
    private val cardSetProperty = SimpleObjectProperty(options.cardSet)
    private val originalCardSet = options.cardSet

    // Skat Rules options
    private val rulesProperty = SimpleObjectProperty(options.rules)
    private val playContraProperty = SimpleBooleanProperty(options.isPlayContra(false))
    private val contraAfterBid18Property = SimpleBooleanProperty(options.isContraAfterBid18(false))
    private val playBockProperty = SimpleBooleanProperty(options.isPlayBock(false))
    private val bockEventAllPlayersPassedProperty = SimpleBooleanProperty(false) // Not in options?
    private val bockEventLostAfterContraProperty = SimpleBooleanProperty(options.isBockEventLostAfterContra(false))
    private val bockEventLostWith60Property = SimpleBooleanProperty(options.isBockEventLostWith60(false))
    private val bockEventContraReAnnouncedProperty = SimpleBooleanProperty(options.isBockEventContraReCalled(false))
    private val bockEventPlayerHasX00PointsProperty =
        SimpleBooleanProperty(options.isBockEventMultipleOfHundredScore(false))
    private val bockEventLostGrandProperty = SimpleBooleanProperty(options.isBockEventLostGrand(false))
    private val playRamschProperty = SimpleBooleanProperty(options.isPlayRamsch(false))
    private val schiebeRamschProperty = SimpleBooleanProperty(options.isSchieberamsch(false))
    private val schiebeRamschJacksInSkatProperty = SimpleBooleanProperty(options.isSchieberamschJacksInSkat(false))
    private val ramschEventNoBidProperty = SimpleBooleanProperty(options.isRamschEventNoBid(false))
    private val ramschEventBockRamschProperty = SimpleBooleanProperty(options.isRamschEventRamschAfterBock(false))
    private val ramschSkatOwnerProperty = SimpleObjectProperty(options.ramschSkatOwner)
    private val playRevolutionProperty = SimpleBooleanProperty(options.isPlayRevolution(false))

    // ISS options
    private val issAddressProperty = SimpleStringProperty(options.issAddress)
    private val issPortProperty = SimpleStringProperty(options.issPort.toString())

    init {
        initOwner(owner)
        title = strings.getString("preferences")

        val tabPane = TabPane().apply {
            padding = Insets.EMPTY
            tabs.add(Tab(strings.getString("common_options"), createGeneralTab()))
            tabs.add(Tab(strings.getString("cardset_options"), createCardSetTab()))
            tabs.add(Tab(strings.getString("skat_rules"), createSkatRulesTab()))
            tabs.add(Tab(strings.getString("iss"), createIssTab()))
        }

        dialogPane.content = tabPane

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        setResultConverter { dialogButton ->
            if (dialogButton == ButtonType.OK) {
                saveOptions()
            }
            dialogButton
        }

        setOnHidden {
            if (result != ButtonType.OK) {
                options.cardSet = originalCardSet
            }
        }
    }

    private fun createGeneralTab(): GridPane {
        val grid = GridPane().apply {
            hgap = 10.0
            vgap = 10.0
            padding = Insets(20.0)
        }

        // Show tips at startup
        val showTipsCheckBox = CheckBox(strings.getString("show_tips_at_startup")).apply {
            selectedProperty().bindBidirectional(showTipsAtStartUpProperty)
        }
        grid.add(showTipsCheckBox, 0, 0, 2, 1)

        // Check for new version at startup
        val checkForNewVersionCheckBox = CheckBox(strings.getString("check_for_new_version_at_startup")).apply {
            selectedProperty().bindBidirectional(checkForNewVersionAtStartUpProperty)
        }
        grid.add(checkForNewVersionCheckBox, 0, 1, 2, 1)

        // Hide toolbar
        val hideToolbarCheckBox = CheckBox(strings.getString("hide_toolbar")).apply {
            selectedProperty().bindBidirectional(hideToolbarProperty)
        }
        hideToolbarProperty.addListener { _, _, newValue ->
            if (newValue) {
                JSkatEventBus.INSTANCE.post(HideToolbarCommand())
            } else {
                JSkatEventBus.INSTANCE.post(ShowToolbarCommand())
            }
        }
        grid.add(hideToolbarCheckBox, 0, 2, 2, 1)

        // Language
        val languageLabel = Label(strings.getString("language"))
        val languageComboBox = ComboBox<JSkatOptions.SupportedLanguage>().apply {
            items.addAll(JSkatOptions.SupportedLanguage.values())
            valueProperty().bindBidirectional(languageProperty)

            val cellFactory =
                Callback<ListView<JSkatOptions.SupportedLanguage>, ListCell<JSkatOptions.SupportedLanguage>> {
                    object : ListCell<JSkatOptions.SupportedLanguage>() {
                        override fun updateItem(item: JSkatOptions.SupportedLanguage?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = if (item == null || empty) {
                                null
                            } else {
                                when (item) {
                                    JSkatOptions.SupportedLanguage.ENGLISH -> strings.getString("english")
                                    JSkatOptions.SupportedLanguage.GERMAN -> strings.getString("german")
                                    else -> item.name
                                }
                            }
                        }
                    }
                }
            setCellFactory(cellFactory)
            buttonCell = cellFactory.call(null)
        }
        grid.add(languageLabel, 0, 3)
        grid.add(languageComboBox, 1, 3)

        // Save path
        val savePathLabel = Label(strings.getString("save_path"))
        val savePathGroup = ToggleGroup()
        val userHomeRadioButton = RadioButton(strings.getString("user_home")).apply {
            toggleGroup = savePathGroup
            userData = JSkatOptions.SavePath.USER_HOME
        }
        val workingDirRadioButton = RadioButton(strings.getString("working_directory")).apply {
            toggleGroup = savePathGroup
            userData = JSkatOptions.SavePath.WORKING_DIRECTORY
        }
        savePathGroup.selectedToggleProperty().addListener { _, _, newToggle ->
            if (newToggle != null) {
                savePathProperty.set((newToggle.userData as JSkatOptions.SavePath).name)
            }
        }
        if (JSkatOptions.SavePath.USER_HOME.name == options.savePath) {
            savePathGroup.selectToggle(userHomeRadioButton)
        } else {
            savePathGroup.selectToggle(workingDirRadioButton)
        }
        val savePathBox = HBox(10.0, userHomeRadioButton, workingDirRadioButton)
        grid.add(savePathLabel, 0, 4)
        grid.add(savePathBox, 1, 4)

        // Wait time after trick
        val waitTimeLabel = Label(strings.getString("wait_time_after_trick"))
        val waitTimeSlider = Slider(0.0, 10.0, options.waitTimeAfterTrick.toDouble()).apply {
            majorTickUnit = 5.0
            minorTickCount = 1
            isShowTickLabels = true
            isShowTickMarks = true
            valueProperty().addListener { _, _, newValue ->
                waitTimeAfterTrickProperty.set(newValue.toInt())
            }
        }
        grid.add(waitTimeLabel, 0, 5)
        grid.add(waitTimeSlider, 1, 5)

        return grid
    }

    private fun createCardSetTab(): VBox {
        val cardSetLabel = Label(strings.getString("card_face"))
        val cardSetComboBox = ComboBox<CardSet>().apply {
            items.addAll(CardSet.values())
            valueProperty().bindBidirectional(cardSetProperty)

            val cellFactory = Callback<ListView<CardSet>, ListCell<CardSet>> {
                object : ListCell<CardSet>() {
                    override fun updateItem(item: CardSet?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (item == null || empty) {
                            null
                        } else {
                            val cardSetName = item.cardSetName.lowercase().replace(" ", "")
                            val cardFace = item.cardFace.toString().lowercase()
                            strings.getString("cardset_${cardSetName}_${cardFace}")
                        }
                    }
                }
            }
            setCellFactory(cellFactory)
            buttonCell = cellFactory.call(null)
        }
        val cardPane = CardPane()
        cardSetProperty.addListener { _, _, newValue ->
            options.cardSet = newValue
            cardPane.redraw()
        }

        val content = VBox(10.0, cardSetLabel, cardSetComboBox, cardPane).apply {
            padding = Insets(10.0)
        }
        return content
    }

    private fun createSkatRulesTab(): ScrollPane {
        val content = VBox(10.0).apply {
            padding = Insets(10.0)
        }

        val rulesGroup = ToggleGroup()
        val ispaRules = RadioButton(strings.getString("ispa_rules")).apply {
            toggleGroup = rulesGroup
            userData = SkatTableOptions.RuleSet.ISPA
        }
        val pubRules = RadioButton(strings.getString("pub_rules")).apply {
            toggleGroup = rulesGroup
            userData = SkatTableOptions.RuleSet.PUB
        }

        rulesGroup.selectedToggleProperty().addListener { _, _, newToggle ->
            if (newToggle != null) {
                rulesProperty.set(newToggle.userData as SkatTableOptions.RuleSet)
            }
        }

        if (options.rules == SkatTableOptions.RuleSet.ISPA) {
            rulesGroup.selectToggle(ispaRules)
        } else {
            rulesGroup.selectToggle(pubRules)
        }

        content.children.addAll(ispaRules, pubRules)

        val pubRulesBox = VBox(10.0).apply {
            padding = Insets(0.0, 0.0, 0.0, 20.0)
        }

        // Contra/Re
        val playContra = CheckBox(strings.getString("play_contra_re")).apply {
            selectedProperty().bindBidirectional(playContraProperty)
        }
        val contraAfterBid18 = CheckBox(strings.getString("contra_after_bid_18")).apply {
            selectedProperty().bindBidirectional(contraAfterBid18Property)
            padding = Insets(0.0, 0.0, 0.0, 20.0)
        }
        pubRulesBox.children.addAll(playContra, contraAfterBid18)

        // Bock
        val playBock = CheckBox(strings.getString("play_bock")).apply {
            selectedProperty().bindBidirectional(playBockProperty)
        }
        val bockBox = VBox(5.0).apply {
            padding = Insets(0.0, 0.0, 0.0, 20.0)
        }
        val bockEventLabel = Label(strings.getString("bock_events"))
        val bockEventAllPlayersPassed = CheckBox(strings.getString("bock_event_all_players_passed")).apply {
            selectedProperty().bindBidirectional(bockEventAllPlayersPassedProperty)
        }
        val bockEventLostAfterContra = CheckBox(strings.getString("bock_event_lost_contra")).apply {
            selectedProperty().bindBidirectional(bockEventLostAfterContraProperty)
        }
        val bockEventLostWith60 = CheckBox(strings.getString("bock_event_lost_game_with_60")).apply {
            selectedProperty().bindBidirectional(bockEventLostWith60Property)
        }
        val bockEventContraReAnnounced = CheckBox(strings.getString("bock_event_contra_re")).apply {
            selectedProperty().bindBidirectional(bockEventContraReAnnouncedProperty)
        }
        val bockEventPlayerHasX00Points = CheckBox(strings.getString("bock_event_player_x00_points")).apply {
            selectedProperty().bindBidirectional(bockEventPlayerHasX00PointsProperty)
        }
        val bockEventLostGrand = CheckBox(strings.getString("bock_event_lost_grand")).apply {
            selectedProperty().bindBidirectional(bockEventLostGrandProperty)
        }
        bockBox.children.addAll(
            bockEventLabel,
            bockEventAllPlayersPassed,
            bockEventLostAfterContra,
            bockEventLostWith60,
            bockEventContraReAnnounced,
            bockEventPlayerHasX00Points,
            bockEventLostGrand
        )
        pubRulesBox.children.addAll(playBock, bockBox)

        // Ramsch
        val playRamsch = CheckBox(strings.getString("play_ramsch")).apply {
            selectedProperty().bindBidirectional(playRamschProperty)
        }
        val ramschBox = VBox(5.0).apply {
            padding = Insets(0.0, 0.0, 0.0, 20.0)
        }
        val schiebeRamsch = CheckBox(strings.getString("schieberamsch")).apply {
            selectedProperty().bindBidirectional(schiebeRamschProperty)
        }
        val schiebeRamschJacksInSkat = CheckBox(strings.getString("schieberamsch_jacks_in_skat")).apply {
            selectedProperty().bindBidirectional(schiebeRamschJacksInSkatProperty)
            padding = Insets(0.0, 0.0, 0.0, 20.0)
        }
        val ramschEventLabel = Label(strings.getString("ramsch_events"))
        val ramschEventNoBid = CheckBox(strings.getString("ramsch_event_no_bid")).apply {
            selectedProperty().bindBidirectional(ramschEventNoBidProperty)
        }
        val ramschEventBockRamsch = CheckBox(strings.getString("ramsch_event_bock_ramsch")).apply {
            selectedProperty().bindBidirectional(ramschEventBockRamschProperty)
        }
        val ramschSkatOwnerLabel = Label(strings.getString("ramsch_skat_owner"))
        val ramschSkatOwnerGroup = ToggleGroup()
        val ramschSkatLastTrick = RadioButton(strings.getString("ramsch_skat_last_trick")).apply {
            toggleGroup = ramschSkatOwnerGroup
            userData = SkatTableOptions.RamschSkatOwner.LAST_TRICK
        }
        val ramschSkatLoser = RadioButton(strings.getString("ramsch_skat_loser")).apply {
            toggleGroup = ramschSkatOwnerGroup
            userData = SkatTableOptions.RamschSkatOwner.LOSER
        }
        ramschSkatOwnerGroup.selectedToggleProperty().addListener { _, _, newToggle ->
            if (newToggle != null) {
                ramschSkatOwnerProperty.set(newToggle.userData as SkatTableOptions.RamschSkatOwner)
            }
        }
        if (options.ramschSkatOwner == SkatTableOptions.RamschSkatOwner.LAST_TRICK) {
            ramschSkatOwnerGroup.selectToggle(ramschSkatLastTrick)
        } else {
            ramschSkatOwnerGroup.selectToggle(ramschSkatLoser)
        }

        ramschBox.children.addAll(
            schiebeRamsch,
            schiebeRamschJacksInSkat,
            ramschEventLabel,
            ramschEventNoBid,
            ramschEventBockRamsch,
            ramschSkatOwnerLabel,
            ramschSkatLastTrick,
            ramschSkatLoser
        )
        pubRulesBox.children.addAll(playRamsch, ramschBox)

        // Revolution
        val playRevolution = CheckBox(strings.getString("play_revolution")).apply {
            selectedProperty().bindBidirectional(playRevolutionProperty)
        }
        pubRulesBox.children.add(playRevolution)

        content.children.add(pubRulesBox)

        // Enable/Disable logic
        fun updatePubRulesState(enabled: Boolean) {
            pubRulesBox.isDisable = !enabled
        }

        rulesProperty.addListener { _, _, newValue ->
            updatePubRulesState(newValue == SkatTableOptions.RuleSet.PUB)
        }
        updatePubRulesState(options.rules == SkatTableOptions.RuleSet.PUB)

        return ScrollPane(content).apply {
            isFitToWidth = true
        }
    }

    private fun createIssTab(): GridPane {
        val grid = GridPane().apply {
            hgap = 10.0
            vgap = 10.0
            padding = Insets(20.0)
        }

        val issAddressLabel = Label(strings.getString("iss_address"))
        val issAddressField = TextField().apply {
            textProperty().bindBidirectional(issAddressProperty)
        }
        grid.add(issAddressLabel, 0, 0)
        grid.add(issAddressField, 1, 0)

        val issPortLabel = Label(strings.getString("iss_port"))
        val issPortField = TextField().apply {
            textProperty().bindBidirectional(issPortProperty)
        }
        grid.add(issPortLabel, 0, 1)
        grid.add(issPortField, 1, 1)

        return grid
    }

    private fun saveOptions() {
        options.isShowTipsAtStartUp = showTipsAtStartUpProperty.get()
        options.isCheckForNewVersionAtStartUp = checkForNewVersionAtStartUpProperty.get()
        options.isHideToolbar = hideToolbarProperty.get()
        options.language = languageProperty.get()
        options.savePath = savePathProperty.get()
        options.waitTimeAfterTrick = waitTimeAfterTrickProperty.get()
        options.cardSet = cardSetProperty.get()

        options.rules = rulesProperty.get()
        options.isPlayContra = playContraProperty.get()
        options.isContraAfterBid18 = contraAfterBid18Property.get()
        options.isPlayBock = playBockProperty.get()
        options.isBockEventLostAfterContra = bockEventLostAfterContraProperty.get()
        options.isBockEventLostWith60 = bockEventLostWith60Property.get()
        options.isBockEventContraReCalled = bockEventContraReAnnouncedProperty.get()
        options.isBockEventMultipleOfHundredScore = bockEventPlayerHasX00PointsProperty.get()
        options.isBockEventLostGrand = bockEventLostGrandProperty.get()
        options.isPlayRamsch = playRamschProperty.get()
        options.setSchieberRamsch(schiebeRamschProperty.get())
        options.setSchieberRamschJacksInSkat(schiebeRamschJacksInSkatProperty.get())
        options.isRamschEventNoBid = ramschEventNoBidProperty.get()
        options.isRamschEventRamschAfterBock = ramschEventBockRamschProperty.get()
        options.ramschSkatOwner = ramschSkatOwnerProperty.get()
        options.isPlayRevolution = playRevolutionProperty.get()

        options.issAddress = issAddressProperty.get()
        try {
            options.issPort = issPortProperty.get().toInt()
        } catch (e: NumberFormatException) {
            // Ignore invalid port
        }

        options.saveJSkatProperties()
    }
}
