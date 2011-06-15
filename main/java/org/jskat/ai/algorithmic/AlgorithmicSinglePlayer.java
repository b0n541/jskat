/**
 * 
 */
package org.jskat.ai.algorithmic;

import org.apache.log4j.Logger;
import org.jskat.ai.IJSkatPlayer;

/**
 * @author Markus J. Luzius <br>
 * created: 15.06.2011 19:13:50
 *
 */
public class AlgorithmicSinglePlayer extends AlgorithmicAIPlayer implements IJSkatPlayer {
	private static final Logger log = Logger.getLogger(AlgorithmicSinglePlayer.class);

	private final AlgorithmicAIPlayer myPlayer;
	
	/**
	 * 
	 */
	public AlgorithmicSinglePlayer(AlgorithmicAIPlayer p) {
		myPlayer = p;
		log.debug("Defining player <"+myPlayer.getPlayerName()+"> as AlgorithmicSinglePlayer");
	}
	
	
}
