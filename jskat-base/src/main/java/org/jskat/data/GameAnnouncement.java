package org.jskat.data;

import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record GameAnnouncement(
        GameType gameType,
        boolean hand,
        boolean schneider,
        boolean schwarz,
        boolean ouvert,
        CardList ouvertCards) {

    public GameAnnouncement(GameType gameType) {
        this(gameType, false, false, false, false, new CardList());
    }

    public GameAnnouncement(GameType gameType, boolean hand) {
        this(gameType, hand, false, false, false, new CardList());
    }

    public GameAnnouncement(GameType gameType, boolean hand, boolean schneider) {
        this(gameType, hand, schneider, false, false, new CardList());
    }

    public GameAnnouncement(GameType gameType, boolean hand, boolean schneider, boolean schwarz) {
        this(gameType, hand, schneider, schwarz, false, new CardList());
    }

    public GameAnnouncement(GameType gameType, boolean ouvert, CardList ouvertCards) {
        this(gameType, false, false, false, ouvert, ouvertCards);
    }

    public GameAnnouncement(GameType gameType, boolean hand, boolean ouvert, CardList ouvertCards) {
        this(gameType, hand, false, false, ouvert, ouvertCards);
    }

    public static Builder builder(GameType gameType) {
        return new Builder(gameType);
    }

    public static final class Builder {
        private final Logger LOG = LoggerFactory.getLogger(GameAnnouncement.Builder.class);
        private final GameType gameType;
        private boolean hand;
        private boolean schneider;
        private boolean schwarz;
        private boolean ouvert;
        private final CardList ouvertCards = new CardList();

        private Builder(GameType gameType) {
            this.gameType = gameType;
        }

        public Builder hand() {
            this.hand = true;
            return this;
        }

        public Builder schneider() {
            this.schneider = true;
            return this;
        }

        public Builder schwarz() {
            this.schwarz = true;
            return this;
        }

        public Builder ouvert(CardList ouvertCards) {
            this.ouvert = true;
            this.ouvertCards.addAll(ouvertCards);
            return this;
        }

        public GameAnnouncement build() {
            if (isValid()) {
                return new GameAnnouncement(gameType, hand, schneider, schwarz, ouvert, ouvertCards);
            }

            throw new IllegalStateException("Invalid game announcement.");
        }

        private boolean isValid() {
            boolean isValid = true;

            if (gameType == null) {
                LOG.warn("Validation failed: Missing game type.");
                isValid = false;
            }
            if (ouvert && (ouvertCards.size() != 10)) {
                LOG.warn("Validation failed: Wrong number of ouvert cards in ouvert game.");
                isValid = false;
            }
            if (!ouvert && !ouvertCards.isEmpty()) {
                LOG.warn("Validation failed: Wrong number of ouvert cards.");
                isValid = false;
            }
            if (gameType == GameType.RAMSCH || gameType == GameType.PASSED_IN) {
                if (hand || ouvert || ouvertCards.size() != 0 || schneider || schwarz) {
                    LOG.warn("Validation failed: Invalid modifiers in Ramsch or passed in game.");
                    isValid = false;
                }
            }
            if (gameType == GameType.NULL && (schneider || schwarz)) {
                LOG.warn("Validation failed: Invalid modifier in Null game.");
                isValid = false;
            }
            if (gameType == GameType.CLUBS
                    || gameType == GameType.SPADES
                    || gameType == GameType.HEARTS
                    || gameType == GameType.DIAMONDS
                    || gameType == GameType.GRAND) {

                if (ouvert && (!hand || !schneider || !schwarz)) {
                    LOG.warn("Validation failed: Invalid modifier in Suit or Grand game.");
                    isValid = false;
                }
                if (schwarz && !schneider) {
                    LOG.warn("Validation failed: Invalid modifier in Suit or Grand game.");
                    isValid = false;
                }
                if ((schwarz || schneider) && !hand) {
                    LOG.warn("Validation failed: Invalid modifier in Suit or Grand game.");
                    isValid = false;
                }
            }

            if (!isValid) {
                LOG.warn("Invalid game announcement " + this);
            }

            return isValid;
        }

        @Override
        public String toString() {
            return "GameAnnouncement{" +
                    "gameType=" + gameType +
                    ", hand=" + hand +
                    ", schneider=" + schneider +
                    ", schwarz=" + schwarz +
                    ", ouvert=" + ouvert +
                    ", ouvertCards=" + ouvertCards +
                    '}';
        }
    }
}
