package de.jskat.control.iss;

import java.awt.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.control.JSkatMaster;
import de.jskat.data.iss.ISSLoginCredentials;
import de.jskat.gui.JSkatView;
import de.jskat.gui.action.JSkatActions;

/**
 * Controls all ISS related actions
 */
public class ISSController {

	private static Log log = LogFactory.getLog(ISSController.class);
	
	private JSkatMaster jskat;
	private JSkatView view;

	private Connector issConnect;
	private OutputChannel issOut;

	/**
	 * Constructor
	 * 
	 * @param controller JSkat master controller
	 * @param newOutput Output channel to ISS
	 */
	public ISSController(JSkatMaster controller, JSkatView newView) {
		
		this.jskat = controller;
		this.view = newView;
	}

	/**
	 * Disconnects from ISS
	 */
	public void disconnect() {
		
		if (this.issConnect != null &&
				this.issConnect.isConnected()) {
			
			log.debug("connection to ISS still open");
			
			this.issConnect.closeConnection();
		}
	}
	
	/**
	 * Shows the login panel for ISS
	 */
	public void showISSLoginPanel() {
		
		this.view.showISSLogin();
	}

	/**
	 * Connects to the ISS
	 * 
	 * @param login Login credentials
	 * @return TRUE if the connection was established successfully
	 */
	public boolean connectToISS(ActionEvent e) {
		
		log.debug("connectToISS");

		if (this.issConnect == null) {
			
			this.issConnect = new Connector(this);
		}
		
		log.debug("connector created");

		Object source = e.getSource();
		String command = e.getActionCommand();
		
		if (JSkatActions.CONNECT_TO_ISS.toString().equals(command)) {
			if (source instanceof ISSLoginCredentials) {
				
				ISSLoginCredentials login = (ISSLoginCredentials) source;
				
				if (!this.issConnect.isConnected()) {

					this.issConnect.setConnectionData(login.getLoginName(), login.getPassword(), login.getPort());
					this.issConnect.establishConnection();
				}
			}
			else {
				
				log.error("Wrong source for " + command);
			}
		}
		
		if (this.issConnect.isConnected()) {
			// show ISS lobby if connection was successfull 
			this.view.showISSLobby();
		}
		
		return this.issConnect.isConnected();
	}

	public void updateISSPlayerList(String playerName, String language,
			long gamesPlayed, double strength) {
		
		this.view.updateISSLobbyPlayerList(playerName, language, gamesPlayed, strength);
	}
}
