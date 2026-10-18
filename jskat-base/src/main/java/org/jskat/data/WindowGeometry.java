package org.jskat.data;

/**
 * Toolkit-neutral persisted position and size of the main application window.
 */
/**
 * Position and size of a window in screen coordinates.
 *
 * @param x horizontal position of the upper-left corner
 * @param y vertical position of the upper-left corner
 * @param width window width in pixels
 * @param height window height in pixels
 */
public record WindowGeometry(int x, int y, int width, int height) {

    /**
     * Value used by existing properties files when a geometry component is unset.
     */
    public static final int UNSET = Integer.MIN_VALUE;

    /**
     * Performs unset.
     * @return unset result.
     */
    public static WindowGeometry unset() {
        return new WindowGeometry(UNSET, UNSET, UNSET, UNSET);
    }

    /**
     * Returns or indicates position.
     * @return has position result.
     */
    public boolean hasPosition() {
        return x != UNSET && y != UNSET;
    }

    /**
     * Returns or indicates size.
     * @return has size result.
     */
    public boolean hasSize() {
        return width != UNSET && height != UNSET;
    }
}
