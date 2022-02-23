package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRuleFactory;

import java.util.Objects;

/**
 * Event for a card played in the trick.
 */
public final class TrickCardPlayedEvent extends AbstractPlayerMoveEvent {

    public final Card card;

    public TrickCardPlayedEvent(final Player player, final Card card) {
        super(player);
        this.card = card;
    }

    @Override
    public void processForward(final SkatGameData data) {

        data.removePlayerCard(player, card);

        if (isNoTricksPlayed(data)) {
            data.addTrick(new Trick(0, Player.FOREHAND));
        }

        data.addTrickCard(card);

        if (isTrickCompleted(data)) {
            final Trick trick = data.getCurrentTrick();
            final Player trickWinner = SkatRuleFactory.getSkatRules(
                    data.getGameType()).calculateTrickWinner(
                    data.getGameType(), trick);
            trick.setTrickWinner(trickWinner);
            if (data.getTricks().size() < 10) {
                data.addTrick(new Trick(data.getTricks().size(), trickWinner));
            }
            data.addPlayerPoints(trickWinner, trick.getValue());
        }
    }

    private boolean isNoTricksPlayed(final SkatGameData data) {
        return data.getTricks().size() == 0;
    }

    private boolean isTrickCompleted(final SkatGameData data) {
        return data.getCurrentTrick().getThirdCard() != null;
    }

    @Override
    public void processBackward(final SkatGameData data) {

        if (isEmptyTrick(data)) {
            data.addPlayerPoints(data.getLastCompletedTrick().getTrickWinner(), data.getLastCompletedTrick().getValue() * -1);
            data.removeLastTrick();
        }

        data.removeTrickCard(card);

        data.addPlayerCard(player, card);
    }

    private boolean isEmptyTrick(final SkatGameData data) {
        return data.getCurrentTrick().getFirstCard() == null
                && data.getCurrentTrick().getSecondCard() == null
                && data.getCurrentTrick().getThirdCard() == null;
    }

    @Override
    protected String getMoveDetails() {
        return card.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, card);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TrickCardPlayedEvent other = (TrickCardPlayedEvent) obj;

        return Objects.equals(player, other.player) &&
                Objects.equals(card, other.card);
    }

}
