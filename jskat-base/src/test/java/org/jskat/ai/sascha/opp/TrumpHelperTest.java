package org.jskat.ai.sascha.opp;

import static org.assertj.core.api.Assertions.assertThat;
import org.jskat.ai.sascha.opponent.TrumpHelper;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.jupiter.api.Test;

public class TrumpHelperTest {

    @Test
    public void grandReactions() {
        var cut = new TrumpHelper(new CardList(Card.SJ, Card.DJ), GameType.GRAND, null, null);
        assertThat(cut.beatingCards(Card.HJ)).hasSize(1);
        assertThat(cut.beatingCards(Card.HJ)).contains(Card.SJ);
        assertThat(cut.higherCardsIn(Card.HJ)).isEqualTo(1);

    }
    @Test
    public void heartsTest() {
        var cut = new TrumpHelper(new CardList(Card.CJ, Card.HT, Card.HK, Card.H9), GameType.HEARTS, null, null);
        assertThat(cut.beatingCards(Card.H7)).hasSize(4);
        assertThat(cut.higherCardsIn(Card.H7)).isEqualTo(6);
    }

}
