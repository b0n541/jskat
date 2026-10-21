package org.jskat.gui.javafx.tournament

import org.jskat.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.JSkatMenuFactory
import org.jskat.gui.javafx.onFxThread
import javafx.scene.control.TabPane
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TournamentHostTest {
    @Test fun `inspection resume keeps shared run context when another inspection closes and monitoring reopens`() {
        JavaFxTestSupport.initializeToolkit()
        val entered = java.util.concurrent.CountDownLatch(1)
        val cleanup = java.util.concurrent.CountDownLatch(1)
        val ready = java.util.concurrent.CountDownLatch(1)
        val cancellation = java.util.concurrent.atomic.AtomicReference<org.jskat.tournament.CancellationToken>()
        val file = java.nio.file.Path.of("/tmp/retained/config.json")
        val requested = org.jskat.tournament.TournamentConfigurationReader().read(
            """{"schemaVersion":1,"ruleset":"standard-skat-v1","epochs":1,"output":"out","competitors":[{"id":"6edcf945-5b86-49a7-a6e2-2e5e85475da2","name":"Retained first","implementation":"random"},{"id":"f234c44a-617c-4bb1-8d7b-c17fa9f2e742","name":"Retained second","implementation":"algorithmic"}]}""", file).value()
        val backend = object : TournamentBackend() {
            override fun open(directory: java.nio.file.Path) = org.jskat.tournament.TournamentService.OpenedRun(
                org.jskat.tournament.DurableTournamentRunStore.Inspection(directory, java.util.Optional.of(java.util.UUID.randomUUID()), java.util.Optional.of(requested), emptyList()), java.util.Optional.empty(), emptyList())
            override fun audit(directory: java.nio.file.Path) = "history"
            override fun run(configuration: org.jskat.tournament.TournamentConfiguration, invocation: org.jskat.tournament.TournamentInvocation,
                options: org.jskat.tournament.TournamentSessionOptions, token: org.jskat.tournament.CancellationToken,
                observer: org.jskat.tournament.TournamentProgress.Observer): org.jskat.tournament.TournamentService.RunResult {
                assertEquals(requested, configuration)
                assertEquals(org.jskat.tournament.TournamentInvocation.Action.RESUME, invocation.action())
                cancellation.set(token); entered.countDown(); assertTrue(cleanup.await(10, java.util.concurrent.TimeUnit.SECONDS))
                return org.jskat.tournament.TournamentService.RunResult(org.jskat.tournament.TournamentService.Disposition.CANCELLED,
                    configuration.output().absolute(), java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(), emptyList(), emptyList())
            }
        }
        lateinit var host: TournamentTabHost
        lateinit var tabs: TabPane
        lateinit var owner: TournamentController
        onFxThread {
            tabs = TabPane(); owner = TournamentController()
            host = TournamentTabHost(tabs, owner, { true }, backend = backend)
            host.openInspection(requested.output().absolute())
            val panel = tabs.tabs[0].content as TournamentPanel
            panel.status.textProperty().addListener { _, _, text -> if (text.startsWith("Read-only")) ready.countDown() }
        }
        assertTrue(ready.await(10, java.util.concurrent.TimeUnit.SECONDS))
        onFxThread { (tabs.tabs[0].content as TournamentPanel).resumeWithRecovery { true } }
        assertTrue(entered.await(10, java.util.concurrent.TimeUnit.SECONDS))
        onFxThread {
            host.openInspection(requested.output().absolute())
            host.close(tabs.tabs[1])
            assertFalse(cancellation.get().isCancellationRequested)
            host.close(tabs.tabs[0])
            assertTrue(cancellation.get().isCancellationRequested)
            assertTrue(owner.active)
            host.openMonitor()
            val monitor = tabs.tabs[0].content as TournamentPanel
            assertEquals("Retained first", monitor.editor.rows[0].name.text)
            assertEquals(file, monitor.editor.configurationFile)
            assertTrue(monitor.editor.isDisable)
            assertFalse(owner.execute { _, _ -> fail("Second invocation") })
            host.close(tabs.tabs[0])
        }
        cleanup.countDown()
    }

    @Test fun `ordinary menu hides tournament and developer action reattaches one monitor`() {
        JSkatOptions.instance(DesktopSavePathResolver())
        JavaFxTestSupport.initializeToolkit()
        onFxThread {
            val ordinary = JSkatMenuFactory.build(false) { fail("Ordinary mode must not expose entry") }
            assertTrue(ordinary.menus.flatMap { it.items }.none { it.id == "tournament-menu" })
            val tabs = TabPane()
            val host = TournamentTabHost(tabs, TournamentController(), { true })
            val developer = JSkatMenuFactory.build(true) { host.openMonitor() }
            val entry = developer.menus.flatMap { it.items }.single { it.id == "tournament-menu" }
            entry.fire(); entry.fire()
            assertEquals(1, tabs.tabs.size)
            val panel = tabs.tabs[0].content
            host.close(tabs.tabs[0])
            assertTrue(tabs.tabs.isEmpty())
            entry.fire()
            assertSame(panel, tabs.tabs[0].content)
            host.close(tabs.tabs[0])
        }
    }
}
