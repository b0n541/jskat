package org.jskat.control.gui.action;

/**
 * Interface for all JSkat actions.
 */
public interface JSkatCommand {

    /**
     * Executes the action.
     *
     * @param event The action event.
     */
    void execute(JSkatActionEvent event);

    /**
     * Sets the name of the action.
     *
     * @param name The name of the action.
     */
    void setName(String name);

    /**
     * Sets the short description of the action.
     *
     * @param shortDescription The short description of the action.
     */
    void setShortDescription(String shortDescription);

    /**
     * Sets whether the action is enabled.
     *
     * @param enabled True if the action is enabled, false otherwise.
     */
    void setEnabled(boolean enabled);
}
