/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.control;

import java.util.Vector;
import java.util.Observable;

import org.apache.log4j.Logger;

import jskat.data.JSkatDataModel;
import jskat.data.JSkatOptions;
import jskat.gui.options.JSkatOptionsDialog;

/**
 * Controls the whole application and all events
 */
public class JSkatMaster extends Observable {

	private static final Logger log = Logger
			.getLogger(jskat.control.JSkatMaster.class);

	/**
	 * Creates a new instance of JSkatMaster
	 * 
	 * @param dataModel
	 *            reference to the data model
	 */
	public JSkatMaster(JSkatDataModel dataModel) {

		jskatOptions = JSkatOptions.instance();

		this.dataModel = dataModel;
		this.dataModel.setJSkatMaster(this);
		this.dataModel.setJSkatOptions(jskatOptions);

		skatTables = new Vector<SkatTable>();
		currSkatTable = -1;
	}

	/** 
	 * Gets the JSkatOptions
	 *  
	 * @return Current options 
	 */
	public JSkatOptions getJSkatOptions() {

		return jskatOptions;
	}

	/**
	 * Sets the options for the current skat table
	 * 
	 * @param optionsDialog
	 */
	public void setJSkatOptions(JSkatOptionsDialog optionsDialog) {

		switch (optionsDialog.getLanguage() + 1) {
			case 1:
				jskatOptions.setLanguage(JSkatOptions.Languages.GERMAN);
				break;
			case 2:
				jskatOptions.setLanguage(JSkatOptions.Languages.ENGLISH);
				break;
			default:
				jskatOptions.setLanguage(JSkatOptions.Languages.ENGLISH);
		}
		jskatOptions.setCardFace(optionsDialog.getCardFace());
		jskatOptions.setSavePath(optionsDialog.getSavePath());
		jskatOptions.setTrickRemoveDelayTime(optionsDialog
				.getTrickRemoveDelayTime() * 1000);
		jskatOptions.setTrickRemoveAfterClick(optionsDialog
				.getTrickRemoveAfterClick());
		jskatOptions.setGameShortCut(optionsDialog.getGameShortCut());
		jskatOptions.setCheatDebugMode(optionsDialog.getCheatDebugMode());
		jskatOptions.setRules(optionsDialog.getRules());
		jskatOptions.setPlayBock(optionsDialog.getPlayBock());
		jskatOptions.setPlayContra(optionsDialog.getPlayContra());
		jskatOptions.setPlayRamsch(optionsDialog.getPlayRamsch());
		jskatOptions.setBockEventContraReAnnounced(optionsDialog
				.getBockEventContraReAnnounced());
		jskatOptions.setBockEventLostAfterContra(optionsDialog
				.getBockEventLostAfterContra());
		jskatOptions.setBockEventLostGrand(optionsDialog
				.getBockEventLostGrand());
		jskatOptions.setBockEventLostWith60(optionsDialog
				.getBockEventLostWith60());
		jskatOptions.setBockEventPlayerHasX00Points(optionsDialog
				.getBockEventPlayerHasX00Points());
		jskatOptions.setRamschEventNoBid(optionsDialog.getRamschEventNoBid());
		jskatOptions.setRamschEventRamschAfterBock(optionsDialog
				.getRamschEventRamschAfterBock());
		jskatOptions.setRamschGrandHandPossible(optionsDialog
				.getRamschGrandHandPossible());
		jskatOptions.setRamschSkat(optionsDialog.getRamschSkat());
		jskatOptions.setSchieberRamsch(optionsDialog.getSchieberRamsch());
		jskatOptions.setSchieberRamschJacksInSkat(optionsDialog
				.getSchieberRamschJacksInSkat());

		/*
		 * TODO: Throws NullPointerException when no game has been started if
		 * (!jskatOptions.isPlayRamsch() )
		 * dataModel.getCurrentRound().setRamschGamesToPlay(0);
		 */

		jskatOptions.setPlayRevolution(optionsDialog.getPlayRevolution());

		// now put the options in the dataModel for access by other methods
		// (e.g. SchiebeRamschThread)
		dataModel.setJSkatOptions(jskatOptions);
	}

	/**
	 * Exits JSkat
	 */
	public void exitJSkat() {

		// TODO Ask for saving the data before closing

		// Save the options
		jskatOptions.saveJSkatProperties();

		log.debug("Bye!");

		System.exit(0);
	}

	/**
	 * Creates a new skat table
	 *
	 */
	public void createNewSkatTable() {
		
		SkatTable newSkatTable = new SkatTable(this.dataModel);
		
		skatTables.add(newSkatTable);
		currSkatTable = skatTables.size() - 1;
		
		setChanged();
		notifyObservers(newSkatTable);
	}
	
	/**
	 * Returns the current skat table
	 * 
	 * @return The current skat table, null if there is none
	 */
	public SkatTable getCurrentSkatTable() {
		
		// 19.05.07 mjl: prevent ArrayIndexOutOfBoundsException, return NULL instead
		if(currSkatTable<0) return null;

		return (SkatTable) skatTables.get(currSkatTable);
	}

	/** Holds a reference to the data model */
	private JSkatDataModel dataModel;

	/** Holds a reference to all JSkatTables */
	private Vector<SkatTable> skatTables;

	private int currSkatTable;

	/** Holds the options of JSkat */
	private JSkatOptions jskatOptions;
}