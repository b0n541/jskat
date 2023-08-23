package org.jskat.ai.sascha;

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Rank;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlayer {
    protected final ImmutablePlayerKnowledge k;
    protected SkatRule rules;
    private static final Logger log = LoggerFactory.getLogger(AIPlayerSascha.class);

    public AbstractPlayer(final ImmutablePlayerKnowledge k) {
        this.k = k;
        this.rules = SkatRuleFactory.getSkatRules(k.getGameType());
    }

    protected abstract Card foreHand();

    protected abstract Card midHand(Card firstCard);

    protected abstract Card rearHand(Card firstCard, Card secondCard);

    protected abstract void beforeCard();

    protected abstract void afterTrick(Trick t);

    protected boolean isTrump(Card c){
        return (c.getRank() == Rank.JACK || c.getSuit() == k.getTrumpSuit());
    }

    protected Card getPlayableCard() {

        boolean isCardAllowed;

        log.info("playing fallback card");

        var trick = k.getCurrentTrick().getCardList();

        for (final Card card : k.getOwnCards()) {
            if (trick.size() > 0 &&
                    rules.isCardAllowed(k.getGameType(), trick.get(0), k.getOwnCards(), card)) {
                isCardAllowed = true;
            } else {
                isCardAllowed = trick.size() == 0;
            }

            if (isCardAllowed) {
                return card;
            }
        }
        return null;

    }

    public Card playCard() {

        if (k.getNoOfTricks() > 0) {
            Trick t = k.getCompletedTricks().get(k.getNoOfTricks() - 1);
            afterTrick(t);
        }

        beforeCard();

        if (k.getTrickCards() == null || k.getTrickCards().isEmpty())
            return foreHand();

        if (k.getTrickCards().size() == 1)
            return midHand(k.getCurrentTrick().getFirstCard());

        return rearHand(k.getCurrentTrick().getFirstCard(), k.getCurrentTrick().getSecondCard());

    }

}
