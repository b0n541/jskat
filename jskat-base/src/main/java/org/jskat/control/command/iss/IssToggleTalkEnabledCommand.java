package org.jskat.control.command.iss;

/**
 * This command is created when the user wants to toggle the talk enabled
 * setting on ISS.
 */
public class IssToggleTalkEnabledCommand {
    public final String tableName;

    public IssToggleTalkEnabledCommand(String tableName) {
        this.tableName = tableName;
    }
}
