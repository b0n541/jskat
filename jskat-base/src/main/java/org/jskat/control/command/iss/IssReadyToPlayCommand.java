package org.jskat.control.command.iss;

/**
 * This command is created if the user is ready to play on ISS.
 */
public class IssReadyToPlayCommand {
    /**
     * Name of the table to join for play.
     */
    public final String tableName;

    /**
     * Performs iss ready to play command.
     * @param tableName table name value.
     */
    public IssReadyToPlayCommand(String tableName) {
        this.tableName = tableName;
    }
}
