package org.jskat.control.command.iss;

/**
 * This command is created if the user is ready to play on ISS.
 */
public class IssReadyToPlayCommand {
    public final String tableName;

    public IssReadyToPlayCommand(String tableName) {
        this.tableName = tableName;
    }
}
