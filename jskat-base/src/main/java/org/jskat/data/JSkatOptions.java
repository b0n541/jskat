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

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import org.jskat.data.SkatTableOptions.ContraCallingTime;
import org.jskat.data.SkatTableOptions.RamschSkatOwner;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.data.SkatTableOptions.SavePath;
import org.jskat.gui.img.CardSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all options of JSkat.
 */
public final class JSkatOptions {

	private final static String PROPERTIES_FILENAME = "jskat.properties"; //$NON-NLS-1$

	/**
	 * Languages supported by JSkat.
	 */
	public enum SupportedLanguage {
		/**
		 * English.
		 */
		ENGLISH,
		/**
		 * German.
		 */
		GERMAN;
	}

	public enum Option {
		/**
		 * Language for the GUI
		 */
		LANGUAGE(SupportedLanguage.class),
		/**
		 * Save path for properties file and log file
		 */
		SAVE_PATH(String.class),
		/**
		 * Properties file and log file are saved in working directory
		 */
		SAVE_IN_WORKING_DIRECTORY(Boolean.class),
		/**
		 * JSkat checks the website for a new version on start up
		 */
		CHECK_FOR_NEW_VERSION_AT_START_UP(Boolean.class),
		/**
		 * JSkat shows tips at start up
		 */
		SHOW_TIPS_AT_START_UP(Boolean.class),
		/**
		 * Cheat/Debug mode, e.g. all cards can be seen
		 */
		CHEAT_DEBUG_MODE(Boolean.class),
		/**
		 * Card set
		 */
		CARD_SET(CardSet.class),
		/**
		 * Maximum players on a table
		 */
		MAX_PLAYER_COUNT(Integer.class),
		/**
		 * Web address of the ISS
		 */
		ISS_ADDRESS(String.class),
		/**
		 * Network port of the ISS
		 */
		ISS_PORT(Integer.class),
		/**
		 * Rule set
		 */
		RULES(RuleSet.class),
		/**
		 * Allow contra/re calling
		 */
		PLAY_CONTRA(Boolean.class, RULES),
		/**
		 * Time of Contra/Re calling
		 */
		CONTRA_CALLING_TIME(ContraCallingTime.class, PLAY_CONTRA),
		/**
		 * Contra/Re calling is only allowed if the player has at least bid 18
		 */
		CONTRA_AFTER_BID_18(Boolean.class, PLAY_CONTRA),
		/**
		 * Play bock games
		 */
		PLAY_BOCK(Boolean.class, RULES),
		/**
		 * Bock event: all players passed, no bid
		 */
		BOCK_EVENT_NO_BID(Boolean.class, PLAY_BOCK),
		/**
		 * Bock event: Contra/Re was called
		 */
		BOCK_EVENT_CONTRA_RE_CALLED(Boolean.class, PLAY_BOCK),
		/**
		 * Bock event: Declarer lost a game after contra was called
		 */
		BOCK_EVENT_LOST_AFTER_CONTRA(Boolean.class, PLAY_BOCK),
		/**
		 * Bock event: Declarer lost a grand game
		 */
		BOCK_EVENT_LOST_GRAND(Boolean.class, PLAY_BOCK),
		/**
		 * Bock event: Declarer lost a game with 60 points
		 */
		BOCK_EVENT_LOST_WITH_60(Boolean.class, PLAY_BOCK),
		/**
		 * Bock event: One player has X00 points, where X is 1..9
		 */
		BOCK_EVENT_MULTIPLE_OF_HUNDRED_SCORE(Boolean.class, PLAY_BOCK),
		/**
		 * Play ramsch games
		 */
		PLAY_RAMSCH(Boolean.class, RULES),
		/**
		 * Ramsch event: all players passed, no bid
		 */
		RAMSCH_EVENT_NO_BID(Boolean.class, PLAY_RAMSCH),
		/**
		 * Ramsch event: play ramsch after a round of bock games
		 */
		RAMSCH_EVENT_RAMSCH_AFTER_BOCK(Boolean.class, PLAY_RAMSCH),
		/**
		 * Player, who gets the skat in a ramsch game
		 */
		RAMSCH_SKAT_OWNER(RamschSkatOwner.class, PLAY_RAMSCH),
		/**
		 * Play Schieberamsch
		 */
		SCHIEBERAMSCH(Boolean.class, PLAY_RAMSCH),
		/**
		 * Allow jacks to discard in Schieberamsch games
		 */
		SCHIEBERAMSCH_JACKS_IN_SKAT(Boolean.class, SCHIEBERAMSCH),
		/**
		 * It is allowed to play Grand hand instead of a ramsch game
		 */
		RAMSCH_GRAND_HAND_POSSIBLE(Boolean.class),
		/**
		 * Allow playing of revolution (not used a.t.m.)
		 */
		PLAY_REVOLUTION(Boolean.class),
		/**
		 * Waiting time after finishing a trick
		 */
		WAIT_TIME_AFTER_TRICK(Integer.class),
		/**
		 * Hide toolbar
		 */
		HIDE_TOOLBAR(Boolean.class),
		/**
		 * X-Position of main frame on screen
		 */
		MAIN_FRAME_X_POSITION(Integer.class),
		/**
		 * Y-Position of main frame on screen
		 */
		MAIN_FRAME_Y_POSITION(Integer.class),
		/**
		 * Width of main frame
		 */
		MAIN_FRAME_WIDTH(Integer.class),
		/**
		 * Height of main frame
		 */
		MAIN_FRAME_HEIGHT(Integer.class);

		public final Class clazz;
		public final Option parent = null;

		private Option(Class clazz) {
			this.clazz = clazz;
		}

		private Option(Class clazz, Option parent) {
			this.clazz = clazz;
		}

		/**
		 * Gets the enum name as property key.<br>
		 * Calls name(), converts everything to lower case and replaces all
		 * occurences of "_[a-z]" to "[A-Z]"
		 *
		 * @return Property name of the enum
		 */
		public String propertyName() {
			String result = name().toLowerCase();
			while (result.contains("_")) {
				int startIndex = result.indexOf('_');
				String search = result.substring(startIndex, startIndex + 2);
				String replace = search.substring(1).toUpperCase();
				result = result.replace(search, replace);
			}
			return result;
		}

		/**
		 * Gets the enum name from a property key.<br>
		 * Inserts a '_' before each capital letter or number.
		 *
		 * @param property
		 *            Property
		 * @return Enum
		 */
		public static Option valueOfProperty(String property) {
			String result = property.replaceAll("[A-Z]|[0-9]+", "_$0");
			return Option.valueOf(result.toUpperCase());
		}
	}

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(JSkatOptions.class);

	/**
	 * Instance for options.
	 */
	private static JSkatOptions optionsInstance = null;
	/**
	 * Save path resolver
	 */
	private final SavePathResolver savePathResolver;

	/**
	 * Returns the instance of the singleton JSkatOptions.<br>
	 * This method must be called at the very beginning.
	 *
	 * @param savePathResolver
	 *            Resolver for save path
	 * @return Options
	 */
	public static JSkatOptions instance(final SavePathResolver savePathResolver) {

		if (optionsInstance == null) {
			optionsInstance = new JSkatOptions(savePathResolver);
		}

		return optionsInstance;
	}

	/**
	 * Returns the instance of the singleton {@link JSkatOptions}<br>
	 * This methods throws a {@link IllegalStateException} if
	 * {@link #instance(SavePathResolver)} was not called before
	 *
	 * @return Options
	 */
	static public JSkatOptions instance() {

		if (optionsInstance == null) {
			throw new IllegalStateException("Options not intialized, yet."); //$NON-NLS-1$
		}

		return optionsInstance;
	}

	private final Properties options = new Properties();

	/** Creates a new instance of JSkatOptions */
	private JSkatOptions(final SavePathResolver savePathResolver) {
		this.savePathResolver = savePathResolver;
		setDefaultProperties();

		try {
			loadOptions();

		} catch (FileNotFoundException e) {

			log.debug("No properties file found. Using standard values."); //$NON-NLS-1$

		} catch (IOException e) {
			log.warn("Could not load properties: " + e.getClass() + ": " //$NON-NLS-1$ //$NON-NLS-2$
					+ e.getMessage());
		}
	}

	/**
	 * Getter for property cardFace
	 *
	 * @return Value of property cardFace
	 */
	public CardSet getCardSet() {
		return CardSet.valueOf(get(Option.CARD_SET));
	}

	/**
	 * Getter for property language.
	 *
	 * @return Value of property language.
	 */
	public SupportedLanguage getLanguage() {
		return SupportedLanguage.valueOf(get(Option.LANGUAGE));
	}

	/**
	 * Gets the maximum number of players allowed in a skat series
	 *
	 * @return Maximum number
	 */
	public Integer getMaxPlayerCount() {
		return getInteger(Option.MAX_PLAYER_COUNT);
	}

	/**
	 * Getter for property ramschSkatOwner.
	 *
	 * @return Value of property ramschSkatOwner.
	 */
	public RamschSkatOwner getRamschSkatOwner() {
		return RamschSkatOwner.valueOf(get(Option.RAMSCH_SKAT_OWNER));
	}

	/**
	 * Getter for property rules.
	 *
	 * @return Value of property rules.
	 */
	public RuleSet getRules() {
		return RuleSet.valueOf(get(Option.RULES));
	}

	/**
	 * Getter for property savePath.
	 *
	 * @return Value of property savePath.
	 */
	public String getSavePath() {
		switch (getSavePathInternal()) {
		case USER_HOME:
			return savePathResolver.getDefaultSavePath();
		case WORKING_DIRECTORY:
			return savePathResolver.getCurrentWorkingDirectory();
		}
		return null;
	}

	public SavePath getSavePathInternal() {
		return SavePath.valueOf(get(Option.SAVE_PATH));
	}

	public Integer getWaitTimeAfterTrick() {
		return getInteger(Option.WAIT_TIME_AFTER_TRICK);
	}

	public Dimension getMainFrameSize() {
		return new Dimension(getInteger(Option.MAIN_FRAME_WIDTH), getInteger(Option.MAIN_FRAME_HEIGHT));
	}

	public void setMainFrameWidth(Integer width) {
		setOption(Option.MAIN_FRAME_WIDTH, width);
	}

	public void setMainFrameHeight(Integer height) {
		setOption(Option.MAIN_FRAME_HEIGHT, height);
	}

	public Point getMainFramePosition() {
		return new Point(getInteger(Option.MAIN_FRAME_X_POSITION), getInteger(Option.MAIN_FRAME_Y_POSITION));
	}

	public void setMainFrameXPosition(Integer xPosition) {
		setOption(Option.MAIN_FRAME_X_POSITION, xPosition);
	}

	public void setMainFrameYPosition(Integer yPosition) {
		setOption(Option.MAIN_FRAME_Y_POSITION, yPosition);
	}

	public void setWaitTimeAfterTrick(Integer waitTime) {
		setOption(Option.WAIT_TIME_AFTER_TRICK, waitTime);
	}

	public Boolean isHideToolbar() {
		return getBoolean(Option.HIDE_TOOLBAR);
	}

	public void setHideToolbar(Boolean isHideToolbar) {
		setOption(Option.HIDE_TOOLBAR, isHideToolbar);
	}

	/**
	 * Gets the current skat table options
	 *
	 * @return The current skat table options
	 */
	public SkatTableOptions getSkatTableOptions() {

		SkatTableOptions result = new SkatTableOptions();

		result.setMaxPlayerCount(getMaxPlayerCount());

		result.setPlayBock(isPlayBock());

		result.setBockEventContraReAnnounced(isBockEventContraReCalled());
		result.setBockEventLostAfterContra(isBockEventLostAfterContra());
		result.setBockEventLostGrand(isBockEventLostGrand());
		result.setBockEventLostWith60(isBockEventLostWith60());
		result.setBockEventMultipleOfHundredScore(isBockEventMultipleOfHundredScore());

		result.setPlayContra(isPlayContra());
		result.setPlayRamsch(isPlayRamsch());
		result.setPlayRevolution(isPlayRevolution());

		return result;
	}

	public Boolean isBockEventAllPlayersPassed() {
		return isBockEventAllPlayersPassed(true);
	}

	public Boolean isBockEventAllPlayersPassed(boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.BOCK_EVENT_NO_BID, checkParentOption, isPlayBock(checkParentOption));
	}

	/**
	 * Getter for property bockEventContraReAnnounced
	 *
	 * @return Value of property bockEventConOptiontraReAnnounced
	 */
	public Boolean isBockEventContraReCalled() {
		return isBockEventContraReCalled(true);
	}

	public Boolean isBockEventContraReCalled(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.BOCK_EVENT_CONTRA_RE_CALLED, checkParentOption,
				isPlayBock(checkParentOption));
	}

	/**
	 * Getter for property bockEventLostAfterContra
	 *
	 * @return Value of property bockEventLostAfterContra
	 */
	public Boolean isBockEventLostAfterContra() {
		return isBockEventLostAfterContra(true);
	}

	public Boolean isBockEventLostAfterContra(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.BOCK_EVENT_LOST_AFTER_CONTRA, checkParentOption,
				isPlayBock(checkParentOption));
	}

	/**
	 * Getter for property bockEventLostGrand
	 *
	 * @return Value of property bockEventLostGrand
	 */
	public Boolean isBockEventLostGrand() {
		return isBockEventLostGrand(true);
	}

	public Boolean isBockEventLostGrand(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.BOCK_EVENT_LOST_GRAND, checkParentOption,
				isPlayBock(checkParentOption));
	}

	/**
	 * Getter for property bockEventLostWith60
	 *
	 * @return Value of property bockEventLostWith60
	 */
	public Boolean isBockEventLostWith60() {
		return isBockEventLostWith60(true);
	}

	public Boolean isBockEventLostWith60(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.BOCK_EVENT_LOST_WITH_60, checkParentOption,
				isPlayBock(checkParentOption));
	}

	/**
	 * Getter for property bockEventPlayerHasX00Points
	 *
	 * @return Value of property bockEventPlayerHasX00Points
	 */
	public Boolean isBockEventMultipleOfHundredScore() {
		return isBockEventMultipleOfHundredScore(true);
	}

	/**
	 * Checks whether a player score of a multiple of hundred is a bock event.
	 *
	 * @param checkParentOption
	 *            <code>true</code>, if the parent option should be checked
	 * @return <code>true</code>, if the player score of a multiple of hundred
	 *         is a bock event
	 */
	public Boolean isBockEventMultipleOfHundredScore(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.BOCK_EVENT_MULTIPLE_OF_HUNDRED_SCORE, checkParentOption,
				isPlayBock(checkParentOption));
	}

	/**
	 * Checks whether at least a bid of 18 has to be done to say contra
	 *
	 * @return TRUE, if the check succeeds
	 */
	public Boolean isContraAfterBid18() {
		return isContraAfterBid18(true);
	}

	/**
	 * Checks whether at least a bid of 18 has to be done to say contra
	 *
	 * @param checkParentOption
	 *            TRUE, if the parent option must be true too
	 * @return TRUE, if the check succeeds
	 */
	public Boolean isContraAfterBid18(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.CONTRA_AFTER_BID_18, checkParentOption,
				isPlayContra(checkParentOption));
	}

	/**
	 * Getter for property cheatDebugMode.
	 *
	 * @return Value of property cheatDebugMode.
	 */
	public Boolean isCheatDebugMode() {
		return getBoolean(Option.CHEAT_DEBUG_MODE);
	}

	/**
	 * Getter for property playBock.
	 *
	 * @return Value of property playBock.
	 */
	public Boolean isPlayBock() {
		return isPlayBock(true);
	}

	/**
	 * Getter for property playBock.
	 *
	 * @param checkParentOption
	 *            TRUE, if the parent option must be true too
	 * @return Value of property playBock.
	 */
	public Boolean isPlayBock(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.PLAY_BOCK, checkParentOption, RuleSet.PUB.equals(getRules()));
	}

	private Boolean getBooleanWithParentCheck(final Option option, final boolean checkParentOption,
			final boolean parentOptionActivated) {

		Boolean result = getBoolean(option);

		if (checkParentOption) {
			if (!parentOptionActivated) {
				result = Boolean.FALSE;
			}
		}

		return result;
	}

	/**
	 * Getter for property playKontra.
	 *
	 * @return Value of property playKontra.
	 */
	public Boolean isPlayContra() {
		return isPlayContra(true);
	}

	/**
	 * Getter for property playKontra.
	 *
	 * @param checkParentOption
	 *            TRUE, if the parent option must be true too
	 * @return Value of property playKontra.
	 */
	public Boolean isPlayContra(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.PLAY_CONTRA, checkParentOption, RuleSet.PUB.equals(getRules()));
	}

	/**
	 * Getter for property playRamsch.
	 *
	 * @return Value of property playRamsch.
	 */
	public Boolean isPlayRamsch() {
		return isPlayRamsch(true);
	}

	/**
	 * Checks whether Ramsch should be played
	 *
	 * @param checkParentOption
	 *            TRUE, if parent option should be checked too
	 * @return TRUE, if Ramsch should be played
	 */
	public Boolean isPlayRamsch(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.PLAY_RAMSCH, checkParentOption, RuleSet.PUB.equals(getRules()));
	}

	/**
	 * Getter for property playRevolution.
	 *
	 * @return Value of property playRevolution.
	 */
	public Boolean isPlayRevolution() {
		return getBoolean(Option.PLAY_REVOLUTION);
	}

	public Boolean isPlayRevolution(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.PLAY_REVOLUTION, checkParentOption, RuleSet.PUB.equals(getRules()));
	}

	/**
	 * Getter for property ramschEventNoBid
	 *
	 * @return Value of property ramschEventNoBid
	 */
	public Boolean isRamschEventNoBid() {
		return isRamschEventNoBid(true);
	}

	public Boolean isRamschEventNoBid(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.RAMSCH_EVENT_NO_BID, checkParentOption,
				isPlayRamsch(checkParentOption));
	}

	/**
	 * Getter for property ramschEventRamschAfterBock
	 *
	 * @return Value of property ramschEventRamschAfterBock
	 */
	public Boolean isRamschEventRamschAfterBock() {
		return isRamschEventRamschAfterBock(true);
	}

	public Boolean isRamschEventRamschAfterBock(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.RAMSCH_EVENT_RAMSCH_AFTER_BOCK, checkParentOption,
				isPlayRamsch(checkParentOption));
	}

	/**
	 * Getter for property ramschGrandHandPossible
	 *
	 * @return Value of property ramschGrandHandPossible
	 */
	public Boolean isRamschGrandHandPossible() {
		return getBoolean(Option.RAMSCH_GRAND_HAND_POSSIBLE);
	}

	/**
	 * Getter for property schieberRamsch
	 *
	 * @return Value of property schieberRamsch
	 */
	public Boolean isSchieberamsch() {
		return isSchieberamsch(true);
	}

	public Boolean isSchieberamsch(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.SCHIEBERAMSCH, checkParentOption, isPlayRamsch(checkParentOption));
	}

	/**
	 * Getter for property schieberRamschJacksInSkat
	 *
	 * @return Value of property schieberRamschJacksInSkat
	 */
	public Boolean isSchieberamschJacksInSkat() {
		return isSchieberamschJacksInSkat(true);
	}

	public Boolean isSchieberamschJacksInSkat(final boolean checkParentOption) {
		return getBooleanWithParentCheck(Option.SCHIEBERAMSCH_JACKS_IN_SKAT, checkParentOption,
				isSchieberamsch(checkParentOption));
	}

	public Boolean isShowTipsOnStartUp() {
		return getBoolean(Option.SHOW_TIPS_AT_START_UP);
	}

	/**
	 * Saves the options to a file .jskat in user home
	 */
	public void saveJSkatProperties() {

		try {
			FileWriter writer;
			File dir = new File(getSavePath());
			File file = new File(getSavePath() + PROPERTIES_FILENAME);
			if (file.exists()) {
				writer = new FileWriter(file);
			} else {
				if (!dir.exists()) {
					dir.mkdirs();
				}
				file.createNewFile();
				writer = new FileWriter(file);
			}
			options.store(writer, "JSkat options"); //$NON-NLS-1$
			writer.close();
			log.debug("Saved options with rules: " + getRules()); //$NON-NLS-1$
		} catch (IOException e) {
			log.warn("Saving of JSkat options failed."); //$NON-NLS-1$
			log.warn(e.toString());
		}
	}

	public void setBockEventNoBid(Boolean bockEventAllPlayersPassed) {
		setOption(Option.BOCK_EVENT_NO_BID, bockEventAllPlayersPassed);
	}

	/**
	 * Setter for property bockEventContraReAnnounced
	 *
	 * @param bockEventContraReCalled
	 *            New value of property bockEventContraReAnnounced
	 */
	public void setBockEventContraReCalled(final Boolean bockEventContraReCalled) {
		setOption(Option.BOCK_EVENT_CONTRA_RE_CALLED, bockEventContraReCalled);
	}

	/**
	 * Setter for property bockEventLostAfterContra
	 *
	 * @param bockEventLostAfterContra
	 *            New value of property bockEventLostAfterContra
	 */
	public void setBockEventLostAfterContra(final Boolean bockEventLostAfterContra) {
		setOption(Option.BOCK_EVENT_LOST_AFTER_CONTRA, bockEventLostAfterContra);
	}

	/**
	 * Setter for property bockEventLostGrand
	 *
	 * @param bockEventLostGrand
	 *            New value of property bockEventLostGrand
	 */
	public void setBockEventLostGrand(final Boolean bockEventLostGrand) {
		setOption(Option.BOCK_EVENT_LOST_GRAND, bockEventLostGrand);
	}

	/**
	 * Setter for property bockEventLostWith60
	 *
	 * @param bockEventLostWith60
	 *            New value of property bockEventLostWith60
	 */
	public void setBockEventLostWith60(final Boolean bockEventLostWith60) {
		setOption(Option.BOCK_EVENT_LOST_WITH_60, bockEventLostWith60);
	}

	/**
	 * Setter for property bockEventPlayerHasX00Points
	 *
	 * @param bockEventMultipleOfHundredScore
	 *            New value of property bockEventPlayerHasX00Points
	 */
	public void setBockEventMultipleOfHundredScore(final Boolean bockEventMultipleOfHundredScore) {
		setOption(Option.BOCK_EVENT_MULTIPLE_OF_HUNDRED_SCORE, bockEventMultipleOfHundredScore);
	}

	/**
	 * Sets the flag for bid at least 18 to say contra
	 *
	 * @param contraAfterBid18
	 *            TRUE, if at least a bid of 18 is needed for the right to say
	 *            contra
	 */
	public void setContraAfterBid18(final Boolean contraAfterBid18) {
		setOption(Option.CONTRA_AFTER_BID_18, contraAfterBid18);
	}

	/**
	 * Setter for property card set.
	 *
	 * @param cardSet
	 *            Card set
	 */
	public void setCardSet(final CardSet cardSet) {
		setOption(Option.CARD_SET, cardSet);
	}

	/**
	 * Setter for property cheatDebugMode.
	 *
	 * @param isCheatDebugMode
	 *            New value of property cheatDebugMode.
	 */
	public void setCheatDebugMode(final Boolean isCheatDebugMode) {
		setOption(Option.CHEAT_DEBUG_MODE, isCheatDebugMode);
	}

	/**
	 * Sets the flag for checking for a new version of JSkat at start up
	 *
	 * @param isCheckForNewVersionAtStartUp
	 *            TRUE, if the check should be performed at start up
	 */
	public void setCheckForNewVersionAtStartUp(final Boolean isCheckForNewVersionAtStartUp) {
		setOption(Option.CHECK_FOR_NEW_VERSION_AT_START_UP, isCheckForNewVersionAtStartUp);
	}

	/**
	 * Sets the address of the ISS
	 *
	 * @param address
	 *            Address
	 */
	public void setIssAddress(final String address) {
		setOption(Option.ISS_ADDRESS, address);
	}

	/**
	 * Sets the port of the ISS
	 *
	 * @param port
	 *            Port
	 */
	public void setIssPort(final Integer port) {
		setOption(Option.ISS_PORT, port);
	}

	/**
	 * Setter for property language.
	 *
	 * @param language
	 *            New value of property language.
	 */
	public void setLanguage(final SupportedLanguage language) {
		setOption(Option.LANGUAGE, language);
	}

	/**
	 * Sets the maximum number of players in a skat series
	 *
	 * @param count
	 *            Maximumn number
	 */
	public void setMaxPlayerCount(final Integer count) {
		setOption(Option.MAX_PLAYER_COUNT, count);
	}

	/**
	 * Setter for property playBock.
	 *
	 * @param playBock
	 *            New value of property playBock.
	 */
	public void setPlayBock(final Boolean playBock) {
		setOption(Option.PLAY_BOCK, playBock);
	}

	/**
	 * Setter for property playKontra.
	 *
	 * @param playContra
	 *            New value of property playKontra.
	 */
	public void setPlayContra(final Boolean playContra) {
		setOption(Option.PLAY_CONTRA, playContra);
	}

	/**
	 * Setter for property playRamsch.
	 *
	 * @param playRamsch
	 *            New value of property playRamsch.
	 */
	public void setPlayRamsch(final Boolean playRamsch) {
		setOption(Option.PLAY_RAMSCH, playRamsch);
	}

	/**
	 * Setter for property playRevolution.
	 *
	 * @param playRevolution
	 *            New value of property playRevolution.
	 */
	public void setPlayRevolution(final Boolean playRevolution) {
		setOption(Option.PLAY_REVOLUTION, playRevolution);
	}

	/**
	 * Setter for property ramschEventNoBid
	 *
	 * @param ramschEventNoBid
	 *            New value of property ramschEventNoBid
	 */
	public void setRamschEventNoBid(final Boolean ramschEventNoBid) {
		setOption(Option.RAMSCH_EVENT_NO_BID, ramschEventNoBid);
	}

	/**
	 * Setter for property ramschEventRamschAfterBock
	 *
	 * @param ramschEventRamschAfterBock
	 *            New value of property ramschEventRamschAfterBock
	 */
	public void setRamschEventRamschAfterBock(final Boolean ramschEventRamschAfterBock) {
		setOption(Option.RAMSCH_EVENT_RAMSCH_AFTER_BOCK, ramschEventRamschAfterBock);
	}

	/**
	 * Setter for property ramschGrandHandPossible
	 *
	 * @param ramschGrandHandPossible
	 *            New value of property ramschGrandHandPossible
	 */
	public void setRamschGrandHandPossible(final Boolean ramschGrandHandPossible) {
		setOption(Option.RAMSCH_GRAND_HAND_POSSIBLE, ramschGrandHandPossible);
	}

	/**
	 * Setter for property ramschSkat.
	 *
	 * @param ramschSkat
	 *            Owner of the skat after the ramsch game
	 */
	public void setRamschSkatOwner(final RamschSkatOwner ramschSkat) {
		setOption(Option.RAMSCH_SKAT_OWNER, ramschSkat);
	}

	/**
	 * Setter for property rules.
	 *
	 * @param ruleSet
	 *            New value of property rules.
	 */
	public void setRules(final RuleSet ruleSet) {
		setOption(Option.RULES, ruleSet.name());
	}

	/**
	 * Setter for property savePath.
	 *
	 * @param savePath
	 *            New value of property savePath.
	 */
	public void setSavePath(final SavePath savePath) {
		setOption(Option.SAVE_PATH, savePath.name());
	}

	/**
	 * Setter for property schieberRamsch
	 *
	 * @param schieberRamsch
	 *            New value of property schieberRamsch
	 */
	public void setSchieberRamsch(final Boolean schieberRamsch) {
		setOption(Option.SCHIEBERAMSCH, schieberRamsch);
	}

	/**
	 * Setter for property schieberRamschJacksInSkat
	 *
	 * @param schieberRamschJacksInSkat
	 *            New value of property schieberRamschJacksInSkat
	 */
	public void setSchieberRamschJacksInSkat(final Boolean schieberRamschJacksInSkat) {
		setOption(Option.SCHIEBERAMSCH_JACKS_IN_SKAT, schieberRamschJacksInSkat);
	}

	private static SupportedLanguage getDefaultLanguage() {

		SupportedLanguage result = SupportedLanguage.ENGLISH;

		if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
			result = SupportedLanguage.GERMAN;
		}

		return result;
	}

	private void loadOptions() throws IOException {

		FileInputStream stream = null;

		try {
			stream = new FileInputStream(savePathResolver.getCurrentWorkingDirectory() + PROPERTIES_FILENAME);
			setSavePath(SavePath.WORKING_DIRECTORY);
		} catch (FileNotFoundException e) {
			stream = new FileInputStream(savePathResolver.getDefaultSavePath() + PROPERTIES_FILENAME);
			setSavePath(SavePath.USER_HOME);
		}

		Properties loadedOptions = new Properties();
		loadedOptions.load(stream);

		Enumeration<Object> props = loadedOptions.keys();
		String property;
		String value;

		while (props.hasMoreElements()) {

			property = (String) props.nextElement();
			value = loadedOptions.getProperty(property);

			Option option = null;
			try {
				option = Option.valueOfProperty(property);
			} catch (IllegalArgumentException e) {
				log.error("Unknown option " + property + " with value " + value); //$NON-NLS-1$ //$NON-NLS-2$

				// handle obsolete or renamed options
				if ("ramschSkat".equals(property)) { //$NON-NLS-1$
					option = Option.RAMSCH_SKAT_OWNER;
				}
			}

			if (option == null) {
				continue;
			}

			parseAndSetOptionValue(option, value);
		}
	}

	private void parseAndSetOptionValue(final Option option, final String value) {
		switch (option) {
		case BOCK_EVENT_CONTRA_RE_CALLED:
			setBockEventContraReCalled(Boolean.valueOf(value));
			break;
		case BOCK_EVENT_LOST_AFTER_CONTRA:
			setBockEventLostAfterContra(Boolean.valueOf(value));
			break;
		case BOCK_EVENT_LOST_GRAND:
			setBockEventLostGrand(Boolean.valueOf(value));
			break;
		case BOCK_EVENT_LOST_WITH_60:
			setBockEventLostWith60(Boolean.valueOf(value));
			break;
		case BOCK_EVENT_MULTIPLE_OF_HUNDRED_SCORE:
			setBockEventMultipleOfHundredScore(Boolean.valueOf(value));
			break;
		case BOCK_EVENT_NO_BID:
			setBockEventNoBid(Boolean.valueOf(value));
			break;
		case CARD_SET:
			try {
				setCardSet(CardSet.valueOf(value));
			} catch (IllegalArgumentException e) {
				// parsing of older options failed
				logEnumParseError(option, getCardSet().name());
			}
			break;
		case CHEAT_DEBUG_MODE:
			setCheatDebugMode(Boolean.valueOf(value));
			break;
		case CHECK_FOR_NEW_VERSION_AT_START_UP:
			setCheckForNewVersionAtStartUp(Boolean.valueOf(value));
			break;
		case CONTRA_AFTER_BID_18:
			setContraAfterBid18(Boolean.valueOf(value));
			break;
		case ISS_ADDRESS:
			setIssAddress(value);
			break;
		case ISS_PORT:
			setIssPort(Integer.valueOf(value));
			break;
		case LANGUAGE:
			try {
				setLanguage(SupportedLanguage.valueOf(value));
			} catch (IllegalArgumentException e) {
				// parsing of older options failed
				logEnumParseError(option, getLanguage().name());
			}
			break;
		case MAX_PLAYER_COUNT:
			setMaxPlayerCount(Integer.valueOf(value));
			break;
		case PLAY_BOCK:
			setPlayBock(Boolean.valueOf(value));
			break;
		case PLAY_CONTRA:
			setPlayContra(Boolean.valueOf(value));
			break;
		case PLAY_RAMSCH:
			setPlayRamsch(Boolean.valueOf(value));
			break;
		case PLAY_REVOLUTION:
			setPlayRevolution(Boolean.valueOf(value));
			break;
		case RAMSCH_EVENT_NO_BID:
			setRamschEventNoBid(Boolean.valueOf(value));
			break;
		case RAMSCH_EVENT_RAMSCH_AFTER_BOCK:
			setRamschEventRamschAfterBock(Boolean.valueOf(value));
			break;
		case RAMSCH_GRAND_HAND_POSSIBLE:
			setRamschGrandHandPossible(Boolean.valueOf(value));
			break;
		case RAMSCH_SKAT_OWNER:
			try {
				setRamschSkatOwner(RamschSkatOwner.valueOf(value));
			} catch (IllegalArgumentException e) {
				// parsing of older options failed
				logEnumParseError(option, getRamschSkatOwner().name());
			}
			break;
		case RULES:
			try {
				setRules(RuleSet.valueOf(value));
			} catch (IllegalArgumentException e) {
				// parsing of older options failed
				logEnumParseError(option, getRules().name());
			}
			break;
		case SCHIEBERAMSCH:
			setSchieberRamsch(Boolean.valueOf(value));
			break;
		case SCHIEBERAMSCH_JACKS_IN_SKAT:
			setSchieberRamschJacksInSkat(Boolean.valueOf(value));
			break;
		case SHOW_TIPS_AT_START_UP:
			setShowTipsAtStartUp(Boolean.valueOf(value));
			break;
		case WAIT_TIME_AFTER_TRICK:
			setWaitTimeAfterTrick(Integer.valueOf(value));
			break;
		case HIDE_TOOLBAR:
			setHideToolbar(Boolean.valueOf(value));
			break;
		case MAIN_FRAME_WIDTH:
			setMainFrameWidth(Integer.valueOf(value));
			break;
		case MAIN_FRAME_HEIGHT:
			setMainFrameHeight(Integer.valueOf(value));
			break;
		case MAIN_FRAME_X_POSITION:
			setMainFrameXPosition(Integer.valueOf(value));
			break;
		case MAIN_FRAME_Y_POSITION:
			setMainFrameYPosition(Integer.valueOf(value));
			break;
		}
	}

	private static void logEnumParseError(final Option option, final String defaultValue) {
		log.warn("Parsing of option " + option.name() + " failed. Using default value: " + defaultValue); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Gets the language code for the language
	 *
	 * @return Language code
	 */
	public String getI18NCode() {

		String result = "en"; //$NON-NLS-1$

		if (SupportedLanguage.GERMAN.equals(getLanguage())) {
			result = "de"; //$NON-NLS-1$
		}

		return result;
	}

	private void setOption(Option option, Boolean value) {
		options.setProperty(option.propertyName(), value.toString());
	}

	private void setOption(Option option, CardSet value) {
		options.setProperty(option.propertyName(), value.name());
	}

	private void setOption(Option option, Integer value) {
		options.setProperty(option.propertyName(), value.toString());
	}

	private void setOption(Option option, RamschSkatOwner value) {
		options.setProperty(option.propertyName(), value.name());
	}

	private void setOption(Option option, SavePath value) {
		options.setProperty(option.propertyName(), value.name());
	}

	private void setOption(Option option, String value) {
		options.setProperty(option.propertyName(), value);
	}

	private void setOption(Option option, SupportedLanguage value) {
		options.setProperty(option.propertyName(), value.name());
	}

	private void setOption(Option option, RuleSet ruleSet) {
		options.setProperty(option.propertyName(), ruleSet.name());
	}

	private void setOption(Option option, ContraCallingTime value) {
		options.setProperty(option.propertyName(), value.name());
	}

	/**
	 * Sets the standard properties
	 *
	 */
	void setDefaultProperties() {

		setOption(Option.HIDE_TOOLBAR, Boolean.FALSE);
		setOption(Option.SHOW_TIPS_AT_START_UP, Boolean.TRUE);
		setOption(Option.CHECK_FOR_NEW_VERSION_AT_START_UP, Boolean.FALSE);
		setOption(Option.LANGUAGE, getDefaultLanguage());
		setOption(Option.SAVE_PATH, SavePath.USER_HOME);
		setOption(Option.CARD_SET, CardSet.ISS_TOURNAMENT);
		setOption(Option.CHEAT_DEBUG_MODE, Boolean.FALSE);
		setOption(Option.MAX_PLAYER_COUNT, 3);
		setOption(Option.RULES, RuleSet.ISPA);
		setOption(Option.PLAY_CONTRA, Boolean.TRUE);
		setOption(Option.CONTRA_CALLING_TIME, ContraCallingTime.BEFORE_FIRST_CARD);
		setOption(Option.CONTRA_AFTER_BID_18, Boolean.TRUE);
		setOption(Option.PLAY_BOCK, Boolean.TRUE);
		setOption(Option.BOCK_EVENT_LOST_GRAND, Boolean.TRUE);
		setOption(Option.BOCK_EVENT_LOST_WITH_60, Boolean.TRUE);
		setOption(Option.BOCK_EVENT_LOST_AFTER_CONTRA, Boolean.TRUE);
		setOption(Option.BOCK_EVENT_CONTRA_RE_CALLED, Boolean.FALSE);
		setOption(Option.BOCK_EVENT_MULTIPLE_OF_HUNDRED_SCORE, Boolean.FALSE);
		setOption(Option.BOCK_EVENT_NO_BID, Boolean.FALSE);
		setOption(Option.PLAY_RAMSCH, Boolean.TRUE);
		setOption(Option.RAMSCH_EVENT_NO_BID, Boolean.TRUE);
		setOption(Option.RAMSCH_EVENT_RAMSCH_AFTER_BOCK, Boolean.FALSE);
		setOption(Option.RAMSCH_SKAT_OWNER, RamschSkatOwner.LAST_TRICK);
		setOption(Option.SCHIEBERAMSCH, Boolean.TRUE);
		setOption(Option.SCHIEBERAMSCH_JACKS_IN_SKAT, Boolean.FALSE);
		setOption(Option.RAMSCH_GRAND_HAND_POSSIBLE, Boolean.TRUE);
		setOption(Option.PLAY_REVOLUTION, Boolean.FALSE);
		setOption(Option.ISS_ADDRESS, "skatgame.net"); //$NON-NLS-1$
		setOption(Option.ISS_PORT, Integer.valueOf(7000));
		setOption(Option.WAIT_TIME_AFTER_TRICK, Integer.valueOf(0));
		setOption(Option.MAIN_FRAME_X_POSITION, Integer.MIN_VALUE);
		setOption(Option.MAIN_FRAME_Y_POSITION, Integer.MIN_VALUE);
		setOption(Option.MAIN_FRAME_WIDTH, Integer.MIN_VALUE);
		setOption(Option.MAIN_FRAME_HEIGHT, Integer.MIN_VALUE);
	}

	/**
	 * Sets the flag for showing the welcome dialog with first steps at startup
	 *
	 * @param isShowTips
	 *            TRUE, if first steps should be shown
	 */
	public void setShowTipsAtStartUp(final Boolean isShowTips) {
		setOption(Option.SHOW_TIPS_AT_START_UP, isShowTips);
	}

	/**
	 * Gets any option as {@link String}
	 *
	 * @param option
	 *            Option
	 * @return Value
	 */
	public String get(Option option) {

		return getProperty(option);
	}

	/**
	 * Gets an option of type {@link String}
	 *
	 * @param option
	 *            Option
	 * @return Value
	 */
	public String getString(Option option) {
		if (option.clazz != String.class) {
			throw new IllegalArgumentException("Option " + option + " is not a string option.");
		}
		return getProperty(option);
	}

	private String getProperty(Option option) {
		return options.getProperty(option.propertyName());
	}

	/**
	 * Gets an option of type {@link Boolean}
	 *
	 * @param option
	 *            Option
	 * @return Value
	 */
	public final Boolean getBoolean(final Option option) {
		if (option.clazz != Boolean.class) {
			throw new IllegalArgumentException("Option " + option + " is not a boolean option.");
		}
		return Boolean.valueOf(getProperty(option));
	}

	/**
	 * Gets an option of type {@link Integer}
	 *
	 * @param option
	 *            Option
	 * @return Value
	 */
	public final Integer getInteger(final Option option) {
		if (option.clazz != Integer.class) {
			throw new IllegalArgumentException("Option " + option + " is not an integer option.");
		}
		return Integer.valueOf(getProperty(option));
	}

	/**
	 * Gets the time when contra calling is allowed.
	 *
	 * @return Time when contra calling is allowed
	 */
	public ContraCallingTime getContraCallingTime() {
		return ContraCallingTime.valueOf(get(Option.CONTRA_CALLING_TIME));
	}

	/**
	 * Resets the options to default
	 */
	public void resetToDefault() {
		setDefaultProperties();
	}

	public void setMainFrameSize(Dimension size) {
		setOption(Option.MAIN_FRAME_WIDTH, (int) size.getWidth());
		setOption(Option.MAIN_FRAME_HEIGHT, (int) size.getHeight());
	}

	public void setMainFramePosition(Point locationOnScreen) {
		setOption(Option.MAIN_FRAME_X_POSITION, locationOnScreen.x);
		setOption(Option.MAIN_FRAME_Y_POSITION, locationOnScreen.y);
	}
}
