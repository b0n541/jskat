package org.jskat.ai.sascha;

import java.util.Arrays;

import org.jskat.ai.sascha.solo.NullSuitHelper;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Suit;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class NullSuiteHelperTest {

    @Test
    public void testUnbeatable() {
        final CardList cards = new CardList(Arrays.asList(Card.C7, Card.C9, Card.CJ, Card.CK));
        NullSuitHelper cut = new NullSuitHelper(Suit.CLUBS, cards);

        assertThat(cut.isUnbeatable()).isTrue();

        assertThat(cut.getUnderCard(Card.CT, Card.HA) ).isEqualTo(Card.C9);

        assertThat(cut.getUnderCard(Card.CT, Card.C8) ).isEqualTo(Card.C9);

        assertThat(cut.getUnderCard( Card.C8, Card.CT) ).isEqualTo(Card.C9);

        assertThat(cut.getUnderCard( Card.C8, Card.HT) ).isEqualTo(Card.C7);

    }

}
