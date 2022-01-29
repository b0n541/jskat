package org.jskat.control.event.general;

/**
 * This event is created when a new version of JSkat is available.
 */
public class NewJSkatVersionAvailableEvent {

    public final String newVersion;

    public NewJSkatVersionAvailableEvent(String newVersion) {
        this.newVersion = newVersion;
    }
}
