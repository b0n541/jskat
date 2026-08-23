package org.jskat.gui.dialog

import javafx.application.Platform
import javafx.scene.image.ImageView
import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.util.JSkatResourceBundle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

class JSkatAboutDialogTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun startJavaFx() {
            JSkatOptions.instance(DesktopSavePathResolver())
            val started = CountDownLatch(1)
            try {
                Platform.startup(started::countDown)
            } catch (_: IllegalStateException) {
                started.countDown()
            }
            check(started.await(10, TimeUnit.SECONDS)) { "JavaFX toolkit did not start" }
        }
    }

    @Test
    fun `recreates the v0 23 about information in a JavaFX dialog`() {
        onJavaFxThread {
            val dialog = JSkatAboutDialog()

            assertThat(dialog.title).isEqualTo(JSkatResourceBundle.INSTANCE.getString("about"))
            assertThat(dialog.headerText).isEqualTo("JSkat Version 0.24.0-SNAPSHOT")
            assertThat(dialog.contentText).contains(
                "https://www.jskat.org",
                "https://github.com/b0n541/jskat",
                JSkatResourceBundle.INSTANCE.getString("authors"),
                JSkatResourceBundle.INSTANCE.getString("cards"),
                JSkatResourceBundle.INSTANCE.getString("icons"),
                JSkatResourceBundle.INSTANCE.getString("background_image")
            )
            assertThat(dialog.graphic).isInstanceOf(ImageView::class.java)
            assertThat(dialog.dialogPane.minWidth).isEqualTo(600.0)
        }
    }

    private fun onJavaFxThread(action: () -> Unit) {
        val task = FutureTask(action, Unit)
        Platform.runLater(task)
        task.get(10, TimeUnit.SECONDS)
    }
}
