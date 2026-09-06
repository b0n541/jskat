package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.Objects;

/**
 * Event for picking up the skat.
 */
public final class PickUpSkatEvent extends AbstractPlayerMoveEvent {

    public final CardList pickedUpSkat;

    public PickUpSkatEvent(Player player) {
        this(player, new CardList());
    }

    public PickUpSkatEvent(Player player, CardList pickedUpSkat) {
        super(player);
        this.pickedUpSkat = pickedUpSkat.getImmutableCopy();
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.setSkatPickUp(true);
        data.addSkatToPlayer(player);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.setSkatPickUp(false);
        data.removeSkatFromPlayer(player);
    }

    @Override
    protected String getMoveDetails() {
        return "pick up skat";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pickedUpSkat);
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
        final PickUpSkatEvent other = (PickUpSkatEvent) obj;

        return Objects.equals(pickedUpSkat, other.pickedUpSkat);
    }
}
