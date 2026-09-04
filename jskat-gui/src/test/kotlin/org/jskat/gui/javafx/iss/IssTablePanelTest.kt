package org.jskat.gui.javafx.iss

import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class IssTablePanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
        }
    }

    @Test
    fun `ISS game actions use the table name`() {
        val resign = RecordingAction("Give up")
        val showCards = RecordingAction("Show cards")

        performIssTableAction(resign, JSkatAction.RESIGN, "ISS-42")
        performIssTableAction(showCards, JSkatAction.SHOW_CARDS, "ISS-42")

        assertThat(resign.source).isEqualTo("ISS-42")
        assertThat(showCards.source).isEqualTo("ISS-42")
    }

    @Test
    fun `ISS game actions provide big-button icons`() {
        assertThat(org.jskat.gui.action.iss.ResignAction().icon).isEqualTo(Icon.WHITE_FLAG)
        assertThat(org.jskat.gui.action.iss.ShowCardsAction().icon).isEqualTo(Icon.PLAY)
    }

    private class RecordingAction(name: String) : AbstractJSkatAction() {
        var source: Any? = null

        init {
            putValue(NAME, name)
        }

        override fun actionPerformed(event: JSkatActionEvent) {
            source = event.source
        }
    }
}
