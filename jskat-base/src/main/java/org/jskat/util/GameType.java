package org.jskat.util;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contains all game types
 */
public enum GameType {
    /**
     * Suit game with Clubs as trump
     */
    CLUBS {
        @Override
        public Suit getTrumpSuit() {
            return Suit.CLUBS;
        }
    },
    /**
     * Suit game with Spades as trump
     */
    SPADES {
        @Override
        public Suit getTrumpSuit() {
            return Suit.SPADES;
        }
    },
    /**
     * Suit game with Hearts as trump
     */
    HEARTS {
        @Override
        public Suit getTrumpSuit() {
            return Suit.HEARTS;
        }
    },
    /**
     * Suit game with Diamonds as trump
     */
    DIAMONDS {
        @Override
        public Suit getTrumpSuit() {
            return Suit.DIAMONDS;
        }
    },
    /**
     * Grand game
     */
    GRAND {
        @Override
        public Suit getTrumpSuit() {
            return null;
        }
    },
    /**
     * Null game
     */
    NULL {
        @Override
        public Suit getTrumpSuit() {
            return null;
        }
    },
    /**
     * Ramsch game, this is not playable under ISPA rules
     */
    RAMSCH {
        @Override
        public Suit getTrumpSuit() {
            return null;
        }
    },
    /**
     * Passed in game, no player did a bid
     */
    PASSED_IN {
        @Override
        public Suit getTrumpSuit() {
            return null;
        }
    };

    /**
     * All game types that are announced by a declarer.
     */
    public static final Set<GameType> GRAND_SUIT_NULL = Set.of(
                    GameType.GRAND,
                    GameType.CLUBS,
                    GameType.SPADES,
                    GameType.HEARTS,
                    GameType.DIAMONDS,
                    GameType.NULL).stream()
            .collect(Collectors.toUnmodifiableSet());

    /**
     * Same as {@link GameType#GRAND_SUIT_NULL} but without NULL games.
     */
    public static final Set<GameType> GRAND_SUIT = GRAND_SUIT_NULL.stream()
            .filter(gameType -> NULL != gameType)
            .collect(Collectors.toUnmodifiableSet());

    public static final Set<GameType> RAMSCH_PASSED_IN = Set.of(RAMSCH, PASSED_IN);

    /**
     * Gets the trump suit
     *
     * @return Trump suit, null if there is no trump suit
     */
    public abstract Suit getTrumpSuit();
}
