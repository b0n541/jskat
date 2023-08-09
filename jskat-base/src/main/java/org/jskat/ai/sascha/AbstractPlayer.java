package org.jskat.ai.sascha;


import org.jskat.ai.newalgorithm.AlgorithmAI;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public abstract class AbstractPlayer {
    protected final AlgorithmAI p;
    protected final ImmutablePlayerKnowledge k;
    protected CardList oppCardList;
    protected SkatRule rules;
    

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
    }

    protected abstract Card foreHand();

    protected abstract Card midHand(Card firstCard);

    protected abstract Card rearHand(Card firstCard, Card secondCard);

    protected abstract void beforeCard();

    protected abstract void afterTrick(Trick t);

    protected Card getPlayableCard() {

        boolean isCardAllowed;

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
        oppCardList.removeAll(k.getTrickCards());

        if (k.getNoOfTricks() > 0) {
            Trick t = k.getCompletedTricks().get(k.getNoOfTricks() - 1);
            oppCardList.removeAll(t.getCardList());
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
