package org.jskat.control;

import com.google.common.eventbus.EventBus;
import org.jskat.AbstractJSkatTest;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.ai.test.*;
import org.jskat.data.*;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.gui.UnitTestView;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link SkatGame}
 */
public class SkatGameTest extends AbstractJSkatTest {

    private static final String TABLE_NAME = "Table 1";
    private final Random random = new Random();

    @BeforeEach
    public void setUp() {
        JSkatOptions.instance().resetToDefault();
        JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, new EventBus());
    }

    @Test
    public void testContra_NoContraActivatedInOptions() {
        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new ContraReCallingTestPlayer(),
                new ContraReCallingTestPlayer(),
                new ContraReCallingTestPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final GameSummary summary = game.getGameSummary();
        assertFalse(summary.isContra());
        assertFalse(summary.isRe());
    }

    private static void runGame(final SkatGame game) {
        try {
            CompletableFuture.runAsync(game::run).get();
        } catch (final InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testContra_ContraActivatedInOptions() {

        final JSkatOptions options = JSkatOptions.instance();
        options.setRules(RuleSet.PUB);
        options.setPlayContra(true);

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new ContraReCallingTestPlayer(),
                new ContraReCallingTestPlayer(),
                new ContraReCallingTestPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final GameSummary summary = game.getGameSummary();
        assertTrue(summary.isContra());
        assertTrue(summary.isRe());
    }

    /**
     * When no player bids, game is passed in
     */
    @Test
    public void testPassIn_NoBids() {

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                getNoBiddingPlayer(), getNoBiddingPlayer(),
                getNoBiddingPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.PASSED_IN);

        final SkatGameResult result = game.getGameResult();
        assertFalse(result.isWon());
        assertThat(result.getGameValue()).isEqualTo(0);
    }

    /**
     * When no player bids, game is passed in
     */
    @Test
    public void testPassIn_NoBidsMockito() {

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                getNoBiddingPlayer(), getNoBiddingPlayer(),
                getNoBiddingPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.PASSED_IN);

        final SkatGameResult result = game.getGameResult();
        assertFalse(result.isWon());
        assertThat(result.getGameValue()).isEqualTo(0);
    }

    private static JSkatPlayer getNoBiddingPlayer() {
        final JSkatPlayer player = mock(JSkatPlayer.class);
        when(player.bidMore(anyInt())).thenReturn(-1);
        when(player.holdBid(anyInt())).thenReturn(false);
        return player;
    }

    /**
     * When no player bids, game is passed in even the options for playing ramsch
     * are set but the active ruleset is ISPA rules
     */
    @Test
    public void testPassIn_NoBids2() {

        final JSkatOptions options = JSkatOptions.instance();
        options.setPlayRamsch(true);
        options.setRamschEventNoBid(true);

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                getNoBiddingPlayer(), getNoBiddingPlayer(),
                getNoBiddingPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.PASSED_IN);

        final SkatGameResult result = game.getGameResult();
        assertFalse(result.isWon());
        assertThat(result.getGameValue()).isEqualTo(0);
    }

    /**
     * When no player bids and pub rules are activated, Ramsch is played
     */
    @Test
    public void testRamsch_NoBids() {

        final JSkatOptions options = JSkatOptions.instance();
        options.setRules(RuleSet.PUB);
        options.setPlayRamsch(true);
        options.setRamschEventNoBid(true);

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new RamschTestPlayer(), new RamschTestPlayer(),
                new RamschTestPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.RAMSCH);
        assertThat(summary.ramschLosers).isNotEmpty();

        final SkatGameResult result = game.getGameResult();
        assertFalse(result.isWon());
        assertTrue(result.getGameValue() < 0);
    }

    /**
     * Tests the fix for Issue #172:<br>
     * <p>
     * https://github.com/b0n541/jskat/issues/172
     */
    @Test
    void testRamsch_Issue172() {
        final var options = JSkatOptions.instance();
        options.setRules(RuleSet.PUB);
        options.setPlayRamsch(true);
        options.setRamschEventNoBid(true);

        final var game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new RamschTestPlayer(), new RamschTestPlayer(), new RamschTestPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final var summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.RAMSCH);
        assertThat(summary.ramschLosers).isNotEmpty();

        final SkatGameResult result = game.getGameResult();
        assertFalse(result.isWon());
        assertTrue(result.getGameValue() < 0);
    }

    /**
     * Forced ramsch game
     */
    @Test
    public void testRamsch_Forced() {

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.FORCED_RAMSCH,
                new RamschTestPlayer(), new RamschTestPlayer(),
                new RamschTestPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.RAMSCH);

        final SkatGameResult result = game.getGameResult();
        assertFalse(result.isWon());
        assertTrue(result.getGameValue() < 0);
    }

    /**
     * Forced ramsch game, grand hand is played if forehand wants to
     */
    @Test
    public void testRamsch_ForcedForeHandGrandHand() {

        final RamschTestPlayer grandHandPlayer = new RamschTestPlayer();
        grandHandPlayer.setPlayGrandHand(true);

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.FORCED_RAMSCH,
                grandHandPlayer, new AIPlayerRND(), new AIPlayerRND());
        game.setView(new UnitTestView());

        runGame(game);

        assertThat(game.getDeclarer()).isEqualTo(Player.FOREHAND);
        final GameContract announcement = game.getGameAnnouncement().contract();
        assertThat(announcement.gameType()).isEqualTo(GameType.GRAND);
        assertThat(announcement.hand()).isTrue();

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.GRAND);
    }

    /**
     * Forced ramsch game, grand hand is played if middle hand wants to
     */
    @Test
    public void testRamsch_ForcedMiddleHandGrandHand() {

        final RamschTestPlayer grandHandPlayer = new RamschTestPlayer();
        grandHandPlayer.setPlayGrandHand(true);

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.FORCED_RAMSCH,
                new RamschTestPlayer(), grandHandPlayer, new RamschTestPlayer());
        game.setView(new UnitTestView());

        runGame(game);

        assertThat(game.getDeclarer()).isEqualTo(Player.MIDDLEHAND);
        final GameContract contract = game.getGameAnnouncement().contract();
        assertThat(contract.gameType()).isEqualTo(GameType.GRAND);
        assertThat(contract.hand()).isTrue();

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.GRAND);
    }

    /**
     * Forced ramsch game, grand hand is played if rear hand wants to
     */
    @Test
    public void testRamsch_ForcedRearHandGrandHand() {

        final RamschTestPlayer grandHandPlayer = new RamschTestPlayer();
        grandHandPlayer.setPlayGrandHand(true);

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.FORCED_RAMSCH,
                new RamschTestPlayer(), new RamschTestPlayer(), grandHandPlayer);
        game.setView(new UnitTestView());

        runGame(game);

        assertThat(game.getDeclarer()).isEqualTo(Player.REARHAND);
        final GameContract contract = game.getGameAnnouncement().contract();
        assertThat(contract.gameType()).isEqualTo(GameType.GRAND);
        assertThat(contract.hand()).isTrue();

        final GameSummary summary = game.getGameSummary();
        assertThat(summary.getGameType()).isEqualTo(GameType.GRAND);
    }

    @Test
    public void exceptionFromPlayerDuringGame() {
        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new ExceptionTestPlayer(), new ExceptionTestPlayer(),
                new ExceptionTestPlayer());
        game.setView(new UnitTestView());

        randomGameAnnouncement(game);

        runGame(game);

        final SkatGameResult gameResult = game.getGameResult();
        if (!nullGameEndedPreliminary(game)) {
            assertTrue(gameResult.isSchwarz());
        }
    }

    private static boolean nullGameEndedPreliminary(final SkatGame game) {
        // in Null games the game might have ended preliminary before all tricks
        // have been played
        return GameType.NULL == game.getGameAnnouncement().contract().gameType()
                && game.getGameSummary().getTricks().size() < 10;
    }

    private void randomGameAnnouncement(final SkatGame game) {
        final CardDeck deck = new CardDeck();
        game.setCardDeck(deck);
        game.dealCards();
        game.setDeclarer(Player.values()[random.nextInt(Player.values().length)]);
        game.setGameAnnouncement(new GameAnnouncement(new GameContract(getRandomGameType()), CardList.of(Card.D7, Card.H7)));
        game.setGameState(GameState.TRICK_PLAYING);
    }

    private GameType getRandomGameType() {
        return Arrays.asList(GameType.GRAND, GameType.CLUBS, GameType.SPADES,
                GameType.HEARTS, GameType.DIAMONDS, GameType.NULL,
                GameType.RAMSCH).get(random.nextInt(6));
    }

    @Test
    public void playerPlaysNonPossessingCard() {
        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new PlayNonPossessingCardTestPlayer(),
                new PlayNonPossessingCardTestPlayer(),
                new PlayNonPossessingCardTestPlayer());
        game.setView(new UnitTestView());

        randomGameAnnouncement(game);

        runGame(game);

        final SkatGameResult gameResult = game.getGameResult();
        assertTrue(gameResult.isSchwarz());
        assertThat(gameResult.getFinalDeclarerPoints() + gameResult.getFinalOpponentPoints()).isEqualTo(120);
    }

    @Test
    public void playerPlaysNotAllowedCard() {
        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new PlayNotAllowedCardTestPlayer(),
                new PlayNotAllowedCardTestPlayer(),
                new PlayNotAllowedCardTestPlayer());
        game.setView(new UnitTestView());

        randomGameAnnouncement(game);

        runGame(game);

        final SkatGameResult gameResult = game.getGameResult();
        assertTrue(gameResult.isSchwarz());
        assertThat(gameResult.getFinalDeclarerPoints() + gameResult.getFinalOpponentPoints()).isEqualTo(120);
    }

    @Test
    public void testCompleteGame() {
        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                new AIPlayerRND(), new AIPlayerRND(), new AIPlayerRND());
        game.setView(new UnitTestView());

        randomGameAnnouncement(game);

        runGame(game);

        final Player declarer = game.getDeclarer();
        final SkatGameResult result = game.getGameResult();

        if (declarer != null && result.getGameValue() > 0) {
            assertThat(result.getFinalDeclarerPoints() + result.getFinalOpponentPoints()).isEqualTo(120);
            final GameSummary summary = game.getGameSummary();

            final Map<Player, Integer> playerPointsInTricks = new HashMap();
            playerPointsInTricks.put(Player.FOREHAND, 0);
            playerPointsInTricks.put(Player.MIDDLEHAND, 0);
            playerPointsInTricks.put(Player.REARHAND, 0);

            for (final Trick trick : summary.getTricks()) {

                Integer playerPoints = playerPointsInTricks.get(trick.getTrickWinner());
                if (playerPoints == null) {
                    playerPoints = 0;
                }
                playerPoints = playerPoints + trick.getValue();
                playerPointsInTricks.put(trick.getTrickWinner(), playerPoints);
            }

            assertThat(result.getFinalOpponentPoints())
                    .isEqualTo(playerPointsInTricks.get(declarer.getRightNeighbor())
                            + playerPointsInTricks.get(declarer.getLeftNeighbor()));
        }
    }

    @Test
    public void testPredefinedCardPlaying() {
        final UnitTestPlayer foreHand = new UnitTestPlayer();
        foreHand.setCardsToPlay(
                List.of(Card.C7, Card.SJ, Card.C9, Card.H8, Card.DQ, Card.D7, Card.CT, Card.DK, Card.CA, Card.CK));

        final UnitTestPlayer middleHand = new UnitTestPlayer();
        middleHand.setCardsToPlay(
                List.of(Card.CQ, Card.C8, Card.SQ, Card.ST, Card.S9, Card.HT, Card.HA, Card.DA, Card.HK, Card.D9));

        final UnitTestPlayer rearHand = new UnitTestPlayer();
        rearHand.setCardsToPlay(
                List.of(Card.DJ, Card.CJ, Card.HJ, Card.SA, Card.S8, Card.H7, Card.H9, Card.D8, Card.HQ, Card.DT));

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD, foreHand, middleHand, rearHand);
        game.setView(new UnitTestView());

        final CardDeck deck = new CardDeck(
                "SJ CA CT CK C9 C7 H8 DK DQ D7",
                "CQ C8 ST SQ S9 HA HT HK DA D9",
                "CJ HJ DJ SA S8 HQ H9 H7 DT D8",
                "SK S7");
        game.setCardDeck(deck);
        game.dealCards();
        game.setDeclarer(Player.MIDDLEHAND);
        game.setGameAnnouncement(new GameAnnouncement(new GameContract(GameType.CLUBS), CardList.of(Card.SK, Card.S7)));
        game.setGameState(GameState.TRICK_PLAYING);

        runGame(game);

        final SkatGameResult result = game.getGameResult();
        assertThat(result.getFinalDeclarerPoints()).isEqualTo(32);
        assertThat(result.getFinalOpponentPoints()).isEqualTo(88);
    }

    /**
     * Tests the fix for Issue #33:<br>
     * <p>
     * https://github.com/b0n541/jskat-multimodule/issues/33
     */
    @Test
    public void testGameResultIssue33() {

        final UnitTestPlayer foreHand = new UnitTestPlayer();
        foreHand.setCardsToPlay(
                Arrays.asList(Card.SJ, Card.CJ, Card.D8, Card.SA, Card.C7, Card.ST, Card.C9, Card.S7, Card.S9, Card.S8));

        final UnitTestPlayer middleHand = new UnitTestPlayer();
        middleHand.setCardsToPlay(
                Arrays.asList(Card.SQ, Card.SK, Card.DA, Card.DT, Card.CK, Card.H8, Card.DQ, Card.DK, Card.HQ, Card.HT));

        final UnitTestPlayer rearHand = new UnitTestPlayer();
        rearHand.setCardsToPlay(
                Arrays.asList(Card.DJ, Card.HJ, Card.D7, Card.H7, Card.CT, Card.HA, Card.CQ, Card.C8, Card.H9, Card.HK));

        final SkatGame game = new SkatGame(TABLE_NAME, GameVariant.STANDARD, foreHand, middleHand, rearHand);
        game.setView(new UnitTestView());

        final CardDeck deck = new CardDeck(
                "CJ SJ SA ST S9 S8 S7 C9 C7 D8",
                "SK SQ CK HT HQ H8 DA DT DK DQ",
                "HJ DJ CT CQ C8 HA HK H9 H7 D7",
                "D9 CA");
        game.setCardDeck(deck);
        game.dealCards();
        game.setDeclarer(Player.FOREHAND);
        game.setGameAnnouncement(new GameAnnouncement(new GameContract(GameType.SPADES), CardList.of(Card.D9, Card.CA)));
        game.setGameState(GameState.TRICK_PLAYING);

        runGame(game);

        final SkatGameResult result = game.getGameResult();
        assertThat(result.getFinalDeclarerPoints()).isEqualTo(89);
        assertThat(result.getFinalOpponentPoints()).isEqualTo(31);
        assertFalse(result.isSchneider());
        assertThat(result.getGameValue()).isEqualTo(33);
    }
}