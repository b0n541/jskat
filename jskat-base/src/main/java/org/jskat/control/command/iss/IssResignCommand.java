package org.jskat.control.command.iss;

/**
 * This command is created when the user resigns on ISS.
 */
public class IssResignCommand {
    public final String tableName;

    public IssResignCommand(String tableName) {
        this.tableName = tableName;
    }
}
