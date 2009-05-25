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
import de.jskat.ai.nn.AIPlayerNN;
import de.jskat.ai.nn.data.SkatNetworks;
import de.jskat.ai.nn.train.NNTrainer;
import de.jskat.ai.rnd.AIPlayerRND;
import de.jskat.control.iss.ISSConnector;
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
	private ISSConnector issConnect;
	
	/**
	 * Constructor
	 * 
	 * @param jskatOptions JSkat options
	 */
	public JSkatMaster(JSkatOptions jskatOptions) {
		
		this.data = new JSkatApplicationData(jskatOptions);
		this.options = jskatOptions;
	}

	/**
	 * Creates a new skat table
	 * 
	 * @return Table name
	 */
	public String createTable() {
		
		// TODO check whether a connection to ISS is established
		// TODO ask whether a local or a remote tabel should be created
		
		SkatTable table = new SkatTable(this, this.data.getTableOptions(), 3);
		String tableName = this.data.addLocalSkatTable(table);
		table.setName(tableName);
		
		this.view.createSkatTablePanel(tableName);
		this.data.setActiveTable(tableName);
		
		table.setView(this.view);

		return tableName;
	}
	
	/**
	 * Starts a new skat series
	 */
	public void startSeries() {
		
		log.debug(this.data.getActiveTable());
		
//		SkatTable table = this.data.getSkatTable(this.data.getActiveTable());
//		
//		if (table.getPlayerCount() == 0) {
//			// TODO this is a dirty hack
//	//		table.placePlayer(new AIPlayerNN());
//			table.placePlayer(new AIPlayerRND());
//			table.placePlayer(new AIPlayerRND());
//			HumanPlayer human = new HumanPlayer();
//			human.setView(this.view);
//			table.placePlayer(human);
//		}
//		
//		table.startSkatSeries(1);
		
		this.view.showStartSkatSeriesDialog();
	}
	
	/**
	 * Starts a new series with given parameters
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
		
		resumeSkatSeries(data.getActiveTable());
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
	}

	/**
	 * Connects to the ISS
	 * 
	 * @param login
	 * @param password
	 * @param port
	 * @return TRUE if the connection was established successfully
	 */
	public boolean connectToISS(String login, String password, int port) {
		
		log.debug("connectToISS");
		
		if (this.issConnect == null) {
		
			this.issConnect = new ISSConnector();
		}
		
		log.debug("connector created");
		
		if (!this.issConnect.isConnected()) {
			
			this.issConnect.setConnectionData(login, password, port);
			this.issConnect.establishConnection();
		}
		
		return this.issConnect.isConnected();
	}
	
	/**
	 * Exits JSkat
	 */
	public void exitJSkat() {
		
		if (this.view.showExitDialog() == JOptionPane.YES_OPTION) {
			
			this.options.saveJSkatProperties();
			
			if (this.issConnect != null &&
					this.issConnect.isConnected()) {
				
				log.debug("connection to ISS still open");
				
				this.issConnect.closeConnection();
			}
			
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
	 * Shows the login panel for ISS
	 */
	public void showISSLoginPanel() {
		
		this.view.showISSLoginPanel();
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
		
		SkatNetworks.loadNetworks(System.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".jskat"));
	}
	
	/**
	 * Saves the weigths for the neural networks
	 */
	public void saveNeuralNetworks() {
		
		SkatNetworks.saveNetworks(System.getProperty("user.home").concat(System.getProperty("file.separator")).concat(".jskat"));
	}
	
	/**
	 * Shows the help dialog
	 */
	public void showHelp() {
		
		this.view.showHelpDialog();
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

	public void takeCardFromSkat(ActionEvent e) {
		
		if (!(e.getSource() instanceof Card)) {
			
			throw new IllegalArgumentException();
		}
		
		this.view.takeCardFromSkat(this.data.getActiveTable(), (Card) e.getSource());
	}

	public void putCardIntoSkat(ActionEvent e) {
		
		if (!(e.getSource() instanceof Card)) {
			
			throw new IllegalArgumentException();
		}

		this.view.putCardIntoSkat(this.data.getActiveTable(), (Card) e.getSource());
	}

	public void loadGame() {
		// TODO Auto-generated method stub
		
	}

	public void saveGame(boolean newName) {
		// TODO Auto-generated method stub
		
	}
}
