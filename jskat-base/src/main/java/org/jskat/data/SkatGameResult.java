package org.jskat.data;

import java.util.Objects;

/**
 * Data class for skat game result
 */
public class SkatGameResult implements Cloneable {

    private int gameValue;
    private int finalDeclarerPoints;
    private int finalOpponentPoints;
    private boolean playWithJacks;
    private int matadors;
    private boolean won;
    private boolean overBid;
    private boolean schneider;
    private boolean schwarz;
    private boolean durchmarsch;
    private boolean jungfrau;

    /**
     * Constructor
     */
    public SkatGameResult() {
        gameValue = -1;
        matadors = 0;
        finalDeclarerPoints = 0;
        finalOpponentPoints = 0;
        playWithJacks = false;
        won = false;
        overBid = false;
        schneider = false;
        schwarz = false;
        durchmarsch = false;
        jungfrau = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkatGameResult clone() {

        final SkatGameResult result = new SkatGameResult();
        result.setGameValue(gameValue);
        result.setMatadors(matadors);
        result.setPlayWithJacks(playWithJacks);
        result.setWon(won);
        result.setOverBid(overBid);
        result.setSchneider(schneider);
        result.setSchwarz(schwarz);
        result.setDurchmarsch(durchmarsch);
        result.setJungfrau(jungfrau);

        return result;
    }

    /**
     * Gets whether the game was won
     *
     * @return TRUE, if the game was won
     */
    public boolean isWon() {
        return won;
    }

    /**
     * Sets whether the game was won
     *
     * @param won TRUE, if the game was won
     */
    public void setWon(final boolean won) {
        this.won = won;
    }

    /**
     * Gets whether the declarer did overbid
     *
     * @return TRUE, if the declarer did overbid
     */
    public boolean getOverBid() {
        return overBid;
    }

    /**
     * Sets whether the declarer did overbid
     *
     * @param overBid TRUE, if the declarer did overvid
     */
    public void setOverBid(final boolean overBid) {
        this.overBid = overBid;
    }

    /**
     * Gets whether the game was a schneider game
     *
     * @return TRUE, if the game was a schneider game
     */
    public boolean isSchneider() {
        return schneider;
    }

    /**
     * Sets whether the game was a schneider game
     *
     * @param schneider TRUE, if the game was a schneider game
     */
    public void setSchneider(final boolean schneider) {
        this.schneider = schneider;
    }

    /**
     * Gets whether the game was a schwarz game
     *
     * @return TRUE, if the game was a schwarz game
     */
    public boolean isSchwarz() {
        return schwarz;
    }

    /**
     * Sets whether the game was a schwarz game
     *
     * @param isSchwarz TRUE, if the game was a schwarz game
     */
    public void setSchwarz(final boolean isSchwarz) {
        schwarz = isSchwarz;
        if (schwarz) {
            // a schwarz game is always schneider
            schneider = true;
        }
    }

    /**
     * Gets whether the game was a durchmarsch game
     *
     * @return TRUE, if the game was a durchmarsch game
     */
    public boolean isDurchmarsch() {
        return durchmarsch;
    }

    /**
     * Sets whether the game was a durchmarsch game<br>
     * If the game is a durchmarsch, then jungfrau is automatically set to
     * false.
     *
     * @param durchmarsch TRUE, if the game was a durchmarsch game
     */
    public void setDurchmarsch(final boolean durchmarsch) {
        if (durchmarsch) {
            setJungfrau(false);
        }
        this.durchmarsch = durchmarsch;
    }

    /**
     * Gets whether the game was a jungfrau game
     *
     * @return TRUE, if the game was a jungfrau game
     */
    public boolean isJungfrau() {
        return jungfrau;
    }

    /**
     * Sets whether the game was a jungfrau game<br>
     * Note: The jungfrau flag is overwritten, if durchmarsch is set to true
     *
     * @param isJungfrau TRUE, if the game was a jungfrau game
     */
    public void setJungfrau(final boolean isJungfrau) {
        jungfrau = isJungfrau;
    }

    /**
     * Gets the game result
     *
     * @return Game result
     */
    public int getGameValue() {
        return gameValue;
    }

    /**
     * Sets the game result
     *
     * @param gameValue Game result
     */
    public void setGameValue(final int gameValue) {
        this.gameValue = gameValue;
    }

    /**
     * Gets whether the declarer played with or without jacks
     *
     * @return TRUE, if the declarer played with jacks
     */
    public boolean isPlayWithJacks() {
        return playWithJacks;
    }

    /**
     * Sets whether the declarer played with or without jacks
     *
     * @param playWithJacks TRUE, if the declarer played with jacks
     */
    public void setPlayWithJacks(final boolean playWithJacks) {
        this.playWithJacks = playWithJacks;
    }

    /**
     * Gets the number of matadors (Jacks and trump cards in a row without gaps).
     *
     * @return Number of matadors
     */
    public int getMatadors() {
        return matadors;
    }

    /**
     * Sets the number of matadors.
     *
     * @param matadors Number of matadors.
     */
    public void setMatadors(final int matadors) {
        this.matadors = matadors;
    }

    /**
     * Sets the final opponent points
     *
     * @param points Final opponent points
     */
    public void setFinalOpponentPoints(final int points) {
        finalOpponentPoints = points;
    }

    /**
     * Gets the final opponent points
     *
     * @return Final opponent points
     */
    public int getFinalOpponentPoints() {
        return finalOpponentPoints;
    }

    /**
     * Sets the final declarer points
     *
     * @param points Final declarer points
     */
    public void setFinalDeclarerPoints(final int points) {
        finalDeclarerPoints = points;
    }

    /**
     * Gets the final declarer points
     *
     * @return Final declarer points
     */
    public int getFinalDeclarerPoints() {
        return finalDeclarerPoints;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(gameValue);
        if (matadors > 0) {
            if (playWithJacks) {
                sb.append(" with: ");
            } else {
                sb.append(" without: ");
            }
            sb.append(matadors).append(" play ").append(matadors + 1);
        }
        if (overBid) {
            sb.append(" (overbid)");
        }
        if (durchmarsch) {
            sb.append(" (Durchmarsch)");
        }
        if (jungfrau) {
            sb.append(" (Jungfrau)");
        }
        if (schwarz) {
            sb.append(" (Schwarz)");
        } else if (schneider) {
            sb.append(" (Schneider)");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(durchmarsch, finalDeclarerPoints, finalOpponentPoints, gameValue,
                        jungfrau, matadors, overBid, playWithJacks, schneider,
                        schwarz, won);
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
        final SkatGameResult other = (SkatGameResult) obj;

        return Objects.equals(durchmarsch, other.durchmarsch) &&
                Objects.equals(finalDeclarerPoints, other.finalDeclarerPoints) &&
                Objects.equals(finalOpponentPoints, other.finalOpponentPoints) &&
                Objects.equals(gameValue, other.gameValue) &&
                Objects.equals(jungfrau, other.jungfrau) &&
                Objects.equals(matadors, other.matadors) &&
                Objects.equals(overBid, other.overBid) &&
                Objects.equals(playWithJacks, other.playWithJacks) &&
                Objects.equals(schneider, other.schneider) &&
                Objects.equals(schwarz, other.schwarz) &&
                Objects.equals(won, other.won);
    }
}
