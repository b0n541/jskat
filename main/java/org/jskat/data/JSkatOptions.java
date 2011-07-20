/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.SkatTableOptions.RamschSkatOwners;
import org.jskat.data.SkatTableOptions.RuleSets;
import org.jskat.gui.img.CardFace;


/**
 * Holds all options of JSkat
 */
public class JSkatOptions {

	private static Log log = LogFactory.getLog(JSkatOptions.class);

	static private JSkatOptions optionsInstance = null;

	/**
	 * Languages supported by JSkat
	 */
	public enum SupportedLanguage {
		/**
		 * English
		 */
		ENGLISH,
		/**
		 * German
		 */
		GERMAN;
	}

	private Properties jskatProperties = new Properties();

	private SupportedLanguage language = SupportedLanguage.GERMAN;

	private String savePath = "";

	private int trickRemoveDelayTime = 1000;

	private boolean trickRemoveAfterClick = false;

	private boolean gameShortCut = false;

	private boolean cheatDebugMode = false;

	private CardFace cardFace = CardFace.TOURNAMENT;

	private SkatTableOptions tableOptions = new SkatTableOptions();

	private boolean showCards = false;

	/**
	 * Returns the instance of the Singleton JSkatOptions
	 * 
	 * @return The unique instance of this class.
	 */
	static public JSkatOptions instance() {

		if (null == optionsInstance) {

			optionsInstance = new JSkatOptions();
		}

		return optionsInstance;
	}

	/** Creates a new instance of JSkatOptions */
	private JSkatOptions() {

		setStandardProperties();

		try {
			loadOptions();

		} catch (FileNotFoundException e) {

			log.debug("No properties file found. Using standard values.");

			File dir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".jskat");
			dir.mkdir();
			String filename = System.getProperty("user.home") + System.getProperty("file.separator") + ".jskat" + System.getProperty("file.separator") + "jskat.properties";
			File file = new File(filename);
			try {
				file.createNewFile();

				log.debug("Property file jskat.properties created: <"+filename+">");
			} catch (IOException e1) {
				log.warn("Could not create property file <"+filename+"> due to "+e1.getClass()+": "+e1.getMessage());
			}

			setStandardProperties();

		} catch (IOException e) {
			log.warn("Could not load properties: "+e.getClass()+": "+e.getMessage());
		}
	}

	private void loadOptions() throws FileNotFoundException, IOException {

		FileInputStream stream = getFileStream();

		this.jskatProperties.load(stream);

		Enumeration<Object> props = this.jskatProperties.keys();
		String property;
		String value;

		while (props.hasMoreElements()) {

			property = (String) props.nextElement();

			value = this.jskatProperties.getProperty(property);

			if (property.equals("language")) {

				if (value.equals("GERMAN")) {

					setLanguage(JSkatOptions.SupportedLanguage.GERMAN);
				} else {

					setLanguage(JSkatOptions.SupportedLanguage.ENGLISH);
				}
			} else if (property.equals("cardFace")) {

				if (value.equals("GERMAN")) {

					setCardFace(CardFace.GERMAN);
				} else if (value.equals("FRENCH")) {

					setCardFace(CardFace.FRENCH);
				} else {

					setCardFace(CardFace.TOURNAMENT);
				}
			} else if (property.equals("savePath")) {
				setSavePath(value);
			} else if (property.equals("trickRemoveDelayTime")) {
				setTrickRemoveDelayTime(Integer.parseInt(value));
			} else if (property.equals("trickRemoveAfterClick")) {
				setTrickRemoveAfterClick(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("gameShortCut")) {
				setGameShortCut(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("cheatDebugMode")) {
				setCheatDebugMode(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("firstPlayerName")) {
				setFirstPlayerName(value);
			} else if (property.equals("firstPlayerType")) {
				setFirstPlayerType(Integer.parseInt(value));
			} else if (property.equals("secondPlayerName")) {
				setSecondPlayerName(value);
			} else if (property.equals("secondPlayerType")) {
				setSecondPlayerType(Integer.parseInt(value));
			} else if (property.equals("thirdPlayerName")) {
				setThirdPlayerName(value);
			} else if (property.equals("thirdPlayerType")) {
				setThirdPlayerType(Integer.parseInt(value));
			} else if (property.equals("rules")) {
				if (value.equals("PUB")) {
					setRules(SkatTableOptions.RuleSets.PUB);
				} else {
					setRules(SkatTableOptions.RuleSets.ISPA);
				}
			} else if (property.equals("playContra")) {
				setPlayContra(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("playBock")) {
				setPlayBock(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("playRamsch")) {
				setPlayRamsch(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("playRevolution")) {
				setPlayRevolution(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventLostGrand")) {
				setBockEventLostGrand(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventLostWith60")) {
				setBockEventLostWith60(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventLostAfterContra")) {
				setBockEventLostAfterContra(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventContraReAnnounced")) {
				setBockEventContraReAnnounced(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventPlayerHasX00Points")) {
				setBockEventPlayerHasX00Points(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschSkat")) {
				if (value.equals("LAST_TRICK")) {
					setRamschSkat(SkatTableOptions.RamschSkatOwners.LAST_TRICK);
				} else {
					setRamschSkat(SkatTableOptions.RamschSkatOwners.LOSER);
				}
			} else if (property.equals("schieberRamsch")) {
				setSchieberRamsch(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("schieberRamschJacksInSkat")) {
				setSchieberRamschJacksInSkat(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschEventNoBid")) {
				setRamschEventNoBid(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschEventRamschAfterBock")) {
				setRamschEventRamschAfterBock(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschGrandHandPossible")) {
				setRamschGrandHandPossible(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("showCards")) {
				setShowCards(Boolean.valueOf(value).booleanValue());
			}
			// table options
			else if (property.equals("maxPlayerCount")) {
				setMaxPlayerCount(Integer.parseInt(value));
			} else if (property.equals("firstPlayerName")) {
				setFirstPlayerName(value);
			} else if (property.equals("firstPlayerType")) {
				setFirstPlayerType(Integer.parseInt(value));
			} else if (property.equals("secondPlayerName")) {
				setSecondPlayerName(value);
			} else if (property.equals("secondPlayerType")) {
				setSecondPlayerType(Integer.parseInt(value));
			} else if (property.equals("thirdPlayerName")) {
				setThirdPlayerName(value);
			} else if (property.equals("thirdPlayerType")) {
				setThirdPlayerType(Integer.parseInt(value));
			} else if (property.equals("rules")) {
				if (value.equals("PUB")) {
					setRules(RuleSets.PUB);
				} else {
					setRules(RuleSets.ISPA);
				}
			} else if (property.equals("playContra")) {
				setPlayContra(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("playBock")) {
				setPlayBock(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("playRamsch")) {
				setPlayRamsch(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("playRevolution")) {
				setPlayRevolution(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventLostGrand")) {
				setBockEventLostGrand(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventLostWith60")) {
				setBockEventLostWith60(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventLostAfterContra")) {
				setBockEventLostAfterContra(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventContraReAnnounced")) {
				setBockEventContraReAnnounced(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("bockEventPlayerHasX00Points")) {
				setBockEventPlayerHasX00Points(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschSkat")) {
				if (value.equals("LAST_TRICK")) {
					setRamschSkat(RamschSkatOwners.LAST_TRICK);
				} else {
					setRamschSkat(RamschSkatOwners.LOSER);
				}
			} else if (property.equals("schieberRamsch")) {
				setSchieberRamsch(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("schieberRamschJacksInSkat")) {
				setSchieberRamschJacksInSkat(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschEventNoBid")) {
				setRamschEventNoBid(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschEventRamschAfterBock")) {
				setRamschEventRamschAfterBock(Boolean.valueOf(value).booleanValue());
			} else if (property.equals("ramschGrandHandPossible")) {
				setRamschGrandHandPossible(Boolean.valueOf(value).booleanValue());
			}
		}
	}

	private FileInputStream getFileStream() throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(System.getProperty("user.home") //$NON-NLS-1$
				+ System.getProperty("file.separator") + ".jskat" //$NON-NLS-1$ //$NON-NLS-2$
				+ System.getProperty("file.separator") //$NON-NLS-1$
				+ "jskat.properties"); //$NON-NLS-1$
		return stream;
	}

	/**
	 * Sets the standard properties
	 * 
	 */
	private void setStandardProperties() {

		// use standard values for the options
		this.jskatProperties.setProperty("language", String.valueOf(this.language));
		this.jskatProperties.setProperty("savePath", this.savePath);
		this.jskatProperties.setProperty("trickRemoveDelayTime", String.valueOf(this.trickRemoveDelayTime));
		this.jskatProperties.setProperty("trickRemoveAfterClick", String.valueOf(this.trickRemoveAfterClick));
		this.jskatProperties.setProperty("gameShortCut", String.valueOf(this.gameShortCut));
		this.jskatProperties.setProperty("cheatDebugMode", String.valueOf(this.cheatDebugMode));
		this.jskatProperties.setProperty("firstPlayerName", this.tableOptions.getFirstPlayerName());
		this.jskatProperties.setProperty("firstPlayerType", String.valueOf(this.tableOptions.getFirstPlayerType()));
		this.jskatProperties.setProperty("secondPlayerName", this.tableOptions.getSecondPlayerName());
		this.jskatProperties.setProperty("secondPlayerType", String.valueOf(this.tableOptions.getSecondPlayerType()));
		this.jskatProperties.setProperty("thirdPlayerName", this.tableOptions.getThirdPlayerName());
		this.jskatProperties.setProperty("thirdPlayerType", String.valueOf(this.tableOptions.getThirdPlayerType()));
		this.jskatProperties.setProperty("rules", String.valueOf(this.tableOptions.getRules()));
		this.jskatProperties.setProperty("playContra", String.valueOf(this.tableOptions.isPlayContra()));
		this.jskatProperties.setProperty("playBock", String.valueOf(this.tableOptions.isPlayBock()));
		this.jskatProperties.setProperty("playRamsch", String.valueOf(this.tableOptions.isPlayRamsch()));
		this.jskatProperties.setProperty("playRevolution", String.valueOf(this.tableOptions.isPlayRevolution()));
		this.jskatProperties
				.setProperty("bockEventLostGrand", String.valueOf(this.tableOptions.isBockEventLostGrand()));
		this.jskatProperties.setProperty("bockEventLostWith60",
				String.valueOf(this.tableOptions.isBockEventLostWith60()));
		this.jskatProperties.setProperty("bockEventLostAfterContra",
				String.valueOf(this.tableOptions.isBockEventLostAfterContra()));
		this.jskatProperties.setProperty("bockEventContraReAnnounced",
				String.valueOf(this.tableOptions.isBockEventContraReAnnounced()));
		this.jskatProperties.setProperty("bockEventPlayerHasX00Points",
				String.valueOf(this.tableOptions.isBockEventPlayerHasX00Points()));
		this.jskatProperties.setProperty("ramschSkat", String.valueOf(this.tableOptions.getRamschSkat()));
		this.jskatProperties.setProperty("schieberRamsch", String.valueOf(this.tableOptions.isSchieberRamsch()));
		this.jskatProperties.setProperty("schieberRamschJacksInSkat",
				String.valueOf(this.tableOptions.isSchieberRamschJacksInSkat()));
		this.jskatProperties.setProperty("ramschEventNoBid", String.valueOf(this.tableOptions.isRamschEventNoBid()));
		this.jskatProperties.setProperty("ramschEventRamschAfterBock",
				String.valueOf(this.tableOptions.isRamschEventRamschAfterBock()));
		this.jskatProperties.setProperty("ramschGrandHandPossible",
				String.valueOf(this.tableOptions.isRamschGrandHandPossible()));
		this.jskatProperties.setProperty("showCards", String.valueOf(this.isShowCards()));
		this.jskatProperties.setProperty("cardFace", String.valueOf(this.cardFace));
		log.debug(this.jskatProperties.getProperty("cardFace"));
	}

	/**
	 * Saves the jskatProperties to a file .jskat in user home
	 * 
	 */
	public void saveJSkatProperties() {

		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(System.getProperty("user.home") + System.getProperty("file.separator")
					+ ".jskat" + System.getProperty("file.separator") + "jskat.properties");

			this.jskatProperties.store(stream, "JSkat options");

		} catch (FileNotFoundException e1) {

			log.debug("No properties file found. Saving of JSkat options failed.");
		} catch (IOException e) {
			log.debug("Saving of JSkat options failed.");
			log.debug(e);
		}
	}

	/**
	 * Getter for property language.
	 * 
	 * @return Value of property language.
	 */
	public SupportedLanguage getLanguage() {

		return this.language;
	}

	/**
	 * Setter for property language.
	 * 
	 * @param language
	 *            New value of property language.
	 */
	public void setLanguage(SupportedLanguage newLanguage) {

		this.language = newLanguage;
		this.jskatProperties.setProperty("language", String.valueOf(this.language));
	}

	/**
	 * Getter for property savePath.
	 * 
	 * @return Value of property savePath.
	 */
	public String getSavePath() {
		return this.savePath;
	}

	/**
	 * Setter for property savePath.
	 * 
	 * @param savePath
	 *            New value of property savePath.
	 */
	public void setSavePath(java.lang.String newSavePath) {

		this.savePath = newSavePath;
		this.jskatProperties.setProperty("savePath", this.savePath);
	}

	/**
	 * Getter for property trickRemoveDelayTime.
	 * 
	 * @return Value of property trickRemoveDelayTime.
	 */
	public int getTrickRemoveDelayTime() {
		return this.trickRemoveDelayTime;
	}

	/**
	 * Setter for property trickRemoveDelayTime.
	 * 
	 * @param trickRemoveDelayTime
	 *            New value of property trickRemoveDelayTime.
	 */
	public void setTrickRemoveDelayTime(int newTrickRemoveDelayTime) {

		this.trickRemoveDelayTime = newTrickRemoveDelayTime;
		this.jskatProperties.setProperty("trickRemoveDelayTime", String.valueOf(this.trickRemoveDelayTime));
	}

	/**
	 * Getter for property trickRemoveAfterClick.
	 * 
	 * @return Value of property trickRemoveAfterClick.
	 */
	public boolean isTrickRemoveAfterClick() {
		return this.trickRemoveAfterClick;
	}

	/**
	 * Setter for property trickRemoveAfterClick.
	 * 
	 * @param trickRemoveAfterClick
	 *            New value of property trickRemoveAfterClick.
	 */
	public void setTrickRemoveAfterClick(boolean newTrickRemoveAfterClick) {

		this.trickRemoveAfterClick = newTrickRemoveAfterClick;
		this.jskatProperties.setProperty("trickRemoveAfterClick", String.valueOf(this.trickRemoveAfterClick));
	}

	/**
	 * Getter for property gameShortCut.
	 * 
	 * @return Value of property gameShortCut.
	 */
	public boolean isGameShortCut() {
		return this.gameShortCut;
	}

	/**
	 * Setter for property gameShortCut.
	 * 
	 * @param gameShortCut
	 *            New value of property gameShortCut.
	 */
	public void setGameShortCut(boolean isGameShortCut) {

		this.gameShortCut = isGameShortCut;
		this.jskatProperties.setProperty("gameShortCut", String.valueOf(this.gameShortCut));
	}

	/**
	 * Getter for property cheatDebugMode.
	 * 
	 * @return Value of property cheatDebugMode.
	 */
	public boolean isCheatDebugMode() {
		return this.cheatDebugMode;
	}

	/**
	 * Setter for property cheatDebugMode.
	 * 
	 * @param cheatDebugMode
	 *            New value of property cheatDebugMode.
	 */
	public void setCheatDebugMode(boolean isCheatDebugMode) {

		this.cheatDebugMode = isCheatDebugMode;
		this.jskatProperties.setProperty("cheatDebugMode", String.valueOf(this.cheatDebugMode));
	}

	/**
	 * Getter for property firstPlayerName.
	 * 
	 * @return Value of property firstPlayerName.
	 */
	public String getFirstPlayerName() {

		return this.tableOptions.getFirstPlayerName();
	}

	private void setMaxPlayerCount(int maxPlayerCount) {

		this.tableOptions.setMaxPlayerCount(maxPlayerCount);
		this.jskatProperties.setProperty("maxPlayerCount", String.valueOf(maxPlayerCount));
	}

	/**
	 * Setter for property firstPlayerName.
	 * 
	 * @param firstPlayerName
	 *            New value of property firstPlayerName.
	 */
	public void setFirstPlayerName(java.lang.String firstPlayerName) {

		this.tableOptions.setFirstPlayerName(firstPlayerName);
		this.jskatProperties.setProperty("firstPlayerName", firstPlayerName);
	}

	/**
	 * Getter for property firstPlayerType.
	 * 
	 * @return Value of property firstPlayerType.
	 */
	public int getFirstPlayerType() {

		return this.tableOptions.getFirstPlayerType();
	}

	/**
	 * Setter for property firstPlayerType.
	 * 
	 * @param firstPlayerType
	 *            New value of property firstPlayerType.
	 */
	public void setFirstPlayerType(int firstPlayerType) {

		this.tableOptions.setFirstPlayerType(firstPlayerType);
		this.jskatProperties.setProperty("firstPlayerType", String.valueOf(firstPlayerType));
	}

	/**
	 * Getter for property secondPlayerName.
	 * 
	 * @return Value of property secondPlayerName.
	 */
	public String getSecondPlayerName() {

		return this.tableOptions.getSecondPlayerName();
	}

	/**
	 * Setter for property secondPlayerName.
	 * 
	 * @param secondPlayerName
	 *            New value of property secondPlayerName.
	 */
	public void setSecondPlayerName(java.lang.String secondPlayerName) {

		this.tableOptions.setSecondPlayerName(secondPlayerName);
		this.jskatProperties.setProperty("secondPlayerName", secondPlayerName);
	}

	/**
	 * Getter for property secondPlayerType.
	 * 
	 * @return Value of property secondPlayerType.
	 */
	public int getSecondPlayerType() {

		return this.tableOptions.getSecondPlayerType();
	}

	/**
	 * Setter for property secondPlayerType.
	 * 
	 * @param secondPlayerType
	 *            New value of property secondPlayerType.
	 */
	public void setSecondPlayerType(int secondPlayerType) {

		this.tableOptions.setSecondPlayerType(secondPlayerType);
		this.jskatProperties.setProperty("secondPlayerType", String.valueOf(secondPlayerType));
	}

	/**
	 * Getter for property thirdPlayerName.
	 * 
	 * @return Value of property thirdPlayerName.
	 */
	public String getThirdPlayerName() {

		return this.tableOptions.getThirdPlayerName();
	}

	/**
	 * Setter for property thirdPlayerName.
	 * 
	 * @param thirdPlayerName
	 *            New value of property thirdPlayerName.
	 */
	public void setThirdPlayerName(java.lang.String thirdPlayerName) {

		this.tableOptions.setThirdPlayerName(thirdPlayerName);
		this.jskatProperties.setProperty("thirdPlayerName", thirdPlayerName);
	}

	/**
	 * Getter for property thirdPlayerType.
	 * 
	 * @return Value of property thirdPlayerType.
	 */
	public int getThirdPlayerType() {

		return this.tableOptions.getThirdPlayerType();
	}

	/**
	 * Setter for property thirdPlayerType.
	 * 
	 * @param thirdPlayerType
	 *            New value of property thirdPlayerType.
	 */
	public void setThirdPlayerType(int thirdPlayerType) {

		this.tableOptions.setThirdPlayerType(thirdPlayerType);
		this.jskatProperties.setProperty("thirdPlayerType", String.valueOf(thirdPlayerType));
	}

	/**
	 * Getter for property rules.
	 * 
	 * @return Value of property rules.
	 */
	public SkatTableOptions.RuleSets getRules() {

		return this.tableOptions.getRules();
	}

	/**
	 * Setter for property rules.
	 * 
	 * @param rules
	 *            New value of property rules.
	 */
	public void setRules(SkatTableOptions.RuleSets rules) {

		this.tableOptions.setRules(rules);
		this.jskatProperties.setProperty("rules", String.valueOf(rules));
	}

	/**
	 * Getter for property playBock.
	 * 
	 * @return Value of property playBock.
	 */
	public boolean isPlayBock() {

		return this.tableOptions.isPlayBock();
	}

	/**
	 * Setter for property playBock.
	 * 
	 * @param playBock
	 *            New value of property playBock.
	 */
	public void setPlayBock(boolean playBock) {

		this.tableOptions.setPlayBock(playBock);
		this.jskatProperties.setProperty("playBock", String.valueOf(playBock));
	}

	/**
	 * Getter for property playKontra.
	 * 
	 * @return Value of property playKontra.
	 */
	public boolean isPlayContra() {

		return this.tableOptions.isPlayContra();
	}

	/**
	 * Setter for property playKontra.
	 * 
	 * @param playContra
	 *            New value of property playKontra.
	 */
	public void setPlayContra(boolean playContra) {

		this.tableOptions.setPlayContra(playContra);
		this.jskatProperties.setProperty("playContra", String.valueOf(playContra));
	}

	/**
	 * Getter for property playRamsch.
	 * 
	 * @return Value of property playRamsch.
	 */
	public boolean isPlayRamsch() {

		return this.tableOptions.isPlayRamsch();
	}

	/**
	 * Setter for property playRamsch.
	 * 
	 * @param playRamsch
	 *            New value of property playRamsch.
	 */
	public void setPlayRamsch(boolean playRamsch) {

		this.tableOptions.setPlayRamsch(playRamsch);
		this.jskatProperties.setProperty("playRamsch", String.valueOf(playRamsch));
	}

	/**
	 * Getter for property playRevolution.
	 * 
	 * @return Value of property playRevolution.
	 */
	public boolean isPlayRevolution() {

		return this.tableOptions.isPlayRevolution();
	}

	/**
	 * Setter for property playRevolution.
	 * 
	 * @param playRevolution
	 *            New value of property playRevolution.
	 */
	public void setPlayRevolution(boolean playRevolution) {

		this.tableOptions.setPlayRevolution(playRevolution);
		this.jskatProperties.setProperty("playRevolution", String.valueOf(playRevolution));
	}

	/**
	 * Getter for property bockEventLostGrand
	 * 
	 * @return Value of property bockEventLostGrand
	 */
	public boolean isBockEventLostGrand() {

		return this.tableOptions.isBockEventLostGrand();
	}

	/**
	 * Setter for property bockEventLostGrand
	 * 
	 * @param bockEventLostGrand
	 *            New value of property bockEventLostGrand
	 */
	public void setBockEventLostGrand(boolean bockEventLostGrand) {

		this.tableOptions.setBockEventLostGrand(bockEventLostGrand);
		this.jskatProperties.setProperty("bockEventLostGrand", String.valueOf(bockEventLostGrand));
	}

	/**
	 * Getter for property bockEventLostWith60
	 * 
	 * @return Value of property bockEventLostWith60
	 */
	public boolean isBockEventLostWith60() {

		return this.tableOptions.isBockEventLostWith60();
	}

	/**
	 * Setter for property bockEventLostWith60
	 * 
	 * @param bockEventLostWith60
	 *            New value of property bockEventLostWith60
	 */
	public void setBockEventLostWith60(boolean bockEventLostWith60) {

		this.tableOptions.setBockEventLostWith60(bockEventLostWith60);
		this.jskatProperties.setProperty("bockEventLostWith60", String.valueOf(bockEventLostWith60));
	}

	/**
	 * Getter for property bockEventLostAfterContra
	 * 
	 * @return Value of property bockEventLostAfterContra
	 */
	public boolean isBockEventLostAfterContra() {

		return this.tableOptions.isBockEventLostAfterContra();
	}

	/**
	 * Setter for property bockEventLostAfterContra
	 * 
	 * @param bockEventLostAfterContra
	 *            New value of property bockEventLostAfterContra
	 */
	public void setBockEventLostAfterContra(boolean bockEventLostAfterContra) {

		this.tableOptions.setBockEventLostAfterContra(bockEventLostAfterContra);
		this.jskatProperties.setProperty("bockEventLostAfterContra", String.valueOf(bockEventLostAfterContra));
	}

	/**
	 * Getter for property bockEventContraReAnnounced
	 * 
	 * @return Value of property bockEventContraReAnnounced
	 */
	public boolean isBockEventContraReAnnounced() {

		return this.tableOptions.isBockEventContraReAnnounced();
	}

	/**
	 * Setter for property bockEventContraReAnnounced
	 * 
	 * @param bockEventContraReAnnounced
	 *            New value of property bockEventContraReAnnounced
	 */
	public void setBockEventContraReAnnounced(boolean bockEventContraReAnnounced) {

		this.tableOptions.setBockEventContraReAnnounced(bockEventContraReAnnounced);
		this.jskatProperties.setProperty("bockEventContraReAnnounced", String.valueOf(bockEventContraReAnnounced));
	}

	/**
	 * Getter for property bockEventPlayerHasX00Points
	 * 
	 * @return Value of property bockEventPlayerHasX00Points
	 */
	public boolean isBockEventPlayerHasX00Points() {

		return this.tableOptions.isBockEventPlayerHasX00Points();
	}

	/**
	 * Setter for property bockEventPlayerHasX00Points
	 * 
	 * @param bockEventPlayerHasX00Points
	 *            New value of property bockEventPlayerHasX00Points
	 */
	public void setBockEventPlayerHasX00Points(boolean bockEventPlayerHasX00Points) {

		this.tableOptions.setBockEventPlayerHasX00Points(bockEventPlayerHasX00Points);
		this.jskatProperties.setProperty("bockEventPlayerHasX00Points", String.valueOf(bockEventPlayerHasX00Points));
	}

	/**
	 * Getter for property ramschSkat.
	 * 
	 * @return Value of property ramschSkat.
	 */
	public SkatTableOptions.RamschSkatOwners getRamschSkat() {

		return this.tableOptions.getRamschSkat();
	}

	/**
	 * Setter for property ramschSkat.
	 * 
	 * @param ramschSkat
	 *            New value of property ramschSkat.
	 */
	public void setRamschSkat(SkatTableOptions.RamschSkatOwners ramschSkat) {

		this.tableOptions.setRamschSkat(ramschSkat);
		this.jskatProperties.setProperty("ramschSkat", String.valueOf(ramschSkat));
	}

	/**
	 * Getter for property schieberRamsch
	 * 
	 * @return Value of property schieberRamsch
	 */
	public boolean isSchieberRamsch() {

		return this.tableOptions.isSchieberRamsch();
	}

	/**
	 * Setter for property schieberRamsch
	 * 
	 * @param schieberRamsch
	 *            New value of property schieberRamsch
	 */
	public void setSchieberRamsch(boolean schieberRamsch) {

		this.tableOptions.setSchieberRamsch(schieberRamsch);
		this.jskatProperties.setProperty("schieberRamsch", String.valueOf(schieberRamsch));
	}

	/**
	 * Getter for property schieberRamschJacksInSkat
	 * 
	 * @return Value of property schieberRamschJacksInSkat
	 */
	public boolean isSchieberRamschJacksInSkat() {

		return this.tableOptions.isSchieberRamschJacksInSkat();
	}

	/**
	 * Setter for property schieberRamschJacksInSkat
	 * 
	 * @param schieberRamschJacksInSkat
	 *            New value of property schieberRamschJacksInSkat
	 */
	public void setSchieberRamschJacksInSkat(boolean schieberRamschJacksInSkat) {

		this.tableOptions.setSchieberRamschJacksInSkat(schieberRamschJacksInSkat);
		this.jskatProperties.setProperty("schieberRamschJacksInSkat", String.valueOf(schieberRamschJacksInSkat));
	}

	/**
	 * Getter for property ramschEventNoBid
	 * 
	 * @return Value of property ramschEventNoBid
	 */
	public boolean isRamschEventNoBid() {

		return this.tableOptions.isRamschEventNoBid();
	}

	/**
	 * Setter for property ramschEventNoBid
	 * 
	 * @param ramschEventNoBid
	 *            New value of property ramschEventNoBid
	 */
	public void setRamschEventNoBid(boolean ramschEventNoBid) {

		this.tableOptions.setRamschEventNoBid(ramschEventNoBid);
		this.jskatProperties.setProperty("ramschEventNoBid", String.valueOf(ramschEventNoBid));
	}

	/**
	 * Getter for property ramschEventRamschAfterBock
	 * 
	 * @return Value of property ramschEventRamschAfterBock
	 */
	public boolean isRamschEventRamschAfterBock() {

		return this.tableOptions.isRamschEventRamschAfterBock();
	}

	/**
	 * Setter for property ramschEventRamschAfterBock
	 * 
	 * @param ramschEventRamschAfterBock
	 *            New value of property ramschEventRamschAfterBock
	 */
	public void setRamschEventRamschAfterBock(boolean ramschEventRamschAfterBock) {

		this.tableOptions.setRamschEventRamschAfterBock(ramschEventRamschAfterBock);
		this.jskatProperties.setProperty("ramschEventRamschAfterBock", String.valueOf(ramschEventRamschAfterBock));
	}

	/**
	 * Getter for property ramschGrandHandPossible
	 * 
	 * @return Value of property ramschGrandHandPossible
	 */
	public boolean isRamschGrandHandPossible() {

		return this.tableOptions.isRamschGrandHandPossible();
	}

	/**
	 * Setter for property ramschGrandHandPossible
	 * 
	 * @param ramschGrandHandPossible
	 *            New value of property ramschGrandHandPossible
	 */
	public void setRamschGrandHandPossible(boolean ramschGrandHandPossible) {

		this.tableOptions.setRamschGrandHandPossible(ramschGrandHandPossible);
		this.jskatProperties.setProperty("ramschGrandHandPossible", String.valueOf(ramschGrandHandPossible));
	}

	/**
	 * Getter for property cardFace
	 * 
	 * @return Value of property cardFace
	 */
	public CardFace getCardFace() {

		return this.cardFace;
	}

	/**
	 * Setter for property cardFace
	 * 
	 * @param cardFace
	 *            New value of property cardFace
	 */
	public void setCardFace(CardFace newCardFace) {

		if (this.cardFace != newCardFace) {

			this.cardFace = newCardFace;

			this.jskatProperties.setProperty("cardFace", String.valueOf(this.cardFace));
		}
	}

	/**
	 * Gets the current skat table options
	 * 
	 * @return The current skat table options
	 */
	public SkatTableOptions getSkatTableOptions() {

		return this.tableOptions;
	}

	/**
	 * Sets the current skat table options
	 * 
	 * @param skatTableOptions
	 *            The current skat table options
	 */
	public void setSkatTableOptions(SkatTableOptions skatTableOptions) {

		this.tableOptions = skatTableOptions;
	}

	public void setShowCards(boolean showCards) {
		this.showCards = showCards;
	}

	public boolean isShowCards() {
		return showCards;
	}
}
