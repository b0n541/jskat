package org.jskat.ai.alex;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    @Test
    public void playCardContinue() {
		PlayerAlex player = new PlayerAlex();
        final CardList playerCards = new CardList(Arrays.asList(Card.CJ, Card.DA, Card.HJ, Card.DJ, Card.SA, Card.HA, Card.SQ,
                Card.HT, Card.H8, Card.D9));
		final CardList trickCards = new CardList(Arrays.asList(Card.SJ));
		
        assertThat(player.playCard(playerCards, GameType.DIAMONDS, trickCards)).isEqualTo(Card.CJ);
    }	
}
