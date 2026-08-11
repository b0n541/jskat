package org.jskat.gui.javafx.dialog.help

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class JSkatHelpContentTest {

    @Test
    fun `local help links resolve from the classpath root`() {
        val classpathRoot = "file:/application/classes/"
        val document = prepareHelpContent(
            "<a href=\"org/jskat/gui/help/en/gettingStarted.html\">Getting started</a>",
            "<html><head></head><body>@@insert@@</body></html>",
            classpathRoot
        )

        assertThat(document).contains("<base href=\"$classpathRoot\">")
        assertThat(URI(classpathRoot).resolve("org/jskat/gui/help/en/gettingStarted.html"))
            .isEqualTo(URI("file:/application/classes/org/jskat/gui/help/en/gettingStarted.html"))
    }
}
