/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.util.Observer;
import java.util.Observable;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import java.awt.GridLayout;

import jskat.control.JSkatMaster;
import jskat.control.SkatGame;
import jskat.control.SkatSeries;
import jskat.control.SkatTable;
import jskat.data.JSkatDataModel;
import jskat.data.SkatGameData;
import jskat.data.SkatSeriesData;
import jskat.data.SkatTableData;
import jskat.share.SkatConstants;

/**
 * Shows some informations about the current game during playing
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net
 */
public class GameScore extends JPanel implements Observer {
    
	private static final long serialVersionUID = 2079176251942408942L;

	static Logger log = Logger.getLogger(GameScore.class);

	/**
     * Creates a new instance of GameScore
     * @param dataModel The JSkatDataModel that holds all data
     */
    public GameScore(JSkatDataModel dataModel) {
        
        jskatStrings = dataModel.getResourceBundle();
        
        setLayout(new GridLayout(5, 2));
        
        gameTypeLabel = new JLabel();
        gameTrumpLabel = new JLabel();
        playerNameLabel = new JLabel();
        playerScoreLabel = new JLabel();
        opponentScoreLabel = new JLabel();
        
        add(new JLabel(jskatStrings.getString("game") + ": "));
        add(gameTypeLabel);
        add(new JLabel(jskatStrings.getString("trump") + ": "));
        add(gameTrumpLabel);
        add(new JLabel(jskatStrings.getString("player") + ": "));
        add(playerNameLabel);
        add(new JLabel(jskatStrings.getString("player") + ": "));
        add(playerScoreLabel);
        add(new JLabel(jskatStrings.getString("opponents") + ": "));
        add(opponentScoreLabel);
    }
    
    /**
     * Updates the GameScore if anything changes in the SkatRound or in a SkatGame
     * 
     * @param observ The Observable that is observed
     * @param obj The Object that has changed in the Observable
     */
    public void update(Observable observ, Object obj) {
        
		// log.debug("UPDATE " + observ + ": " + obj + " has changed...");

    	
        if (observ instanceof JSkatMaster && obj instanceof SkatTable) {
            
			((SkatTable) obj).addObserver(this);
			((SkatTable) obj).getSkatTableData().addObserver(this);
        }
        else if (observ instanceof SkatTableData && obj instanceof SkatSeries) {
        	
        	((SkatSeries) obj).addObserver(this);
        	((SkatSeries) obj).getSkatSeriesData().addObserver(this);
        }
		else if (observ instanceof SkatSeriesData && obj instanceof SkatGame) {
	
			((SkatGame) obj).addObserver(this);
			((SkatGame) obj).getSkatGameData().addObserver(this);
		}
		else if (observ instanceof SkatGame && obj instanceof Integer) {
	
			// state of current game has changed
			SkatGame game = (SkatGame) observ;
			Integer state = (Integer) obj;
	
			switch (state.intValue()) {
			
			case SkatGame.GAMESTATE_STARTED:
			case SkatGame.GAMESTATE_NEW_TRICK_STARTED:
				
				SkatGameData data = game.getSkatGameData();
				SkatConstants.GameTypes gameType = data.getGameType();
                
				if (gameType == SkatConstants.GameTypes.SUIT) {
					
					gameTypeLabel.setText(jskatStrings.getString("suit_game"));

					SkatConstants.Suits trump = data.getTrump();
					
					if (trump == SkatConstants.Suits.CLUBS) {
						
						gameTrumpLabel.setText(jskatStrings.getString("clubs"));
					}
					else if (trump == SkatConstants.Suits.SPADES) {
						
						gameTrumpLabel.setText(jskatStrings.getString("spades"));
					}
					else if (trump == SkatConstants.Suits.HEARTS) {
						
						gameTrumpLabel.setText(jskatStrings.getString("hearts"));
					}
					else if (trump == SkatConstants.Suits.DIAMONDS) {
						
						gameTrumpLabel.setText(jskatStrings.getString("diamonds"));
					}
				}
				else if (gameType == SkatConstants.GameTypes.GRAND) {
					
					gameTypeLabel.setText(jskatStrings.getString("grand_game"));
					gameTrumpLabel.setText("");
				}
				else if (gameType == SkatConstants.GameTypes.NULL) {
					
					gameTypeLabel.setText(jskatStrings.getString("null_game"));
					gameTrumpLabel.setText("");
				}
				else if (gameType == SkatConstants.GameTypes.RAMSCH) {
					
					gameTypeLabel.setText(jskatStrings.getString("ramsch_game"));
					gameTrumpLabel.setText("");
				}

	            // only set the name when there actually is a single player
	            if(data.getSinglePlayer()>=0) {
	            	playerNameLabel.setText(data.getPlayers()[data.getSinglePlayer()].getPlayerName());
	            }
	            else {
	            	playerNameLabel.setText("---");
	            }
	            
	            if(data.getGameType() != SkatConstants.GameTypes.RAMSCH) {
		            playerScoreLabel.setText("" + data.getSinglePlayerScore());
		            opponentScoreLabel.setText("" + data.getOpponentScore());
	            }
	            else {
		            playerScoreLabel.setText("--");
		            opponentScoreLabel.setText("--");
	            }
			}
		}
        else {
        	// log.debug("UPDATE " + observ + ": " + obj + " unhandled...");
        }
        
        /*
        else if (observ instanceof SkatSeriesData && obj instanceof SkatGameData) {
            
            ((SkatGameData)obj).addObserver(this);
        }
        else if (observ instanceof SkatGameData) {
            
            SkatGameData gameData = (SkatGameData)observ;
            
            // TODO take card face into account
            switch(gameData.getGameType()) {
                
                case(SkatConstants.SUIT):
                    gameTypeLabel.setText(jskatStrings.getString("suit_game"));
                    break;
                case(SkatConstants.GRAND):
                    gameTypeLabel.setText(jskatStrings.getString("grand_game"));
                    break;
                case(SkatConstants.NULL):
                    gameTypeLabel.setText(jskatStrings.getString("null_game"));
                    break;
                case(SkatConstants.RAMSCH):
                    gameTypeLabel.setText(jskatStrings.getString("ramsch_game"));
                    break;
            }
            
            switch(gameData.getTrump()) {
                
                case(SkatConstants.CLUBS):
                    gameTrumpLabel.setText(jskatStrings.getString("clubs"));
                    break;
                case(SkatConstants.SPADES):
                    gameTrumpLabel.setText(jskatStrings.getString("spades"));
                    break;
                case(SkatConstants.HEARTS):
                    gameTrumpLabel.setText(jskatStrings.getString("hearts"));
                    break;
                case(SkatConstants.DIAMONDS):
                    gameTrumpLabel.setText(jskatStrings.getString("diamonds"));
                    break;
                default:
                    gameTrumpLabel.setText("");
                    break;
            }

            // only set the name when there actually is a single player
            if(gameData.getSinglePlayer()>=0) {
            	
            	playerNameLabel.setText(gameData.getPlayers()[gameData.getSinglePlayer()].getPlayerName());
            }
            
            playerScoreLabel.setText("" + ((SkatGameData)observ).getSinglePlayerScore());
            opponentScoreLabel.setText("" + ((SkatGameData)observ).getOpponentScore());
        }
        */
    }
    
    private JLabel gameTypeLabel;
    private JLabel gameTrumpLabel;
    private JLabel playerNameLabel;
    private JLabel playerScoreLabel;
    private JLabel opponentScoreLabel;
    private ResourceBundle jskatStrings;
}
