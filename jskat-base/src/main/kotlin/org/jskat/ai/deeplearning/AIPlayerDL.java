package org.jskat.ai.deeplearning;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameContract;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Player using deep learning techniques to learn playing Skat.
 */
public class AIPlayerDL extends AbstractAIPlayer {

    private static final Logger log = LoggerFactory.getLogger(AIPlayerDL.class);

    /**
     * Random generator for decision-making.
     */
    private final Random random = new Random();

    /**
     * Creates a new instance of AIPlayerRND.
     */
    public AIPlayerDL() {

        this("unknown");
    }

    /**
     * Creates a new instance of AIPlayerRND.
     *
     * @param newPlayerName Player's name
     */
    public AIPlayerDL(final String newPlayerName) {

        log.debug("Constructing new AIPlayerDL");
        setPlayerName(newPlayerName);
    }

    @Override
    public boolean pickUpSkat() {
        return random.nextBoolean();
    }

    @Override
    public boolean playGrandHand() {
        return random.nextBoolean();
    }

    @Override
    public GameContract announceGame() {
        log.debug("position: " + knowledge.getPlayerPosition());
        log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) +
                " " + knowledge.getHighestBid(Player.MIDDLEHAND) +
                " " + knowledge.getHighestBid(Player.REARHAND));

        // select a random game type (without RAMSCH and PASSED_IN)
        final GameType gameType = GameType.values()[random.nextInt(GameType.values().length - 2)];

        final var contract = new GameContract(gameType);
        if (random.nextBoolean()) {
            contract.withOuvert(knowledge.getOwnCards());
        }

        return contract;
    }

    @Override
    public int bidMore(final int nextBidValue) {
        int result = 0;

        if (random.nextBoolean()) {
            result = nextBidValue;
        }

        return result;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        return random.nextBoolean();
    }

    @Override
    public void startGame() {
        // do nothing
    }

    @Override
    public Card playCard() {

        int index = -1;

        log.debug('\n' + knowledge.toString());

        // first find all possible cards
        final CardList possibleCards = getPlayableCards(knowledge
                .getTrickCards());

        log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards);

        // then choose a random one
        index = random.nextInt(possibleCards.size());

        log.debug("choosing card " + index);
        log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(index));

        return possibleCards.get(index);
    }

    @Override
    public CardList getCardsToDiscard() {
        final CardList result = new CardList();

        final CardList discardableCards = new CardList(knowledge.getOwnCards());

        // just discard two random cards
        result.add(discardableCards.remove(random.nextInt(discardableCards.size())));
        result.add(discardableCards.remove(random.nextInt(discardableCards.size())));

        return result;
    }

    @Override
    public void prepareForNewGame() {
        // nothing to do for AIPlayerRND
    }

    @Override
    public void finalizeGame() {
        // nothing to do for AIPlayerRND
    }

    @Override
    public boolean callContra() {
        return random.nextBoolean();
    }

    @Override
    public boolean callRe() {
        return random.nextBoolean();
    }
}