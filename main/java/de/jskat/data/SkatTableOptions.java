/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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

/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.data;

import java.util.Observable;

/**
 * Skat table options
 */
public class SkatTableOptions extends Observable {
	
	/**
	 * Getter for property firstPlayerName.
	 * 
	 * @return Value of property firstPlayerName.
	 */
	public String getFirstPlayerName() {
		
		return this.firstPlayerName;
	}

	/**
	 * Setter for property firstPlayerName.
	 * 
	 * @param newFirstPlayerName
	 *            New value of property firstPlayerName.
	 */
	public void setFirstPlayerName(String newFirstPlayerName) {

		this.firstPlayerName = newFirstPlayerName;
	}

	/**
	 * Getter for property firstPlayerType.
	 * 
	 * @return Value of property firstPlayerType.
	 */
	public int getFirstPlayerType() {
		
		return this.firstPlayerType;
	}

	/**
	 * Setter for property firstPlayerType.
	 * 
	 * @param newFirstPlayerType
	 *            New value of property firstPlayerType.
	 */
	public void setFirstPlayerType(int newFirstPlayerType) {

		this.firstPlayerType = newFirstPlayerType;
	}

	/**
	 * Getter for property secondPlayerName.
	 * 
	 * @return Value of property secondPlayerName.
	 */
	public String getSecondPlayerName() {
		
		return this.secondPlayerName;
	}

	/**
	 * Setter for property secondPlayerName.
	 * 
	 * @param newSecondPlayerName
	 *            New value of property secondPlayerName.
	 */
	public void setSecondPlayerName(java.lang.String newSecondPlayerName) {

		this.secondPlayerName = newSecondPlayerName;
	}

	/**
	 * Getter for property secondPlayerType.
	 * 
	 * @return Value of property secondPlayerType.
	 */
	public int getSecondPlayerType() {
		
		return this.secondPlayerType;
	}

	/**
	 * Setter for property secondPlayerType.
	 * 
	 * @param newSecondPlayerType
	 *            New value of property secondPlayerType.
	 */
	public void setSecondPlayerType(int newSecondPlayerType) {

		this.secondPlayerType = newSecondPlayerType;
	}

	/**
	 * Getter for property thirdPlayerName.
	 * 
	 * @return Value of property thirdPlayerName.
	 */
	public String getThirdPlayerName() {
		
		return this.thirdPlayerName;
	}

	/**
	 * Setter for property thirdPlayerName.
	 * 
	 * @param newThirdPlayerName
	 *            New value of property thirdPlayerName.
	 */
	public void setThirdPlayerName(java.lang.String newThirdPlayerName) {

		this.thirdPlayerName = newThirdPlayerName;
	}

	/**
	 * Getter for property thirdPlayerType.
	 * 
	 * @return Value of property thirdPlayerType.
	 */
	public int getThirdPlayerType() {
		
		return this.thirdPlayerType;
	}

	/**
	 * Setter for property thirdPlayerType.
	 * 
	 * @param newThirdPlayerType
	 *            New value of property thirdPlayerType.
	 */
	public void setThirdPlayerType(int newThirdPlayerType) {

		this.thirdPlayerType = newThirdPlayerType;
	}

	/**
	 * Getter for property rules.
	 * 
	 * @return Value of property rules.
	 */
	public RuleSets getRules() {

		return this.rules;
	}

	/**
	 * Setter for property rules.
	 * 
	 * @param newRules
	 *            New value of property rules.
	 */
	public void setRules(RuleSets newRules) {

		this.rules = newRules;
	}

	/**
	 * Getter for property playBock.
	 * 
	 * @return Value of property playBock.
	 */
	public boolean isPlayBock() {

		return this.playBock;
	}

	/**
	 * Setter for property playBock.
	 * 
	 * @param newPlayBock
	 *            New value of property playBock.
	 */
	public void setPlayBock(boolean newPlayBock) {

		this.playBock = newPlayBock;
	}

	/**
	 * Getter for property playKontra.
	 * 
	 * @return Value of property playKontra.
	 */
	public boolean isPlayContra() {

		return this.playContra;
	}

	/**
	 * Setter for property playKontra.
	 * 
	 * @param newPlayContra
	 *            New value of property playKontra.
	 */
	public void setPlayContra(boolean newPlayContra) {

		this.playContra = newPlayContra;
	}

	/**
	 * Getter for property playRamsch.
	 * 
	 * @return Value of property playRamsch.
	 */
	public boolean isPlayRamsch() {

		return this.playRamsch;
	}

	/**
	 * Setter for property playRamsch.
	 * 
	 * @param newPlayRamsch
	 *            New value of property playRamsch.
	 */
	public void setPlayRamsch(boolean newPlayRamsch) {

		this.playRamsch = newPlayRamsch;
	}

	/**
	 * Getter for property playRevolution.
	 * 
	 * @return Value of property playRevolution.
	 */
	public boolean isPlayRevolution() {

		return this.playRevolution;
	}

	/**
	 * Setter for property playRevolution.
	 * 
	 * @param newPlayRevolution
	 *            New value of property playRevolution.
	 */
	public void setPlayRevolution(boolean newPlayRevolution) {

		this.playRevolution = newPlayRevolution;
	}

	/**
	 * Getter for property bockEventLostGrand
	 * 
	 * @return Value of property bockEventLostGrand
	 */
	public boolean isBockEventLostGrand() {

		return this.bockEventLostGrand;
	}

	/**
	 * Setter for property bockEventLostGrand
	 * 
	 * @param newBockEventLostGrand
	 *            New value of property bockEventLostGrand
	 */
	public void setBockEventLostGrand(boolean newBockEventLostGrand) {

		this.bockEventLostGrand = newBockEventLostGrand;
	}

	/**
	 * Getter for property bockEventLostWith60
	 * 
	 * @return Value of property bockEventLostWith60
	 */
	public boolean isBockEventLostWith60() {

		return this.bockEventLostWith60;
	}

	/**
	 * Setter for property bockEventLostWith60
	 * 
	 * @param newBockEventLostWith60
	 *            New value of property bockEventLostWith60
	 */
	public void setBockEventLostWith60(boolean newBockEventLostWith60) {

		this.bockEventLostWith60 = newBockEventLostWith60;
	}

	/**
	 * Getter for property bockEventLostAfterContra
	 * 
	 * @return Value of property bockEventLostAfterContra
	 */
	public boolean isBockEventLostAfterContra() {
		
		return this.bockEventLostAfterContra;
	}

	/**
	 * Setter for property bockEventLostAfterContra
	 * 
	 * @param newBockEventLostAfterContra
	 *            New value of property bockEventLostAfterContra
	 */
	public void setBockEventLostAfterContra(boolean newBockEventLostAfterContra) {

		this.bockEventLostAfterContra = newBockEventLostAfterContra;
	}

	/**
	 * Getter for property bockEventContraReAnnounced
	 * 
	 * @return Value of property bockEventContraReAnnounced
	 */
	public boolean isBockEventContraReAnnounced() {

		return this.bockEventContraReAnnounced;
	}

	/**
	 * Setter for property bockEventContraReAnnounced
	 * 
	 * @param newBockEventContraReAnnounced
	 *            New value of property bockEventContraReAnnounced
	 */
	public void setBockEventContraReAnnounced(boolean newBockEventContraReAnnounced) {

		this.bockEventContraReAnnounced = newBockEventContraReAnnounced;
	}

	/**
	 * Getter for property bockEventPlayerHasX00Points
	 * 
	 * @return Value of property bockEventPlayerHasX00Points
	 */
	public boolean isBockEventPlayerHasX00Points() {

		return this.bockEventPlayerHasX00Points;
	}

	/**
	 * Setter for property bockEventPlayerHasX00Points
	 * 
	 * @param newBockEventPlayerHasX00Points
	 *            New value of property bockEventPlayerHasX00Points
	 */
	public void setBockEventPlayerHasX00Points(
			boolean newBockEventPlayerHasX00Points) {

		this.bockEventPlayerHasX00Points = newBockEventPlayerHasX00Points;
	}

	/**
	 * Getter for property ramschSkat.
	 * 
	 * @return Value of property ramschSkat.
	 */
	public RamschSkatOwners getRamschSkat() {

		return this.ramschSkat;
	}

	/**
	 * Setter for property ramschSkat.
	 * 
	 * @param newRamschSkat
	 *            New value of property ramschSkat.
	 */
	public void setRamschSkat(RamschSkatOwners newRamschSkat) {

		this.ramschSkat = newRamschSkat;
	}

	/**
	 * Getter for property schieberRamsch
	 * 
	 * @return Value of property schieberRamsch
	 */
	public boolean isSchieberRamsch() {

		return this.schieberRamsch;
	}

	/**
	 * Setter for property schieberRamsch
	 * 
	 * @param newSchieberRamsch
	 *            New value of property schieberRamsch
	 */
	public void setSchieberRamsch(boolean newSchieberRamsch) {

		this.schieberRamsch = newSchieberRamsch;
	}

	/**
	 * Getter for property schieberRamschJacksInSkat
	 * 
	 * @return Value of property schieberRamschJacksInSkat
	 */
	public boolean isSchieberRamschJacksInSkat() {

		return this.schieberRamschJacksInSkat;
	}

	/**
	 * Setter for property schieberRamschJacksInSkat
	 * 
	 * @param newSchieberRamschJacksInSkat
	 *            New value of property schieberRamschJacksInSkat
	 */
	public void setSchieberRamschJacksInSkat(boolean newSchieberRamschJacksInSkat) {

		this.schieberRamschJacksInSkat = newSchieberRamschJacksInSkat;
	}

	/**
	 * Getter for property ramschEventNoBid
	 * 
	 * @return Value of property ramschEventNoBid
	 */
	public boolean isRamschEventNoBid() {

		return this.ramschEventNoBid;
	}

	/**
	 * Setter for property ramschEventNoBid
	 * 
	 * @param newRamschEventNoBid
	 *            New value of property ramschEventNoBid
	 */
	public void setRamschEventNoBid(boolean newRamschEventNoBid) {

		this.ramschEventNoBid = newRamschEventNoBid;
	}

	/**
	 * Getter for property ramschEventRamschAfterBock
	 * 
	 * @return Value of property ramschEventRamschAfterBock
	 */
	public boolean isRamschEventRamschAfterBock() {

		return this.ramschEventRamschAfterBock;
	}

	/**
	 * Setter for property ramschEventRamschAfterBock
	 * 
	 * @param newRamschEventRamschAfterBock
	 *            New value of property ramschEventRamschAfterBock
	 */
	public void setRamschEventRamschAfterBock(boolean newRamschEventRamschAfterBock) {

		this.ramschEventRamschAfterBock = newRamschEventRamschAfterBock;
	}

	/**
	 * Getter for property ramschGrandHandPossible
	 * 
	 * @return Value of property ramschGrandHandPossible
	 */
	public boolean isRamschGrandHandPossible() {

		return this.ramschGrandHandPossible;
	}

	/**
	 * Setter for property ramschGrandHandPossible
	 * 
	 * @param newRamschGrandHandPossible
	 *            New value of property ramschGrandHandPossible
	 */
	public void setRamschGrandHandPossible(boolean newRamschGrandHandPossible) {

		this.ramschGrandHandPossible = newRamschGrandHandPossible;
	}

	/**
	 * Gets the maximum count of players
	 * 
	 * @return Maximum player count
	 */
	public int getMaxPlayerCount() {

		return this.maxPlayerCount;
	}

	/**
	 * Sets the maximum count of players
	 * 
	 * @param newMaxPlayerCount Maximum player count
	 */
	public void setMaxPlayerCount(int newMaxPlayerCount) {
	
		this.maxPlayerCount = newMaxPlayerCount;
	}

	/**
	 * Holds the different rule sets
	 */
	public enum RuleSets {
		/**
		 * Official rules according the ISPA
		 */
		ISPA,
		/**
		 * Extensions to the ISPA rules, played in the pubs
		 */
		PUB
	}

	/**
	 * Holds different rules for the owner of the skat after a ramsch game
	 */
	public enum RamschSkatOwners {
		/**
		 * Skat goes to winner of last trick
		 */
		LAST_TRICK,
		/**
		 * Skat goes to player with the most points
		 */
		LOSER
	}

	private int maxPlayerCount = 3;

	private String firstPlayerName = "Markus"; //$NON-NLS-1$

	private int firstPlayerType = 0;

	private String secondPlayerName = "Jan"; //$NON-NLS-1$

	private int secondPlayerType = 0;

	private String thirdPlayerName = "Nobody"; //$NON-NLS-1$

	private int thirdPlayerType = 0;

	private RuleSets rules = RuleSets.ISPA;

	private boolean playContra = false;

	private boolean playBock = false;

	private boolean playRamsch = false;

	private boolean playRevolution = false;

	private boolean bockEventLostGrand = false;

	private boolean bockEventLostWith60 = false;

	private boolean bockEventLostAfterContra = false;

	private boolean bockEventContraReAnnounced = false;

	private boolean bockEventPlayerHasX00Points = false;

	private RamschSkatOwners ramschSkat = RamschSkatOwners.LAST_TRICK;

	private boolean schieberRamsch = false;

	private boolean schieberRamschJacksInSkat = false;

	private boolean ramschEventNoBid = false;

	private boolean ramschEventRamschAfterBock = false;

	private boolean ramschGrandHandPossible = false;
}
