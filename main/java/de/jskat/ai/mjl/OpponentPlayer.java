/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.PlayerKnowledge;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Rank;
import de.jskat.util.Suit;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class OpponentPlayer extends AbstractCardPlayer {

	/**
	 * 
	 */
	OpponentPlayer(CardList cards) {
		super(cards);
		log.debug("Constructing new opponent player...");
	}
	
	/** Gets the next card that the player wants to play
	 * @param cards hand of the player
	 * @param trickInfo all necessary information about the trick
	 * @return index of the card to play
	 * @see de.jskat.ai.mjl.CardPlayer#playNextCard(jskat.share.CardList, de.jskat.ai.mjl.TrickInfo)
	 */
	public Card playNextCard(PlayerKnowledge knowledge) {
		log.debug("Play next card with trick size "+knowledge.getTrickCards().size());
		if(knowledge.getGame().getGameType()==GameType.NULL) {
			return playNextCardNullGame(knowledge);
		}

		GameType gameType = knowledge.getGame().getGameType();
		Card initialCard = (knowledge.getTrickCards().size()>0?knowledge.getTrickCards().get(0):null);
		Suit trumpSuit = gameType.getTrumpSuit();
		// TODO refactor method calls: just include CardMemory instead of all the details

		int bestToBePlayed = -1;
        log.debug(".playNextCard(): Processing hand ["+cards+"] with trick ["+knowledge.getTrickCards()+"]. Game type is "+gameType+".");
        
		if (knowledge.getTrickCards().size()>1) {
			// I'm in hindhand
			// 1: check if player can match initial suit
			if(Helper.isAbleToMatch(cards, initialCard, gameType)) {
				// 1.1 if yes (i.e. I can match the initial suit): check if necessary to beat
				log.debug(".playNextCard(): I can match the demanded color");
				if(Helper.isSinglePlayerWin(knowledge)) {
					log.debug(".playNextCard(): Trick is SinglePlayerWin");
					// 1.1.1: if yes: can beat?
					Card currentWinner;
					if (initialCard.beats(gameType, knowledge.getTrickCards().get(1))) {
						currentWinner = initialCard;
					}
					else {
						currentWinner = knowledge.getTrickCards().get(1);
					}
					bestToBePlayed = Helper.isAbleToBeat(cards, currentWinner, initialCard, gameType); 
					if(bestToBePlayed > -1) {
						// 1.1.1.1: if I can beat: do it
						log.debug(".playNextCard(): ...but I can beat it...");
					} 
					else {
						log.debug(".playNextCard(): ...which I can't beat...");
						// 1.1.1.2: if I can't beat: find lowest matching card
						if(initialCard.isTrump(gameType)) {
							bestToBePlayed = cards.getFirstIndexOfSuit(gameType.getTrumpSuit(), false);
							if(bestToBePlayed < 0) {
								log.debug(".playNextCard(): Damn! I have to play a Jack...");
								bestToBePlayed = 0;
								while(cards.size()>bestToBePlayed+1 && cards.get(bestToBePlayed+1).getRank()==Rank.JACK) bestToBePlayed++;
							}
						}
						else {
							bestToBePlayed = cards.getFirstIndexOfSuit(initialCard.getSuit(), false);
						}
					}
				}
				else {
					log.debug(".playNextCard(): I can match the demanded color, and it's our trick already...");
					// 1.1.2: if no: find highest matching card
					if(initialCard.isTrump(gameType)) {
					    bestToBePlayed = cards.getLastIndexOfSuit(gameType.getTrumpSuit());
						if(bestToBePlayed < 0) {
							log.debug(".playNextCard(): Damn! I have to play a Jack...");
							bestToBePlayed = 0;
							while(cards.size()>bestToBePlayed+1 && cards.get(bestToBePlayed+1).getRank()==Rank.JACK) bestToBePlayed++;
						}
					}
					else {
						bestToBePlayed = cards.getLastIndexOfSuit(initialCard.getSuit());
					}
				}
			}
			else {
				// 1.2: if no (i.e. I can't match the initial suit): is ours?
				if(Helper.isSinglePlayerWin(knowledge)) {
					// 1.2.1: if no: do I have trump? 
					log.debug(".playNextCard(): I cannot match and trick is SinglePlayerWin");
					if(cards.hasTrump(gameType)) {
						// 1.2.1.1: if yes: do I want to trump
						if(knowledge.getTrickCards().getTotalValue() > 3) {
							log.debug(".playNextCard(): But I can and will take it...");
							// 1.2.1.1.1: if yes: find a good trump
							bestToBePlayed = findValuableTrump(cards, trumpSuit);
						}
						else {
							log.debug(".playNextCard(): I could trump, but I don't want to...");
							// 1.2.1.1.2: if no: find a low card
							bestToBePlayed = findLowCard(cards, trumpSuit);
						}
					} 
					else {
						log.debug(".playNextCard(): I'd love to have it, but I can't match the initial suit...");
						// 1.2.1.2: if no: find a low card
						bestToBePlayed = findLowCard(cards, trumpSuit);
					}
				}
				else {
					log.debug(".playNextCard(): I cannot match but it's our trick already...");
					// 1.2.2: if yes: find highest value card (but no ace)
					bestToBePlayed = findHighCard(cards, trumpSuit);
					log.debug(".playNextCard(): got back value "+bestToBePlayed );
					
				}
			}
		}
				
		else if (initialCard!=null) {
            
			// Only one card is played in this trick yet
			// the player is forced to play after the color
			// of the first card
            
			// At first: Is trump played?
			if (initialCard.getSuit() == trumpSuit ||
					initialCard.getRank() == Rank.JACK) {
                
				// Trump is played
				log.debug(".playNextCard(): first card is trump...");
				// Does the player have trump?
				if (Helper.hasTrump(cards, trumpSuit)) {
                    
					// Play the highest trump
					// Check whether there is a Jack in the cards or not
					if (cards.contains(Card.CJ)) {
						bestToBePlayed = cards.getIndexOf(Card.CJ);
					} else if (cards.contains(Card.SJ)) {
						bestToBePlayed = cards.getIndexOf(Card.SJ);
					} else if (cards.contains(Card.HJ)) {
						bestToBePlayed = cards.getIndexOf(Card.HJ);
					} else if (cards.contains(Card.DJ)) {
						bestToBePlayed = cards.getIndexOf(Card.DJ);
					} else {
						// No Jack in the cards
						log.debug(".playNextCard(): ... but i don't have a jack...");
						bestToBePlayed = cards.getLastIndexOfSuit(trumpSuit);
					}
                    
				} else {
                    
					// Player doesn't have trump
					// it doesn't matter what card is played
					// just play the first card in the CardList
					bestToBePlayed = findLowCard(cards, trumpSuit);
					log.debug(".playNextCard(): ... but i don't have any trumps...");
				}
                
			} else {
                
				log.debug(".playNextCard(): first card is a color card...");
				// If trump is not played the player is forced
				// to play the color of the first card
				if (cards.hasSuit(gameType, initialCard.getSuit())) {
                    
					// Player has the color
					// check if it's ours or if I can beat 
					if(Helper.isSinglePlayerWin(knowledge)) {
						bestToBePlayed = Helper.isAbleToBeat(cards, initialCard, initialCard, gameType); 
						if(bestToBePlayed < 0) {
							log.debug(".playNextCard(): ...which i can't beat...");
							bestToBePlayed = cards.getFirstIndexOfSuit(initialCard.getSuit());
						}
					}
					else {
						// Play the card with the highest value
						log.debug(".playNextCard(): ...to which i try to put some value...");
						bestToBePlayed = cards.getLastIndexOfSuit(initialCard.getSuit());
					}
					
                    
				} else {
                    
					log.debug(".playNextCard(): ...which i don't have...");
					// Player doesn't have the color
					// is there any trump in the cards
					if (Helper.hasTrump(cards, trumpSuit)) {
                        
						// Play the highest trump
						// Check whether there is a Jack in the cards or not
						if (cards.contains(Card.CJ)) {
                            
							bestToBePlayed = cards.getIndexOf(Card.CJ);
                            
						} else if (cards.contains(Card.SJ)) {
                            
							bestToBePlayed = cards.getIndexOf(Card.SJ);
                            
						} else if (cards.contains(Card.HJ)) {
                            
							bestToBePlayed = cards.getIndexOf(Card.HJ);
                            
						} else if (cards.contains(Card.DJ)) {
                            
							bestToBePlayed = cards.getIndexOf(Card.DJ);
                            
						} else {
                            
							// No Jack in the cards
							bestToBePlayed = cards.getLastIndexOfSuit(trumpSuit);
						}
                        
						log.debug(".playNextCard(): ...so i take it...");
					} else {
                        
						// it doesn't matter what card is played
						// TODO check card value!
						bestToBePlayed = cards.size() - 1;
						log.debug(".playNextCard(): ...but i can't take it...");
					}
				}
			}
            
			log.debug(".playNextCard(): player " + "@@playerID" + ": " + cards.get(bestToBePlayed));
		} else {
            
			// No card on the table yet
			
			bestToBePlayed = findInitial(cards, gameType);
			log.debug(".playNextCard(): (in forehand) " + "@@playerID" + ": " + cards.get(bestToBePlayed));
		}
        
		if(bestToBePlayed < 0 || bestToBePlayed > cards.size()-1) {
			log.debug("----- Error in finding a good opponent card: "+bestToBePlayed+" -----");
			bestToBePlayed = 0;
		}
		
		log.debug("Playing "+cards.get(bestToBePlayed));

		if(bestToBePlayed<0) {
			log.warn("Can't find a suitable card!");
			return null;
		}
		return cards.remove(bestToBePlayed);
	}

	/**
	 * Gets the next card that the player wants to play in a null game
	 * @param cards
	 * @param trickInfo
	 * @return the index of the card that should be played
	 */
	private Card playNextCardNullGame(PlayerKnowledge knowledge) {
		int bestToBePlayed = -1;
		log.debug(".playNextCardNullGame(): cards: ["+cards+"]");
		Card initialCard = knowledge.getTrickCards().get(0);
        
		if (knowledge.getTrickCards().size() > 0) {
			if(!cards.hasSuit(knowledge.getGame().getGameType(), initialCard.getSuit())) {
				// TODO null game: abwerfen
				log.debug(".playNextCardNullGame(): abwerfen...");
				bestToBePlayed = 0;
			}
			else {
				// do I have a lower card?
				int toBePlayed = findLowerCard(cards, initialCard);
				if(toBePlayed < 0) {
					// no: take the highest one of that suit
					bestToBePlayed = cards.getFirstIndexOfSuit(initialCard.getSuit());
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
		log.debug(".playNextCardNullGame(): playing: ["+cards.get(bestToBePlayed)+"]");
		if(bestToBePlayed<0) return null;
		return cards.remove(bestToBePlayed);
	}

	/**
	 * Chooses the highest valued trump card
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findValuableTrump(CardList cards, Suit trump) {
		// TODO consider, which other trumps have already been played
		if (cards.size()<1) return 0;
		int highCard = 0;
		int index = 0;
		while (++index<cards.size()) {
//			if(cards.get(index).isTrump(GameType.SUIT, trump) && cards.get(index).getPoints() > cards.get(highCard).getPoints()) { 
//				highCard = index;
//				log.debug("     highest card set to "+index);
//			}
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
	private int findMostValuableMatchingCard(CardList cards, Suit suit, Suit trump) {
		if (cards.size()<1) return 0;
		int highCard = 0;
		int index = 0;
		while (++index<cards.size()) {
//			if(!cards.get(index).isTrump(GameType.SUIT, trump) 
//					&& cards.get(index).getPoints() > cards.get(highCard).getPoints() 
//					&& cards.get(index).getRank() != Rank.ACE 
//					&& cards.get(index).getSuit() == suit) { 
//				highCard = index;
//			}
		}
		return (highCard<cards.size()?highCard:0);
	}

	/**
	 * Chooses the highest valued card that is not trump (and not an ace)
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findHighCard(CardList cards, Suit trump) {
	    // TODO: add a flag whether aces should be included
	    //       or just consider the CardMemory
	    if (cards.size()<1) return 0;
		int highCard = 0;
		int index = 0;
		while (++index<cards.size()) {
//		    if(cards.get(highCard).isTrump(GameType.SUIT, trump) && 
//		            !cards.get(index).isTrump(GameType.SUIT, trump) &&
//		            cards.get(index).getRank() != Rank.ACE) {
//				highCard = index;
//		    }
//			else if(!cards.get(index).isTrump(GameType.SUIT, trump) && 
//			        cards.get(index).getPoints() > cards.get(highCard).getPoints() && 
//			        cards.get(index).getRank() != Rank.ACE) { 
//				highCard = index;
//			}
		}
		return (highCard<cards.size()?highCard:0);
	}

	/**
	 * Selects the first card of the hand that has no value and is not trump
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findLowCard(CardList cards, Suit trump) {
		if (cards.size()<2) return 0;
		int lowCard = 0;
		int index = 0;
		boolean found = false;
		while (!found && ++index<cards.size()) {
//			if(!cards.get(index).isTrump(GameType.SUIT, trump) && cards.get(index).getPoints() < cards.get(lowCard).getPoints()) {
//			    lowCard = index;
//			}
//			if(cards.get(lowCard).getPoints() == 0) found = true;
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
	private int findLowerCard(CardList cards, Card card) {
		int index = -1;
		int possibleCards = Helper.suitCardsToBinaryNullGame(cards, card.getSuit());
		int counter = 0;
		while(possibleCards > 0 && card.getNullOrder()>counter && counter < 8) {
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
	private int findInitialForNullGame(CardList cards) {
		// TODO initial card for null game
		return cards.size()-1;
	}

	/**
	 * Finds an initial card to play
	 * @param cards
	 * @param game
	 * @return index of the card
	 */
	private int findInitial(CardList cards, GameType game) {
		for(int x=0;x<cards.size();x++)
		{
			if (cards.get(x).getRank() == Rank.ACE)
					if(cards.get(x).getSuit() != game.getTrumpSuit()) return x;
		}
		// If you don't have any, find a low face card
		log.debug(".findInitial(): no Ace found");
		int store = 0;
		for(int x=0;x<cards.size();x++)
		{
			Rank cardRank = cards.get(x).getRank();
			
			if (cardRank == Rank.SEVEN || // "7"
					cardRank == Rank.EIGHT || // "8"
					cardRank == Rank.NINE) { // "9"
				// If you find one and it is a trumpf, remember it for later (in case you find nothing else)
				if(cards.get(x).getSuit() != game.getTrumpSuit()) return x;
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
	 * log
	 */
	private Log log = LogFactory.getLog(OpponentPlayer.class);

}
