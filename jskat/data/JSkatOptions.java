/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Properties;

/**
 * Holds all options of JSkat
 */
public class JSkatOptions extends Observable {

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

		FileInputStream stream = null;

		try {
			stream = new FileInputStream(System.getProperty("user.home")
					+ System.getProperty("file.separator") + ".jskat"
					+ System.getProperty("file.separator") + "jskat.properties");

			jskatProperties.load(stream);

			Enumeration<Object> props = jskatProperties.keys();
			String property;
			String value;

			while (props.hasMoreElements()) {

				property = (String) props.nextElement();

				value = jskatProperties.getProperty(property);

				if (property.equals("language")) {
					
					if (value.equals("GERMAN")) {
						
						setLanguage(JSkatOptions.Languages.GERMAN);
					}
					else {
						
						setLanguage(JSkatOptions.Languages.ENGLISH);
					}
				} else if (property.equals("cardFace")) {
					
					if (value.equals("GERMAN")) {
						
						setCardFace(JSkatOptions.CardFaces.GERMAN);
					}
					else if (value.equals("FRENCH")) {
						
						setCardFace(JSkatOptions.CardFaces.FRENCH);
					}
					else {
						
						setCardFace(JSkatOptions.CardFaces.TOURNAMENT);
					}
				} else if (property.equals("savePath")) {
					setSavePath(value);
				} else if (property.equals("trickRemoveDelayTime")) {
					setTrickRemoveDelayTime(Integer.parseInt(value));
				} else if (property.equals("trickRemoveAfterClick")) {
					setTrickRemoveAfterClick(Boolean.valueOf(value)
							.booleanValue());
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
					}
					else {
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
					setBockEventLostWith60(Boolean.valueOf(value)
							.booleanValue());
				} else if (property.equals("bockEventLostAfterContra")) {
					setBockEventLostAfterContra(Boolean.valueOf(value)
							.booleanValue());
				} else if (property.equals("bockEventContraReAnnounced")) {
					setBockEventContraReAnnounced(Boolean.valueOf(value)
							.booleanValue());
				} else if (property.equals("bockEventPlayerHasX00Points")) {
					setBockEventPlayerHasX00Points(Boolean.valueOf(value)
							.booleanValue());
				} else if (property.equals("ramschSkat")) {
					if (value.equals("LAST_TRICK")) {
						setRamschSkat(SkatTableOptions.RamschSkatOwners.LAST_TRICK);
					}
					else {
						setRamschSkat(SkatTableOptions.RamschSkatOwners.LOSER);
					}
				} else if (property.equals("schieberRamsch")) {
					setSchieberRamsch(Boolean.valueOf(value).booleanValue());
				} else if (property.equals("schieberRamschJacksInSkat")) {
					setSchieberRamschJacksInSkat(Boolean.valueOf(value)
							.booleanValue());
				} else if (property.equals("ramschEventNoBid")) {
					setRamschEventNoBid(Boolean.valueOf(value).booleanValue());
				} else if (property.equals("ramschEventRamschAfterBock")) {
					setRamschEventRamschAfterBock(Boolean.valueOf(value)
							.booleanValue());
				} else if (property.equals("ramschGrandHandPossible")) {
					setRamschGrandHandPossible(Boolean.valueOf(value)
							.booleanValue());
				}
			}

		} catch (FileNotFoundException e) {

			System.err
					.println("No properties file found. Using standard values.");

			File dir = new File(System.getProperty("user.home")
					+ System.getProperty("file.separator") + ".jskat");
			dir.mkdir();
			File file = new File(System.getProperty("user.home")
					+ System.getProperty("file.separator") + ".jskat"
					+ System.getProperty("file.separator") + "jskat.properties");
			try {
				file.createNewFile();

				System.out
						.println("Property file jskat.properties created at <user_home>/.jskat.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			setStandardProperties();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets the standard properties
	 * 
	 */
	private void setStandardProperties() {

		// use standard values for the options
		jskatProperties.setProperty("language", String.valueOf(language));
		jskatProperties.setProperty("savePath", savePath);
		jskatProperties.setProperty("trickRemoveDelayTime", String
				.valueOf(trickRemoveDelayTime));
		jskatProperties.setProperty("trickRemoveAfterClick", String
				.valueOf(trickRemoveAfterClick));
		jskatProperties.setProperty("gameShortCut", String
				.valueOf(gameShortCut));
		jskatProperties.setProperty("cheatDebugMode", String
				.valueOf(cheatDebugMode));
		jskatProperties.setProperty("firstPlayerName", skatTableOptions
				.getFirstPlayerName());
		jskatProperties.setProperty("firstPlayerType", String
				.valueOf(skatTableOptions.getFirstPlayerType()));
		jskatProperties.setProperty("secondPlayerName", skatTableOptions
				.getSecondPlayerName());
		jskatProperties.setProperty("secondPlayerType", String
				.valueOf(skatTableOptions.getSecondPlayerType()));
		jskatProperties.setProperty("thirdPlayerName", skatTableOptions
				.getThirdPlayerName());
		jskatProperties.setProperty("thirdPlayerType", String
				.valueOf(skatTableOptions.getThirdPlayerType()));
		jskatProperties.setProperty("rules", String.valueOf(skatTableOptions
				.getRules()));
		jskatProperties.setProperty("playContra", String
				.valueOf(skatTableOptions.isPlayContra()));
		jskatProperties.setProperty("playBock", String.valueOf(skatTableOptions
				.isPlayBock()));
		jskatProperties.setProperty("playRamsch", String
				.valueOf(skatTableOptions.isPlayRamsch()));
		jskatProperties.setProperty("playRevolution", String
				.valueOf(skatTableOptions.isPlayRevolution()));
		jskatProperties.setProperty("bockEventLostGrand", String
				.valueOf(skatTableOptions.isBockEventLostGrand()));
		jskatProperties.setProperty("bockEventLostWith60", String
				.valueOf(skatTableOptions.isBockEventLostWith60()));
		jskatProperties.setProperty("bockEventLostAfterContra", String
				.valueOf(skatTableOptions.isBockEventLostAfterContra()));
		jskatProperties.setProperty("bockEventContraReAnnounced", String
				.valueOf(skatTableOptions.isBockEventContraReAnnounced()));
		jskatProperties.setProperty("bockEventPlayerHasX00Points", String
				.valueOf(skatTableOptions.isBockEventPlayerHasX00Points()));
		jskatProperties.setProperty("ramschSkat", String
				.valueOf(skatTableOptions.getRamschSkat()));
		jskatProperties.setProperty("schieberRamsch", String
				.valueOf(skatTableOptions.isSchieberRamsch()));
		jskatProperties.setProperty("schieberRamschJacksInSkat", String
				.valueOf(skatTableOptions.isSchieberRamschJacksInSkat()));
		jskatProperties.setProperty("ramschEventNoBid", String
				.valueOf(skatTableOptions.isRamschEventNoBid()));
		jskatProperties.setProperty("ramschEventRamschAfterBock", String
				.valueOf(skatTableOptions.isRamschEventRamschAfterBock()));
		jskatProperties.setProperty("ramschGrandHandPossible", String
				.valueOf(skatTableOptions.isRamschGrandHandPossible()));
		jskatProperties.setProperty("cardFace", String.valueOf(cardFace));
	}

	/**
	 * Saves the jskatProperties to a file .jskat in user home
	 * 
	 */
	public void saveJSkatProperties() {

		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(System.getProperty("user.home")
					+ System.getProperty("file.separator") + ".jskat"
					+ System.getProperty("file.separator") + "jskat.properties");

			jskatProperties.store(stream, "JSkat options");

		} catch (FileNotFoundException e1) {

			System.err
					.println("No properties file found. Saving of JSkat options failed.");
		} catch (IOException e) {
			System.err.println("Saving of JSkat options failed.");
			System.err.println(e);
		}
	}

	/**
	 * Getter for property language.
	 * 
	 * @return Value of property language.
	 */
	public Languages getLanguage() {
		
		return language;
	}

	/**
	 * Setter for property language.
	 * 
	 * @param language
	 *            New value of property language.
	 */
	public void setLanguage(Languages language) {

		this.language = language;
		jskatProperties.setProperty("language", String.valueOf(language));
	}

	/**
	 * Getter for property savePath.
	 * 
	 * @return Value of property savePath.
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * Setter for property savePath.
	 * 
	 * @param savePath
	 *            New value of property savePath.
	 */
	public void setSavePath(java.lang.String savePath) {

		this.savePath = savePath;
		jskatProperties.setProperty("savePath", savePath);
	}

	/**
	 * Getter for property trickRemoveDelayTime.
	 * 
	 * @return Value of property trickRemoveDelayTime.
	 */
	public int getTrickRemoveDelayTime() {
		return trickRemoveDelayTime;
	}

	/**
	 * Setter for property trickRemoveDelayTime.
	 * 
	 * @param trickRemoveDelayTime
	 *            New value of property trickRemoveDelayTime.
	 */
	public void setTrickRemoveDelayTime(int trickRemoveDelayTime) {

		this.trickRemoveDelayTime = trickRemoveDelayTime;
		jskatProperties.setProperty("trickRemoveDelayTime", String
				.valueOf(trickRemoveDelayTime));
	}

	/**
	 * Getter for property trickRemoveAfterClick.
	 * 
	 * @return Value of property trickRemoveAfterClick.
	 */
	public boolean isTrickRemoveAfterClick() {
		return trickRemoveAfterClick;
	}

	/**
	 * Setter for property trickRemoveAfterClick.
	 * 
	 * @param trickRemoveAfterClick
	 *            New value of property trickRemoveAfterClick.
	 */
	public void setTrickRemoveAfterClick(boolean trickRemoveAfterClick) {

		this.trickRemoveAfterClick = trickRemoveAfterClick;
		jskatProperties.setProperty("trickRemoveAfterClick", String
				.valueOf(trickRemoveAfterClick));
	}

	/**
	 * Getter for property gameShortCut.
	 * 
	 * @return Value of property gameShortCut.
	 */
	public boolean isGameShortCut() {
		return gameShortCut;
	}

	/**
	 * Setter for property gameShortCut.
	 * 
	 * @param gameShortCut
	 *            New value of property gameShortCut.
	 */
	public void setGameShortCut(boolean gameShortCut) {

		this.gameShortCut = gameShortCut;
		jskatProperties.setProperty("gameShortCut", String
				.valueOf(gameShortCut));
	}

	/**
	 * Getter for property cheatDebugMode.
	 * 
	 * @return Value of property cheatDebugMode.
	 */
	public boolean isCheatDebugMode() {
		return cheatDebugMode;
	}

	/**
	 * Setter for property cheatDebugMode.
	 * 
	 * @param cheatDebugMode
	 *            New value of property cheatDebugMode.
	 */
	public void setCheatDebugMode(boolean cheatDebugMode) {

		this.cheatDebugMode = cheatDebugMode;
		jskatProperties.setProperty("cheatDebugMode", String
				.valueOf(cheatDebugMode));
	}

	/**
	 * Getter for property firstPlayerName.
	 * 
	 * @return Value of property firstPlayerName.
	 */
	public String getFirstPlayerName() {

		return skatTableOptions.getFirstPlayerName();
	}

	/**
	 * Setter for property firstPlayerName.
	 * 
	 * @param firstPlayerName
	 *            New value of property firstPlayerName.
	 */
	public void setFirstPlayerName(java.lang.String firstPlayerName) {

		skatTableOptions.setFirstPlayerName(firstPlayerName);
		jskatProperties.setProperty("firstPlayerName", firstPlayerName);
	}

	/**
	 * Getter for property firstPlayerType.
	 * 
	 * @return Value of property firstPlayerType.
	 */
	public int getFirstPlayerType() {

		return skatTableOptions.getFirstPlayerType();
	}

	/**
	 * Setter for property firstPlayerType.
	 * 
	 * @param firstPlayerType
	 *            New value of property firstPlayerType.
	 */
	public void setFirstPlayerType(int firstPlayerType) {

		skatTableOptions.setFirstPlayerType(firstPlayerType);
		jskatProperties.setProperty("firstPlayerType", String
				.valueOf(firstPlayerType));
	}

	/**
	 * Getter for property secondPlayerName.
	 * 
	 * @return Value of property secondPlayerName.
	 */
	public String getSecondPlayerName() {

		return skatTableOptions.getSecondPlayerName();
	}

	/**
	 * Setter for property secondPlayerName.
	 * 
	 * @param secondPlayerName
	 *            New value of property secondPlayerName.
	 */
	public void setSecondPlayerName(java.lang.String secondPlayerName) {

		skatTableOptions.setSecondPlayerName(secondPlayerName);
		jskatProperties.setProperty("secondPlayerName", secondPlayerName);
	}

	/**
	 * Getter for property secondPlayerType.
	 * 
	 * @return Value of property secondPlayerType.
	 */
	public int getSecondPlayerType() {

		return skatTableOptions.getSecondPlayerType();
	}

	/**
	 * Setter for property secondPlayerType.
	 * 
	 * @param secondPlayerType
	 *            New value of property secondPlayerType.
	 */
	public void setSecondPlayerType(int secondPlayerType) {

		skatTableOptions.setSecondPlayerType(secondPlayerType);
		jskatProperties.setProperty("secondPlayerType", String
				.valueOf(secondPlayerType));
	}

	/**
	 * Getter for property thirdPlayerName.
	 * 
	 * @return Value of property thirdPlayerName.
	 */
	public String getThirdPlayerName() {

		return skatTableOptions.getThirdPlayerName();
	}

	/**
	 * Setter for property thirdPlayerName.
	 * 
	 * @param thirdPlayerName
	 *            New value of property thirdPlayerName.
	 */
	public void setThirdPlayerName(java.lang.String thirdPlayerName) {

		skatTableOptions.setThirdPlayerName(thirdPlayerName);
		jskatProperties.setProperty("thirdPlayerName", thirdPlayerName);
	}

	/**
	 * Getter for property thirdPlayerType.
	 * 
	 * @return Value of property thirdPlayerType.
	 */
	public int getThirdPlayerType() {

		return skatTableOptions.getThirdPlayerType();
	}

	/**
	 * Setter for property thirdPlayerType.
	 * 
	 * @param thirdPlayerType
	 *            New value of property thirdPlayerType.
	 */
	public void setThirdPlayerType(int thirdPlayerType) {

		skatTableOptions.setThirdPlayerType(thirdPlayerType);
		jskatProperties.setProperty("thirdPlayerType", String
				.valueOf(thirdPlayerType));
	}

	/**
	 * Getter for property rules.
	 * 
	 * @return Value of property rules.
	 */
	public SkatTableOptions.RuleSets getRules() {

		return skatTableOptions.getRules();
	}

	/**
	 * Setter for property rules.
	 * 
	 * @param rules
	 *            New value of property rules.
	 */
	public void setRules(SkatTableOptions.RuleSets rules) {

		skatTableOptions.setRules(rules);
		jskatProperties.setProperty("rules", String.valueOf(rules));
	}

	/**
	 * Getter for property playBock.
	 * 
	 * @return Value of property playBock.
	 */
	public boolean isPlayBock() {

		return skatTableOptions.isPlayBock();
	}

	/**
	 * Setter for property playBock.
	 * 
	 * @param playBock
	 *            New value of property playBock.
	 */
	public void setPlayBock(boolean playBock) {

		skatTableOptions.setPlayBock(playBock);
		jskatProperties.setProperty("playBock", String.valueOf(playBock));
	}

	/**
	 * Getter for property playKontra.
	 * 
	 * @return Value of property playKontra.
	 */
	public boolean isPlayContra() {

		return skatTableOptions.isPlayContra();
	}

	/**
	 * Setter for property playKontra.
	 * 
	 * @param playContra
	 *            New value of property playKontra.
	 */
	public void setPlayContra(boolean playContra) {

		skatTableOptions.setPlayContra(playContra);
		jskatProperties.setProperty("playContra", String.valueOf(playContra));
	}

	/**
	 * Getter for property playRamsch.
	 * 
	 * @return Value of property playRamsch.
	 */
	public boolean isPlayRamsch() {

		return skatTableOptions.isPlayRamsch();
	}

	/**
	 * Setter for property playRamsch.
	 * 
	 * @param playRamsch
	 *            New value of property playRamsch.
	 */
	public void setPlayRamsch(boolean playRamsch) {

		skatTableOptions.setPlayRamsch(playRamsch);
		jskatProperties.setProperty("playRamsch", String.valueOf(playRamsch));
	}

	/**
	 * Getter for property playRevolution.
	 * 
	 * @return Value of property playRevolution.
	 */
	public boolean isPlayRevolution() {

		return skatTableOptions.isPlayRevolution();
	}

	/**
	 * Setter for property playRevolution.
	 * 
	 * @param playRevolution
	 *            New value of property playRevolution.
	 */
	public void setPlayRevolution(boolean playRevolution) {

		skatTableOptions.setPlayRevolution(playRevolution);
		jskatProperties.setProperty("playRevolution", String
				.valueOf(playRevolution));
	}

	/**
	 * Getter for property bockEventLostGrand
	 * 
	 * @return Value of property bockEventLostGrand
	 */
	public boolean isBockEventLostGrand() {

		return skatTableOptions.isBockEventLostGrand();
	}

	/**
	 * Setter for property bockEventLostGrand
	 * 
	 * @param bockEventLostGrand
	 *            New value of property bockEventLostGrand
	 */
	public void setBockEventLostGrand(boolean bockEventLostGrand) {

		skatTableOptions.setBockEventLostGrand(bockEventLostGrand);
		jskatProperties.setProperty("bockEventLostGrand", String
				.valueOf(bockEventLostGrand));
	}

	/**
	 * Getter for property bockEventLostWith60
	 * 
	 * @return Value of property bockEventLostWith60
	 */
	public boolean isBockEventLostWith60() {

		return skatTableOptions.isBockEventLostWith60();
	}

	/**
	 * Setter for property bockEventLostWith60
	 * 
	 * @param bockEventLostWith60
	 *            New value of property bockEventLostWith60
	 */
	public void setBockEventLostWith60(boolean bockEventLostWith60) {

		skatTableOptions.setBockEventLostWith60(bockEventLostWith60);
		jskatProperties.setProperty("bockEventLostWith60", String
				.valueOf(bockEventLostWith60));
	}

	/**
	 * Getter for property bockEventLostAfterContra
	 * 
	 * @return Value of property bockEventLostAfterContra
	 */
	public boolean isBockEventLostAfterContra() {

		return skatTableOptions.isBockEventLostAfterContra();
	}

	/**
	 * Setter for property bockEventLostAfterContra
	 * 
	 * @param bockEventLostAfterContra
	 *            New value of property bockEventLostAfterContra
	 */
	public void setBockEventLostAfterContra(boolean bockEventLostAfterContra) {

		skatTableOptions.setBockEventLostAfterContra(bockEventLostAfterContra);
		jskatProperties.setProperty("bockEventLostAfterContra", String
				.valueOf(bockEventLostAfterContra));
	}

	/**
	 * Getter for property bockEventContraReAnnounced
	 * 
	 * @return Value of property bockEventContraReAnnounced
	 */
	public boolean isBockEventContraReAnnounced() {

		return skatTableOptions.isBockEventContraReAnnounced();
	}

	/**
	 * Setter for property bockEventContraReAnnounced
	 * 
	 * @param bockEventContraReAnnounced
	 *            New value of property bockEventContraReAnnounced
	 */
	public void setBockEventContraReAnnounced(boolean bockEventContraReAnnounced) {

		skatTableOptions
				.setBockEventContraReAnnounced(bockEventContraReAnnounced);
		jskatProperties.setProperty("bockEventContraReAnnounced", String
				.valueOf(bockEventContraReAnnounced));
	}

	/**
	 * Getter for property bockEventPlayerHasX00Points
	 * 
	 * @return Value of property bockEventPlayerHasX00Points
	 */
	public boolean isBockEventPlayerHasX00Points() {

		return skatTableOptions.isBockEventPlayerHasX00Points();
	}

	/**
	 * Setter for property bockEventPlayerHasX00Points
	 * 
	 * @param bockEventPlayerHasX00Points
	 *            New value of property bockEventPlayerHasX00Points
	 */
	public void setBockEventPlayerHasX00Points(
			boolean bockEventPlayerHasX00Points) {

		skatTableOptions
				.setBockEventPlayerHasX00Points(bockEventPlayerHasX00Points);
		jskatProperties.setProperty("bockEventPlayerHasX00Points", String
				.valueOf(bockEventPlayerHasX00Points));
	}

	/**
	 * Getter for property ramschSkat.
	 * 
	 * @return Value of property ramschSkat.
	 */
	public SkatTableOptions.RamschSkatOwners getRamschSkat() {

		return skatTableOptions.getRamschSkat();
	}

	/**
	 * Setter for property ramschSkat.
	 * 
	 * @param ramschSkat
	 *            New value of property ramschSkat.
	 */
	public void setRamschSkat(SkatTableOptions.RamschSkatOwners ramschSkat) {

		skatTableOptions.setRamschSkat(ramschSkat);
		jskatProperties.setProperty("ramschSkat", String.valueOf(ramschSkat));
	}

	/**
	 * Getter for property schieberRamsch
	 * 
	 * @return Value of property schieberRamsch
	 */
	public boolean isSchieberRamsch() {

		return skatTableOptions.isSchieberRamsch();
	}

	/**
	 * Setter for property schieberRamsch
	 * 
	 * @param schieberRamsch
	 *            New value of property schieberRamsch
	 */
	public void setSchieberRamsch(boolean schieberRamsch) {

		skatTableOptions.setSchieberRamsch(schieberRamsch);
		jskatProperties.setProperty("schieberRamsch", String
				.valueOf(schieberRamsch));
	}

	/**
	 * Getter for property schieberRamschJacksInSkat
	 * 
	 * @return Value of property schieberRamschJacksInSkat
	 */
	public boolean isSchieberRamschJacksInSkat() {

		return skatTableOptions.isSchieberRamschJacksInSkat();
	}

	/**
	 * Setter for property schieberRamschJacksInSkat
	 * 
	 * @param schieberRamschJacksInSkat
	 *            New value of property schieberRamschJacksInSkat
	 */
	public void setSchieberRamschJacksInSkat(boolean schieberRamschJacksInSkat) {

		skatTableOptions
				.setSchieberRamschJacksInSkat(schieberRamschJacksInSkat);
		jskatProperties.setProperty("schieberRamschJacksInSkat", String
				.valueOf(schieberRamschJacksInSkat));
	}

	/**
	 * Getter for property ramschEventNoBid
	 * 
	 * @return Value of property ramschEventNoBid
	 */
	public boolean isRamschEventNoBid() {

		return skatTableOptions.isRamschEventNoBid();
	}

	/**
	 * Setter for property ramschEventNoBid
	 * 
	 * @param ramschEventNoBid
	 *            New value of property ramschEventNoBid
	 */
	public void setRamschEventNoBid(boolean ramschEventNoBid) {

		skatTableOptions.setRamschEventNoBid(ramschEventNoBid);
		jskatProperties.setProperty("ramschEventNoBid", String
				.valueOf(ramschEventNoBid));
	}

	/**
	 * Getter for property ramschEventRamschAfterBock
	 * 
	 * @return Value of property ramschEventRamschAfterBock
	 */
	public boolean isRamschEventRamschAfterBock() {

		return skatTableOptions.isRamschEventRamschAfterBock();
	}

	/**
	 * Setter for property ramschEventRamschAfterBock
	 * 
	 * @param ramschEventRamschAfterBock
	 *            New value of property ramschEventRamschAfterBock
	 */
	public void setRamschEventRamschAfterBock(boolean ramschEventRamschAfterBock) {

		skatTableOptions
				.setRamschEventRamschAfterBock(ramschEventRamschAfterBock);
		jskatProperties.setProperty("ramschEventRamschAfterBock", String
				.valueOf(ramschEventRamschAfterBock));
	}

	/**
	 * Getter for property ramschGrandHandPossible
	 * 
	 * @return Value of property ramschGrandHandPossible
	 */
	public boolean isRamschGrandHandPossible() {

		return skatTableOptions.isRamschGrandHandPossible();
	}

	/**
	 * Setter for property ramschGrandHandPossible
	 * 
	 * @param ramschGrandHandPossible
	 *            New value of property ramschGrandHandPossible
	 */
	public void setRamschGrandHandPossible(boolean ramschGrandHandPossible) {

		skatTableOptions.setRamschGrandHandPossible(ramschGrandHandPossible);
		jskatProperties.setProperty("ramschGrandHandPossible", String
				.valueOf(ramschGrandHandPossible));
	}

	/**
	 * Getter for property cardFace
	 * 
	 * @return Value of property cardFace
	 */
	public CardFaces getCardFace() {

		return cardFace;
	}

	/**
	 * Setter for property cardFace
	 * 
	 * @param cardFace
	 *            New value of property cardFace
	 */
	public void setCardFace(CardFaces cardFace) {

		if (this.cardFace != cardFace) {

			this.cardFace = cardFace;

			jskatProperties.setProperty("cardFace", String.valueOf(cardFace));
		}
		
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the current skat table options
	 * 
	 * @return The current skat table options
	 */
	public SkatTableOptions getSkatTableOptions() {
		
		return skatTableOptions;
	}
	
	/**
	 * Sets the current skat table options
	 * 
	 * @param skatTableOptions The current skat table options
	 */
	public void setSkatTableOptions(SkatTableOptions skatTableOptions) {
		
		this.skatTableOptions = skatTableOptions;
	}
	
	static private JSkatOptions optionsInstance = null;
	
	/**
	 * Languages supported by JSkat
	 */
	public enum Languages {
		/**
		 * German
		 */
		GERMAN,
		/**
		 * English
		 */
		ENGLISH
	};

	/**
	 * Card faces supported by JSkat
	 */
	public enum CardFaces {
		/**
		 * French (Clubs, Spades, Hearts, Diamonds)
		 */
		FRENCH,
		/**
		 * German (Eichel, Grün, Herz, Schellen)
		 */
		GERMAN,
		/**
		 * Tournament (Clubs (black), Spades (green), Hearts (red), Diamonds (orange))
		 */
		TOURNAMENT
	}

	private Properties jskatProperties = new Properties();

	private Languages language = Languages.GERMAN;

	private String savePath = "";

	private int trickRemoveDelayTime = 1000;

	private boolean trickRemoveAfterClick = false;

	private boolean gameShortCut = false;

	private boolean cheatDebugMode = false;

	private CardFaces cardFace = CardFaces.FRENCH;

	private SkatTableOptions skatTableOptions = new SkatTableOptions();
}
