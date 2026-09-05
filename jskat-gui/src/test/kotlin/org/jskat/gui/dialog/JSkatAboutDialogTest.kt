package org.jskat.gui.dialog

import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.util.JSkatResourceBundle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class JSkatAboutDialogTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
        }
    }

    @Test
    fun `recreates the v0 23 about information in a JavaFX dialog`() {
        val content = JSkatAboutDialogContent("0.24.0-SNAPSHOT")

        assertThat(content.title).isEqualTo(JSkatResourceBundle.INSTANCE.getString("about"))
        assertThat(content.headerText).isEqualTo("JSkat Version 0.24.0-SNAPSHOT")
        assertThat(content.text).contains(
            "https://www.jskat.org",
            "https://github.com/b0n541/jskat",
            JSkatResourceBundle.INSTANCE.getString("authors"),
            JSkatResourceBundle.INSTANCE.getString("cards"),
            JSkatResourceBundle.INSTANCE.getString("icons"),
            JSkatResourceBundle.INSTANCE.getString("backgroundImage")
        )
    }
}
