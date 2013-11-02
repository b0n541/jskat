package org.jskat.ai;

import org.jskat.player.AbstractJSkatPlayer;

public abstract class AbstractAIPlayer extends AbstractJSkatPlayer {
	@Override
	public final boolean isAIPlayer() {
		return true;
	}
}
