/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.0
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.test;
import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that does not bid, it makes random moves during other game phases
 */
public class NoBiddingTestPlayer extends AIPlayerRND {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int bidMore(@SuppressWarnings("unused") int nextBidValue) {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean holdBid(@SuppressWarnings("unused") int currBidValue) {
		return false;
	}
}
