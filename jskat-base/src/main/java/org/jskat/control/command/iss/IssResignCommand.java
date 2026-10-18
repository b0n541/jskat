package org.jskat.control.command.iss;

/**
 * This command is created when the user resigns on ISS.
 */
public class IssResignCommand {
    /**
     * Name of the table from which to resign.
     */
    public final String tableName;

    /**
     * Performs iss resign command.
     * @param tableName table name value.
     */
    public IssResignCommand(String tableName) {
        this.tableName = tableName;
    }
}
