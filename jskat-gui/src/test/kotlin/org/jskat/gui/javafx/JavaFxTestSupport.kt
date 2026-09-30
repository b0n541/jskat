package org.jskat.gui.javafx

import javafx.application.Platform
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val FX_THREAD_TIMEOUT_SECONDS = 10L

object JavaFxTestSupport {

    @Synchronized
    fun initializeToolkit() {
        val toolkitStarted = CountDownLatch(1)
        try {
            Platform.startup(toolkitStarted::countDown)
            check(toolkitStarted.await(FX_THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                "Timed out starting the JavaFX toolkit"
            }
        } catch (_: IllegalStateException) {
            // JavaFX owns one toolkit per JVM; it was initialized by an earlier test.
        }
    }
}

fun <T> onFxThread(action: () -> T): T {
    if (Platform.isFxApplicationThread()) return action()

    val result = arrayOfNulls<Any>(1)
    val failure = arrayOfNulls<Throwable>(1)
    val completed = CountDownLatch(1)
    Platform.runLater {
        try {
            result[0] = action()
        } catch (error: Throwable) {
            failure[0] = error
        } finally {
            completed.countDown()
        }
    }
    check(completed.await(FX_THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        "Timed out waiting for the JavaFX application thread"
    }
    failure[0]?.let { throw it }
    @Suppress("UNCHECKED_CAST")
    return result[0] as T
}
