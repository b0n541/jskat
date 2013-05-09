package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public interface NetworkInputGenerator {
	double[] getNetInputs(PlayerKnowledge knowledge, Card cardToPlay);
}