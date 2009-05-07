/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn.train;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.JSkatPlayer;
import de.jskat.ai.nn.AIPlayerNN;
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
 * Trains the neural networks
 */
public class NNTrainer extends JSkatThread {

	private static Log log = LogFactory.getLog(NNTrainer.class);

	private Random rand;
	private List<Boolean> gameWonLost;
	private List<StringBuffer> nullGames;
	
	private GameType gameType;

	/**
	 * Constructor
	 */
	public NNTrainer() {
		
		this.gameWonLost = new ArrayList<Boolean>();
		this.nullGames = new ArrayList<StringBuffer>();
		this.rand = new Random();
		
		initLearningPatterns();
	}
	
	private void initLearningPatterns() {
		
		// test a perfect null game
		StringBuffer buffer = new StringBuffer();
		buffer.append("CA CT CK "); // 3 cards fore hand
		buffer.append("C9 ST SK "); // 3 cards middle hand
		buffer.append("H7 DA DT "); // 3 cards hind hand
		buffer.append("SA HA "); // skat
		buffer.append("CQ CJ C8 C7 "); // 4 cards fore hand
		buffer.append("SQ SJ HK HQ "); // 4 cards middle hand
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand
		buffer.append("S9 S8 S7 "); // 3 cards fore hand
		buffer.append("HJ H9 H8 "); // 3 cards middle hand
		buffer.append("D7 HT DJ"); // 3 cards hind hand
		this.nullGames.add(buffer);
		
		buffer = new StringBuffer();
		buffer.append("CA CT CK "); // 3 cards fore hand
		buffer.append("S9 ST SK "); // 3 cards middle hand
		buffer.append("H7 DA DT "); // 3 cards hind hand
		buffer.append("SA HA "); // skat
		buffer.append("CQ CJ C8 C7 "); // 4 cards fore hand
		buffer.append("SQ SJ HK HQ "); // 4 cards middle hand
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand
		buffer.append("C9 S8 S7 "); // 3 cards fore hand
		buffer.append("HJ H9 H8 "); // 3 cards middle hand
		buffer.append("D7 HT DJ"); // 3 cards hind hand
		this.nullGames.add(buffer);
		
		buffer = new StringBuffer();
		buffer.append("C9 C8 C7 "); // 3 cards fore hand
		buffer.append("CA CT CK "); // 3 cards middle hand
		buffer.append("CQ CJ SA "); // 3 cards hind hand
		buffer.append("SJ SK "); // skat
		buffer.append("ST S9 S8 S7 "); // 4 cards fore hand
		buffer.append("SQ HA HT HK "); // 4 cards middle hand
		buffer.append("HQ DA DT DK "); // 4 cards hind hand
		buffer.append("H9 H8 H7 "); // 3 cards fore hand
		buffer.append("DQ DJ D9 "); // 3 cards middle hand
		buffer.append("D8 D7 HJ"); // 3 cards hind hand
		this.nullGames.add(buffer);
		
		buffer = new StringBuffer();
		buffer.append("SA ST SK "); // 3 cards fore hand
		buffer.append("S9 CT CK "); // 3 cards middle hand
		buffer.append("H7 DA DT "); // 3 cards hind hand
		buffer.append("CA HA "); // skat
		buffer.append("SQ SJ S8 S7 "); // 4 cards fore hand
		buffer.append("CQ CJ HK HQ "); // 4 cards middle hand
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand
		buffer.append("C9 C8 C7 "); // 3 cards fore hand
		buffer.append("HJ H9 H8 "); // 3 cards middle hand
		buffer.append("D7 HT DJ"); // 3 cards hind hand
		this.nullGames.add(buffer);

		buffer = new StringBuffer();
		buffer.append("HA HT HK "); // 3 cards fore hand
		buffer.append("H9 CT CK "); // 3 cards middle hand
		buffer.append("S7 DA DT "); // 3 cards hind hand
		buffer.append("CA SA "); // skat
		buffer.append("HQ HJ H8 H7 "); // 4 cards fore hand
		buffer.append("CQ CJ SK SQ "); // 4 cards middle hand
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand
		buffer.append("S9 S8 S7 "); // 3 cards fore hand
		buffer.append("SJ S9 S8 "); // 3 cards middle hand
		buffer.append("D7 ST DJ"); // 3 cards hind hand
		this.nullGames.add(buffer);
	}
	
	/**
	 * Sets the game type to learn
	 * 
	 * @param newGameType Game type
	 */
	public void setGameType(GameType newGameType) {
		
		this.gameType = newGameType;
	}
	
	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		trainNets();
	}
	
	/**
	 * Trains the neural networks
	 */
	private void trainNets() {
		
		JSkatPlayer nnPlayer1 = new AIPlayerNN();
		((AIPlayerNN) nnPlayer1).setIsLearning(true);
		JSkatPlayer nnPlayer2 = new AIPlayerNN();
		((AIPlayerNN) nnPlayer2).setIsLearning(true);
		JSkatPlayer nnPlayer3 = new AIPlayerNN();
		((AIPlayerNN) nnPlayer3).setIsLearning(true);
		NeuralNetwork declarerNet = SkatNetworks.getNetwork(this.gameType, true);
		NeuralNetwork opponentNet = SkatNetworks.getNetwork(this.gameType, false);
		double avgDeclDiff = 0.0d;
		double avgOppDiff = 0.0d;
		
		long episodes = 0;
		long episodesWonGames = 0;
		long totalWonGames = 0;
		long totalGames = 0;
		int episodeSteps = 1000;
		
		while(true) {
			
			if (episodes % episodeSteps == 1) {
				
				System.out.println(this.gameType + ": Episode " + episodes + " won games " + episodesWonGames + " (" + 100 * episodesWonGames / (episodeSteps * 3) + " %)" + " total won games " + totalWonGames + " (" + 100 * totalWonGames / totalGames + " %)");
				System.out.println("        Declarer net: " + declarerNet.getIterations() + " iterations " + Math.round(avgDeclDiff * 10000.0d) / 10000.d + " avg diff");
				System.out.println("        Opponent net: " + opponentNet.getIterations() + " iterations " + Math.round(avgOppDiff * 10000.0d) / 10000.d + " avg diff");
				episodesWonGames = 0;
			}

			for (Player currPlayer : Player.values()) {
			
				SkatGame game = new SkatGame(null, null, nnPlayer1, nnPlayer2, nnPlayer3);
				game.setView(new NullView());
				game.setMaxSleep(0);
		
	//			CardDeck deck = new CardDeck(this.nullGames.get(rand.nextInt(nullGames.size())).toString());
				CardDeck deck = new CardDeck();
				deck.shuffle();
				log.debug("Card deck: " + deck);
				game.setCardDeck(deck);
				game.dealCards();
				
				game.setSinglePlayer(currPlayer);
				
				GameAnnouncement ann = new GameAnnouncement();
	//			ann.setGameType(GameTypes.NULL);
				ann.setGameType(this.gameType);
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
