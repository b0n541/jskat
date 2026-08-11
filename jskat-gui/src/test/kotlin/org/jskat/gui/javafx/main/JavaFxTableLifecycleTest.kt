package org.jskat.gui.javafx.main

import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.JSkatViewType
import org.junit.jupiter.api.Test

class JavaFxTableLifecycleTest {

    @Test
    fun `suggests the next numbered local table name`() {
        assertThat(JavaFxTableLifecycle.defaultLocalTableName("Local table", 0)).isEqualTo("Local table 1")
        assertThat(JavaFxTableLifecycle.defaultLocalTableName("Local table", 4)).isEqualTo("Local table 5")
    }

    @Test
    fun `limits submitted table names to the legacy maximum while preserving valid names`() {
        assertThat(JavaFxTableLifecycle.submittedTableName("A table")).isEqualTo("A table")
        assertThat(JavaFxTableLifecycle.submittedTableName("x".repeat(101))).hasSize(100)
        assertThat(JavaFxTableLifecycle.isValidTableName("")).isFalse()
        assertThat(JavaFxTableLifecycle.isValidTableName("A table")).isTrue()
    }

    @Test
    fun `identifies only local and ISS tabs as selectable tables`() {
        val local = JavaFxTableLifecycle.Table(JSkatViewType.LOCAL_TABLE, "Local table 1")
        val iss = JavaFxTableLifecycle.Table(JSkatViewType.ISS_TABLE, "ISS table")
        val welcome = JavaFxTableLifecycle.Table(JSkatViewType.OTHER, "Welcome")

        assertThat(JavaFxTableLifecycle.selectedTable(local)).isEqualTo(local)
        assertThat(JavaFxTableLifecycle.selectedTable(iss)).isEqualTo(iss)
        assertThat(JavaFxTableLifecycle.selectedTable(welcome)).isNull()
        assertThat(JavaFxTableLifecycle.selectedTable(null)).isNull()
    }

    @Test
    fun `selecting a table activates it and requests focus`() {
        val table = JavaFxTableLifecycle.Table(JSkatViewType.LOCAL_TABLE, "Local table 1")
        var activatedTable: JavaFxTableLifecycle.Table? = null
        var focusRequested = false

        JavaFxTableLifecycle.selectTable(
            table,
            activateTable = { activatedTable = it },
            requestFocus = { focusRequested = true },
        )

        assertThat(activatedTable).isEqualTo(table)
        assertThat(focusRequested).isTrue()
    }

    @Test
    fun `closes local tables by removal and ISS tables by leaving`() {
        assertThat(JavaFxTableLifecycle.closeAction(JavaFxTableLifecycle.Table(JSkatViewType.LOCAL_TABLE, "Local")))
            .isEqualTo(JavaFxTableLifecycle.CloseAction.RemoveLocalTable("Local"))
        assertThat(JavaFxTableLifecycle.closeAction(JavaFxTableLifecycle.Table(JSkatViewType.ISS_TABLE, "ISS")))
            .isEqualTo(JavaFxTableLifecycle.CloseAction.LeaveIssTable("ISS"))
    }

    @Test
    fun `closing a table dispatches its matching lifecycle action`() {
        var removedLocalTable: String? = null
        var leftIssTable: String? = null

        JavaFxTableLifecycle.closeTable(
            JavaFxTableLifecycle.Table(JSkatViewType.LOCAL_TABLE, "Local"),
            removeLocalTable = { removedLocalTable = it },
            leaveIssTable = { leftIssTable = it },
        )
        JavaFxTableLifecycle.closeTable(
            JavaFxTableLifecycle.Table(JSkatViewType.ISS_TABLE, "ISS"),
            removeLocalTable = { removedLocalTable = it },
            leaveIssTable = { leftIssTable = it },
        )

        assertThat(removedLocalTable).isEqualTo("Local")
        assertThat(leftIssTable).isEqualTo("ISS")
    }
}
