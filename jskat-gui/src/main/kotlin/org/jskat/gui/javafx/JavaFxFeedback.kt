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
            } ?: strings.getString("unknownCard")
            return JavaFxFeedback(
                strings.getString("playerPlayedSchwarzTitle"),
                strings.getString("playerPlayedSchwarzDiscarding", playerName, cardString)
            )
        }

        fun schwarzCardPlay(playerName: String, card: Card?): JavaFxFeedback = JavaFxFeedback(
            strings.getString("playerPlayedSchwarzTitle"),
            strings.getString(
                "playerPlayedSchwarzCardPlay",
                playerName,
                card?.let(strings::getCardStringForCardFace) ?: strings.getString("unknownCard")
            )
        )

        fun invalidNumberOfDiscardedCards(): JavaFxFeedback = error(
            "invalidNumberOfCardsInSkatTitle",
            "invalidNumberOfCardsInSkatMessage"
        )

        fun noJacksAllowedInDiscardedSkat(): JavaFxFeedback = error(
            "noJacksAllowedInSchieberamschSkatTitle",
            "noJacksAllowedInSchieberamschSkatMessage"
        )

        fun duplicateTableName(tableName: String): JavaFxFeedback = JavaFxFeedback(
            strings.getString("duplicateTableNameTitle"),
            strings.getString("duplicateTableNameMessage", tableName)
        )

        fun emptyTableName(): JavaFxFeedback = error(
            "invalidNameInputNullTitle",
            "invalidNameInputNullMessage"
        )

        private fun error(titleKey: String, messageKey: String): JavaFxFeedback = JavaFxFeedback(
            strings.getString(titleKey),
            strings.getString(messageKey)
        )
    }
}
