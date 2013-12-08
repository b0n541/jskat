/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn.input;

import org.jskat.util.Card;

/**
 * Abstract card related strategy for network inputs
 */
public abstract class AbstractCardInputStrategy extends AbstractInputStrategy {

	/**
	 * Gets the index for a card for network inputs
	 * 
	 * @param card
	 *            Card
	 * @return Index of card in network input
	 */
	protected static int getNetworkInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

	@Override
	public final int getNeuronCount() {
		return 32;
	}
}
