
package org.jskat.control.event;


import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.CardDealEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DealCardEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private CardDealEvent event;

    @BeforeEach
    public void setUp() {

        data = new SkatGameData();

        final Map<Player, CardList> playerCards = new HashMap<Player, CardList>();
        playerCards.put(Player.FOREHAND,
                new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ)));
        playerCards.put(Player.MIDDLEHAND,
                new CardList(Arrays.asList(Card.CA, Card.SA, Card.HA, Card.DA)));
        playerCards.put(Player.REARHAND,
                new CardList(Arrays.asList(Card.CT, Card.ST, Card.HT, Card.DT)));

        final CardList skat = new CardList(Card.D7, Card.D8);

        event = new CardDealEvent(playerCards, skat);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        checkPlayerCards(Player.FOREHAND, Card.CJ, Card.SJ, Card.HJ, Card.DJ);
        checkPlayerCards(Player.MIDDLEHAND, Card.CA, Card.SA, Card.HA, Card.DA);
        checkPlayerCards(Player.REARHAND, Card.CT, Card.ST, Card.HT, Card.DT);

        checkCardList(data.getDealtSkat(), Card.D7, Card.D8);
        checkCardList(data.getSkat(), Card.D7, Card.D8);
    }

    private void checkPlayerCards(final Player player, final Card... cards) {
        final CardList cardList = data.getDealtCards().get(player);
        checkCardList(cardList, cards);
    }

    private static void checkCardList(final CardList cardList, final Card... cards) {
        assertThat(cardList).containsExactlyInAnyOrder(cards);
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        for (final Player player : Player.values()) {
            checkCardList(data.getDealtCards().get(player));
        }
        checkCardList(data.getDealtSkat());
        checkCardList(data.getSkat());
    }
}
