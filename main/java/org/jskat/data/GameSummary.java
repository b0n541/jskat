/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.control.SkatGame;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Game summary
 * 
 * An object of this class is returned by {@link SkatGame}
 */
public class GameSummary {

	static final Log log = LogFactory.getLog(GameSummary.class);
	/**
	 * Fore hand player
	 */
	String foreHand;
	/**
	 * Middle hand player
	 */
	String middleHand;
	/**
	 * Rear hand player
	 */
	String rearHand;
	/**
	 * Declarer
	 */
	Player declarer;
	/**
	 * Game type
	 */
	GameType gameType;
	/**
	 * Ouvert game
	 */
	Boolean ouvert = Boolean.FALSE;
	/**
	 * Hand game
	 */
	Boolean hand = Boolean.FALSE;
	/**
	 * Schneider game
	 */
	Boolean schneider = Boolean.FALSE;
	/**
	 * Schwarz game
	 */
	Boolean schwarz = Boolean.FALSE;
	/**
	 * Game result
	 */
	SkatGameResult gameResult;
	/**
	 * Tricks
	 */
	List<Trick> tricks = new ArrayList<Trick>();
	/**
	 * Player points
	 */
	Map<Player, Integer> playerPoints = new HashMap<Player, Integer>();

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
		public final void setGameType(GameType gameType) {
			tmpSummary.gameType = gameType;
		}

		/**
		 * Sets the flag for a hand game
		 * 
		 * @param isHand
		 *            TRUE, if a hand game was announced
		 */
		public final void setHand(Boolean isHand) {
			tmpSummary.hand = isHand;
		}

		/**
		 * Sets the flag for an ouvert game
		 * 
		 * @param isOuvert
		 *            TRUE, if an ouvert game was announced
		 */
		public final void setOuvert(Boolean isOuvert) {
			tmpSummary.ouvert = isOuvert;
		}

		/**
		 * Sets the flag for a schneider game
		 * 
		 * @param isSchneider
		 *            TRUE, if schneider was announced
		 */
		public final void setSchneider(Boolean isSchneider) {
			tmpSummary.schneider = isSchneider;
		}

		/**
		 * Sets the flag for a schwarz game
		 * 
		 * @param isSchwarz
		 *            TRUE, if a schwarz was announced
		 */
		public final void setSchwarz(Boolean isSchwarz) {
			tmpSummary.schwarz = isSchwarz;
		}

		/**
		 * Sets the tricks
		 * 
		 * @param tricks
		 *            Tricks of the game
		 */
		public final void setTricks(List<Trick> tricks) {
			tmpSummary.tricks.clear();
			tmpSummary.tricks.addAll(tricks);
		}

		public final void setPlayerPoints(Map<Player, Integer> playerAndPoints) {
			tmpSummary.playerPoints.putAll(playerAndPoints);
		}

		/**
		 * Sets the game result
		 * 
		 * @param gameResult
		 *            Game result
		 */
		public final void setGameResult(SkatGameResult gameResult) {
			tmpSummary.gameResult = gameResult;
		}

		/**
		 * Sets the fore hand player
		 * 
		 * @param name
		 *            Name of fore hand player
		 */
		public final void setForeHand(String name) {
			tmpSummary.foreHand = name;
		}

		/**
		 * Sets the middle hand player
		 * 
		 * @param name
		 *            Name of middle hand player
		 */
		public final void setMiddleHand(String name) {
			tmpSummary.middleHand = name;
		}

		/**
		 * Sets the rear hand player
		 * 
		 * @param name
		 *            Name of rear hand player
		 */
		public final void setRearHand(String name) {
			tmpSummary.rearHand = name;
		}

		/**
		 * Sets the declarer
		 * 
		 * @param position
		 *            Position of declarer
		 */
		public final void setDeclarer(Player position) {
			tmpSummary.declarer = position;
		}

		private boolean validate() {
			if (tmpSummary.gameType == null) {
				log.error("game type is null"); //$NON-NLS-1$
				return false;
			} else if (!GameType.RAMSCH.equals(tmpSummary.gameType) && !GameType.PASSED_IN.equals(tmpSummary.gameType)
					&& tmpSummary.declarer == null) {
				log.error("declarer is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.foreHand == null) {
				log.error("fore hand is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.middleHand == null) {
				log.error("middle hand is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.rearHand == null) {
				log.error("rear hand is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.tricks.size() > 10) {
				log.error("more than 10 tricks"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.gameResult == null) {
				log.error("game result is null"); //$NON-NLS-1$
				return false;
			} else if (tmpSummary.playerPoints.size() != 3) {
				log.error("missing player points"); //$NON-NLS-1$
				return false;
			}
			return true;
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
	public int getPlayerPoints(Player player) {
		return playerPoints.get(player).intValue();
	}
}
