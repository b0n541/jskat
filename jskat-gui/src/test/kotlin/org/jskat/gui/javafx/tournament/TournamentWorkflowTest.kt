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
    @Test fun `native workflow starts real model-free run and verifies completed evidence without replay`() {
        JavaFxTestSupport.initializeToolkit()
        val root = root(); val file = configuration(root)
        val controller = onFxThread { TournamentController() }
        val panel = onFxThread { TournamentPanel(controller).apply { importConfiguration(file) } }
        awaitUi { panel.editor.configurationFile == file }
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
        assertEquals(6, TournamentService().verify(root.resolve("out"), CancellationToken.NONE).committedSet().games().size)
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
