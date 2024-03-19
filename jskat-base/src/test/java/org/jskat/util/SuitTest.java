package org.jskat.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SuitTest {
    private static Stream<Arguments> suitSymbols() {
        return Stream.of(
                Arguments.of(Suit.CLUBS, "♣"),
                Arguments.of(Suit.SPADES, "♠"),
                Arguments.of(Suit.HEARTS, "♥"),
                Arguments.of(Suit.DIAMONDS, "♦")
        );
    }

    @ParameterizedTest
    @MethodSource("suitSymbols")
    public void testSuitSymbols(final Suit suit, final String expectedSymbol) {
        assertThat(suit.getSymbol()).isEqualTo(expectedSymbol);
    }
}
