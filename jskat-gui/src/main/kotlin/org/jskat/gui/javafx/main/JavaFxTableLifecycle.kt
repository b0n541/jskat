package org.jskat.gui.javafx.main

import org.jskat.data.JSkatViewType

/**
 * Toolkit-neutral decisions for JavaFX table tabs.
 */
object JavaFxTableLifecycle {

    private const val MAXIMUM_TABLE_NAME_LENGTH = 100

    data class Table(val type: JSkatViewType, val name: String)

    sealed interface CloseAction {
        data class RemoveLocalTable(val tableName: String) : CloseAction
        data class LeaveIssTable(val tableName: String) : CloseAction
    }

    fun defaultLocalTableName(localTableLabel: String, localTablesCreated: Int): String =
        "$localTableLabel ${localTablesCreated + 1}"

    fun submittedTableName(tableName: String): String = tableName.take(MAXIMUM_TABLE_NAME_LENGTH)

    fun isValidTableName(tableName: String): Boolean = tableName.isNotEmpty()

    fun selectedTable(tabTable: Table?): Table? = tabTable?.takeIf {
        it.type == JSkatViewType.LOCAL_TABLE || it.type == JSkatViewType.ISS_TABLE
    }

    fun closeAction(table: Table): CloseAction = when (table.type) {
        JSkatViewType.LOCAL_TABLE -> CloseAction.RemoveLocalTable(table.name)
        JSkatViewType.ISS_TABLE -> CloseAction.LeaveIssTable(table.name)
        else -> error("Cannot close a non-table tab")
    }
}
