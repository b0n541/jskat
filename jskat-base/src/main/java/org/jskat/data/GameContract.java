package org.jskat.data;

import org.jskat.util.CardList;
import org.jskat.util.GameType;

import java.util.Objects;


/**
 * Contract for the game to play as announced by the declarer.
 *
 * @param gameType    Game type
 * @param hand        Hand game
 * @param schneider   Schneider announced
 * @param schwarz     Schwarz announced
 * @param ouvert      Ouvert game
 * @param ouvertCards Declarer cards in an ouvert game
 */
public record GameContract(
        GameType gameType,
        boolean hand,
        boolean schneider,
        boolean schwarz,
        boolean ouvert,
        CardList ouvertCards) {

    public GameContract {
        Objects.requireNonNull(gameType);
        Objects.requireNonNull(ouvertCards);

        if (ouvert && (ouvertCards.size() != 10)) {
            throw new IllegalArgumentException("Validation failed: Wrong number of ouvert cards in ouvert game.");
        }
        if (!ouvert && !ouvertCards.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: Wrong number of ouvert cards.");
        }
        if (gameType == GameType.RAMSCH || gameType == GameType.PASSED_IN) {
            if (hand || ouvert || ouvertCards.size() != 0 || schneider || schwarz) {
                throw new IllegalArgumentException("Validation failed: Invalid modifiers in Ramsch or passed in game.");
            }
        }
        if (gameType == GameType.NULL && (schneider || schwarz)) {
            throw new IllegalArgumentException("Validation failed: Invalid modifier in Null game.");
        }
        if (GameType.GRAND_SUIT.contains(gameType)) {
            if (ouvert && (!hand || !schneider || !schwarz)) {
                throw new IllegalArgumentException("Validation failed: Invalid modifier in Suit or Grand game.");
            }
            if (schwarz && !schneider) {
                throw new IllegalArgumentException("Validation failed: Invalid modifier in Suit or Grand game.");
            }
            if ((schwarz || schneider) && !hand) {
                throw new IllegalArgumentException("Validation failed: Invalid modifier in Suit or Grand game.");
            }
        }
    }

    public GameContract(final GameType gameType) {
        this(gameType, false, false, false, false, new CardList());
    }

    public GameContract(final GameType gameType, final boolean hand) {
        this(gameType, hand, false, false, false, new CardList());
    }

    public GameContract(final GameType gameType, final boolean ouvert, final CardList ouvertCards) {
        this(
                gameType,
                GameType.GRAND_SUIT.contains(gameType),
                GameType.GRAND_SUIT.contains(gameType),
                GameType.GRAND_SUIT.contains(gameType),
                ouvert,
                ouvertCards);
    }

    public GameContract(final GameType gameType, final boolean schneider, final boolean schwarz) {
        this(gameType, true, schneider, schwarz, false, new CardList());
    }

    public GameContract withHand() {
        return new GameContract(gameType,
                true,
                schneider,
                schwarz,
                ouvert,
                ouvertCards);
    }

    public GameContract withSchneider() {
        return new GameContract(gameType,
                GameType.GRAND_SUIT.contains(gameType),
                GameType.GRAND_SUIT.contains(gameType),
                schwarz,
                ouvert,
                ouvertCards);
    }

    public GameContract withSchwarz() {
        return new GameContract(gameType,
                GameType.GRAND_SUIT.contains(gameType),
                GameType.GRAND_SUIT.contains(gameType),
                GameType.GRAND_SUIT.contains(gameType),
                ouvert,
                ouvertCards);
    }

    public GameContract withOuvert(final CardList ouvertCards) {
        return new GameContract(gameType,
                gameType == GameType.NULL && hand,
                gameType == GameType.NULL && schneider,
                gameType == GameType.NULL && schwarz,
                true,
                ouvertCards);
    }
}
