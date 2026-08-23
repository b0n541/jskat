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

        assertThat(discarding.title).isEqualTo(strings.getString("player_played_schwarz_title"))
        assertThat(discarding.message).isEqualTo(
            strings.getString(
                "player_played_schwarz_discarding",
                "Ada",
                " ${strings.getCardStringForCardFace(Card.CA)} ${strings.getCardStringForCardFace(Card.SK)}"
            )
        )
        assertThat(cardPlay.message).isEqualTo(
            strings.getString("player_played_schwarz_card_play", "Bert", strings.getCardStringForCardFace(Card.H7))
        )
    }

    @Test
    fun `localizes unknown Schwarz cards instead of failing`() {
        assertThat(JavaFxFeedback.schwarzDiscarding("Ada", null).message)
            .contains(strings.getString("unknown_card"))
        assertThat(JavaFxFeedback.schwarzCardPlay("Bert", null).message)
            .contains(strings.getString("unknown_card"))
    }

    @Test
    fun `localizes every validation error shown to the user`() {
        assertThat(JavaFxFeedback.invalidNumberOfDiscardedCards()).isEqualTo(
            JavaFxFeedback(
                strings.getString("invalid_number_of_cards_in_skat_title"),
                strings.getString("invalid_number_of_cards_in_skat_message")
            )
        )
        assertThat(JavaFxFeedback.noJacksAllowedInDiscardedSkat()).isEqualTo(
            JavaFxFeedback(
                strings.getString("no_jacks_allowed_in_schieberamsch_skat_title"),
                strings.getString("no_jacks_allowed_in_schieberamsch_skat_message")
            )
        )
        assertThat(JavaFxFeedback.duplicateTableName("Table 1").message).isEqualTo(
            strings.getString("duplicate_table_name_message", "Table 1")
        )
        assertThat(JavaFxFeedback.emptyTableName()).isEqualTo(
            JavaFxFeedback(
                strings.getString("invalid_name_input_null_title"),
                strings.getString("invalid_name_input_null_message")
            )
        )
    }
}
