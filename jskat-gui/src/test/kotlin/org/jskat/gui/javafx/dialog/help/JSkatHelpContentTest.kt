package org.jskat.gui.javafx.dialog.help

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JSkatHelpContentTest {

    @Test
    fun `local help links use their resource URL`() {
        val gettingStartedPath = "org/jskat/gui/help/en/gettingStarted.html"
        val gettingStarted = checkNotNull(ClassLoader.getSystemResource(gettingStartedPath)).toExternalForm()
        val document = prepareHelpContent(
            "<a href=\"$gettingStartedPath\">Getting started</a>"
                + "<a href=\"https://github.com\">GitHub</a>",
            "<html><head></head><body>@@insert@@</body></html>",
        ) { path ->
            ClassLoader.getSystemResource(path)?.toExternalForm()
        }

        assertThat(document).contains("href=\"$gettingStarted\"")
        assertThat(document).contains("href=\"https://github.com\"")
    }
}
