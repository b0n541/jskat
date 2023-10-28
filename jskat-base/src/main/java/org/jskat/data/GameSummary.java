package org.jskat.data;

import org.jskat.control.SkatGame;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Game summary
 * <p>
 * An object of this class is returned by {@link SkatGame}
 */
public class GameSummary {

    private static final Logger LOG = LoggerFactory.getLogger(GameSummary.class);
    /**
     * Declarer
     */
    public Player declarer;
    /**
     * Game type
     */
    public GameType gameType;
    /**
     * Ouvert game
     */
    public Boolean ouvert = Boolean.FALSE;
    /**
     * Hand game
     */
    public Boolean hand = Boolean.FALSE;
    /**
     * Schneider game
     */
    public Boolean schneider = Boolean.FALSE;
    /**
     * Schwarz game
     */
    public Boolean schwarz = Boolean.FALSE;
    /**
     * Contra game
     */
    public Boolean contra = Boolean.FALSE;
    /**
     * Re game
     */
    public Boolean re = Boolean.FALSE;
    /**
     * Game result
     */
    public SkatGameResult gameResult;
    /**
     * Tricks
     */
    public List<Trick> tricks = new ArrayList<>();
    /**
     * Player points
     */
    public Map<Player, Integer> playerPoints = new HashMap<>();

    public Set<Player> ramschLosers = new HashSet<>();

    /**
     * Constructor
     */
    GameSummary() {
    }

    /**
     * Gets the factory for a {@link GameSummary}
     *
     * @return Factory
     */
    public static GameSummaryFactory getFactory() {
        return new GameSummaryFactory();
    }

    /**
     * Factory for a {@link GameSummary}
     */
    public final static class GameSummaryFactory {

        private GameSummary tmpSummary;

        GameSummaryFactory() {
            tmpSummary = new GameSummary();
        }

        /**
         * Gets the {@link GameSummary}
         *
         * @return Game announcement
         */
        public GameSummary getSummary() {
            final GameSummary result;
            if (validate()) {
                result = tmpSummary;
                tmpSummary = new GameSummary();
            } else {
                throw new RuntimeException("Game summary not valid.");
            }
            return result;
        }

        /**
         * Sets the {@link GameType}
         *
         * @param gameType Game type
         */
        public void setGameType(final GameType gameType) {
            tmpSummary.gameType = gameType;
        }

        /**
         * Sets the flag for a hand game
         *
         * @param isHand TRUE, if a hand game was announced
         */
        public void setHand(final Boolean isHand) {
            tmpSummary.hand = isHand;
        }

        /**
         * Sets the flag for an ouvert game
         *
         * @param isOuvert TRUE, if an ouvert game was announced
         */
        public void setOuvert(final Boolean isOuvert) {
            tmpSummary.ouvert = isOuvert;
        }

        /**
         * Sets the flag for a schneider game
         *
         * @param isSchneider TRUE, if schneider was announced
         */
        public void setSchneider(final Boolean isSchneider) {
            tmpSummary.schneider = isSchneider;
        }

        /**
         * Sets the flag for a schwarz game
         *
         * @param isSchwarz TRUE, if a schwarz was announced
         */
        public void setSchwarz(final Boolean isSchwarz) {
            tmpSummary.schwarz = isSchwarz;
        }

        /**
         * Sets the tricks
         *
         * @param tricks Tricks of the game
         */
        public void setTricks(final List<Trick> tricks) {
            tmpSummary.tricks.clear();
            tmpSummary.tricks.addAll(tricks);
        }

        public void setPlayerPoints(final Map<Player, Integer> playerAndPoints) {
            tmpSummary.playerPoints.putAll(playerAndPoints);
        }

        /**
         * Sets the game result
         *
         * @param gameResult Game result
         */
        public void setGameResult(final SkatGameResult gameResult) {
            tmpSummary.gameResult = gameResult;
        }


        /**
         * Sets the declarer
         *
         * @param position Position of declarer
         */
        public void setDeclarer(final Player position) {
            tmpSummary.declarer = position;
        }

        public void addRamschLooser(final Player looser) {
            tmpSummary.ramschLosers.add(looser);
        }

        private boolean validate() {
            if (tmpSummary.gameType == null) {
                LOG.error("game type is null");
                return false;
            } else if (!GameType.RAMSCH.equals(tmpSummary.gameType) && !GameType.PASSED_IN.equals(tmpSummary.gameType)
                    && tmpSummary.declarer == null) {
                LOG.error("declarer is null");
                return false;
            } else if (tmpSummary.tricks.size() > 10) {
                LOG.error("more than 10 tricks");
                return false;
            } else if (tmpSummary.gameResult == null) {
                LOG.error("game result is null");
                return false;
            } else if (tmpSummary.playerPoints.size() != 3) {
                LOG.error("missing player points");
                return false;
            } else if (tmpSummary.gameType == GameType.RAMSCH && tmpSummary.ramschLosers.size() == 0) {
                LOG.error("missing ramsch looser");
            }
            return true;
        }

        public void setContra(final Boolean contra) {
            tmpSummary.contra = contra;
        }

        public void setRe(final Boolean re) {
            tmpSummary.re = re;
        }
    }

    /**
     * Gets the game type
     *
     * @return Game type
     */
    public final GameType getGameType() {

        return gameType;
    }

    /**
     * Checks whether schneider was announced or not
     *
     * @return TRUE if schneider was announced
     */
    public final boolean isSchneider() {

        return schneider.booleanValue();
    }

    /**
     * Checks whether schwarz was announced or not
     *
     * @return TRUE if schwarz was announced
     */
    public final boolean isSchwarz() {

        return schwarz.booleanValue();
    }

    /**
     * Checks whether an ouvert game was announced or not
     *
     * @return TRUE if an ouvert game was announced
     */
    public final boolean isOuvert() {

        return ouvert.booleanValue();
    }

    /**
     * Checks whether a hand game was announced or not
     *
     * @return TRUE if a hand game was announced
     */
    public final boolean isHand() {

        return hand.booleanValue();
    }

    /**
     * Checks whether the game was won or not
     *
     * @return TRUE if the game was won
     */
    public final boolean isGameWon() {

        return gameResult.isWon();
    }

    /**
     * Gets the game value
     *
     * @return Game value
     */
    public final int getGameValue() {
        return gameResult.getGameValue();
    }

    /**
     * Gets the final declarer points
     *
     * @return Final declarer points
     */
    public int getFinalDeclarerPoints() {
        return gameResult.getFinalDeclarerPoints();
    }

    /**
     * Gets the final opponent points
     *
     * @return Final opponent points
     */
    public int getFinalOpponentScore() {
        return gameResult.getFinalOpponentPoints();
    }

    /**
     * Gets the matadors for the game
     *
     * @return Matadors for the game
     */
    public int getMatadors() {
        return gameResult.getMatadors();
    }

    /**
     * Checks whether the game was played with or without jacks
     *
     * @return TRUE, if the game was played with jacks
     */
    public boolean isGamePlayedWithJacks() {
        return gameResult.isPlayWithJacks();
    }

    /**
     * Gets the position of declarer
     *
     * @return Declarer position
     */
    public final Player getDeclarer() {
        return declarer;
    }

    /**
     * Gets the tricks of the skat game
     *
     * @return Tricks
     */
    public final List<Trick> getTricks() {
        return tricks;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {

        final StringBuffer result = new StringBuffer();

        result.append("Game summary: ").append(gameType);

        if (hand.booleanValue()) {

            result.append(" hand");
        }

        if (ouvert.booleanValue()) {

            result.append(" ouvert");
        }

        if (schneider.booleanValue()) {

            result.append(" schneider");
        }

        if (schwarz.booleanValue()) {

            result.append(" schwarz");
        }

        result.append(" game value: " + gameResult.getGameValue());

        result.append(" declarer: " + declarer);

        return result.toString();
    }

    /**
     * Gets the player points
     *
     * @param player Player
     * @return Points
     */
    public int getPlayerPoints(final Player player) {
        return playerPoints.get(player).intValue();
    }

    /**
     * Get all players that lost in a Ramsch game.
     *
     * @return Set of losers
     */
    public Set<Player> getRamschLosers() {
        return Collections.unmodifiableSet(ramschLosers);
    }

    public Boolean isContra() {
        return contra;
    }

    public Boolean isRe() {
        return re;
    }

    @Override
    public int hashCode() {
        return Objects.hash(declarer,
                gameType, ouvert, hand, schneider, schwarz,
                contra, re, gameResult, tricks, playerPoints, ramschLosers);
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
        final GameSummary other = (GameSummary) obj;

        return Objects.equals(declarer, other.declarer) &&
                Objects.equals(gameType, other.gameType) &&
                Objects.equals(ouvert, other.ouvert) &&
                Objects.equals(hand, other.hand) &&
                Objects.equals(schneider, other.schneider) &&
                Objects.equals(schwarz, other.schwarz) &&
                Objects.equals(contra, other.contra) &&
                Objects.equals(re, other.re) &&
                Objects.equals(gameResult, other.gameResult) &&
                Objects.equals(tricks, other.tricks) &&
                Objects.equals(playerPoints, other.playerPoints) &&
                Objects.equals(ramschLosers, other.ramschLosers);
    }
}
