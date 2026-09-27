package org.jskat.gui.javafx.table

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.image.ImageView
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.assertj.core.api.Assertions.assertThat
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.img.JSkatGraphicRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class BigActionButtonTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeJavaFx() = JavaFxTestSupport.initializeToolkit()
    }

    @Test
    fun `creates a styled big button that invokes its callback and follows enabled state`() {
        val enabled = SimpleBooleanProperty(true)
        var invoked = false
        val button = BigActionButton.create("Continue", JSkatGraphicRepository.Icon.PLAY, enabled) { invoked = true }

        assertThat(button.styleClass).contains(BigActionButton.STYLE_CLASS)
        assertThat(button.graphic).isInstanceOf(ImageView::class.java)
        assertThat(button.isDisable).isFalse()

        button.fire()
        enabled.set(false)

        assertThat(invoked).isTrue()
        assertThat(button.isDisable).isTrue()
    }

    @Test
    fun `dispatches a typed action with the supplied source`() {
        val action = RecordingAction()
        BigActionButton.dispatch(action, JSkatAction.PASS_BID, "ISS-42")

        assertThat(action.event?.actionCommand).isEqualTo(JSkatAction.PASS_BID.toString())
        assertThat(action.event?.source).isEqualTo("ISS-42")
    }

    private class RecordingAction : AbstractJSkatAction() {
        var event: JSkatActionEvent? = null
        override fun actionPerformed(event: JSkatActionEvent) {
            this.event = event
        }
    }
}
