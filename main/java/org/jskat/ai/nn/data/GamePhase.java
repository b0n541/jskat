/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
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
package org.jskat.ai.nn.data;

public enum GamePhase {
	OPENING, MIDDLEGAME, ENDGAME;

	public final static GamePhase of(int trickNoInGame) {
		switch (trickNoInGame) {
		case 0:
		case 1:
		case 2:
			return OPENING;
		case 3:
		case 4:
		case 5:
		case 6:
			return MIDDLEGAME;
		case 7:
		case 8:
		case 9:
			return ENDGAME;
		}
		return null;
	}
}
