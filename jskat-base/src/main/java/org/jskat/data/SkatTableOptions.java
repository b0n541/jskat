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

import java.util.Observable;

/**
 * Skat table options
 */
public class SkatTableOptions extends Observable {

	/**
	 * Holds the different rule sets
	 */
	public enum RuleSet {
		/**
		 * Official rules according the ISPA
		 */
		ISPA,
		/**
		 * provides a standard set of pub rules
		 */
		PUB
	}

	/**
	 * Defines the times, when Contra/Re can be called.
	 */
	public enum ContraCallingTime {
		/**
		 * Calling contra directly after game announcement
		 */
		AFTER_GAME_ANNOUNCEMENT,
		/**
		 * Calling contra before the player plays out the first card from
		 * his/her hand
		 */
		BEFORE_FIRST_CARD;
	}

	/**
	 * Holds different rules for the owner of the skat after a ramsch game
	 */
	public enum RamschSkatOwner {
		/**
		 * Skat goes to winner of last trick
		 */
		LAST_TRICK,
		/**
		 * Skat goes to player with the most points
		 */
		LOSER
	}
	
	/**
	 * Supported save paths
	 */
	public enum SavePath {
		/**
		 * User home
		 */
		USER_HOME,
		/**
		 * Working directory
		 */
		WORKING_DIRECTORY;
	}

	private Integer maxPlayerCount;
	private String firstPlayerName;
	private String firstPlayerType;
	private String secondPlayerName;
	private String secondPlayerType;
	private String thirdPlayerName;
	private String thirdPlayerType;
	private RuleSet rules = RuleSet.ISPA;
	private Boolean playContra;
	private Boolean playBock;
	private Boolean playRamsch;
	private Boolean playRevolution;
	private Boolean bockEventLostGrand;
	private Boolean bockEventLostWith60;
	private Boolean bockEventLostAfterContra;
	private Boolean bockEventContraReAnnounced;
	private Boolean bockEventMultipleOfHundredScore;
	private RamschSkatOwner ramschSkat;
	private Boolean schieberRamsch;
	private Boolean schieberRamschJacksInSkat;
	private Boolean ramschEventNoBid;
	private Boolean ramschEventRamschAfterBock;
	private Boolean ramschGrandHandPossible;

	/**
	 * Getter for property firstPlayerName.
	 * 
	 * @return Value of property firstPlayerName.
	 */
	public String getFirstPlayerName() {

		return firstPlayerName;
	}

	/**
	 * Setter for property firstPlayerName.
	 * 
	 * @param newFirstPlayerName
	 *            New value of property firstPlayerName.
	 */
	public void setFirstPlayerName(String newFirstPlayerName) {

		firstPlayerName = newFirstPlayerName;
	}

	/**
	 * Getter for property firstPlayerType.
	 * 
	 * @return Value of property firstPlayerType.
	 */
	public String getFirstPlayerType() {

		return firstPlayerType;
	}

	/**
	 * Setter for property firstPlayerType.
	 * 
	 * @param type
	 *            New value of property firstPlayerType.
	 */
	public void setFirstPlayerType(String type) {
		firstPlayerType = type;
	}

	/**
	 * Getter for property secondPlayerName.
	 * 
	 * @return Value of property secondPlayerName.
	 */
	public String getSecondPlayerName() {

		return secondPlayerName;
	}

	/**
	 * Setter for property secondPlayerName.
	 * 
	 * @param newSecondPlayerName
	 *            New value of property secondPlayerName.
	 */
	public void setSecondPlayerName(String newSecondPlayerName) {

		secondPlayerName = newSecondPlayerName;
	}

	/**
	 * Getter for property secondPlayerType.
	 * 
	 * @return Value of property secondPlayerType.
	 */
	public String getSecondPlayerType() {

		return secondPlayerType;
	}

	/**
	 * Sets the player type of the second player
	 * 
	 * @param type
	 *            Player type
	 */
	public void setSecondPlayerType(String type) {
		secondPlayerType = type;
	}

	/**
	 * Getter for property thirdPlayerName.
	 * 
	 * @return Value of property thirdPlayerName.
	 */
	public String getThirdPlayerName() {

		return thirdPlayerName;
	}

	/**
	 * Setter for property thirdPlayerName.
	 * 
	 * @param newThirdPlayerName
	 *            New value of property thirdPlayerName.
	 */
	public void setThirdPlayerName(String newThirdPlayerName) {

		thirdPlayerName = newThirdPlayerName;
	}

	/**
	 * Getter for property thirdPlayerType.
	 * 
	 * @return Value of property thirdPlayerType.
	 */
	public String getThirdPlayerType() {

		return thirdPlayerType;
	}

	/**
	 * Setter for property thirdPlayerType.
	 * 
	 * @param type
	 *            New value of property thirdPlayerType.
	 */
	public void setThirdPlayerType(String type) {

		thirdPlayerType = type;
	}

	/**
	 * Getter for property rules.
	 * 
	 * @return Value of property rules.
	 */
	public RuleSet getRules() {

		return rules;
	}

	/**
	 * Setter for property rules.
	 * 
	 * @param newRules
	 *            New value of property rules.
	 */
	public void setRules(RuleSet newRules) {

		rules = newRules;
	}

	/**
	 * Getter for property playBock.
	 * 
	 * @return Value of property playBock.
	 */
	public Boolean isPlayBock() {

		return playBock;
	}

	/**
	 * Setter for property playBock.
	 * 
	 * @param newPlayBock
	 *            New value of property playBock.
	 */
	public void setPlayBock(Boolean newPlayBock) {

		playBock = newPlayBock;
	}

	/**
	 * Getter for property playKontra.
	 * 
	 * @return Value of property playKontra.
	 */
	public Boolean isPlayContra() {

		return playContra;
	}

	/**
	 * Setter for property playKontra.
	 * 
	 * @param newPlayContra
	 *            New value of property playKontra.
	 */
	public void setPlayContra(Boolean newPlayContra) {

		playContra = newPlayContra;
	}

	/**
	 * Getter for property playRamsch.
	 * 
	 * @return Value of property playRamsch.
	 */
	public Boolean isPlayRamsch() {

		return playRamsch;
	}

	/**
	 * Setter for property playRamsch.
	 * 
	 * @param newPlayRamsch
	 *            New value of property playRamsch.
	 */
	public void setPlayRamsch(Boolean newPlayRamsch) {

		playRamsch = newPlayRamsch;
	}

	/**
	 * Getter for property playRevolution.
	 * 
	 * @return Value of property playRevolution.
	 */
	public Boolean isPlayRevolution() {

		return playRevolution;
	}

	/**
	 * Setter for property playRevolution.
	 * 
	 * @param newPlayRevolution
	 *            New value of property playRevolution.
	 */
	public void setPlayRevolution(Boolean newPlayRevolution) {

		playRevolution = newPlayRevolution;
	}

	/**
	 * Getter for property bockEventLostGrand
	 * 
	 * @return Value of property bockEventLostGrand
	 */
	public Boolean isBockEventLostGrand() {

		return bockEventLostGrand;
	}

	/**
	 * Setter for property bockEventLostGrand
	 * 
	 * @param newBockEventLostGrand
	 *            New value of property bockEventLostGrand
	 */
	public void setBockEventLostGrand(Boolean newBockEventLostGrand) {

		bockEventLostGrand = newBockEventLostGrand;
	}

	/**
	 * Getter for property bockEventLostWith60
	 * 
	 * @return Value of property bockEventLostWith60
	 */
	public Boolean isBockEventLostWith60() {

		return bockEventLostWith60;
	}

	/**
	 * Setter for property bockEventLostWith60
	 * 
	 * @param newBockEventLostWith60
	 *            New value of property bockEventLostWith60
	 */
	public void setBockEventLostWith60(Boolean newBockEventLostWith60) {

		bockEventLostWith60 = newBockEventLostWith60;
	}

	/**
	 * Getter for property bockEventLostAfterContra
	 * 
	 * @return Value of property bockEventLostAfterContra
	 */
	public Boolean isBockEventLostAfterContra() {

		return bockEventLostAfterContra;
	}

	/**
	 * Setter for property bockEventLostAfterContra
	 * 
	 * @param newBockEventLostAfterContra
	 *            New value of property bockEventLostAfterContra
	 */
	public void setBockEventLostAfterContra(Boolean newBockEventLostAfterContra) {

		bockEventLostAfterContra = newBockEventLostAfterContra;
	}

	/**
	 * Getter for property bockEventContraReAnnounced
	 * 
	 * @return Value of property bockEventContraReAnnounced
	 */
	public Boolean isBockEventContraReAnnounced() {

		return bockEventContraReAnnounced;
	}

	/**
	 * Setter for property bockEventContraReAnnounced
	 * 
	 * @param newBockEventContraReAnnounced
	 *            New value of property bockEventContraReAnnounced
	 */
	public void setBockEventContraReAnnounced(
			Boolean newBockEventContraReAnnounced) {

		bockEventContraReAnnounced = newBockEventContraReAnnounced;
	}

	/**
	 * Getter for property bockEventPlayerHasX00Points
	 * 
	 * @return Value of property bockEventPlayerHasX00Points
	 */
	public Boolean isBockEventPlayerHasX00Points() {

		return bockEventMultipleOfHundredScore;
	}

	/**
	 * Getter for property ramschSkatOwner.
	 * 
	 * @return Value of property ramschSkatOwner.
	 */
	public RamschSkatOwner getRamschSkat() {

		return ramschSkat;
	}

	/**
	 * Setter for property ramschSkatOwner.
	 * 
	 * @param newRamschSkat
	 *            New value of property ramschSkatOwner.
	 */
	public void setRamschSkat(RamschSkatOwner newRamschSkat) {

		ramschSkat = newRamschSkat;
	}

	/**
	 * Getter for property schieberRamsch
	 * 
	 * @return Value of property schieberRamsch
	 */
	public Boolean isSchieberRamsch() {

		return schieberRamsch;
	}

	/**
	 * Setter for property schieberRamsch
	 * 
	 * @param newSchieberRamsch
	 *            New value of property schieberRamsch
	 */
	public void setSchieberRamsch(Boolean newSchieberRamsch) {

		schieberRamsch = newSchieberRamsch;
	}

	/**
	 * Getter for property schieberRamschJacksInSkat
	 * 
	 * @return Value of property schieberRamschJacksInSkat
	 */
	public Boolean isSchieberRamschJacksInSkat() {

		return schieberRamschJacksInSkat;
	}

	/**
	 * Setter for property schieberRamschJacksInSkat
	 * 
	 * @param newSchieberRamschJacksInSkat
	 *            New value of property schieberRamschJacksInSkat
	 */
	public void setSchieberRamschJacksInSkat(
			Boolean newSchieberRamschJacksInSkat) {

		schieberRamschJacksInSkat = newSchieberRamschJacksInSkat;
	}

	/**
	 * Getter for property ramschEventNoBid
	 * 
	 * @return Value of property ramschEventNoBid
	 */
	public Boolean isRamschEventNoBid() {

		return ramschEventNoBid;
	}

	/**
	 * Setter for property ramschEventNoBid
	 * 
	 * @param newRamschEventNoBid
	 *            New value of property ramschEventNoBid
	 */
	public void setRamschEventNoBid(Boolean newRamschEventNoBid) {

		ramschEventNoBid = newRamschEventNoBid;
	}

	/**
	 * Getter for property ramschEventRamschAfterBock
	 * 
	 * @return Value of property ramschEventRamschAfterBock
	 */
	public Boolean isRamschEventRamschAfterBock() {

		return ramschEventRamschAfterBock;
	}

	/**
	 * Setter for property ramschEventRamschAfterBock
	 * 
	 * @param newRamschEventRamschAfterBock
	 *            New value of property ramschEventRamschAfterBock
	 */
	public void setRamschEventRamschAfterBock(
			Boolean newRamschEventRamschAfterBock) {

		ramschEventRamschAfterBock = newRamschEventRamschAfterBock;
	}

	/**
	 * Getter for property ramschGrandHandPossible
	 * 
	 * @return Value of property ramschGrandHandPossible
	 */
	public Boolean isRamschGrandHandPossible() {

		return ramschGrandHandPossible;
	}

	/**
	 * Setter for property ramschGrandHandPossible
	 * 
	 * @param newRamschGrandHandPossible
	 *            New value of property ramschGrandHandPossible
	 */
	public void setRamschGrandHandPossible(Boolean newRamschGrandHandPossible) {

		ramschGrandHandPossible = newRamschGrandHandPossible;
	}

	/**
	 * Gets the maximum count of players
	 * 
	 * @return Maximum player count
	 */
	public Integer getMaxPlayerCount() {

		return maxPlayerCount;
	}

	/**
	 * Sets the maximum count of players
	 * 
	 * @param newMaxPlayerCount
	 *            Maximum player count
	 */
	public void setMaxPlayerCount(Integer newMaxPlayerCount) {

		maxPlayerCount = newMaxPlayerCount;
	}

	public void setBockEventMultipleOfHundredScore(
			Boolean isBockEventMultipleOfHundredScore) {
		bockEventMultipleOfHundredScore = isBockEventMultipleOfHundredScore;
	}
}
