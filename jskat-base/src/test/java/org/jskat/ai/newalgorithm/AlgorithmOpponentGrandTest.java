package org.jskat.ai.newalgorithm;


import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import org.jskat.AbstractJSkatTest;
import org.jskat.ai.test.UnitTestPlayer;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.SkatGame;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatGameResult;
import org.jskat.gui.UnitTestView;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlgorithmOpponentGrandTest extends AbstractJSkatTest {

    private static final String TABLE_NAME = "Issue 57 Bug";

    @BeforeEach
    public void setUp() {
        JSkatOptions.instance().resetToDefault();
        JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, new EventBus());
    }

    @Test
    public void testOpponentPlayerFollowsJackInGrandGame() {
        final UnitTestPlayer deterministicPlayer = new UnitTestPlayer();
        deterministicPlayer.setCardsToPlay(Lists.newArrayList(Card.SJ));
        final AlgorithmAI faultyPlayer = new AlgorithmAI();
        final JSkatPlayer helperPlayer = new UnitTestPlayer();

        deterministicPlayer.newGame(Player.FOREHAND);
        faultyPlayer.newGame(Player.MIDDLEHAND);
        helperPlayer.newGame(Player.REARHAND);

        final SkatGame skatGame = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
                deterministicPlayer,
                faultyPlayer,
                helperPlayer);
        skatGame.setView(new UnitTestView());

        skatGame.setCardDeck(new CardDeck("SJ DJ CA CT CK CQ C9 C8 C7 SA",
                "CJ ST SK SQ S9 S8 S7 HA HT HK",
                "HJ HQ H9 H8 H7 DA DT DK DQ D9",
                "D8 D7"));
        skatGame.dealCards();

        skatGame.setDeclarer(Player.FOREHAND);
        var gameAnnouncement = GameAnnouncement.builder(GameType.GRAND).build();
        skatGame.setGameAnnouncement(gameAnnouncement);
        deterministicPlayer.startGame(Player.FOREHAND, gameAnnouncement);
        faultyPlayer.startGame(Player.FOREHAND, gameAnnouncement);
        helperPlayer.startGame(Player.FOREHAND, gameAnnouncement);

        skatGame.setGameState(GameState.TRICK_PLAYING);

        final SkatGameResult gameResult = skatGame.run();
        assertTrue(gameResult.isWon());
        assertAlgorithmAIPlayerFollowsJack(skatGame);
    }

    private static void assertAlgorithmAIPlayerFollowsJack(final SkatGame skatGame) {
        // first two game moves are card dealing and game announcement
        assertThat(skatGame.getGameMoves().get(2)).isEqualTo(new TrickCardPlayedEvent(Player.FOREHAND, Card.SJ));
        assertThat(skatGame.getGameMoves().get(3)).isEqualTo(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.CJ));
    }
}