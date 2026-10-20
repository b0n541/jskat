package org.jskat.tournament;

import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

/** Strict version-one envelopes. Payload schemas are validated by typed callers. */
public final class TournamentRecords {
    private TournamentRecords() {}

    static final ObjectMapper MAPPER =
            new ObjectMapper()
                    .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                    .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                    .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);

    public static byte[] encode(UUID run, String type, JsonNode payload) {
        var root = CanonicalJson.object();
        root.put("schemaVersion", 1);
        root.put("recordType", type);
        root.put("runId", run.toString());
        root.set("payload", payload);
        root.put("payloadSha256", CanonicalJson.sha256(CanonicalJson.bytes(payload)));
        return CanonicalJson.bytes(root);
    }

    public static JsonNode decode(byte[] bytes, UUID run, String type) throws IOException {
        try {
            String text =
                    StandardCharsets.UTF_8
                            .newDecoder()
                            .onMalformedInput(CodingErrorAction.REPORT)
                            .onUnmappableCharacter(CodingErrorAction.REPORT)
                            .decode(ByteBuffer.wrap(bytes))
                            .toString();
            var root = CanonicalJson.parse(text);
            fields(root, "schemaVersion", "recordType", "runId", "payload", "payloadSha256");
            if (!root.get("schemaVersion").isIntegralNumber()
                    || !root.get("schemaVersion").canConvertToInt()
                    || root.get("schemaVersion").intValue() != 1)
                throw new IOException("Unsupported schemaVersion");
            if (!root.get("recordType").isTextual()
                    || !root.get("recordType").textValue().equals(type))
                throw new IOException("Wrong recordType");
            if (!root.get("runId").isTextual()
                    || !root.get("runId").textValue().equals(run.toString()))
                throw new IOException("Wrong runId");
            String hash = root.get("payloadSha256").asText();
            if (!hash.matches("[0-9a-f]{64}")
                    || !hash.equals(CanonicalJson.sha256(CanonicalJson.bytes(root.get("payload")))))
                throw new IOException("Invalid payload checksum");
            return root.get("payload");
        } catch (IllegalArgumentException | CharacterCodingException e) {
            throw new IOException("Invalid record envelope", e);
        }
    }

    static void fields(JsonNode value, String... names) throws IOException {
        var actual = new HashSet<String>();
        value.fieldNames().forEachRemaining(actual::add);
        if (!value.isObject()
                || !actual.equals(Set.of(names))
                || Arrays.stream(names).anyMatch(n -> value.get(n).isNull()))
            throw new IOException("Unexpected/missing/null fields: " + actual);
    }

    static JsonNode tree(Object value) {
        return MAPPER.valueToTree(value);
    }

    static <T> T typed(JsonNode value, Class<T> type) throws IOException {
        try {
            return MAPPER.treeToValue(value, type);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid " + type.getSimpleName(), e);
        }
    }
}
