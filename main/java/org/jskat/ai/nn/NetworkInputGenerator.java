package org.jskat.ai.nn;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

interface NetworkInputGenerator {
	double[] getNetInputs(PlayerKnowledge knowledge, Card cardToPlay);
}