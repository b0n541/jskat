package org.jskat.control.command.iss;

/**
 * This command is created when the user wants to toggle the talk enabled
 * setting on ISS.
 */
public class IssToggleTalkEnabledCommand {
    /**
     * Name of the table whose talk state should change.
     */
    public final String tableName;

    /**
     * Performs iss toggle talk enabled command.
     * @param tableName table name value.
     */
    public IssToggleTalkEnabledCommand(String tableName) {
        this.tableName = tableName;
    }
}
