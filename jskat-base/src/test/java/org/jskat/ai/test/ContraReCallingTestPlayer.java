/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that calls Contra and Re everytime.
 */
public class ContraReCallingTestPlayer extends AIPlayerRND {
	@Override
	public Boolean callContra() {
		return true;
	}

	@Override
	public Boolean callRe() {
		return true;
	}

	@Override
	public Integer bidMore(int currentBid) {
		if (currentBid == 18) {
			return 20;
		}
		return -1;
	}

	@Override
	public Boolean holdBid(int currentBid) {
		if (currentBid == 18) {
			return true;
		}
		return false;
	}
}
