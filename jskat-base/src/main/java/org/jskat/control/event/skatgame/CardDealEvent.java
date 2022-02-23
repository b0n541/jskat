package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Event for card dealing.
 */
public final class CardDealEvent implements SkatGameEvent {

    public final Map<Player, CardList> playerCards;
    public final CardList skat;

    public CardDealEvent(Map<Player, CardList> playerCards, CardList skat) {
        this.playerCards = Collections.unmodifiableMap(playerCards);
        this.skat = skat.getImmutableCopy();
    }

    @Override
    public final void processForward(SkatGameData data) {
        for (Player player : playerCards.keySet()) {
            data.addDealtCards(player, playerCards.get(player));
        }
        data.setDealtSkatCards(skat);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        for (Player player : playerCards.keySet()) {
            data.removeDealtCards(player, playerCards.get(player));
        }
        data.removeDealtSkatCards(skat);
    }

    @Override
    public String toString() {
        String result = "Dealt cards:\n";
        for (Entry<Player, CardList> entry : playerCards.entrySet()) {
            result += entry.getKey() + ": " + entry.getValue() + "\n";
        }
        result += "Skat: " + skat;
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerCards, skat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CardDealEvent other = (CardDealEvent) obj;

        return Objects.equals(playerCards, other.playerCards) &&
                Objects.equals(skat, other.skat);
    }

}
