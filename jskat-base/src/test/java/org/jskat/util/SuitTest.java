package org.jskat.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SuitTest {
    private static Stream<Arguments> suitSymbols() {
        return Stream.of(
                Arguments.of(Suit.CLUBS, "♣\uFE0F"),
                Arguments.of(Suit.SPADES, "♠\uFE0F"),
                Arguments.of(Suit.HEARTS, "♥\uFE0F"),
                Arguments.of(Suit.DIAMONDS, "♦\uFE0F")
        );
    }

    @ParameterizedTest
    @MethodSource("suitSymbols")
    public void testSuitSymbols(final Suit suit, final String expectedSymbol) {
        assertThat(suit.getSymbol()).isEqualTo(expectedSymbol);
    }
}
