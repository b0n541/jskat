/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.control;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.JSkatPlayer;
import de.jskat.ai.mjl.AIPlayerMJL;
import de.jskat.ai.nn.AIPlayerNN;
import de.jskat.ai.nn.data.SkatNetworks;
import de.jskat.ai.nn.train.NNTrainer;
import de.jskat.ai.rnd.AIPlayerRND;
import de.jskat.control.iss.ISSController;
import de.jskat.data.JSkatApplicationData;
import de.jskat.data.JSkatOptions;
import de.jskat.gui.JSkatView;
import de.jskat.gui.human.HumanPlayer;
import de.jskat.util.Card;
import de.jskat.util.GameType;

/**
 * Controls everything in JSkat
 */
public class JSkatMaster {

	private static Log log = LogFactory.getLog(JSkatMaster.class);

	private JSkatOptions options;
	private JSkatApplicationData data;
	private JSkatView view;
	
	private ISSController issControl;
	
	/**
	 * Constructor
	 * 
	 * @param jskatOptions JSkat options
	 */
	public JSkatMaster(JSkatOptions jskatOptions) {
		
		this.data = new JSkatApplicationData(jskatOptions);
		this.options = jskatOptions;
		
		this.issControl = new ISSController(this, this.view);
	}

	/**
	 * Creates a new skat table
	 * 
	 */
	public void createTable() {
		
		// TODO check whether a connection to ISS is established
		// TODO ask whether a local or a remote tabel should be created
		
		String tableName = this.view.getNewTableName();
		if(tableName==null) {
			log.debug("Create table was cancelled..."); //$NON-NLS-1$
			return;
		}
		
		SkatTable table = new SkatTable(this.data.getTableOptions());
		table.setName(tableName);
		this.data.addLocalSkatTable(table);
		
		this.view.createSkatTablePanel(table.getName());
		this.data.setActiveTable(table.getName());
		
		table.setView(this.view);

	}
	
	/**
	 * Starts a new skat series
	 */
	public void startSeries() {
		
		log.debug(this.data.getActiveTable());
		
		this.view.showStartSkatSeriesDialog();
	}
	
	/**
	 * Starts a new series with given parameters
	 * @param playerNames Player names
	 * @param numberOfRounds Number of rounds to be played
	 * @param unlimited TRUE, if unlimited rounds should be played
	 */
	public void startSeries(ArrayList<String> playerNames, int numberOfRounds, boolean unlimited) {
		
		log.debug(this.data.getActiveTable());
		
		SkatTable table = this.data.getSkatTable(this.data.getActiveTable());
		
		table.removePlayers();
		
		for (String player : playerNames) {

			table.placePlayer(getPlayerInstance(player));
		}
		
		table.startSkatSeries(numberOfRounds);
	}
	
	private JSkatPlayer getPlayerInstance(String playerName) {

		// FIXME dirty hack
		JSkatPlayer player = null;
		
		if ("Random Player".equals(playerName)) {
			
			player = new AIPlayerRND();
		}
		else if ("Neuronal Network Player".equals(playerName)) {
			
			player = new AIPlayerNN();
			((AIPlayerNN) player).setIsLearning(true);
		}
		else if ("Algorithmic Player".equals(playerName)) {
			
			player = new AIPlayerMJL();
		}
		else if ("Human Player".equals(playerName)) {
			
			HumanPlayer human = new HumanPlayer();
			human.setView(this.view);
			player = human;
		}
		
		return player;
	}
	
	/**
	 * Pauses a skat series at a table
	 * 
	 * @param tableName Table name
	 */
	public void pauseSkatSeries(String tableName) {
		
		SkatTable table = this.data.getSkatTable(tableName);
		
		if (table.isSeriesRunning()) {
			
			table.pauseSkatSeries();
		}
	}
	
	/**
	 * Starts a new skat series
	 */
	public void resumeSkatSeries() {
		
		log.debug(this.data.getActiveTable());
		
		resumeSkatSeries(this.data.getActiveTable());
	}
	
	/**
	 * Resumes a skat series at a table
	 * 
	 * @param tableName Table name
	 */
	public void resumeSkatSeries(String tableName) {
		
		SkatTable table = this.data.getSkatTable(tableName);
		
		if (table.isSeriesRunning()) {
		
			table.resumeSkatSeries();
		}
	}
	
	/**
	 * Pauses a skat game at a table
	 * 
	 * @param tableName Table name
	 */
	public void pauseSkatGame(String tableName) {
		
		SkatTable table = this.data.getSkatTable(tableName);
		
		if (table.isSeriesRunning()) {
			
			table.pauseSkatGame();
		}
	}
	
	/**
	 * Resumes a skat game at a table
	 * 
	 * @param tableName Table name
	 */
	public void resumeSkatGame(String tableName) {
		
		SkatTable table = this.data.getSkatTable(tableName);
		
		if (table.isSeriesRunning()) {
		
			table.resumeSkatGame();
		}
	}
	
	/**
	 * Checks whether a skat game is waiting
	 * 
	 * @param tableName Table name
	 * @return TRUE if the game is waiting
	 */
	public boolean isSkatGameWaiting(String tableName) {
		
		boolean result = false;
		
		SkatTable table = this.data.getSkatTable(tableName);
		
		if (table.isSeriesRunning()) {
		
			result = table.isSkatGameWaiting();
		}
		
		return result;
	}
	
	/**
	 * Checks whether a skat series is waiting
	 * 
	 * @param tableName Table name
	 * @return TRUE if the series is waiting
	 */
	public boolean isSkatSeriesWaiting(String tableName) {
		
		boolean result = false;
		
		SkatTable table = this.data.getSkatTable(tableName);
		
		if (table.isSeriesRunning()) {
		
			result = table.isSkatSeriesWaiting();
		}
		
		return result;
	}

	/**
	 * Places a skat player on a table
	 * 
	 * @param tableName Table ID
	 * @param player Skat player
	 * @return TRUE if the placing was successful
	 */
	public synchronized boolean placePlayer(String tableName, JSkatPlayer player) {
		
		boolean result = false;
		
		SkatTable table = this.data.getSkatTable(tableName);
			
		if (!table.isSeriesRunning()) {
			
			if (table.getPlayerCount() < table.getMaxPlayerCount()) {
				
				result = table.placePlayer(player);
			}
		}
		
		return result;
	}

	/**
	 * Sets the view (for MVC)
	 * 
	 * @param newView View
	 */
	public void setView(JSkatView newView) {
		
		this.view = newView;
		
		// TODO check whether this is a dirty hack or not
		this.issControl = new ISSController(this, this.view);
	}

	/**
	 * Exits JSkat
	 */
	public void exitJSkat() {
		
		if (this.view.showExitDialog() == JOptionPane.YES_OPTION) {
			
			this.options.saveJSkatProperties();
			
			System.exit(0);
		}
	}

	/**
	 * Shows the about message box
	 */
	public void showAboutMessage() {
		
		this.view.showAboutMessage();
	}

	/**
	 * Set the active table
	 * 
	 * @param tableName Table name
	 */
	public void setActiveTable(String tableName) {
		
		this.data.setActiveTable(tableName);
	}

	/**
	 * Trains the neural networks
	 */
	public void trainNeuralNetworks() {
		
		NNTrainer nullTrainer = new NNTrainer();
		nullTrainer.setGameType(GameType.NULL);
		nullTrainer.start();
		NNTrainer grandTrainer = new NNTrainer();
		grandTrainer.setGameType(GameType.GRAND);
		grandTrainer.start();
		NNTrainer clubsTrainer = new NNTrainer();
		clubsTrainer.setGameType(GameType.CLUBS);
		clubsTrainer.start();
		NNTrainer spadesTrainer = new NNTrainer();
		spadesTrainer.setGameType(GameType.SPADES);
		spadesTrainer.start();
		NNTrainer heartsTrainer = new NNTrainer();
		heartsTrainer.setGameType(GameType.HEARTS);
		heartsTrainer.start();
		NNTrainer diamondsTrainer = new NNTrainer();
		diamondsTrainer.setGameType(GameType.DIAMONDS);
		diamondsTrainer.start();
	}

	/**
	 * Loads the weigths for the neural networks
	 */
	public void loadNeuralNetworks() {
		
		SkatNetworks.loadNetworks(System.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".jskat"));  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Saves the weigths for the neural networks
	 */
	public void saveNeuralNetworks() {
		
		SkatNetworks.saveNetworks(System.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".jskat")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Shows the help dialog
	 */
	public void showHelp() {
		
		this.view.showHelpDialog();
	}

	/**
	 * Shows the license dialog
	 */
	public void showLicense() {
		
		this.view.showLicenseDialog();
	}

	/**
	 * Triggers the human player interface to stop waiting
	 * 
	 * @param event Action event
	 */
	public void triggerHuman(ActionEvent event) {
		
		SkatTable table = this.data.getSkatTable(this.data.getActiveTable());
		
		table.getHuman().actionPerformed(event);
	}

	/**
	 * Takes a card from the skat on the active skat table
	 * 
	 * @param e Event
	 */
	public void takeCardFromSkat(ActionEvent e) {
		
		if (!(e.getSource() instanceof Card)) {
			
			throw new IllegalArgumentException();
		}
		
		this.view.takeCardFromSkat(this.data.getActiveTable(), (Card) e.getSource());
	}

	/**
	 * Put a card into the skat on the active skat table
	 * 
	 * @param e
	 */
	public void putCardIntoSkat(ActionEvent e) {
		
		if (!(e.getSource() instanceof Card)) {
			
			throw new IllegalArgumentException();
		}

		this.view.putCardIntoSkat(this.data.getActiveTable(), (Card) e.getSource());
	}

	/**
	 * Loads a series
	 */
	public void loadSeries() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Saves a series
	 * 
	 * @param newName TRUE, if a new name should be given to the save file
	 */
	public void saveSeries(boolean newName) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gets the controller for playing on the ISS
	 * 
	 * @return ISS controller
	 */
	public ISSController getISSController() {
		
		return this.issControl;
	}

	/**
	 * Shows the preference dialog
	 */
	public void showPreferences() {
		
		this.view.showPreferences();
	}
}
