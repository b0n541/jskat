package org.jskat.control.event.general;

/**
 * This event is created when a new version of JSkat is available.
 */
public class NewJSkatVersionAvailableEvent {

    /**
     * Newly available application version.
     */
    public final String newVersion;

    /**
     * Performs new j skat version available event.
     * @param newVersion new version value.
     */
    public NewJSkatVersionAvailableEvent(String newVersion) {
        this.newVersion = newVersion;
    }
}
