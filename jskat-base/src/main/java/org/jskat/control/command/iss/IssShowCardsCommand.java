package org.jskat.control.command.iss;

/**
 * This command is created when the user wants to show the cards on ISS.
 */
public class IssShowCardsCommand {
    public final String tableName;

    public IssShowCardsCommand(String tableName) {
        this.tableName = tableName;
    }
}
