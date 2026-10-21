package org.jskat.gui.javafx.tournament

import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.jskat.tournament.TournamentConfigurationReader
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Path

class TournamentEditorTest {
    @Test fun `invalid discovered release is visible but cannot replace imported source identity`() {
        JavaFxTestSupport.initializeToolkit()
        val file = Path.of("/tmp/imported/config.json")
        val requested = TournamentConfigurationReader().read("""{"schemaVersion":1,"ruleset":"standard-skat-v1","epochs":1,"output":"out","competitors":[{"id":"6edcf945-5b86-49a7-a6e2-2e5e85475da2","name":"ML","implementation":"ml","model":{"source":"installed","version":"broken"}},{"id":"f234c44a-617c-4bb1-8d7b-c17fa9f2e742","name":"Random","implementation":"random"}]}""", file).value()
        val invalid = org.jskat.tournament.DefaultTournamentInputResolver.Candidate(org.jskat.tournament.ModelSource.Installed("broken"), file.parent, "broken",
            listOf(org.jskat.tournament.ValidationIssue(org.jskat.tournament.ValidationIssue.Severity.ERROR,"model","/models","Wrong signature")))
        onFxThread {
            val editor = TournamentEditor(file)
            editor.discovery(org.jskat.tournament.PlayerImplementation.ML, listOf(invalid))
            editor.load(requested, false)
            assertEquals("broken", (TournamentConfigurationReader().read(editor.json(), file).value().competitors()[0].model() as org.jskat.tournament.ModelSource.Installed).version())
            val added = editor.addRow(org.jskat.tournament.PlayerImplementation.ML_PRO)
            editor.discovery(org.jskat.tournament.PlayerImplementation.ML_PRO, listOf(invalid))
            assertTrue(added.installed.items.contains(invalid))
            added.installed.value = invalid
            assertNull(added.installed.value)
        }
    }

    @Test fun `native roster edits and export preserve UUIDs and imported relative path context`() {
        JavaFxTestSupport.initializeToolkit()
        val file = Path.of("/tmp/imported/config.json")
        val json = """{"schemaVersion":1,"ruleset":"standard-skat-v1","epochs":2,"output":"runs/one","competitors":[{"id":"6edcf945-5b86-49a7-a6e2-2e5e85475da2","name":"Random","implementation":"random"},{"id":"f234c44a-617c-4bb1-8d7b-c17fa9f2e742","name":"Algorithmic","implementation":"algorithmic"}]}"""
        val requested = TournamentConfigurationReader().read(json, file).value()
        val exported = onFxThread {
            val editor = TournamentEditor(file)
            editor.load(requested, false)
            editor.rows[0].name.text = "Renamed"
            assertTrue(editor.count.text.contains("12"))
            assertEquals("standard-skat-v1", editor.ruleset.text)
            editor.exportJson(Path.of("/tmp/other/export.json"))
        }
        val result = TournamentConfigurationReader().read(exported, Path.of("/tmp/other/export.json"))
        assertTrue(result.valid(), result.issues().toString())
        assertEquals(requested.competitors()[0].id(), result.value().competitors()[0].id())
        assertEquals(requested.output().absolute(), result.value().output().absolute())
    }
}
