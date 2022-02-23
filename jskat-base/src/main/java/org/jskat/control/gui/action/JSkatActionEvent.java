package org.jskat.control.gui.action;

/**
 * Action event for JSkat<br>
 * Swings action event is not used to enable other platforms like Android
 */
public class JSkatActionEvent {

    private String actionCommand = null;
    private Object actionSource = null;

    /**
     * Constructor
     *
     * @param actionCommand Action command
     * @param actionSource  Action source
     */
    public JSkatActionEvent(final String actionCommand,
                            final Object actionSource) {
        this.actionCommand = actionCommand;
        this.actionSource = actionSource;
    }

    /**
     * Constructor
     *
     * @param jskatAction  JSkat action
     * @param actionSource Action source
     */
    public JSkatActionEvent(final JSkatAction jskatAction,
                            final Object actionSource) {
        this.actionCommand = jskatAction.toString();
        this.actionSource = actionSource;
    }

    /**
     * Constructor
     *
     * @param jskatAction JSkat action
     */
    public JSkatActionEvent(final JSkatAction jskatAction) {
        this.actionCommand = jskatAction.toString();
    }

    /**
     * Gets the action command
     *
     * @return Action command
     */
    public String getActionCommand() {
        return this.actionCommand;
    }

    /**
     * Gets the action source
     *
     * @return Action source
     */
    public Object getSource() {
        return this.actionSource;
    }

    @Override
    public String toString() {
        return "Action: " + actionCommand + " source: " + actionSource; //$NON-NLS-1$//$NON-NLS-2$
    }
}
