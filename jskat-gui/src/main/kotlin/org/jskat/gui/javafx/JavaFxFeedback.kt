package org.jskat.gui.javafx

import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.JSkatResourceBundle

data class JavaFxFeedback(val title: String, val message: String) {
    companion object {
        private val strings = JSkatResourceBundle.INSTANCE

        fun schwarzDiscarding(playerName: String, discardedCards: CardList?): JavaFxFeedback {
            val cardString = discardedCards?.joinToString(separator = "") {
                " ${strings.getCardStringForCardFace(it)}"
            } ?: strings.getString("unknown_card")
            return JavaFxFeedback(
                strings.getString("player_played_schwarz_title"),
                strings.getString("player_played_schwarz_discarding", playerName, cardString)
            )
        }

        fun schwarzCardPlay(playerName: String, card: Card?): JavaFxFeedback = JavaFxFeedback(
            strings.getString("player_played_schwarz_title"),
            strings.getString(
                "player_played_schwarz_card_play",
                playerName,
                card?.let(strings::getCardStringForCardFace) ?: strings.getString("unknown_card")
            )
        )

        fun invalidNumberOfDiscardedCards(): JavaFxFeedback = error(
            "invalid_number_of_cards_in_skat_title",
            "invalid_number_of_cards_in_skat_message"
        )

        fun noJacksAllowedInDiscardedSkat(): JavaFxFeedback = error(
            "no_jacks_allowed_in_schieberamsch_skat_title",
            "no_jacks_allowed_in_schieberamsch_skat_message"
        )

        fun duplicateTableName(tableName: String): JavaFxFeedback = JavaFxFeedback(
            strings.getString("duplicate_table_name_title"),
            strings.getString("duplicate_table_name_message", tableName)
        )

        fun emptyTableName(): JavaFxFeedback = error(
            "invalid_name_input_null_title",
            "invalid_name_input_null_message"
        )

        private fun error(titleKey: String, messageKey: String): JavaFxFeedback = JavaFxFeedback(
            strings.getString(titleKey),
            strings.getString(messageKey)
        )
    }
}
