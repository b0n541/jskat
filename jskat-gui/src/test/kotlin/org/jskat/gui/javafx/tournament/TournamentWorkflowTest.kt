package org.jskat.gui.javafx.tournament

import javafx.scene.control.Button
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.jskat.tournament.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class TournamentWorkflowTest {
    private fun root(): Path {
        val selected = System.getenv("JSKAT_TOURNAMENT_EXT4_TEST_ROOT")
        assumeTrue(selected != null, "Select supported ext4 root for durable integration tests")
        return Files.createDirectories(Path.of(selected).resolve("gui-${UUID.randomUUID()}"))
    }
    private fun configuration(root: Path, model: Path? = null): Path {
        val file = root.resolve("config.json")
        val first = if (model == null) """"implementation":"random"""" else """"implementation":"ml","model":{"source":"development","path":"$model"}"""
        Files.writeString(file, """{"schemaVersion":1,"ruleset":"standard-skat-v1","epochs":1,"seed":"123","output":"out","competitors":[{"id":"6edcf945-5b86-49a7-a6e2-2e5e85475da2","name":"First",$first},{"id":"f234c44a-617c-4bb1-8d7b-c17fa9f2e742","name":"Algorithmic","implementation":"algorithmic"}]}""")
        return file
    }
    private fun awaitUi(condition: () -> Boolean) {
        val end = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(30)
        while (System.nanoTime() < end) {
            if (onFxThread(condition)) return
            Thread.sleep(20)
        }
        fail<Unit>("UI did not reach expected state")
    }
    @Test fun `native import and real source preflight preserve complete shared ordered issues`() {
        JavaFxTestSupport.initializeToolkit()
        val root = root(); val file = configuration(root)
        val panel = onFxThread { TournamentPanel(TournamentController()) }
        Files.writeString(file, """{"schemaVersion":2,"epochs":1.5,"unknown":true}""")
        val structural = TournamentService().read(file).issues()
        onFxThread { panel.importConfiguration(file) }
        awaitUi { panel.issuesView.items.isNotEmpty() }
        onFxThread { assertEquals(structural, panel.issuesView.items.toList()) }
        configuration(root, root.resolve("missing-source"))
        Files.createDirectory(root.resolve("out"))
        val service = TournamentService()
        val expected = service.preflightNew(service.read(file).value(), CancellationToken.NONE).issues()
        assertTrue(expected.size >= 2)
        onFxThread { panel.importConfiguration(file) }
        awaitUi { panel.editor.configurationFile == file && !(panel.lookup("#tournament-validate") as Button).isDisable }
        onFxThread { (panel.lookup("#tournament-validate") as Button).fire() }
        awaitUi { panel.status.text == "Validation failed" }
        onFxThread {
            assertEquals(expected, panel.issuesView.items.toList())
            panel.detach()
        }
        Files.list(root.resolve("out")).use { assertEquals(0, it.count()) }
    }

    @Test fun `native workflow starts real model-free run and verifies completed evidence without replay`() {
        JavaFxTestSupport.initializeToolkit()
        val root = root(); val file = configuration(root)
        val controller = onFxThread { TournamentController() }
        val panel = onFxThread { TournamentPanel(controller).apply { importConfiguration(file) } }
        awaitUi { panel.editor.configurationFile == file && !(panel.lookup("#tournament-validate") as Button).isDisable }
        onFxThread { (panel.lookup("#tournament-validate") as Button).fire() }
        awaitUi { panel.status.text.startsWith("Validation passed") }
        assertFalse(Files.exists(root.resolve("out")))
        onFxThread { (panel.lookup("#tournament-start") as Button).fire() }
        awaitUi { !controller.active && controller.progress?.state() == TournamentProgress.State.COMPLETED }
        assertEquals(6, controller.progress!!.committedGames())
        onFxThread { panel.openRun(root.resolve("out")) }
        awaitUi { panel.status.text.startsWith("Read-only retained inspection") }
        onFxThread { (panel.lookup("#tournament-verify") as Button).fire() }
        awaitUi { panel.status.text.startsWith("Verified completed") }
        onFxThread {
            assertTrue((panel.lookup("#tournament-resume") as Button).isDisable)
            assertTrue(panel.editor.output.isDisable)
            panel.detach()
        }
        val before = TournamentService().verify(root.resolve("out"), CancellationToken.NONE)
        assertEquals(6, before.committedSet().games().size)
        Files.delete(root.resolve("out/reports").resolve(before.reports().orElseThrow().generationId()).resolve("leaderboard.csv"))
        val recovery = onFxThread { TournamentPanel(controller).apply { openRun(root.resolve("out")) } }
        awaitUi { recovery.status.text.startsWith("Read-only retained inspection") }
        onFxThread { (recovery.lookup("#tournament-verify") as Button).fire() }
        awaitUi { recovery.status.text.startsWith("Verified retained snapshots") }
        onFxThread {
            assertFalse((recovery.lookup("#tournament-resume") as Button).isDisable)
            (recovery.lookup("#tournament-resume") as Button).fire()
        }
        awaitUi { !controller.active && recovery.status.text == "COMPLETED" }
        assertEquals(0, controller.progress!!.admittedGames())
        val after = TournamentService().verify(root.resolve("out"), CancellationToken.NONE)
        assertTrue(after.successfullyCompleted())
        assertEquals(before.committedSet(), after.committedSet())
        assertNotEquals(before.reports().orElseThrow().generationId(), after.reports().orElseThrow().generationId())
        val ready = root.resolve("out/ready.json")
        val unsupported = Files.readString(ready).replace("\"schemaVersion\":1", "\"schemaVersion\":2")
        Files.writeString(ready, unsupported)
        onFxThread { recovery.openRun(root.resolve("out")) }
        awaitUi { recovery.status.text.startsWith("Read-only retained inspection") }
        onFxThread {
            assertTrue((recovery.lookup("#tournament-start") as Button).isDisable)
            assertTrue((recovery.lookup("#tournament-resume") as Button).isDisable)
            recovery.detach()
        }
        assertEquals(unsupported, Files.readString(ready))
    }

    @Test fun `retained native inspection verifies snapshots after original source disappears and corruption stays inspectable`() {
        JavaFxTestSupport.initializeToolkit()
        val installed = Path.of(System.getProperty("user.home"), ".jskat/models/releases/v1.4.0")
        assumeTrue(Files.exists(installed.resolve("models.json")), "Installed ONNX fixture required")
        val root = root(); val source = Files.createDirectory(root.resolve("development"))
        Files.list(installed).use { files -> files.filter { Files.isRegularFile(it) }.forEach { Files.copy(it, source.resolve(it.fileName)) } }
        val file = configuration(root, source)
        val requested = TournamentConfigurationReader().read(file).value()
        DurableTournamentRunStore.create(requested, 123, TournamentBuildIdentity.current(), DefaultTournamentInputResolver(), CancellationToken.NONE).close()
        Files.move(source, root.resolve("removed-source"))
        val panel = onFxThread { TournamentPanel(TournamentController()).apply { openRun(root.resolve("out")) } }
        awaitUi { panel.status.text.startsWith("Read-only retained inspection") }
        onFxThread { (panel.lookup("#tournament-verify") as Button).fire() }
        awaitUi { panel.status.text.startsWith("Verified retained snapshots") }
        onFxThread {
            assertEquals(file, panel.editor.configurationFile)
            assertTrue(panel.editor.output.isDisable)
            assertFalse((panel.lookup("#tournament-resume") as Button).isDisable)
            panel.openRun(root.resolve("absent"))
        }
        awaitUi { panel.status.text.startsWith("Read-only retained inspection") }
        onFxThread {
            assertTrue((panel.lookup("#tournament-start") as Button).isDisable)
            assertTrue((panel.lookup("#tournament-resume") as Button).isDisable)
            assertTrue((panel.lookup("#tournament-verify") as Button).isDisable)
            panel.detach()
        }
    }
}
