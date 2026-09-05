package org.jskat.gui.javafx

import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.JSkatResourceBundle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class JavaFxFeedbackTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
        }
    }

    private val strings = JSkatResourceBundle.INSTANCE

    @Test
    fun `localizes Schwarz feedback for discarded cards and card play`() {
        val discarding = JavaFxFeedback.schwarzDiscarding("Ada", CardList.of(Card.CA, Card.SK))
        val cardPlay = JavaFxFeedback.schwarzCardPlay("Bert", Card.H7)

        assertThat(discarding.title).isEqualTo(strings.getString("playerPlayedSchwarzTitle"))
        assertThat(discarding.message).isEqualTo(
            strings.getString(
                "playerPlayedSchwarzDiscarding",
                "Ada",
                " ${strings.getCardStringForCardFace(Card.CA)} ${strings.getCardStringForCardFace(Card.SK)}"
            )
        )
        assertThat(cardPlay.message).isEqualTo(
            strings.getString("playerPlayedSchwarzCardPlay", "Bert", strings.getCardStringForCardFace(Card.H7))
        )
    }

    @Test
    fun `localizes unknown Schwarz cards instead of failing`() {
        assertThat(JavaFxFeedback.schwarzDiscarding("Ada", null).message)
            .contains(strings.getString("unknownCard"))
        assertThat(JavaFxFeedback.schwarzCardPlay("Bert", null).message)
            .contains(strings.getString("unknownCard"))
    }

    @Test
    fun `localizes every validation error shown to the user`() {
        assertThat(JavaFxFeedback.invalidNumberOfDiscardedCards()).isEqualTo(
            JavaFxFeedback(
                strings.getString("invalidNumberOfCardsInSkatTitle"),
                strings.getString("invalidNumberOfCardsInSkatMessage")
            )
        )
        assertThat(JavaFxFeedback.noJacksAllowedInDiscardedSkat()).isEqualTo(
            JavaFxFeedback(
                strings.getString("noJacksAllowedInSchieberamschSkatTitle"),
                strings.getString("noJacksAllowedInSchieberamschSkatMessage")
            )
        )
        assertThat(JavaFxFeedback.duplicateTableName("Table 1").message).isEqualTo(
            strings.getString("duplicateTableNameMessage", "Table 1")
        )
        assertThat(JavaFxFeedback.emptyTableName()).isEqualTo(
            JavaFxFeedback(
                strings.getString("invalidNameInputNullTitle"),
                strings.getString("invalidNameInputNullMessage")
            )
        )
    }
}
