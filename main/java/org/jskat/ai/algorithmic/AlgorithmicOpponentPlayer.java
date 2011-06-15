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
public class AlgorithmicOpponentPlayer extends AlgorithmicAIPlayer implements IJSkatPlayer {
	private static final Logger log = Logger.getLogger(AlgorithmicOpponentPlayer.class);

	private final AlgorithmicAIPlayer myPlayer;
	
	/**
	 * 
	 */
	public AlgorithmicOpponentPlayer(AlgorithmicAIPlayer p) {
		myPlayer = p;
		log.debug("Defining player <"+myPlayer.getPlayerName()+"> as AlgorithmicSinglePlayer");
	}
	
	
}
