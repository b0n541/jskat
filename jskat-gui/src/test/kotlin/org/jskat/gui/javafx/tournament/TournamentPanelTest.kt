package org.jskat.gui.javafx.tournament

import javafx.application.Platform
import javafx.scene.control.Button
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.jskat.tournament.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TournamentPanelTest {
    @Test fun `validation runs off FX and exposes the shared ordered issues on native fields`() {
        JavaFxTestSupport.initializeToolkit()
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val completed = CountDownLatch(1)
        val issues = listOf(ValidationIssue(ValidationIssue.Severity.ERROR, "OUTPUT_EXISTS", "/output", "Choose fresh output"))
        val backend = object : TournamentBackend() {
            override fun discover(implementation: PlayerImplementation) = emptyList<DefaultTournamentInputResolver.Candidate>()
            override fun preflight(configuration: TournamentConfiguration, cancellation: CancellationToken): TournamentService.Preflight {
                assertFalse(Platform.isFxApplicationThread())
                entered.countDown()
                assertTrue(release.await(10, TimeUnit.SECONDS))
                return TournamentService.Preflight(configuration, Optional.empty(), emptyList(), issues)
            }
        }
        val owner = onFxThread { TournamentController() }
        val panel = onFxThread {
            TournamentPanel(owner, backend).apply {
                issuesView.items.addListener(javafx.collections.ListChangeListener { if (issuesView.items.isNotEmpty()) completed.countDown() })
                (lookup("#tournament-validate") as Button).fire()
            }
        }
        assertTrue(entered.await(10, TimeUnit.SECONDS))
        onFxThread {
            assertTrue(owner.inputBusy)
            assertFalse(owner.execute { _, _ -> fail("Run must wait for source preflight") })
        }
        release.countDown()
        assertTrue(completed.await(10, TimeUnit.SECONDS))
        onFxThread {
            assertEquals(issues, panel.issuesView.items.toList())
            assertTrue(panel.editor.output.style.contains("border"))
            panel.detach()
        }
    }
}
