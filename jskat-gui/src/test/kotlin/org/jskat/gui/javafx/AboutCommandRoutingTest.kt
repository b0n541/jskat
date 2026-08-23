package org.jskat.gui.javafx

import com.google.common.eventbus.Subscribe
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.command.general.ShowAboutInformationCommand
import org.junit.jupiter.api.Test

class AboutCommandRoutingTest {

    @Test
    fun `JavaFX view subscribes to the about command`() {
        val handler = JSkatViewFX::class.java.getMethod(
            "showAboutDialogOn",
            ShowAboutInformationCommand::class.java
        )

        assertThat(handler.isAnnotationPresent(Subscribe::class.java)).isTrue()
    }
}
