package org.jskat.gui.javafx.tournament

import javafx.application.Platform
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TournamentControllerTest {
    @Test fun `routine progress coalesces independently of human wording`() {
        JavaFxTestSupport.initializeToolkit()
        val emitted = CountDownLatch(1)
        val release = CountDownLatch(1)
        val finished = CountDownLatch(1)
        val displayed = mutableListOf<String>()
        val controller = onFxThread {
            TournamentController().apply {
                subscribe { progress?.let { displayed.add(it.message()) } }
                execute { _, observer ->
                    for (i in 0..2) observer.onProgress(org.jskat.tournament.TournamentProgress(
                        org.jskat.tournament.TournamentProgress.State.RUNNING, 6, 0, 0, 0, 0,
                        false, true, "Changed wording $i", org.jskat.tournament.TournamentMetrics.empty()))
                    emitted.countDown()
                    assertTrue(release.await(10, TimeUnit.SECONDS))
                }
                // Keep FX blocked until the observation burst is queued.
                assertTrue(emitted.await(10, TimeUnit.SECONDS))
            }
        }
        onFxThread {
            assertEquals(listOf("Changed wording 0", "Changed wording 2"), displayed)
            controller.requestExit({ true }) { finished.countDown() }
        }
        release.countDown()
        assertTrue(finished.await(10, TimeUnit.SECONDS))
    }

    @Test fun `Continue preserves monitoring and finalizing approval detaches without new cancellation`() {
        JavaFxTestSupport.initializeToolkit()
        val entered = CountDownLatch(1)
        val cleanup = CountDownLatch(1)
        val exited = CountDownLatch(1)
        val token = java.util.concurrent.atomic.AtomicReference<org.jskat.tournament.TournamentCancellation>()
        val owner = onFxThread {
            TournamentController().apply { execute { cancellation, observer ->
                token.set(cancellation)
                observer.onProgress(org.jskat.tournament.TournamentProgress(org.jskat.tournament.TournamentProgress.State.FINALIZING,
                    6,6,6,0,1,false,true,"Publishing final reports"))
                entered.countDown(); assertTrue(cleanup.await(10, TimeUnit.SECONDS))
            } }
        }
        assertTrue(entered.await(10, TimeUnit.SECONDS))
        onFxThread {
            owner.closeMonitor({ false }) { fail("Continue / Escape must keep monitoring") }
            assertFalse(token.get().isCancellationRequested)
            var detached = false
            owner.closeMonitor({ true }) { detached = true }
            assertTrue(detached)
            assertTrue(owner.active)
            assertFalse(token.get().isCancellationRequested)
            owner.requestExit({ true }) { exited.countDown() }
            assertEquals(1, exited.count)
        }
        cleanup.countDown(); assertTrue(exited.await(10, TimeUnit.SECONDS))
    }

    @Test fun `broken monitor cannot prevent runner cleanup and application exit`() {
        JavaFxTestSupport.initializeToolkit()
        val entered = CountDownLatch(1)
        val cleanup = CountDownLatch(1)
        val exited = CountDownLatch(1)
        val controller = onFxThread {
            TournamentController().apply {
                subscribe { throw IllegalStateException("Disposed view") }
                assertTrue(execute { _, _ -> entered.countDown(); cleanup.await(10, TimeUnit.SECONDS); throw AssertionError("Boundary failure") })
            }
        }
        assertTrue(entered.await(10, TimeUnit.SECONDS))
        onFxThread { controller.requestExit({ true }) { exited.countDown() } }
        cleanup.countDown()
        assertTrue(exited.await(10, TimeUnit.SECONDS))
        onFxThread { assertFalse(controller.active); assertTrue(controller.diagnostic.contains("Boundary failure")) }
    }

    @Test fun `detaching a cancelled monitor keeps ownership until background cleanup ends`() {
        JavaFxTestSupport.initializeToolkit()
        val entered = CountDownLatch(1)
        val cleanup = CountDownLatch(1)
        val exited = CountDownLatch(1)
        val controller = onFxThread { TournamentController() }
        onFxThread {
            assertTrue(controller.execute { cancellation, _ ->
                assertFalse(Platform.isFxApplicationThread())
                entered.countDown()
                assertTrue(cleanup.await(10, TimeUnit.SECONDS))
                assertTrue(cancellation.isCancellationRequested)
            })
        }
        assertTrue(entered.await(10, TimeUnit.SECONDS))
        onFxThread {
            controller.closeMonitor({ true }) { }
            assertTrue(controller.active)
            assertFalse(controller.execute { _, _ -> fail("Second invocation") })
            controller.requestExit({ false }) { fail("Stay must not exit") }
            controller.requestExit({ true }) { exited.countDown() }
            assertEquals(1, exited.count)
        }
        cleanup.countDown()
        assertTrue(exited.await(10, TimeUnit.SECONDS))
        onFxThread { assertFalse(controller.active) }
    }
}
