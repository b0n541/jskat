package org.jskat.gui.javafx.tournament

import javafx.scene.control.TableView
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.jskat.tournament.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.Optional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TournamentPresentationTest {
    @Test fun `published ratings show values and unavailable uncertainty without changing shared ordering`() {
        JavaFxTestSupport.initializeToolkit()
        val displayed = CountDownLatch(1)
        val panel = onFxThread {
            val owner = TournamentController()
            TournamentPanel(owner).apply {
                javafx.scene.Scene(this, 1200.0, 900.0); applyCss(); layout()
                val table = lookup("#tournament-leaderboard") as TableView<*>
                table.items.addListener(javafx.collections.ListChangeListener { if (table.items.size == 2) displayed.countDown() })
                owner.execute { _, observer ->
                    val json = """{"provisional":true,"reproducible":false,"hasRatedEvidence":true,"leaderboard":[{"rank":1,"name":"A","implementation":"random","rating":1510.25,"interval":null,"intervalUnavailableReason":"fewer-than-two-complete-epochs"},{"rank":2,"name":"B","implementation":"algorithmic","rating":1489.75,"interval":[1400,1550]}]}"""
                    observer.onProgress(TournamentProgress(TournamentProgress.State.RUNNING,12,6,6,0,1,false,false,"Ratings published",
                        TournamentMetrics(1, Duration.ofSeconds(5),1.2,Optional.empty(),emptyList(),Optional.empty(),Optional.of(json))))
                }
            }
        }
        assertTrue(displayed.await(10, TimeUnit.SECONDS))
        onFxThread {
            val table = panel.lookup("#tournament-leaderboard") as TableView<*>
            assertTrue(table.items[0].toString().contains("1510.25"))
            assertTrue(table.items[0].toString().contains("fewer-than-two"))
            assertTrue(table.items[1].toString().contains("1400"))
            panel.detach()
        }
    }
}
