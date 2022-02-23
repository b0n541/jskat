package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.Objects;

/**
 * Event for discard skat.
 */
public final class DiscardSkatEvent extends AbstractPlayerMoveEvent {

    public final CardList discardedSkat = new CardList();

    public DiscardSkatEvent(Player player, CardList discardedSkat) {
        super(player);
        this.discardedSkat.addAll(discardedSkat);
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.setDiscardedSkat(player, discardedSkat);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.setSkatCards(new CardList());
        data.addPlayerCards(player, discardedSkat);
    }

    @Override
    protected String getMoveDetails() {
        return discardedSkat.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(discardedSkat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiscardSkatEvent other = (DiscardSkatEvent) obj;

        return Objects.equals(discardedSkat, other.discardedSkat);
    }
}
