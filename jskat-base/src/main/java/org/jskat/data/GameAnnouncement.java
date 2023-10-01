package org.jskat.data;

import org.jskat.util.CardList;
import org.jskat.util.GameType;

import java.util.Objects;

/**
 * Game announcement done by the declarer
 *
 * @param contract       Game contract
 * @param discardedCards Discarded cards of the declarer in game when the declarer looked into the skat
 */
public record GameAnnouncement(GameContract contract, CardList discardedCards) {

    public GameAnnouncement {
        Objects.requireNonNull(contract);
        Objects.requireNonNull(discardedCards);

        if (GameType.GRAND_SUIT_NULL.contains(contract.gameType())) {
            if (contract.hand() && discardedCards.size() != 0) {
                throw new IllegalArgumentException("No discarded cards allowed in hand games.");
            }
            if (!contract.hand() && discardedCards.size() != 2) {
                throw new IllegalArgumentException("Two discarded cards needed in non hand games.");
            }
        }
        if (GameType.RAMSCH_PASSED_IN.contains(contract.gameType())) {
            if (discardedCards.size() != 0) {
                throw new IllegalArgumentException("No discarded cards allowed in hand games.");
            }
        }
    }

    public GameAnnouncement(final GameContract contract) {
        this(contract, CardList.empty());
    }
}
