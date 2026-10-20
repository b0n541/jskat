package org.jskat.tournament;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;

/** Strict side-effect-free reader used by every tournament adapter. */
public final class TournamentConfigurationReader {
    private static final ObjectMapper JSON =
            new ObjectMapper().enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

    public ValidationResult<TournamentConfiguration> read(Path file) {
        try {
            String text =
                    StandardCharsets.UTF_8
                            .newDecoder()
                            .onMalformedInput(CodingErrorAction.REPORT)
                            .onUnmappableCharacter(CodingErrorAction.REPORT)
                            .decode(ByteBuffer.wrap(Files.readAllBytes(file)))
                            .toString();
            return read(text, file);
        } catch (CharacterCodingException e) {
            return failure("INVALID_UTF8", "Configuration must be strict UTF-8");
        } catch (IOException e) {
            return failure("CONFIGURATION_IO", e.getMessage());
        }
    }

    private ValidationResult<TournamentConfiguration> failure(String code, String message) {
        return new ValidationResult<>(
                null,
                List.of(new ValidationIssue(ValidationIssue.Severity.ERROR, code, "", message)));
    }

    public ValidationResult<TournamentConfiguration> read(String json, Path file) {
        List<ValidationIssue> issues = new ArrayList<>();
        JsonNode root;
        try (var parser = JSON.createParser(json)) {
            root = JSON.readTree(parser);
            if (parser.nextToken() != null) return failure("INVALID_JSON", "Trailing JSON content");
        } catch (IOException e) {
            return failure("INVALID_JSON", e.getMessage());
        }
        if (root == null || !root.isObject())
            return failure("INVALID_TYPE", "Configuration must be an object");
        fields(
                root,
                Set.of("schemaVersion", "ruleset", "seed", "epochs", "output", "competitors"),
                "",
                issues);
        JsonNode version = required(root, "schemaVersion", "", issues);
        if (version != null
                && (!version.isIntegralNumber()
                        || !version.canConvertToInt()
                        || version.intValue() != 1))
            issue(
                    issues,
                    "UNSUPPORTED_VERSION",
                    "/schemaVersion",
                    "Only schema version 1 is supported");
        String ruleset = text(root, "ruleset", "", issues);
        if (ruleset != null && !ruleset.equals("standard-skat-v1"))
            issue(issues, "INVALID_RULESET", "/ruleset", "Expected standard-skat-v1");
        JsonNode epoch = required(root, "epochs", "", issues);
        long epochs = 0;
        if (epoch != null) {
            if (!epoch.isIntegralNumber() || !epoch.canConvertToLong() || epoch.longValue() < 1)
                issue(
                        issues,
                        "INVALID_INTEGER",
                        "/epochs",
                        "Expected positive exact signed-64-bit integer");
            else epochs = epoch.longValue();
        }
        Optional<Long> seed = Optional.empty();
        if (root.has("seed")) {
            JsonNode value = root.get("seed");
            try {
                if (!value.isTextual() || !value.textValue().matches("[0-9]+"))
                    throw new NumberFormatException();
                var integer = new java.math.BigInteger(value.textValue());
                if (integer.bitLength() > 64) throw new NumberFormatException();
                seed = Optional.of(integer.longValue());
            } catch (NumberFormatException e) {
                issue(issues, "INVALID_SEED", "/seed", "Expected unsigned-64-bit decimal string");
            }
        }
        String output = text(root, "output", "", issues);
        ConfiguredPath outputPath = path(output, file, "/output", issues);
        var competitors = new ArrayList<TournamentCompetitor>();
        JsonNode roster = required(root, "competitors", "", issues);
        Set<UUID> ids = new HashSet<>();
        Set<String> names = new HashSet<>();
        Set<PlayerImplementation> modelFree = new HashSet<>();
        if (roster != null && !roster.isArray())
            issue(issues, "INVALID_TYPE", "/competitors", "Expected array");
        else if (roster != null) {
            if (roster.size() < 2)
                issue(
                        issues,
                        "ROSTER_TOO_SMALL",
                        "/competitors",
                        "At least two competitors required");
            for (int i = 0; i < roster.size(); i++) {
                int previousIssues = issues.size();
                String pointer = "/competitors/" + i;
                JsonNode c = roster.get(i);
                if (!c.isObject()) {
                    issue(issues, "INVALID_TYPE", pointer, "Expected competitor object");
                    continue;
                }
                fields(c, Set.of("id", "name", "implementation", "model"), pointer, issues);
                String id = text(c, "id", pointer, issues);
                UUID uuid = null;
                if (id != null)
                    try {
                        // RFC variant, version nibble 1–8; canonical lowercase spelling only.
                        if (!id.matches(
                                "[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"))
                            throw new IllegalArgumentException();
                        uuid = UUID.fromString(id);
                        if (!ids.add(uuid))
                            issue(
                                    issues,
                                    "DUPLICATE_ID",
                                    pointer + "/id",
                                    "Competitor ID must be unique");
                    } catch (IllegalArgumentException e) {
                        issue(
                                issues,
                                "INVALID_UUID",
                                pointer + "/id",
                                "Expected canonical lowercase standards-compliant UUID");
                    }
                String name = text(c, "name", pointer, issues);
                if (name != null) {
                    name = Normalizer.normalize(name.strip(), Normalizer.Form.NFC);
                    int length = name.codePointCount(0, name.length());
                    if (length < 1 || length > 128)
                        issue(
                                issues,
                                "INVALID_NAME",
                                pointer + "/name",
                                "Name must contain 1–128 Unicode code points");
                    if (!names.add(name))
                        issue(
                                issues,
                                "DUPLICATE_NAME",
                                pointer + "/name",
                                "Normalized competitor name must be unique");
                }
                String implementation = text(c, "implementation", pointer, issues);
                PlayerImplementation entry = null;
                if (implementation != null)
                    try {
                        entry = PlayerImplementation.fromId(implementation);
                    } catch (IllegalArgumentException e) {
                        issue(
                                issues,
                                "INVALID_IMPLEMENTATION",
                                pointer + "/implementation",
                                "Unknown registered implementation");
                    }
                ModelSource model = null;
                if (entry != null && !entry.modelRequired()) {
                    if (c.has("model"))
                        issue(
                                issues,
                                "MODEL_FORBIDDEN",
                                pointer + "/model",
                                "Model-free implementation must omit model");
                    if (!modelFree.add(entry))
                        issue(
                                issues,
                                "DUPLICATE_INPUTS",
                                pointer + "/implementation",
                                "Duplicate implementation and effective inputs");
                } else if (entry != null) {
                    JsonNode m = required(c, "model", pointer, issues);
                    if (m != null && !m.isObject())
                        issue(issues, "INVALID_TYPE", pointer + "/model", "Expected model object");
                    else if (m != null) {
                        String mp = pointer + "/model";
                        String source = text(m, "source", mp, issues);
                        if ("active".equals(source)) {
                            fields(m, Set.of("source"), mp, issues);
                            model = new ModelSource.Active();
                        } else if ("installed".equals(source)) {
                            fields(m, Set.of("source", "version"), mp, issues);
                            String label = text(m, "version", mp, issues);
                            if (label != null) {
                                if (label.isBlank()
                                        || label.equals(".")
                                        || label.equals("..")
                                        || label.contains("/")
                                        || label.contains("\\")
                                        || label.indexOf(0) >= 0)
                                    issue(
                                            issues,
                                            "INVALID_VERSION",
                                            mp + "/version",
                                            "Version must not be blank");
                                else model = new ModelSource.Installed(label);
                            }
                        } else if ("development".equals(source)) {
                            fields(m, Set.of("source", "path"), mp, issues);
                            ConfiguredPath p =
                                    path(text(m, "path", mp, issues), file, mp + "/path", issues);
                            if (p != null) model = new ModelSource.Development(p);
                        } else {
                            fields(m, Set.of("source", "version", "path"), mp, issues);
                            if (source != null)
                                issue(
                                        issues,
                                        "INVALID_SOURCE",
                                        mp + "/source",
                                        "Expected active, installed or development");
                        }
                    }
                }
                if (issues.size() == previousIssues
                        && uuid != null
                        && name != null
                        && entry != null)
                    competitors.add(new TournamentCompetitor(uuid, name, entry, model));
            }
            if (epochs > 0 && roster.size() >= 2)
                try {
                    TournamentScheduleSize.gameCount(roster.size(), epochs);
                } catch (IllegalArgumentException | ArithmeticException e) {
                    issue(
                            issues,
                            "SCHEDULE_SIZE",
                            "/epochs",
                            "Schedule arithmetic overflow or int-indexed representation limit"
                                    + " exceeded");
                }
        }
        return new ValidationResult<>(
                issues.isEmpty()
                        ? new TournamentConfiguration(
                                1, ruleset, seed, epochs, outputPath, competitors, file, json)
                        : null,
                issues);
    }

    private static void issue(
            List<ValidationIssue> issues, String code, String path, String message) {
        issues.add(new ValidationIssue(ValidationIssue.Severity.ERROR, code, path, message));
    }

    private static void fields(
            JsonNode node, Set<String> allowed, String pointer, List<ValidationIssue> issues) {
        node.fieldNames()
                .forEachRemaining(
                        f -> {
                            if (!allowed.contains(f))
                                issue(
                                        issues,
                                        "UNKNOWN_FIELD",
                                        pointer + "/" + f.replace("~", "~0").replace("/", "~1"),
                                        "Unknown field");
                        });
    }

    private static JsonNode required(
            JsonNode node, String field, String pointer, List<ValidationIssue> issues) {
        JsonNode value = node.get(field);
        if (value == null)
            issue(issues, "REQUIRED_FIELD", pointer + "/" + field, "Required field is missing");
        return value;
    }

    private static String text(
            JsonNode node, String field, String pointer, List<ValidationIssue> issues) {
        JsonNode value = required(node, field, pointer, issues);
        if (value == null) return null;
        if (!value.isTextual()) {
            issue(issues, "INVALID_TYPE", pointer + "/" + field, "Expected string");
            return null;
        }
        if (!StandardCharsets.UTF_8.newEncoder().canEncode(value.textValue())) {
            issue(issues, "INVALID_UNICODE", pointer + "/" + field, "Expected well-formed Unicode");
            return null;
        }
        return value.textValue();
    }

    private static ConfiguredPath path(
            String value, Path file, String pointer, List<ValidationIssue> issues) {
        if (value == null) return null;
        try {
            if (value.isEmpty()) throw new InvalidPathException(value, "Empty path");
            return ConfiguredPath.resolve(value, file);
        } catch (InvalidPathException e) {
            issue(issues, "INVALID_PATH", pointer, "Expected filesystem path");
            return null;
        }
    }
}
