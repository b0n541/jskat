package org.jskat.ai.newalgorithm.exception;

public class IllegalMethodException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalMethodException(String pMSG) {
        super(pMSG);
    }

    public IllegalMethodException(String pMSG, Throwable pThrow) {
        super(pMSG, pThrow);
    }
}
