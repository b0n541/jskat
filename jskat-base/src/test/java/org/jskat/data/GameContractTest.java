package org.jskat.data;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GameContractTest {
    @Test
    void simplestContract() {
        final var contract = new GameContract(GameType.GRAND);

        assertThat(contract.gameType()).isEqualTo(GameType.GRAND);
        assertThat(contract.hand()).isFalse();
        assertThat(contract.ouvert()).isFalse();
        assertThat(contract.schneider()).isFalse();
        assertThat(contract.schwarz()).isFalse();
    }

    @Test
    void suitGrandOuvert() {

        final var contracts = List.of(
                new GameContract(GameType.CLUBS)
                        .withOuvert(CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8)),
                new GameContract(
                        GameType.CLUBS,
                        true,
                        CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8))
        );

        contracts.forEach(contract -> {
            assertThat(contract.gameType()).isEqualTo(GameType.CLUBS);
            assertThat(contract.hand()).isTrue();
            assertThat(contract.schneider()).isTrue();
            assertThat(contract.schwarz()).isTrue();
            assertThat(contract.ouvert()).isTrue();
            assertThat(contract.ouvertCards())
                    .containsExactlyInAnyOrder(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8);
        });
    }

    @Test
    void nullOuvert() {
        assertThat(
                new GameContract(
                        GameType.NULL,
                        true,
                        CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8)))
                .extracting("gameType", "hand", "schneider", "schwarz", "ouvert", "ouvertCards")
                .containsExactly(GameType.NULL, false, false, false, true, CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8));
    }

    @Test
    void nullHandOuvert() {
        final var contracts = List.of(
                new GameContract(
                        GameType.NULL,
                        true,
                        false,
                        false,
                        true,
                        CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8)),
                new GameContract(
                        GameType.NULL,
                        true,
                        true,
                        CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8)));

        contracts.forEach(contract ->
                assertThat(contract)
                        .extracting("gameType", "hand", "schneider", "schwarz", "ouvert", "ouvertCards")
                        .containsExactly(GameType.NULL, true, false, false, true, CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8)));
    }
}
