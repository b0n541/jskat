package org.jskat.util;

/**
 * Defines all modes for a skat list
 */
public enum SkatListMode {

    /**
     * Normal mode: plus and minus points for declarer
     */
    NORMAL,
    /**
     * Tournament mode: like normal mode plus extra points used in tournaments
     * after Seeger-Fabian system
     */
    TOURNAMENT,
    /**
     * Bierlachs mode: Only minus points are listed
     */
    BIERLACHS;
}
