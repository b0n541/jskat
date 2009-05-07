/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.JSkatPlayer;
import de.jskat.ai.nn.data.SkatNetworks;
import de.jskat.ai.nn.util.NeuralNetwork;
import de.jskat.control.JSkatThread;
import de.jskat.control.SkatGame;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData.GameStates;
import de.jskat.gui.NullView;
import de.jskat.util.CardDeck;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Simulates games during bidding 
 */
class GameSimulationThread extends JSkatThread {

	private static Log log = LogFactory.getLog(GameSimulationThread.class);

	private CardDeck knownCards = null;
	private Player position = null;
	
	void setCards(CardDeck cards) {
		
		this.knownCards = cards;
	}

	void setPlayerPosition(Player newPosition) {
		
		this.position = newPosition;
	}
	
	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		simulateGames();
	}
	
	/**
	 * Trains the neural networks
	 */
	private void simulateGames() {
		
		JSkatPlayer nnPlayer1 = new AIPlayerNN();
		((AIPlayerNN) nnPlayer1).setIsLearning(true);
		JSkatPlayer nnPlayer2 = new AIPlayerNN();
		((AIPlayerNN) nnPlayer2).setIsLearning(true);
		JSkatPlayer nnPlayer3 = new AIPlayerNN();
		((AIPlayerNN) nnPlayer3).setIsLearning(true);
		double avgDeclDiff = 0.0d;
		double avgOppDiff = 0.0d;
		
		long episodes = 0;
		long episodesWonGames = 0;
		long totalWonGames = 0;
		long totalGames = 0;
		int episodeSteps = 1000;
		
		while(true) {
			
			for (GameType gameType : GameType.values()) {
			
				NeuralNetwork declarerNet = SkatNetworks.getNetwork(gameType, true);
				NeuralNetwork opponentNet = SkatNetworks.getNetwork(gameType, false);

				SkatGame game = new SkatGame(null, null, nnPlayer1, nnPlayer2, nnPlayer3);
				game.setView(new NullView());
				game.setMaxSleep(0);
		
				CardDeck simCards = (CardDeck) this.knownCards.clone();
				CardDeckSimulator.simulateUnknownCards(simCards);
				
				
				
				log.debug("Card deck: " + simCards);
				game.setCardDeck(simCards);
				game.dealCards();
				
				game.setSinglePlayer(this.position);
				
				GameAnnouncement ann = new GameAnnouncement();
				ann.setGameType(gameType);
				game.setGameAnnouncement(ann);
				
				game.setGameState(GameStates.TRICK_PLAYING);
				
				game.start();
				try {
					game.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (game.isGameWon()) {
					
					episodesWonGames++;
					totalWonGames++;
				}
				
				totalGames++;
				avgDeclDiff = (avgDeclDiff * (totalGames - 1) + declarerNet.getAvgDiff()) / totalGames;
				avgOppDiff = (avgOppDiff * (totalGames - 1) + opponentNet.getAvgDiff()) / totalGames;
			}
			
			episodes++;
			
			checkWaitCondition();
		}
	}
}
