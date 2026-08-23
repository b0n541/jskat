package org.jskat.gui.javafx

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JSkatThemeTest {

    @Test
    fun `packages the application theme with the expected base color`() {
        val stylesheet = requireNotNull(
            JSkatTheme::class.java.getResource("/org/jskat/gui/javafx/jskat.css")
        ) { "JSkat stylesheet is missing" }

        assertThat(JSkatTheme.stylesheetUrl).isEqualTo(stylesheet.toExternalForm())
        assertThat(stylesheet.readText()).contains(
            "-color-primary-0: #E2D9CA",
            "-fx-base: -color-primary-0"
        )
    }
}
