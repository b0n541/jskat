package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public interface InputStrategy {
	public int getNeuronCount();

	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay);
}
