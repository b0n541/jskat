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
package org.jskat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jskat.control.SkatGame;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game summary
 *
 * An object of this class is returned by {@link SkatGame}
 */
public class GameSummary {

	private static Logger LOG = LoggerFactory.getLogger(GameSummary.class);
	/**
	 * Fore hand player
	 */
	public String foreHand;
	/**
	 * Middle hand player
	 */
	public String middleHand;
	/**
	 * Rear hand player
	 */
	public String rearHand;
	/**
	 * Declarer
	 */
	public Player declarer;
	/**
	 * Game type
	 */
	public GameType gameType;
	/**
	 * Ouvert game
	 */
	public Boolean ouvert = Boolean.FALSE;
	/**
	 * Hand game
	 */
	public Boolean hand = Boolean.FALSE;
	/**
	 * Schneider game
	 */
	public Boolean schneider = Boolean.FALSE;
	/**
	 * Schwarz game
	 */
	public Boolean schwarz = Boolean.FALSE;
	/**
	 * Contra game
	 */
	public Boolean contra = Boolean.FALSE;
	/**
	 * Re game
	 */
	public Boolean re = Boolean.FALSE;
	/**
	 * Game result
	 */
	public SkatGameResult gameResult;
	/**
	 * Tricks
	 */
	public List<Trick> tricks = new ArrayList<>();
	/**
	 * Player points
	 */
	public Map<Player, Integer> playerPoints = new HashMap<>();

	public Set<Player> ramschLosers = new HashSet<>();

	/**
	 * Constructor
	 */
	GameSummary() {
	}

	/**
	 * Gets the factory for a {@link GameSummary}
	 *
	 * @return Factory
	 */
	public static GameSummaryFactory getFactory() {
		return new GameSummaryFactory();
	}

	/**
	 * Factory for a {@link GameSummary}
	 */
	public final static class GameSummaryFactory {

		private GameSummary tmpSummary;

		GameSummaryFactory() {
			tmpSummary = new GameSummary();
		}

		/**
		 * Gets the {@link GameSummary}
		 *
		 * @return Game announcement
		 */
		public final GameSummary getSummary() {
			GameSummary result = null;
			if (validate()) {
				result = tmpSummary;
				tmpSummary = new GameSummary();
			} else {
				throw new RuntimeException("Game summary not valid."); //$NON-NLS-1$
			}
			return result;
		}

		/**
		 * Sets the {@link GameType}
		 *
		 * @param gameType
		 *            Game type
		 */
		public final void setGameType(final GameType gameType) {
			tmpSummary.gameType = gameType;
		}

		/**
		 * Sets the flag for a hand game
		 *
		 * @param isHand
		 *            TRUE, if a hand game was announced
		 */
		public final void setHand(final Boolean isHand) {
			tmpSummary.hand = isHand;
		}

		/**
		 * Sets the flag for an ouvert game
		 *
		 * @param isOuvert
		 *            TRUE, if an ouvert game was announced
		 */
		public final void setOuvert(final Boolean isOuvert) {
			tmpSummary.ouvert = isOuvert;
		}

		/**
		 * Sets the flag for a schneider game
		 *
		 * @param isSchneider
		 *            TRUE, if schneider was announced
		 */
		public final void setSchneider(final Boolean isSchneider) {
			tmpSummary.schneider = isSchneider;
		}

		/**
		 * Sets the flag for a schwarz game
		 *
		 * @param isSchwarz
		 *            TRUE, if a schwarz was announced
		 */
		public final void setSchwarz(final Boolean isSchwarz) {
			tmpSummary.schwarz = isSchwarz;
		}

		/**
		 * Sets the tricks
		 *
		 * @param tricks
		 *            Tricks of the game
		 */
		public final void setTricks(final List<Trick> tricks) {
			tmpSummary.tricks.clear();
			tmpSummary.tricks.addAll(tricks);
		}

		public final void setPlayerPoints(final Map<Player, Integer> playerAndPoints) {
			tmpSummary.playerPoints.putAll(playerAndPoints);
		}

		/**
		 * Sets the game result
		 *
		 * @param gameResult
		 *            Game result
		 */
		public final void setGameResult(final SkatGameResult gameResult) {
			tmpSummary.gameResult = gameResult;
		}

		/**
		 * Sets the fore hand player
		 *
		 * @param name
		 *            Name of fore hand player
		 */
		public final void setForeHand(final String name) {
			tmpSummary.foreHand = name;
		}

		/**
		 * Sets the middle hand player
		 *
		 * @param name
		 *            Name of middle hand player
		 */
		public final void setMiddleHand(final String name) {
			tmpSummary.middleHand = name;
		}

		/**
		 * Sets the rear hand player
		 *
		 * @param name
		 *            Name of rear hand player
		 */
		public final void setRearHand(final String name) {
			tmpSummary.rearHand = name;
		}

		/**
		 * Sets the declarer
		 *
		 * @param position
		 *            Position of declarer
		 */
		public final void setDeclarer(final Player position) {
			tmpSummary.declarer = position;
		}

		public final void addRamschLooser(Player looser) {
			tmpSummary.ramschLosers.add(looser);
		}

		private boolean validate() {
			if (tmpSummary.gameType == null) {
				LOG.error("game type is null"); //$NON-NLS-1$
				return false;
			} else if (!GameType.RAMSCH.equals(tmpSummary.gameType) && !GameType.PASSED_IN.equals(tmpSummary.gameType)
					&& tmpSummary.declarer == null) {
				LOG.error("declarer is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.foreHand == null) {
				LOG.error("fore hand is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.middleHand == null) {
				LOG.error("middle hand is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.rearHand == null) {
				LOG.error("rear hand is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.tricks.size() > 10) {
				LOG.error("more than 10 tricks"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.gameResult == null) {
				LOG.error("game result is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.playerPoints.size() != 3) {
				LOG.error("missing player points"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.gameType == GameType.RAMSCH && tmpSummary.ramschLosers.size() == 0) {
				LOG.error("missing ramsch looser");
			}
			return true;
		}

		public void setContra(Boolean contra) {
			tmpSummary.contra = contra;
		}

		public void setRe(Boolean re) {
			tmpSummary.re = re;
		}
	}

	/**
	 * Gets the game type
	 *
	 * @return Game type
	 */
	public final GameType getGameType() {

		return gameType;
	}

	/**
	 * Checks whether schneider was announced or not
	 *
	 * @return TRUE if schneider was announced
	 */
	public final boolean isSchneider() {

		return schneider.booleanValue();
	}

	/**
	 * Checks whether schwarz was announced or not
	 *
	 * @return TRUE if schwarz was announced
	 */
	public final boolean isSchwarz() {

		return schwarz.booleanValue();
	}

	/**
	 * Checks whether an ouvert game was announced or not
	 *
	 * @return TRUE if an ouvert game was announced
	 */
	public final boolean isOuvert() {

		return ouvert.booleanValue();
	}

	/**
	 * Checks whether a hand game was announced or not
	 *
	 * @return TRUE if a hand game was announced
	 */
	public final boolean isHand() {

		return hand.booleanValue();
	}

	/**
	 * Checks whether the game was won or not
	 *
	 * @return TRUE if the game was won
	 */
	public final boolean isGameWon() {

		return gameResult.isWon();
	}

	/**
	 * Gets the game value
	 *
	 * @return Game value
	 */
	public final int getGameValue() {
		return gameResult.getGameValue();
	}

	/**
	 * Gets the final declarer points
	 *
	 * @return Final declarer points
	 */
	public int getFinalDeclarerPoints() {
		return gameResult.getFinalDeclarerPoints();
	}

	/**
	 * Gets the final opponent points
	 *
	 * @return Final opponent points
	 */
	public int getFinalOpponentScore() {
		return gameResult.getFinalOpponentPoints();
	}

	/**
	 * Gets the multiplier for the game
	 *
	 * @return Multiplier for the game
	 */
	public int getGameMultiplier() {
		return gameResult.getMultiplier();
	}

	/**
	 * Checks whether the game was played with or without jacks
	 *
	 * @return TRUE, if the game was played with jacks
	 */
	public boolean isGamePlayedWithJacks() {
		return gameResult.isPlayWithJacks();
	}

	/**
	 * Gets the fore hand player
	 *
	 * @return Fore hand player
	 */
	public final String getForeHand() {
		return foreHand;
	}

	/**
	 * Gets the middle hand player
	 *
	 * @return Middle hand player
	 */
	public final String getMiddleHand() {
		return middleHand;
	}

	/**
	 * Gets the rear hand player
	 *
	 * @return Rear hand player
	 */
	public final String getRearHand() {
		return rearHand;
	}

	/**
	 * Gets the position of declarer
	 *
	 * @return Declarer position
	 */
	public final Player getDeclarer() {
		return declarer;
	}

	/**
	 * Gets the tricks of the skat game
	 *
	 * @return Tricks
	 */
	public final List<Trick> getTricks() {
		return tricks;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append("Game summary: ").append(gameType); //$NON-NLS-1$

		if (hand.booleanValue()) {

			result.append(" hand"); //$NON-NLS-1$
		}

		if (ouvert.booleanValue()) {

			result.append(" ouvert"); //$NON-NLS-1$
		}

		if (schneider.booleanValue()) {

			result.append(" schneider"); //$NON-NLS-1$
		}

		if (schwarz.booleanValue()) {

			result.append(" schwarz"); //$NON-NLS-1$
		}

		result.append(" game value: " + gameResult.getGameValue()); //$NON-NLS-1$

		result.append(" fore hand: " + foreHand); //$NON-NLS-1$
		result.append(" middle hand: " + middleHand); //$NON-NLS-1$
		result.append(" rear hand: " + rearHand); //$NON-NLS-1$
		result.append(" declarer: " + declarer); //$NON-NLS-1$

		return result.toString();
	}

	/**
	 * Gets the player points
	 *
	 * @param player
	 *            Player
	 * @return Points
	 */
	public int getPlayerPoints(final Player player) {
		return playerPoints.get(player).intValue();
	}

	/**
	 * Get all players that lost in a Ramsch game.
	 *
	 * @return Set of losers
	 */
	public Set<Player> getRamschLosers() {
		return Collections.unmodifiableSet(ramschLosers);
	}

	public Boolean isContra() {
		return contra;
	}

	public Boolean isRe() {
		return re;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contra == null) ? 0 : contra.hashCode());
		result = prime * result + ((declarer == null) ? 0 : declarer.hashCode());
		result = prime * result + ((foreHand == null) ? 0 : foreHand.hashCode());
		result = prime * result + ((gameResult == null) ? 0 : gameResult.hashCode());
		result = prime * result + ((gameType == null) ? 0 : gameType.hashCode());
		result = prime * result + ((hand == null) ? 0 : hand.hashCode());
		result = prime * result + ((middleHand == null) ? 0 : middleHand.hashCode());
		result = prime * result + ((ouvert == null) ? 0 : ouvert.hashCode());
		result = prime * result + ((playerPoints == null) ? 0 : playerPoints.hashCode());
		result = prime * result + ((ramschLosers == null) ? 0 : ramschLosers.hashCode());
		result = prime * result + ((re == null) ? 0 : re.hashCode());
		result = prime * result + ((rearHand == null) ? 0 : rearHand.hashCode());
		result = prime * result + ((schneider == null) ? 0 : schneider.hashCode());
		result = prime * result + ((schwarz == null) ? 0 : schwarz.hashCode());
		result = prime * result + ((tricks == null) ? 0 : tricks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameSummary other = (GameSummary) obj;
		if (contra == null) {
			if (other.contra != null)
				return false;
		} else if (!contra.equals(other.contra))
			return false;
		if (declarer != other.declarer)
			return false;
		if (foreHand == null) {
			if (other.foreHand != null)
				return false;
		} else if (!foreHand.equals(other.foreHand))
			return false;
		if (gameResult == null) {
			if (other.gameResult != null)
				return false;
		} else if (!gameResult.equals(other.gameResult))
			return false;
		if (gameType != other.gameType)
			return false;
		if (hand == null) {
			if (other.hand != null)
				return false;
		} else if (!hand.equals(other.hand))
			return false;
		if (middleHand == null) {
			if (other.middleHand != null)
				return false;
		} else if (!middleHand.equals(other.middleHand))
			return false;
		if (ouvert == null) {
			if (other.ouvert != null)
				return false;
		} else if (!ouvert.equals(other.ouvert))
			return false;
		if (playerPoints == null) {
			if (other.playerPoints != null)
				return false;
		} else if (!playerPoints.equals(other.playerPoints))
			return false;
		if (ramschLosers == null) {
			if (other.ramschLosers != null)
				return false;
		} else if (!ramschLosers.equals(other.ramschLosers))
			return false;
		if (re == null) {
			if (other.re != null)
				return false;
		} else if (!re.equals(other.re))
			return false;
		if (rearHand == null) {
			if (other.rearHand != null)
				return false;
		} else if (!rearHand.equals(other.rearHand))
			return false;
		if (schneider == null) {
			if (other.schneider != null)
				return false;
		} else if (!schneider.equals(other.schneider))
			return false;
		if (schwarz == null) {
			if (other.schwarz != null)
				return false;
		} else if (!schwarz.equals(other.schwarz))
			return false;
		if (tricks == null) {
			if (other.tricks != null)
				return false;
		} else if (!tricks.equals(other.tricks))
			return false;
		return true;
	}
}
