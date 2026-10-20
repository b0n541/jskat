package org.jskat.tournament;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HexFormat;

/**
 * Version one: NFC UTF-8, lexicographic object keys, compact JSON, plain normalized finite
 * decimals.
 */
public final class CanonicalJson {
    private static final ObjectMapper JSON =
            new ObjectMapper()
                    .enable(
                            com.fasterxml.jackson.core.JsonParser.Feature
                                    .STRICT_DUPLICATE_DETECTION)
                    .enable(
                            com.fasterxml.jackson.databind.DeserializationFeature
                                    .USE_BIG_DECIMAL_FOR_FLOATS)
                    .enable(
                            com.fasterxml.jackson.databind.DeserializationFeature
                                    .FAIL_ON_TRAILING_TOKENS)
                    .enable(
                            com.fasterxml.jackson.databind.SerializationFeature
                                    .WRITE_BIGDECIMAL_AS_PLAIN);

    private CanonicalJson() {}

    public static ObjectNode object() {
        return JSON.createObjectNode();
    }

    public static JsonNode parse(String text) {
        try {
            JsonNode value = JSON.readTree(text);
            if (value == null) throw new IllegalArgumentException("Empty JSON payload");
            return normalize(value);
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("Invalid canonical JSON payload", e);
        }
    }

    public static String encode(JsonNode value) {
        try {
            return JSON.writeValueAsString(normalize(value));
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("Cannot encode canonical JSON", e);
        }
    }

    public static byte[] bytes(JsonNode value) {
        return encode(value).getBytes(StandardCharsets.UTF_8);
    }

    public static String sha256(byte[] value) {
        return HexFormat.of().formatHex(hash(value));
    }

    public static byte[] hash(byte[] value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private static JsonNode normalize(JsonNode node) {
        if (node.isObject()) {
            var result = object();
            var values = new java.util.TreeMap<String, JsonNode>();
            node.fields()
                    .forEachRemaining(
                            entry -> {
                                if (!StandardCharsets.UTF_8.newEncoder().canEncode(entry.getKey()))
                                    throw new IllegalArgumentException(
                                            "Unpaired Unicode surrogate in object key");
                                String key =
                                        Normalizer.normalize(entry.getKey(), Normalizer.Form.NFC);
                                if (values.putIfAbsent(key, normalize(entry.getValue())) != null)
                                    throw new IllegalArgumentException("NFC object-key collision");
                            });
            values.forEach(result::set);
            return result;
        }
        if (node.isArray()) {
            var result = JSON.createArrayNode();
            for (var value : node) result.add(normalize(value));
            return result;
        }
        if (node.isTextual()) {
            if (!StandardCharsets.UTF_8.newEncoder().canEncode(node.textValue()))
                throw new IllegalArgumentException("Unpaired Unicode surrogate");
            return TextNode.valueOf(Normalizer.normalize(node.textValue(), Normalizer.Form.NFC));
        }
        if (node.isFloatingPointNumber()) {
            if (!node.isBigDecimal() && !Double.isFinite(node.doubleValue()))
                throw new IllegalArgumentException("Non-finite canonical number");
            return DecimalNode.valueOf(node.decimalValue().stripTrailingZeros());
        }
        return node.deepCopy();
    }
}
