package org.jskat.data;

/**
 * Toolkit-neutral persisted position and size of the main application window.
 */
public record WindowGeometry(int x, int y, int width, int height) {

    /** Value used by existing properties files when a geometry component is unset. */
    public static final int UNSET = Integer.MIN_VALUE;

    public static WindowGeometry unset() {
        return new WindowGeometry(UNSET, UNSET, UNSET, UNSET);
    }

    public boolean hasPosition() {
        return x != UNSET && y != UNSET;
    }

    public boolean hasSize() {
        return width != UNSET && height != UNSET;
    }
}
