package org.jskat.gui.javafx

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

class JSkatThemeTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun startJavaFx() {
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
    fun `applies the application theme to ownerless error alerts without dialog-specific setup`() {
        onJavaFxThread {
            JSkatTheme.install()

            val alert = Alert(Alert.AlertType.ERROR)
            alert.show()
            alert.dialogPane.scene.root.applyCss()
            assertThat(alert.dialogPane.background).isNotNull()

            alert.dialogPane.style = "-fx-background-color: -fx-base"
            alert.dialogPane.scene.root.applyCss()

            assertThat(alert.dialogPane.scene.stylesheets).containsExactly(JSkatTheme.stylesheetUrl)
            assertThat(alert.dialogPane.background.fills.single().fill).isEqualTo(Color.web("#E2D9CA"))
            alert.hide()
        }
    }

    private fun onJavaFxThread(action: () -> Unit) {
        val task = FutureTask(action, Unit)
        Platform.runLater(task)
        task.get(10, TimeUnit.SECONDS)
    }
}
