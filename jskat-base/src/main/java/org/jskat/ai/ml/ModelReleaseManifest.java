package org.jskat.ai.ml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Parsed model-release manifest. The manifest owns the file inventory and integrity data.
 */
public final class ModelReleaseManifest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final String version;
    private final URI baseUrl;
    private final List<ModelAsset> assets;
    private final String sourceJson;

    /**
     * Performs model release manifest.
     * @param version version value.
     * @param baseUrl base url value.
     * @param assets assets value.
     */
    public ModelReleaseManifest(final String version, final URI baseUrl, final List<ModelAsset> assets) {
        this(version, baseUrl, assets, null);
    }

    private ModelReleaseManifest(final String version, final URI baseUrl, final List<ModelAsset> assets,
                                 final String sourceJson) {
        this.version = Objects.requireNonNull(version, "version");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
        this.assets = List.copyOf(assets);
        this.sourceJson = sourceJson;
    }

    /**
     * Performs parse.
     * @param json json value.
     * @return parse result.
     */
    public static ModelReleaseManifest parse(final String json) {
        try {
            final JsonNode root = JSON.readTree(json);
            if (root == null || !root.isObject()) {
                throw invalid("manifest must be a JSON object");
            }

            final JsonNode versionNode = root.get("version");
            final JsonNode baseUrlNode = root.get("base_url");
            final JsonNode modelsNode = root.get("models");
            if (versionNode == null || !versionNode.isTextual() || versionNode.asText().isBlank()) {
                throw invalid("manifest version is missing");
            }
            if (baseUrlNode == null || !baseUrlNode.isTextual()) {
                throw invalid("manifest base_url is missing");
            }
            if (modelsNode == null || !modelsNode.isObject()) {
                throw invalid("manifest models must be an object");
            }

            final URI baseUrl;
            try {
                baseUrl = URI.create(baseUrlNode.asText());
            } catch (final IllegalArgumentException e) {
                throw invalid("manifest base_url is not a valid URI");
            }

            final List<ModelAsset> assets = new ArrayList<>();
            final Set<String> names = new HashSet<>();
            final Iterator<Map.Entry<String, JsonNode>> models = modelsNode.fields();
            while (models.hasNext()) {
                final JsonNode model = models.next().getValue();
                final JsonNode variants = model.get("variants");
                if (variants == null || !variants.isObject()) {
                    throw invalid("each model must contain variants");
                }
                final Iterator<Map.Entry<String, JsonNode>> variantIterator = variants.fields();
                while (variantIterator.hasNext()) {
                    final JsonNode variant = variantIterator.next().getValue();
                    final JsonNode files = variant.get("files");
                    if (files == null || !files.isArray() || files.isEmpty()) {
                        throw invalid("each variant must contain files");
                    }
                    for (final JsonNode file : files) {
                        final JsonNode name = file.get("name");
                        final JsonNode size = file.get("size_bytes");
                        final JsonNode sha256 = file.get("sha256");
                        if (name == null || !name.isTextual() || name.asText().isBlank()
                                || name.asText().contains("/") || name.asText().contains("\\")) {
                            throw invalid("manifest contains an invalid file name");
                        }
                        if (size == null || !size.canConvertToLong() || size.asLong() < 0) {
                            throw invalid("manifest contains an invalid file size");
                        }
                        if (sha256 == null || !sha256.isTextual()
                                || !sha256.asText().matches("[0-9a-fA-F]{64}")) {
                            throw invalid("manifest contains an invalid SHA-256 value");
                        }
                        if (!names.add(name.asText())) {
                            throw invalid("manifest contains a duplicate file: " + name.asText());
                        }
                        assets.add(new ModelAsset(name.asText(), size.asLong(), sha256.asText().toLowerCase()));
                    }
                }
            }
            if (assets.isEmpty()) {
                throw invalid("manifest contains no files");
            }
            return new ModelReleaseManifest(versionNode.asText(), baseUrl, assets, json);
        } catch (final IOException e) {
            throw invalid("manifest is not valid JSON");
        }
    }

    private static IllegalArgumentException invalid(final String message) {
        return new IllegalArgumentException(message);
    }

    /**
     * Performs version.
     * @return version result.
     */
    public String version() {
        return version;
    }

    /**
     * Performs base url.
     * @return base url result.
     */
    public URI baseUrl() {
        return baseUrl;
    }

    /**
     * Performs assets.
     * @return assets result.
     */
    public List<ModelAsset> assets() {
        return assets;
    }

    /**
     * Performs source json.
     * @return source json result.
     */
    public String sourceJson() {
        if (sourceJson != null) {
            return sourceJson;
        }
        final ObjectNode root = JSON.createObjectNode();
        root.put("version", version);
        root.put("base_url", baseUrl.toString());
        final ObjectNode variants = root.putObject("models").putObject("artifacts").putObject("variants");
        final ArrayNode files = variants.putObject("all").putArray("files");
        for (final ModelAsset asset : assets) {
            final ObjectNode file = files.addObject();
            file.put("name", asset.name());
            file.put("size_bytes", asset.sizeBytes());
            file.put("sha256", asset.sha256());
        }
        try {
            return JSON.writeValueAsString(root);
        } catch (final IOException e) {
            throw new IllegalStateException("Could not serialize model manifest", e);
        }
    }

    /**
     * Total bytes.
     * @return total bytes result.
     */
    public long totalBytes() {
        return assets.stream().mapToLong(ModelAsset::sizeBytes).sum();
    }

    /**
     * Performs asset.
     * @param name name value.
     * @return asset result.
     */
    public ModelAsset asset(final String name) {
        return assets.stream()
                .filter(asset -> asset.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Manifest does not contain " + name));
    }
}
