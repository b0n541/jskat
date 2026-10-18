package org.jskat.ai.newalgorithm.exception;

/**
 * Defines the illegal method exception type.
 */
public class IllegalMethodException extends RuntimeException {


    /**
     * Performs illegal method exception.
     * @param pMSG p m s g value.
     */
    public IllegalMethodException(String pMSG) {
        super(pMSG);
    }

    /**
     * Performs illegal method exception.
     * @param pMSG p m s g value.
     * @param pThrow p throw value.
     */
    public IllegalMethodException(String pMSG, Throwable pThrow) {
        super(pMSG, pThrow);
    }
}
