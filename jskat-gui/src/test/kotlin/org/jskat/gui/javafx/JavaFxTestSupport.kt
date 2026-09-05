package org.jskat.gui.javafx

import javafx.application.Platform
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object JavaFxTestSupport {

    @Synchronized
    fun initializeToolkit() {
        val toolkitStarted = CountDownLatch(1)
        try {
            Platform.startup(toolkitStarted::countDown)
            check(toolkitStarted.await(1, TimeUnit.SECONDS))
        } catch (_: IllegalStateException) {
            // JavaFX owns one toolkit per JVM; it was initialized by an earlier test.
        }
    }
}
