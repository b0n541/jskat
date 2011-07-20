/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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

/**
 * Data class for skat game result
 */
public class SkatGameResult implements Cloneable {

	private int gameValue;
	private int multiplier;
	private boolean playWithJacks;
	private boolean won;
	private boolean overBidded;
	private boolean schneider;
	private boolean schwarz;
	private boolean durchmarsch;
	private boolean jungfrau;

	/**
	 * Constructor
	 */
	public SkatGameResult() {
		gameValue = -1;
		multiplier = 0;
		playWithJacks = false;
		won = false;
		overBidded = false;
		schneider = false;
		schwarz = false;
		durchmarsch = false;
		jungfrau = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SkatGameResult clone() {

		SkatGameResult result = new SkatGameResult();
		result.setGameValue(gameValue);
		result.setMultiplier(multiplier);
		result.setPlayWithJacks(playWithJacks);
		result.setWon(won);
		result.setOverBidded(overBidded);
		result.setSchneider(schneider);
		result.setSchwarz(schwarz);
		result.setDurchmarsch(durchmarsch);
		result.setJungfrau(jungfrau);

		return result;
	}

	/**
	 * Gets whether the game was won
	 * 
	 * @return TRUE, if the game was won
	 */
	public boolean isWon() {
		return won;
	}

	/**
	 * Sets whether the game was won
	 * 
	 * @param won
	 *            TRUE, if the game was won
	 */
	public void setWon(boolean won) {
		this.won = won;
	}

	/**
	 * Gets whether the declarer did overbid
	 * 
	 * @return TRUE, if the declarer did overbid
	 */
	public boolean isOverBidded() {
		return overBidded;
	}

	/**
	 * Sets whether the declarer did overbid
	 * 
	 * @param overBidded
	 *            TRUE, if the declarer did overvid
	 */
	public void setOverBidded(boolean overBidded) {
		this.overBidded = overBidded;
	}

	/**
	 * Gets whether the game was a schneider game
	 * 
	 * @return TRUE, if the game was a schneider game
	 */
	public boolean isSchneider() {
		return schneider;
	}

	/**
	 * Sets whether the game was a schneider game
	 * 
	 * @param schneider
	 *            TRUE, if the game was a schneider game
	 */
	public void setSchneider(boolean schneider) {
		this.schneider = schneider;
	}

	/**
	 * Gets whether the game was a schwarz game
	 * 
	 * @return TRUE, if the game was a schwarz game
	 */
	public boolean isSchwarz() {
		return schwarz;
	}

	/**
	 * Sets whether the game was a schwarz game
	 * 
	 * @param isSchwarz
	 *            TRUE, if the game was a schwarz game
	 */
	public void setSchwarz(boolean isSchwarz) {
		schwarz = isSchwarz;
		if (schwarz) {
			// a schwarz game is always schneider
			schneider = true;
		}
	}

	/**
	 * Gets whether the game was a durchmarsch game
	 * 
	 * @return TRUE, if the game was a durchmarsch game
	 */
	public boolean isDurchmarsch() {
		return durchmarsch;
	}

	/**
	 * Sets whether the game was a durchmarsch game
	 * 
	 * @param durchmarsch
	 *            TRUE, if the game was a durchmarsch game
	 */
	public void setDurchmarsch(boolean durchmarsch) {
		this.durchmarsch = durchmarsch;
	}

	/**
	 * Gets whether the game was a jungfrau game
	 * 
	 * @return TRUE, if the game was a jungfrau game
	 */
	public boolean isJungfrau() {
		return jungfrau;
	}

	/**
	 * Sets whether the game was a jungfrau game
	 * 
	 * @param jungfrau
	 *            TRUE, if the game was a jungfrau game
	 */
	public void setJungfrau(boolean jungfrau) {
		this.jungfrau = jungfrau;
	}

	/**
	 * Gets the game result
	 * 
	 * @return Game result
	 */
	public int getGameValue() {
		return gameValue;
	}

	/**
	 * Sets the game result
	 * 
	 * @param gameValue
	 *            Game result
	 */
	public void setGameValue(int gameValue) {
		this.gameValue = gameValue;
	}

	/**
	 * Gets the multiplier<br>
	 * only meaningful in suit and grand games
	 * 
	 * @return Multiplier
	 */
	public int getMultiplier() {
		return multiplier;
	}

	/**
	 * Sets the multiplier<br>
	 * only meaningful in suit and grand games
	 * 
	 * @param multiplier
	 *            Multiplier
	 */
	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	/**
	 * Gets wether the declarer played with or without jacks
	 * 
	 * @return TRUE, if the declarer played with jacks
	 */
	public boolean isPlayWithJacks() {
		return playWithJacks;
	}

	/**
	 * Sets whether the declarer played with or without jacks
	 * 
	 * @param playWithJacks
	 *            TRUE, if the declarer played with jacks
	 */
	public void setPlayWithJacks(boolean playWithJacks) {
		this.playWithJacks = playWithJacks;
	}
}
