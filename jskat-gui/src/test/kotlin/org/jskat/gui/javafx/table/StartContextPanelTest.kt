package org.jskat.gui.javafx.table

import javafx.geometry.Pos
import javafx.scene.control.Button
import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.main.StartSkatSeriesAction
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.javafx.JavaFxTestSupport
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class StartContextPanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeJavaFx() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `shows the start skat series button in the context panel`() {
        val action = StartSkatSeriesAction()
        val panel = StartContextPanel(action)

        assertThat(panel.children).singleElement().isInstanceOf(Button::class.java)
        assertThat((panel.children.single() as Button).text).isEqualTo(action.getValue(AbstractJSkatAction.NAME))
        assertThat(panel.alignment).isEqualTo(Pos.CENTER)
    }
}
