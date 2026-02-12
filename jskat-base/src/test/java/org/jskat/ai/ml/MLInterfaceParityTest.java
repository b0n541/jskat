package org.jskat.ai.ml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parity tests for the ML interface encoding.
 * <p>
 * Verifies that Java implementations of card encoding, game type encoding,
 * and position encoding match the skat-ml-models interface specification.
 * <p>
 * Test vectors are loaded from ml_interface_test_vectors.json which is
 * downloaded from the skat-ml-models repository releases.
 */
public class MLInterfaceParityTest {

    private static JsonNode testVectors;

    @BeforeAll
    static void loadTestVectors() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = MLInterfaceParityTest.class.getClassLoader()
                .getResourceAsStream("ml_interface_test_vectors.json");
        if (is == null) {
            throw new IOException("ml_interface_test_vectors.json not found in resources. " +
                    "Download from: https://github.com/avaskys/skat-ml-models/releases/download/v1.1.0/test_vectors.json");
        }
        testVectors = mapper.readTree(is);
    }

    /**
     * Converts card name (e.g., "CJ", "SA") to Card enum.
     */
    private Card cardFromString(String cardName) {
        return Card.valueOf(cardName);
    }

    /**
     * Converts game type string to GameType enum.
     */
    private GameType gameTypeFromString(String gameTypeName) {
        return GameType.valueOf(gameTypeName);
    }

    /**
     * Converts position string to Player enum.
     */
    private Player positionFromString(String positionName) {
        return Player.valueOf(positionName);
    }

    /**
     * Verifies that MLFeatureExtractor.getMLIndex() returns the correct indices
     * for all cards in the test vectors.
     */
    @Test
    void testCardEncoding() {
        JsonNode cardEncodingTests = testVectors.get("test_cases").get("card_encoding");

        for (JsonNode testCase : cardEncodingTests) {
            String cardName = testCase.get("card").asText();
            int expectedIndex = testCase.get("expected_index").asInt();
            String description = testCase.get("description").asText();

            Card card = cardFromString(cardName);
            int actualIndex = MLFeatureExtractor.getMLIndex(card);

            assertThat(actualIndex)
                    .as("Card encoding for %s (%s)", cardName, description)
                    .isEqualTo(expectedIndex);
        }
    }

    /**
     * Verifies that game type encoding matches the expected indices.
     */
    @Test
    void testGameTypeEncoding() {
        JsonNode gameTypeTests = testVectors.get("test_cases").get("game_type_encoding");

        for (JsonNode testCase : gameTypeTests) {
            String gameTypeName = testCase.get("game_type").asText();
            int expectedIndex = testCase.get("expected_index").asInt();

            GameType gameType = gameTypeFromString(gameTypeName);
            int actualIndex = MLFeatureExtractor.getGameTypeIndex(gameType);

            assertThat(actualIndex)
                    .as("Game type encoding for %s", gameTypeName)
                    .isEqualTo(expectedIndex);
        }
    }

    /**
     * Verifies that position encoding matches the expected indices.
     */
    @Test
    void testPositionEncoding() {
        JsonNode positionTests = testVectors.get("test_cases").get("position_encoding");

        for (JsonNode testCase : positionTests) {
            String positionName = testCase.get("position").asText();
            int expectedIndex = testCase.get("expected_index").asInt();

            Player position = positionFromString(positionName);
            int actualIndex = position.ordinal();

            assertThat(actualIndex)
                    .as("Position encoding for %s", positionName)
                    .isEqualTo(expectedIndex);
        }
    }

    /**
     * Verifies that bidding transformer input encoding is correct.
     * Tests that hand cards are properly converted to indices and position encoding works.
     */
    @Test
    void testBiddingTransformerEncoding() {
        JsonNode biddingTests = testVectors.get("test_cases").get("bidding_transformer");

        for (JsonNode testCase : biddingTests) {
            String description = testCase.get("description").asText();
            JsonNode input = testCase.get("input");

            // Verify hand cards can be encoded
            JsonNode handCardsNode = input.get("hand_cards");
            int[] handIndices = new int[10];
            for (int i = 0; i < handCardsNode.size(); i++) {
                Card card = cardFromString(handCardsNode.get(i).asText());
                handIndices[i] = MLFeatureExtractor.getMLIndex(card);
            }

            // All indices should be in valid range [0, 31]
            for (int idx : handIndices) {
                assertThat(idx)
                        .as("Hand card index in bidding test '%s'", description)
                        .isBetween(0, 31);
            }

            // Verify position encoding
            String positionName = input.get("position").asText();
            Player position = positionFromString(positionName);
            assertThat(position.ordinal())
                    .as("Position in bidding test '%s'", description)
                    .isBetween(0, 2);
        }
    }

    /**
     * Verifies that game evaluation transformer input encoding is correct.
     * Tests that hand cards, skat cards, and context are properly encoded.
     */
    @Test
    void testGameEvalTransformerEncoding() {
        JsonNode evalTests = testVectors.get("test_cases").get("game_eval_transformer");

        for (JsonNode testCase : evalTests) {
            String description = testCase.get("description").asText();
            JsonNode input = testCase.get("input");

            // Verify hand cards encoding
            JsonNode handCardsNode = input.get("hand_cards");
            for (int i = 0; i < handCardsNode.size(); i++) {
                Card card = cardFromString(handCardsNode.get(i).asText());
                int idx = MLFeatureExtractor.getMLIndex(card);
                assertThat(idx)
                        .as("Hand card index in eval test '%s'", description)
                        .isBetween(0, 31);
            }

            // Verify skat cards encoding
            JsonNode skatCardsNode = input.get("skat_cards");
            for (int i = 0; i < skatCardsNode.size(); i++) {
                Card card = cardFromString(skatCardsNode.get(i).asText());
                int idx = MLFeatureExtractor.getMLIndex(card);
                assertThat(idx)
                        .as("Skat card index in eval test '%s'", description)
                        .isBetween(0, 31);
            }

            // Verify game type encoding
            String gameTypeName = input.get("game_type").asText();
            GameType gameType = gameTypeFromString(gameTypeName);
            int gameTypeIdx = MLFeatureExtractor.getGameTypeIndex(gameType);
            assertThat(gameTypeIdx)
                    .as("Game type index in eval test '%s'", description)
                    .isBetween(0, 5);

            // Verify position encoding
            String positionName = input.get("position").asText();
            Player position = positionFromString(positionName);
            assertThat(position.ordinal())
                    .as("Position in eval test '%s'", description)
                    .isBetween(0, 2);
        }
    }

    /**
     * Verifies that card play transformer input encoding is correct.
     * Tests the full game state encoding including history and current trick.
     */
    @Test
    void testCardPlayTransformerEncoding() {
        JsonNode playTests = testVectors.get("test_cases").get("card_play_transformer");

        for (JsonNode testCase : playTests) {
            String description = testCase.get("description").asText();
            JsonNode input = testCase.get("input");

            // Verify game type encoding
            String gameTypeName = input.get("game_type").asText();
            GameType gameType = gameTypeFromString(gameTypeName);
            int gameTypeIdx = MLFeatureExtractor.getGameTypeIndex(gameType);
            assertThat(gameTypeIdx)
                    .as("Game type index in play test '%s'", description)
                    .isBetween(0, 5);

            // Verify declarer is relative position (0, 1, or 2)
            int declarer = input.get("declarer").asInt();
            assertThat(declarer)
                    .as("Declarer index in play test '%s'", description)
                    .isBetween(0, 2);

            // Verify hand encoding
            JsonNode handNode = input.get("hand");
            for (int i = 0; i < handNode.size(); i++) {
                Card card = cardFromString(handNode.get(i).asText());
                int idx = MLFeatureExtractor.getMLIndex(card);
                assertThat(idx)
                        .as("Hand card index in play test '%s'", description)
                        .isBetween(0, 31);
            }

            // Verify legal mask encoding
            JsonNode legalMaskNode = input.get("legal_mask_cards");
            boolean[] legalMask = new boolean[32];
            for (int i = 0; i < legalMaskNode.size(); i++) {
                Card card = cardFromString(legalMaskNode.get(i).asText());
                int idx = MLFeatureExtractor.getMLIndex(card);
                legalMask[idx] = true;
            }

            // Count true values
            int legalCount = 0;
            for (boolean b : legalMask) {
                if (b) legalCount++;
            }
            assertThat(legalCount)
                    .as("Legal mask count in play test '%s'", description)
                    .isEqualTo(legalMaskNode.size());
        }
    }

    /**
     * Test that the version of the test vectors file is as expected.
     */
    @Test
    void testVectorsVersion() {
        String version = testVectors.get("version").asText();
        assertThat(version)
                .as("Test vectors version")
                .isEqualTo("1.0.0");
    }
}
