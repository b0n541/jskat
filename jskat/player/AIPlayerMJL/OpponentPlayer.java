/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerMJL;

import org.apache.log4j.Logger;

import jskat.share.CardVector;
import jskat.share.Card;
import jskat.share.SkatConstants;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class OpponentPlayer implements CardPlayer {

	/** Constructor
	 * @param id playerID
	 */
	public OpponentPlayer(int id) {
		log.debug("Constructing new opponent player.");
		this.playerID = id;
	}

	/** Gets the next card that the player wants to play
	 * @param cards hand of the player
	 * @param trickInfo all necessary information about the trick
	 * @return index of the card to play
	 * @see jskat.player.AIPlayerMJL.CardPlayer#playNextCard(jskat.share.CardVector, jskat.player.AIPlayerMJL.TrickInfo)
	 */
	public int playNextCard(CardVector cards, TrickInfo trickInfo) {
		if(trickInfo.getGameType()==SkatConstants.NULL) {
			return playNextCardNullGame(cards, trickInfo);
		}
    	
		// TODO refactor method calls: just include CardMemory instead of all the details

		int bestToBePlayed = -1;
		log.debug("--------------------- start ----------------------------------");
        log.debug(".playNextCard(): Processing hand ["+cards+"] with trick ["+trickInfo.getTrick()+"]. Trump is "+Helper.suitName(trickInfo.getTrump())+".");
        
		if (trickInfo.size() > 1) {
			// 1: check if player can match initial suit
			if(Helper.isAbleToMatch(cards, trickInfo.getTrump(), trickInfo.getCard(0), trickInfo.getGameType())) {
				// 1.1 if yes (i.e. I can match the initial suit): check if necessary to beat
				log.debug(".playNextCard(): I can match the demanded color");
				if(Helper.isSinglePlayerWin(trickInfo)) {
					log.debug(".playNextCard(): Trick is SinglePlayerWin");
					// 1.1.1: if yes: can beat?
					Card currentWinner;
					if (trickInfo.getTrick().getCard(0).beats(trickInfo.getTrick().getCard(1), trickInfo.getGameType(), trickInfo.getTrump(), trickInfo.getDemandSuit())) {
						currentWinner = trickInfo.getCard(0);
					}
					else {
						currentWinner = trickInfo.getCard(1);
					}
					bestToBePlayed = Helper.isAbleToBeat(cards, currentWinner, trickInfo.getTrump(), trickInfo.getCard(0), trickInfo.getGameType()); 
					if(bestToBePlayed > -1) {
						// 1.1.1.1: if I can beat: do it
						log.debug(this+".playNextCard(): ...but I can beat it...");
					} 
					else {
						log.debug(".playNextCard(): ...which I can't beat...");
						// 1.1.1.2: if I can't beat: find lowest matching card
						if(trickInfo.getCard(0).isTrump(trickInfo.getTrump())) {
							bestToBePlayed = cards.getFirstIndexOfSuit(trickInfo.getGameType(), trickInfo.getTrump());
							if(bestToBePlayed < 0) {
								log.debug(".playNextCard(): Damn! I have to play a Jack...");
								bestToBePlayed = 0;
								while(cards.size()>bestToBePlayed+1 && cards.getCard(bestToBePlayed+1).getValue()==SkatConstants.JACK) bestToBePlayed++;
							}
						}
						else {
							bestToBePlayed = cards.getFirstIndexOfSuit(trickInfo.getGameType(), trickInfo.getDemandSuit());
						}
					}
				}
				else {
					log.debug(".playNextCard(): I can match the demanded color, and it's our trick already...");
					// 1.1.2: if no: find highest matching card
					if(trickInfo.getTrick().getCard(0).isTrump(trickInfo.getTrump())) {
					    bestToBePlayed = cards.getLastIndexOfSuit(trickInfo.getGameType(), trickInfo.getTrump());
						if(bestToBePlayed < 0) {
							log.debug(".playNextCard(): Damn! I have to play a Jack...");
							bestToBePlayed = 0;
							while(cards.size()>bestToBePlayed+1 && cards.getCard(bestToBePlayed+1).getValue()==SkatConstants.JACK) bestToBePlayed++;
						}
					}
					else {
						bestToBePlayed = cards.getLastIndexOfSuit(trickInfo.getGameType(), trickInfo.getDemandSuit());
					}
				}
			}
			else {
				// 1.2: if no (i.e. I can't match the initial suit): is ours?
				if(Helper.isSinglePlayerWin(trickInfo)) {
					// 1.2.1: if no: do I have trump? 
					log.debug(".playNextCard(): I cannot match and trick is SinglePlayerWin");
					if(cards.hasTrump(trickInfo.getTrump())) {
						// 1.2.1.1: if yes: do I want to trump
						if(trickInfo.getTrickValue() > 3) {
							log.debug(".playNextCard(): But I can and will take it...");
							// 1.2.1.1.1: if yes: find a good trump
							bestToBePlayed = findValuableTrump(cards, trickInfo.getTrump());
						}
						else {
							log.debug(".playNextCard(): I could trump, but I don't want to...");
							// 1.2.1.1.2: if no: find a low card
							bestToBePlayed = findLowCard(cards, trickInfo.getTrump());
						}
					} 
					else {
						log.debug(".playNextCard(): I'd love to have it, but I can't match the initial suit...");
						// 1.2.1.2: if no: find a low card
						bestToBePlayed = findLowCard(cards, trickInfo.getTrump());
					}
				}
				else {
					log.debug(".playNextCard(): I cannot match but it's our trick already...");
					// 1.2.2: if yes: find highest value card (but no ace)
					bestToBePlayed = findHighCard(cards, trickInfo.getTrump());
					log.debug(".playNextCard(): got back value "+bestToBePlayed );
					
				}
			}
		}
				
		else 
		if (trickInfo.getTrick().size() > 0) {
            
			// Only one card is played in this trick yet
			// the player is forced to play after the color
			// of the first card
            
			// At first: Is trump played?
			if (trickInfo.getTrick().getCard(0).getSuit() == trickInfo.getTrump() ||
			trickInfo.getTrick().getCard(0).getValue() == SkatConstants.JACK) {
                
				// Trump is played
				// Does the player have trump?
				if (Helper.hasTrump(cards, trickInfo.getTrump())) {
                    
					// Play the highest trump
					// Check whether there is a Jack in the cards or not
					if (cards.contains(SkatConstants.CLUBS, SkatConstants.JACK)) {
						bestToBePlayed = cards.getIndexOf(SkatConstants.CLUBS, SkatConstants.JACK);
					} else if (cards.contains(SkatConstants.SPADES, SkatConstants.JACK)) {
						bestToBePlayed = cards.getIndexOf(SkatConstants.SPADES, SkatConstants.JACK);
					} else if (cards.contains(SkatConstants.HEARTS, SkatConstants.JACK)) {
						bestToBePlayed = cards.getIndexOf(SkatConstants.HEARTS, SkatConstants.JACK);
					} else if (cards.contains(SkatConstants.DIAMONDS, SkatConstants.JACK)) {
						bestToBePlayed = cards.getIndexOf(SkatConstants.DIAMONDS, SkatConstants.JACK);
					} else {
						// No Jack in the cards
						bestToBePlayed = cards.getLastIndexOfSuit(SkatConstants.SUIT, trickInfo.getTrump());
					}
                    
				} else {
                    
					// Player doesn't have trump
					// it doesn't matter what card is played
					// just play the first card in the CardVector
					bestToBePlayed = findLowCard(cards, trickInfo.getTrump());
				}
                
			} else {
                
				// If trump is not played the player is forced
				// to play the color of the first card
				if (cards.hasSuit(SkatConstants.SUIT, trickInfo.getTrick().getCard(0).getSuit())) {
                    
					// Player has the color
					// check if it's ours or if I can beat 
					if(Helper.isSinglePlayerWin(trickInfo)) {
						bestToBePlayed = Helper.isAbleToBeat(cards, trickInfo.getCard(0), trickInfo.getTrump(), trickInfo.getCard(0), trickInfo.getGameType()); 
						if(bestToBePlayed < 0) {
							bestToBePlayed = cards.getFirstIndexOfSuit(SkatConstants.SUIT, trickInfo.getTrick().getCard(0).getSuit());
						}
					}
					else {
						// Play the card with the highest value
						bestToBePlayed = cards.getLastIndexOfSuit(SkatConstants.SUIT, trickInfo.getTrick().getCard(0).getSuit());
					}
					
                    
				} else {
                    
					// Player doesn't have the color
					// is there any trump in the cards
					if (Helper.hasTrump(cards, trickInfo.getTrump())) {
                        
						// Play the highest trump
						// Check whether there is a Jack in the cards or not
						if (cards.contains(SkatConstants.CLUBS, SkatConstants.JACK)) {
                            
							bestToBePlayed = cards.getIndexOf(SkatConstants.CLUBS, SkatConstants.JACK);
                            
						} else if (cards.contains(SkatConstants.SPADES, SkatConstants.JACK)) {
                            
							bestToBePlayed = cards.getIndexOf(SkatConstants.SPADES, SkatConstants.JACK);
                            
						} else if (cards.contains(SkatConstants.HEARTS, SkatConstants.JACK)) {
                            
							bestToBePlayed = cards.getIndexOf(SkatConstants.HEARTS, SkatConstants.JACK);
                            
						} else if (cards.contains(SkatConstants.DIAMONDS, SkatConstants.JACK)) {
                            
							bestToBePlayed = cards.getIndexOf(SkatConstants.DIAMONDS, SkatConstants.JACK);
                            
						} else {
                            
							// No Jack in the cards
							bestToBePlayed = cards.getLastIndexOfSuit(SkatConstants.SUIT, trickInfo.getTrump());
						}
                        
					} else {
                        
						// it doesn't matter what card is played
						bestToBePlayed = cards.size() - 1;
					}
				}
			}
            
			log.debug(".playNextCard(): player " + playerID + ": " + cards.getCard(bestToBePlayed));
		} else {
            
			// No card on the table yet
			
			bestToBePlayed = findInitial(cards, trickInfo.getGameInfo());
			log.debug(".playNextCard(): (in forehand) " + playerID + ": " + cards.getCard(bestToBePlayed));
		}
        
		if(bestToBePlayed < 0 || bestToBePlayed > cards.size()-1) {
			log.debug("----- Error in finding a good opponent card: "+bestToBePlayed+" -----");
			bestToBePlayed = 0;
		}
		
		log.debug("Playing "+cards.getCard(bestToBePlayed));
		log.debug("--------------------- done -----------------------------------");
		return bestToBePlayed;
	}

	/**
	 * Gets the next card that the player wants to play in a null game
	 * @param cards
	 * @param trickInfo
	 * @return the index of the card that should be played
	 */
	private int playNextCardNullGame(CardVector cards, TrickInfo trickInfo) {
		int bestToBePlayed = -1;
		log.debug(".playNextCardNullGame(): cards: ["+cards+"]");
        
		if (trickInfo.getTrick().size() > 0) {
			if(!cards.hasSuit(SkatConstants.NULL, trickInfo.getTrick().getCard(0).getSuit())) {
				// TODO null game: abwerfen
				log.debug(".playNextCardNullGame(): abwerfen...");
				bestToBePlayed = 0;
			}
			else {
				// do I have a lower card?
				int toBePlayed = findLowerCard(cards, trickInfo.getTrick().getCard(0));
				if(toBePlayed < 0) {
					// no: take the highest one of that suit
					bestToBePlayed = cards.getFirstIndexOfSuit(trickInfo.getTrick().getCard(0).getSuit());
				} else {
					bestToBePlayed = toBePlayed;
				}
			}
		}
		else {
			int toBePlayed = findInitialForNullGame(cards);
			log.debug(".playNextCardNullGame(): (initial for null): "+toBePlayed);
			bestToBePlayed = toBePlayed;
		}
		log.debug(".playNextCardNullGame(): playing: ["+cards.getCard(bestToBePlayed)+"]");
		return bestToBePlayed;
	}

	/**
	 * Chooses the highest valued trump card
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findValuableTrump(CardVector cards, int trump) {
		// TODO consider, which other trumps have already been played
		if (cards.size()<1) return 0;
		int highCard = 0;
		int index = 0;
		while (++index<cards.size()) {
			if(cards.getCard(index).isTrump(trump) && cards.getCard(index).getCalcValue() > cards.getCard(highCard).getCalcValue()) { 
				highCard = index;
				log.debug("     highest card set to "+index);
			}
		}
		return (highCard<cards.size()?highCard:0);
	}

	/**
	 * Chooses the most valuable matching card of the given suit
	 * (considering the jacks if the suit is trump)
	 * @param cards
	 * @param suit
	 * @param trump
	 * @return index of the card
	 */
	private int findMostValuableMatchingCard(CardVector cards, int suit, int trump) {
		if (cards.size()<1) return 0;
		int highCard = 0;
		int index = 0;
		while (++index<cards.size()) {
			if(!cards.getCard(index).isTrump(trump) 
					&& cards.getCard(index).getCalcValue() > cards.getCard(highCard).getCalcValue() 
					&& cards.getCard(index).getValue() != SkatConstants.ACE 
					&& cards.getCard(index).getSuit() == suit) { 
				highCard = index;
			}
		}
		return (highCard<cards.size()?highCard:0);
	}

	/**
	 * Chooses the highest valued card that is not trump (and not an ace)
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findHighCard(CardVector cards, int trump) {
	    // TODO: add a flag whether aces should be included
	    //       or just consider the CardMemory
	    if (cards.size()<1) return 0;
		int highCard = 0;
		int index = 0;
		while (++index<cards.size()) {
		    if(cards.getCard(highCard).isTrump(trump) && 
		            !cards.getCard(index).isTrump(trump) &&
		            cards.getCard(index).getValue() != SkatConstants.ACE) {
				highCard = index;
		    }
			else if(!cards.getCard(index).isTrump(trump) && 
			        cards.getCard(index).getCalcValue() > cards.getCard(highCard).getCalcValue() && 
			        cards.getCard(index).getValue() != SkatConstants.ACE) { 
				highCard = index;
			}
		}
		return (highCard<cards.size()?highCard:0);
	}

	/**
	 * Selects the first card of the hand that has no value and is not trump
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findLowCard(CardVector cards, int trump) {
		if (cards.size()<2) return 0;
		int lowCard = 0;
		int index = 0;
		boolean found = false;
		while (!found && ++index<cards.size()) {
			if(!cards.getCard(index).isTrump(trump) && cards.getCard(index).getCalcValue() < cards.getCard(lowCard).getCalcValue()) {
			    lowCard = index;
			}
			if(cards.getCard(lowCard).getCalcValue() == 0) found = true;
		}
		return (index<cards.size()?index:0);
	}

	/**
	 * Tries to find a lower card than the given card
	 * (should only be used in null games)
	 * @param cards
	 * @param card
	 * @return index of the card, -1 if none is found
	 */
	private int findLowerCard(CardVector cards, Card card) {
		int index = -1;
		int possibleCards = Helper.suitCardsToBinaryNullGame(cards, card.getSuit());
		int counter = 0;
		while(possibleCards > 0 && card.getNullValue()>counter && counter < 8) {
			if((possibleCards & (2^counter)) == 1) index = counter;
			counter++;
		}
		log.debug(".findLowerCard(): "+index);
		return index;
	}

	/**
	 * Finds an initial card in a null game
	 * (not implemented yet --> just takes the last one)
	 * @param cards
	 * @return index of the card
	 */
	private int findInitialForNullGame(CardVector cards) {
		// TODO initial card for null game
		return cards.size()-1;
	}

	/**
	 * Finds an initial card to play
	 * @param cards
	 * @param game
	 * @return index of the card
	 */
	private int findInitial(CardVector cards, GameInfo game) {
		for(int x=0;x<cards.size();x++)
		{
			if (cards.getCard(x).getValue() == SkatConstants.ACE)
					if(cards.getCard(x).getSuit() != game.getTrump()) return x;
		}
		// If you don't have any, find a low face card
		log.debug(".findInitial(): no Ace found");
		int store = 0;
		for(int x=0;x<cards.size();x++)
		{
			switch (cards.getCard(x).getValue()) {
			case SkatConstants.SEVEN: // "7"
			case SkatConstants.EIGHT: // "8"
			case SkatConstants.NINE: // "9"
				// If you find one and it is a trumpf, remember it for later (in case you find nothing else)
				if(cards.getCard(x).getSuit() != game.getTrump()) return x;
					// else {store = x; break; };
			}
		}
		// if you have only found a trumpf, then (for heaven's sake) use it
		if(store > 0) return store;
		// If you find nothing else, just take the first in the row
		log.debug(".findInitial(): no low card found");
		for(int x=0;x<cards.size();x++)
		{
			return x;
		}
		log.debug("WARNING: "+this+".findInitial(): No initial card found - returning 0.");
		return 0;
		// TODO: Implement opening strategies:
		// e.g. "Kurzer Weg, lange Farbe - langer Weg, kurze Farbe"
	}


	/**
	 * Getter for playerID
	 * @return playerID
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Setter for playerID
	 * @param i
	 */
	public void setPlayerID(int i) {
		playerID= i;
	}

	/**
	 * The id of the player
	 */
	private int playerID = -1;

	/**
	 * log
	 */
	private static final Logger log = Logger.getLogger(OpponentPlayer.class);
}
