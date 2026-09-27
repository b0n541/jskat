package org.jskat.ai.newalgorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Suit;
import org.junit.jupiter.api.Test;

public class HelperTest extends AbstractJSkatTest {

    /**
     * A holding with no trump for the asked suit must not hang the caller.
     *
     * <p>The timeout is the assertion: getSuitMultiplier shifts a bit mask down
     * until it meets a trump the hand holds, and with nothing to meet the shift
     * reaches zero and stays there. Without a bound this test does not fail, it
     * never finishes.
     */
    @Test
    public void getSuitMultiplierTerminatesWithoutTrumps() {
        assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> Helper.getSuitMultiplier(new CardList(), Suit.CLUBS));
    }

    @Test
    public void getSuitMultiplierCountsAllElevenMatadorsWhenTheHandHasNone() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () ->
                assertThat(Helper.getSuitMultiplier(new CardList(), Suit.CLUBS)).isEqualTo(12));
    }

    /**
     * The same question with cards in hand, none of them clubs or jacks: the
     * answer must not depend on the hand holding something irrelevant.
     */
    @Test
    public void getSuitMultiplierIgnoresCardsOfOtherSuits() {
        final CardList hand = CardList.of(Card.HA, Card.HT, Card.SA);
        assertTimeoutPreemptively(Duration.ofSeconds(5),
                () -> Helper.getSuitMultiplier(hand, Suit.CLUBS));
    }
}
