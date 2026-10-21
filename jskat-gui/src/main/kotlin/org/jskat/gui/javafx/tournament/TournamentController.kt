package org.jskat.gui.javafx.tournament

import javafx.application.Platform
import org.jskat.tournament.TournamentCancellation
import org.jskat.tournament.TournamentProgress
import org.jskat.tournament.TournamentConfiguration
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/** Process lifetime owner. A monitor subscription never owns execution or cleanup. */
class TournamentController {
    var configuration: TournamentConfiguration? = null
        private set

    fun bindConfiguration(configuration: TournamentConfiguration) {
        check(Platform.isFxApplicationThread())
        this.configuration = configuration
        notifyMonitors()
    }

    private var inputOperations = 0
    val inputBusy: Boolean get() = inputOperations > 0

    fun beginInputWork(): AutoCloseable? {
        check(Platform.isFxApplicationThread())
        if (active) return null
        inputOperations++
        notifyMonitors()
        return AutoCloseable { inputOperations--; notifyMonitors() }
    }

    var active: Boolean = false
        private set
    var progress: TournamentProgress? = null
        private set
    var diagnostic: String = "Ready"
        private set
    private var cancellation = TournamentCancellation()
    private val listeners = linkedSetOf<() -> Unit>()
    private var afterCleanup: (() -> Unit)? = null
    @Volatile var nonReproducible: Boolean = false
        private set
    private var observedState: TournamentProgress.State? = null
    private val latest = AtomicReference<TournamentProgress?>()
    private val scheduled = AtomicBoolean()

    fun subscribe(listener: () -> Unit): AutoCloseable {
        listeners.add(listener)
        notifyListener(listener)
        return AutoCloseable { listeners.remove(listener) }
    }

    private fun notifyListener(listener: () -> Unit) {
        try { listener() } catch (_: Throwable) { /* Disposed monitors cannot change execution ownership. */ }
    }
    private fun notifyMonitors() = listeners.toList().forEach(::notifyListener)

    fun execute(configuration: TournamentConfiguration? = null, operation: (TournamentCancellation, TournamentProgress.Observer) -> Unit): Boolean {
        check(Platform.isFxApplicationThread())
        if (active || inputBusy) return false
        active = true
        this.configuration = configuration
        cancellation = TournamentCancellation()
        progress = null
        nonReproducible = false
        observedState = null
        diagnostic = "Preparing tournament"
        notifyMonitors()
        // This non-daemon owner survives all monitor disposal until operation returns after cleanup.
        val executor = Executors.newSingleThreadExecutor { Thread(it, "tournament-owner") }
        executor.execute {
            var failure: String? = null
            try {
                operation(cancellation, TournamentProgress.Observer(::observe))
            } catch (error: Throwable) {
                failure = error.toString()
            } finally {
                executor.shutdown()
                Platform.runLater {
                    latest.getAndSet(null)?.let(::applyProgress)
                    active = false
                    diagnostic = failure ?: progress?.message() ?: "Operation finished"
                    notifyMonitors()
                    val exit = afterCleanup
                    afterCleanup = null
                    exit?.invoke()
                }
            }
        }
        return true
    }

    private fun observe(value: TournamentProgress) {
        if (!value.reproducible()) nonReproducible = true
        val immediate = value.state() != observedState || value.message() !in setOf("Tournament progress", "Game progress")
        observedState = value.state()
        if (immediate) {
            latest.set(null)
            Platform.runLater { applyProgress(value); notifyMonitors() }
            return
        }
        latest.set(value)
        if (scheduled.compareAndSet(false, true)) Platform.runLater {
            scheduled.set(false)
            latest.getAndSet(null)?.let(::applyProgress)
            notifyMonitors()
        }
    }

    private fun applyProgress(value: TournamentProgress) {
        if (progress == null || value.metrics().sequence() >= progress!!.metrics().sequence()) progress = value
    }

    fun cancel() {
        if (!active || cancellation.isCancellationRequested) return
        if (progress?.state() == TournamentProgress.State.FINALIZING) return
        cancellation.requestCancellation()
        diagnostic = "Cancellation requested; admitted games and cleanup continue in background"
        notifyMonitors()
    }

    fun closeMonitor(confirm: () -> Boolean, detach: () -> Unit) {
        if (!active || confirm()) {
            cancel()
            detach()
        }
    }

    fun requestExit(confirm: () -> Boolean, exit: () -> Unit) {
        if (!active) exit()
        else if (afterCleanup == null && confirm()) {
            afterCleanup = exit
            cancel()
        }
    }
}
