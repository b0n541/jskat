package org.jskat.ai.sascha;

import java.util.HashMap;

import org.jskat.ai.newalgorithm.AlgorithmAI;
import org.jskat.ai.sascha.solo.SuitHelper;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public abstract class AbstractPlayer {
    protected final AlgorithmAI p;
    protected final ImmutablePlayerKnowledge k;
    protected CardList oppCardList;
    protected SkatRule rules;
    protected HashMap<Suit, SuitHelper> suits = new HashMap<Suit, SuitHelper>();

    public AbstractPlayer(final AlgorithmAI p, final ImmutablePlayerKnowledge k) {
        this.p = p;
        this.k = k;
        this.rules = SkatRuleFactory.getSkatRules(k.getGameType());

        oppCardList = new CardList(Card.values());
        for (Card c : k.getOwnCards()) {
            oppCardList.remove(c);
        }
        for (Card c : k.getSkat()) {
            oppCardList.remove(c);
        }

        for (Suit s : Suit.values()) {
            if (s != k.getTrumpSuit()) {
                suits.put(s, new SuitHelper(s, k.getOwnCards()));
            }
        }
    }

    protected abstract Card foreHand();

    protected abstract Card midHand(Card firstCard);

    protected abstract Card rearHand(Card firstCard, Card secondCard);

    protected abstract void beforeCard();

    protected abstract void afterTrick(Trick t);

    public Card playCard() {
        oppCardList.removeAll(k.getTrickCards());

        if (k.getNoOfTricks() > 0) {
            Trick t = k.getCompletedTricks().get(k.getNoOfTricks() - 1);
            oppCardList.removeAll(t.getCardList());
            for (SuitHelper sh : this.suits.values()) {
                sh.registerTrick(t);
            }
            afterTrick(t);
        }

        beforeCard();

        if (k.getNoOfTricks() > 0)
            oppCardList.removeAll(k.getCompletedTricks().get(k.getNoOfTricks() - 1).getCardList());

        if (p.getPlayableCards(k.getTrickCards()).size() == 1)
            return p.getPlayableCards(k.getTrickCards()).get(0);

        if (k.getTrickCards() == null || k.getTrickCards().isEmpty())
            return foreHand();

        if (k.getTrickCards().size() == 1)
            return midHand(k.getCurrentTrick().getFirstCard());

        return rearHand(k.getCurrentTrick().getFirstCard(), k.getCurrentTrick().getSecondCard());

    }

}
