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
 * Skat table options
 */
public class SkatTableOptions extends Observable {

	/** Creates a new instance of JSkatOptions */
	public SkatTableOptions() {

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

				if (property.equals("firstPlayerName")) {
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
					}
					else {
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
						setRamschSkat(RamschSkatOwners.LAST_TRICK);
					}
					else {
						setRamschSkat(RamschSkatOwners.LOSER);
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
		jskatProperties.setProperty("firstPlayerName", firstPlayerName);
		jskatProperties.setProperty("firstPlayerType", String
				.valueOf(firstPlayerType));
		jskatProperties.setProperty("secondPlayerName", secondPlayerName);
		jskatProperties.setProperty("secondPlayerType", String
				.valueOf(secondPlayerType));
		jskatProperties.setProperty("thirdPlayerName", thirdPlayerName);
		jskatProperties.setProperty("thirdPlayerType", String
				.valueOf(thirdPlayerType));
		jskatProperties.setProperty("rules", String.valueOf(rules));
		jskatProperties.setProperty("playContra", String.valueOf(playContra));
		jskatProperties.setProperty("playBock", String.valueOf(playBock));
		jskatProperties.setProperty("playRamsch", String.valueOf(playRamsch));
		jskatProperties.setProperty("playRevolution", String
				.valueOf(playRevolution));
		jskatProperties.setProperty("bockEventLostGrand", String
				.valueOf(bockEventLostGrand));
		jskatProperties.setProperty("bockEventLostWith60", String
				.valueOf(bockEventLostWith60));
		jskatProperties.setProperty("bockEventLostAfterContra", String
				.valueOf(bockEventLostAfterContra));
		jskatProperties.setProperty("bockEventContraReAnnounced", String
				.valueOf(bockEventContraReAnnounced));
		jskatProperties.setProperty("bockEventPlayerHasX00Points", String
				.valueOf(bockEventPlayerHasX00Points));
		jskatProperties.setProperty("ramschSkat", String.valueOf(ramschSkat));
		jskatProperties.setProperty("schieberRamsch", String
				.valueOf(schieberRamsch));
		jskatProperties.setProperty("schieberRamschJacksInSkat", String
				.valueOf(schieberRamschJacksInSkat));
		jskatProperties.setProperty("ramschEventNoBid", String
				.valueOf(ramschEventNoBid));
		jskatProperties.setProperty("ramschEventRamschAfterBock", String
				.valueOf(ramschEventRamschAfterBock));
		jskatProperties.setProperty("ramschGrandHandPossible", String
				.valueOf(ramschGrandHandPossible));
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
	 * @param firstPlayerName
	 *            New value of property firstPlayerName.
	 */
	public void setFirstPlayerName(java.lang.String firstPlayerName) {

		this.firstPlayerName = firstPlayerName;
		jskatProperties.setProperty("firstPlayerName", firstPlayerName);
	}

	/**
	 * Getter for property firstPlayerType.
	 * 
	 * @return Value of property firstPlayerType.
	 */
	public int getFirstPlayerType() {
		return firstPlayerType;
	}

	/**
	 * Setter for property firstPlayerType.
	 * 
	 * @param firstPlayerType
	 *            New value of property firstPlayerType.
	 */
	public void setFirstPlayerType(int firstPlayerType) {

		this.firstPlayerType = firstPlayerType;
		jskatProperties.setProperty("firstPlayerType", String
				.valueOf(firstPlayerType));
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
	 * @param secondPlayerName
	 *            New value of property secondPlayerName.
	 */
	public void setSecondPlayerName(java.lang.String secondPlayerName) {

		this.secondPlayerName = secondPlayerName;
		jskatProperties.setProperty("secondPlayerName", secondPlayerName);
	}

	/**
	 * Getter for property secondPlayerType.
	 * 
	 * @return Value of property secondPlayerType.
	 */
	public int getSecondPlayerType() {
		return secondPlayerType;
	}

	/**
	 * Setter for property secondPlayerType.
	 * 
	 * @param secondPlayerType
	 *            New value of property secondPlayerType.
	 */
	public void setSecondPlayerType(int secondPlayerType) {

		this.secondPlayerType = secondPlayerType;
		jskatProperties.setProperty("secondPlayerType", String
				.valueOf(secondPlayerType));
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
	 * @param thirdPlayerName
	 *            New value of property thirdPlayerName.
	 */
	public void setThirdPlayerName(java.lang.String thirdPlayerName) {

		this.thirdPlayerName = thirdPlayerName;
		jskatProperties.setProperty("thirdPlayerName", thirdPlayerName);
	}

	/**
	 * Getter for property thirdPlayerType.
	 * 
	 * @return Value of property thirdPlayerType.
	 */
	public int getThirdPlayerType() {
		return thirdPlayerType;
	}

	/**
	 * Setter for property thirdPlayerType.
	 * 
	 * @param thirdPlayerType
	 *            New value of property thirdPlayerType.
	 */
	public void setThirdPlayerType(int thirdPlayerType) {

		this.thirdPlayerType = thirdPlayerType;
		jskatProperties.setProperty("thirdPlayerType", String
				.valueOf(thirdPlayerType));
	}

	/**
	 * Getter for property rules.
	 * 
	 * @return Value of property rules.
	 */
	public RuleSets getRules() {
		return rules;
	}

	/**
	 * Setter for property rules.
	 * 
	 * @param rules
	 *            New value of property rules.
	 */
	public void setRules(RuleSets rules) {

		this.rules = rules;
		jskatProperties.setProperty("rules", String.valueOf(rules));
	}

	/**
	 * Getter for property playBock.
	 * 
	 * @return Value of property playBock.
	 */
	public boolean isPlayBock() {
		return playBock;
	}

	/**
	 * Setter for property playBock.
	 * 
	 * @param playBock
	 *            New value of property playBock.
	 */
	public void setPlayBock(boolean playBock) {

		this.playBock = playBock;
		jskatProperties.setProperty("playBock", String.valueOf(playBock));
	}

	/**
	 * Getter for property playKontra.
	 * 
	 * @return Value of property playKontra.
	 */
	public boolean isPlayContra() {
		return playContra;
	}

	/**
	 * Setter for property playKontra.
	 * 
	 * @param playContra
	 *            New value of property playKontra.
	 */
	public void setPlayContra(boolean playContra) {

		this.playContra = playContra;
		jskatProperties.setProperty("playContra", String.valueOf(playContra));
	}

	/**
	 * Getter for property playRamsch.
	 * 
	 * @return Value of property playRamsch.
	 */
	public boolean isPlayRamsch() {
		return playRamsch;
	}

	/**
	 * Setter for property playRamsch.
	 * 
	 * @param playRamsch
	 *            New value of property playRamsch.
	 */
	public void setPlayRamsch(boolean playRamsch) {

		this.playRamsch = playRamsch;
		jskatProperties.setProperty("playRamsch", String.valueOf(playRamsch));
	}

	/**
	 * Getter for property playRevolution.
	 * 
	 * @return Value of property playRevolution.
	 */
	public boolean isPlayRevolution() {
		return playRevolution;
	}

	/**
	 * Setter for property playRevolution.
	 * 
	 * @param playRevolution
	 *            New value of property playRevolution.
	 */
	public void setPlayRevolution(boolean playRevolution) {

		this.playRevolution = playRevolution;
		jskatProperties.setProperty("playRevolution", String
				.valueOf(playRevolution));
	}

	/**
	 * Getter for property bockEventLostGrand
	 * 
	 * @return Value of property bockEventLostGrand
	 */
	public boolean isBockEventLostGrand() {
		return bockEventLostGrand;
	}

	/**
	 * Setter for property bockEventLostGrand
	 * 
	 * @param bockEventLostGrand
	 *            New value of property bockEventLostGrand
	 */
	public void setBockEventLostGrand(boolean bockEventLostGrand) {

		this.bockEventLostGrand = bockEventLostGrand;
		jskatProperties.setProperty("bockEventLostGrand", String
				.valueOf(bockEventLostGrand));
	}

	/**
	 * Getter for property bockEventLostWith60
	 * 
	 * @return Value of property bockEventLostWith60
	 */
	public boolean isBockEventLostWith60() {
		return bockEventLostWith60;
	}

	/**
	 * Setter for property bockEventLostWith60
	 * 
	 * @param bockEventLostWith60
	 *            New value of property bockEventLostWith60
	 */
	public void setBockEventLostWith60(boolean bockEventLostWith60) {

		this.bockEventLostWith60 = bockEventLostWith60;
		jskatProperties.setProperty("bockEventLostWith60", String
				.valueOf(bockEventLostWith60));
	}

	/**
	 * Getter for property bockEventLostAfterContra
	 * 
	 * @return Value of property bockEventLostAfterContra
	 */
	public boolean isBockEventLostAfterContra() {
		return bockEventLostAfterContra;
	}

	/**
	 * Setter for property bockEventLostAfterContra
	 * 
	 * @param bockEventLostAfterContra
	 *            New value of property bockEventLostAfterContra
	 */
	public void setBockEventLostAfterContra(boolean bockEventLostAfterContra) {

		this.bockEventLostAfterContra = bockEventLostAfterContra;
		jskatProperties.setProperty("bockEventLostAfterContra", String
				.valueOf(bockEventLostAfterContra));
	}

	/**
	 * Getter for property bockEventContraReAnnounced
	 * 
	 * @return Value of property bockEventContraReAnnounced
	 */
	public boolean isBockEventContraReAnnounced() {
		return bockEventContraReAnnounced;
	}

	/**
	 * Setter for property bockEventContraReAnnounced
	 * 
	 * @param bockEventContraReAnnounced
	 *            New value of property bockEventContraReAnnounced
	 */
	public void setBockEventContraReAnnounced(boolean bockEventContraReAnnounced) {

		this.bockEventContraReAnnounced = bockEventContraReAnnounced;
		jskatProperties.setProperty("bockEventContraReAnnounced", String
				.valueOf(bockEventContraReAnnounced));
	}

	/**
	 * Getter for property bockEventPlayerHasX00Points
	 * 
	 * @return Value of property bockEventPlayerHasX00Points
	 */
	public boolean isBockEventPlayerHasX00Points() {
		return bockEventPlayerHasX00Points;
	}

	/**
	 * Setter for property bockEventPlayerHasX00Points
	 * 
	 * @param bockEventPlayerHasX00Points
	 *            New value of property bockEventPlayerHasX00Points
	 */
	public void setBockEventPlayerHasX00Points(
			boolean bockEventPlayerHasX00Points) {

		this.bockEventPlayerHasX00Points = bockEventPlayerHasX00Points;
		jskatProperties.setProperty("bockEventPlayerHasX00Points", String
				.valueOf(bockEventPlayerHasX00Points));
	}

	/**
	 * Getter for property ramschSkat.
	 * 
	 * @return Value of property ramschSkat.
	 */
	public RamschSkatOwners getRamschSkat() {
		return ramschSkat;
	}

	/**
	 * Setter for property ramschSkat.
	 * 
	 * @param ramschSkat
	 *            New value of property ramschSkat.
	 */
	public void setRamschSkat(RamschSkatOwners ramschSkat) {

		this.ramschSkat = ramschSkat;
		jskatProperties.setProperty("ramschSkat", String.valueOf(ramschSkat));
	}

	/**
	 * Getter for property schieberRamsch
	 * 
	 * @return Value of property schieberRamsch
	 */
	public boolean isSchieberRamsch() {
		return schieberRamsch;
	}

	/**
	 * Setter for property schieberRamsch
	 * 
	 * @param schieberRamsch
	 *            New value of property schieberRamsch
	 */
	public void setSchieberRamsch(boolean schieberRamsch) {

		this.schieberRamsch = schieberRamsch;
		jskatProperties.setProperty("schieberRamsch", String
				.valueOf(schieberRamsch));
	}

	/**
	 * Getter for property schieberRamschJacksInSkat
	 * 
	 * @return Value of property schieberRamschJacksInSkat
	 */
	public boolean isSchieberRamschJacksInSkat() {
		return schieberRamschJacksInSkat;
	}

	/**
	 * Setter for property schieberRamschJacksInSkat
	 * 
	 * @param schieberRamschJacksInSkat
	 *            New value of property schieberRamschJacksInSkat
	 */
	public void setSchieberRamschJacksInSkat(boolean schieberRamschJacksInSkat) {

		this.schieberRamschJacksInSkat = schieberRamschJacksInSkat;
		jskatProperties.setProperty("schieberRamschJacksInSkat", String
				.valueOf(schieberRamschJacksInSkat));
	}

	/**
	 * Getter for property ramschEventNoBid
	 * 
	 * @return Value of property ramschEventNoBid
	 */
	public boolean isRamschEventNoBid() {
		return ramschEventNoBid;
	}

	/**
	 * Setter for property ramschEventNoBid
	 * 
	 * @param ramschEventNoBid
	 *            New value of property ramschEventNoBid
	 */
	public void setRamschEventNoBid(boolean ramschEventNoBid) {

		this.ramschEventNoBid = ramschEventNoBid;
		jskatProperties.setProperty("ramschEventNoBid", String
				.valueOf(ramschEventNoBid));
	}

	/**
	 * Getter for property ramschEventRamschAfterBock
	 * 
	 * @return Value of property ramschEventRamschAfterBock
	 */
	public boolean isRamschEventRamschAfterBock() {
		return ramschEventRamschAfterBock;
	}

	/**
	 * Setter for property ramschEventRamschAfterBock
	 * 
	 * @param ramschEventRamschAfterBock
	 *            New value of property ramschEventRamschAfterBock
	 */
	public void setRamschEventRamschAfterBock(boolean ramschEventRamschAfterBock) {

		this.ramschEventRamschAfterBock = ramschEventRamschAfterBock;
		jskatProperties.setProperty("ramschEventRamschAfterBock", String
				.valueOf(ramschEventRamschAfterBock));
	}

	/**
	 * Getter for property ramschGrandHandPossible
	 * 
	 * @return Value of property ramschGrandHandPossible
	 */
	public boolean isRamschGrandHandPossible() {
		return ramschGrandHandPossible;
	}

	/**
	 * Setter for property ramschGrandHandPossible
	 * 
	 * @param ramschGrandHandPossible
	 *            New value of property ramschGrandHandPossible
	 */
	public void setRamschGrandHandPossible(boolean ramschGrandHandPossible) {

		this.ramschGrandHandPossible = ramschGrandHandPossible;
		jskatProperties.setProperty("ramschGrandHandPossible", String
				.valueOf(ramschGrandHandPossible));
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
	};

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
	};

	private Properties jskatProperties = new Properties();

	private String firstPlayerName = "Markus";

	private int firstPlayerType = 0;

	private String secondPlayerName = "Jan";

	private int secondPlayerType = 0;

	private String thirdPlayerName = "Nobody";

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
