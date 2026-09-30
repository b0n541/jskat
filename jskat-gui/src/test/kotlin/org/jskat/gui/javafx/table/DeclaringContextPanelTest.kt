package org.jskat.gui.javafx.table

import javafx.scene.layout.GridPane
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class DeclaringContextPanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `game announce panel is positioned to the right of the declaring context`() {
        onFxThread {
            val actions = emptyMap<JSkatAction, AbstractJSkatAction>()
            val userPanel = JSkatUserPanel("local", 10, true, actions)
            val panel = DeclaringContextPanel("local", actions, userPanel)

            val announcePanel = panel.children.single { it is GameAnnouncePanel }
            assertThat(GridPane.getColumnIndex(announcePanel)).isEqualTo(2)
        }
    }

}
