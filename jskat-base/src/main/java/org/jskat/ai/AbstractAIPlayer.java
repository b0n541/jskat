package org.jskat.ai;

import org.jskat.player.AbstractJSkatPlayer;

/**
 * Abstract implementation of an AI player. All AI player must extend this
 * class.
 */
public abstract class AbstractAIPlayer extends AbstractJSkatPlayer {

    @Override
    public final boolean isAIPlayer() {
        return true;
    }
}
