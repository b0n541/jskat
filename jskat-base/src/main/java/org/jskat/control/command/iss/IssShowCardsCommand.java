package org.jskat.control.command.iss;

/**
 * This command is created when the user wants to show the cards on ISS.
 */
public class IssShowCardsCommand {
    /**
     * Name of the table whose cards should be shown.
     */
    public final String tableName;

    /**
     * Performs iss show cards command.
     * @param tableName table name value.
     */
    public IssShowCardsCommand(String tableName) {
        this.tableName = tableName;
    }
}
