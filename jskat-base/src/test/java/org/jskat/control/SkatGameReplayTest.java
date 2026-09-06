package org.jskat.control;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jskat.AbstractJSkatTest;
import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.skatgame.*;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameResult;
import org.jskat.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SkatGameReplayTest extends AbstractJSkatTest {

    private static final String TABLE_NAME = "replay-table";
    private final ReplayMoveListener moves = new ReplayMoveListener();

    @BeforeEach
    void setUp() {
        final EventBus tableEventBus = new EventBus();
        tableEventBus.register(moves);
        JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, tableEventBus);
    }

    @Test
    void replaysCardsAfterSkatExchange() {
        final CardList dealtHand = CardList.of(Card.CA, Card.CK, Card.CQ, Card.CT, Card.C8, Card.SA, Card.SK, Card.SQ, Card.ST, Card.S8);
        final CardList skat = CardList.of(Card.C9, Card.S9);
        final CardList discarded = CardList.of(Card.CA, Card.SA);
        final List<SkatGameEvent> events = List.of(
                new GameStartedEvent(1, GameVariant.STANDARD, Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND),
                new CardDealEvent(Map.of(
                        Player.FOREHAND, dealtHand,
                        Player.MIDDLEHAND, CardList.of(Card.HA, Card.HK, Card.HQ, Card.HT, Card.H9, Card.H8, Card.H7, Card.DA, Card.DK, Card.DQ),
                        Player.REARHAND, CardList.of(Card.DT, Card.D9, Card.D8, Card.D7, Card.C7, Card.S7, Card.HJ, Card.DJ, Card.CJ, Card.SJ)), skat),
                new PickUpSkatEvent(Player.FOREHAND, skat),
                new DiscardSkatEvent(Player.FOREHAND, discarded),
                new GameAnnouncementEvent(Player.FOREHAND,
                        new GameAnnouncement(new GameContract(GameType.HEARTS), discarded)),
                new GameFinishEvent("Forehand", gameSummary()));

        final SkatGameReplay replay = new SkatGameReplay(TABLE_NAME, events);

        replay.toEnd();

        assertThat(moves.moves).containsExactly(
                events.get(0), events.get(1), events.get(2), events.get(3), events.get(4), events.get(5));
        assertThat(moves.showCards.cards.get(Player.FOREHAND))
                .containsExactlyInAnyOrder(Card.CK, Card.CQ, Card.CT, Card.C8, Card.SK, Card.SQ, Card.ST, Card.S8, Card.C9, Card.S9);
        assertThat(moves.showCards.skat).containsExactlyInAnyOrderElementsOf(discarded);
    }

    private static GameSummary gameSummary() {
        final GameSummary.GameSummaryFactory factory = GameSummary.getFactory();
        factory.setGameType(GameType.HEARTS);
        factory.setDeclarer(Player.FOREHAND);
        factory.setGameResult(new SkatGameResult());
        factory.setPlayerPoints(Map.of(Player.FOREHAND, 0, Player.MIDDLEHAND, 0, Player.REARHAND, 0));
        return factory.getSummary();
    }

    private static final class ReplayMoveListener {
        private final List<SkatGameEvent> moves = new java.util.ArrayList<>();
        private ShowCardsCommand showCards;

        @Subscribe
        public void on(final SkatGameEvent event) {
            moves.add(event);
        }

        @Subscribe
        public void on(final ShowCardsCommand command) {
            showCards = command;
        }
    }
}
