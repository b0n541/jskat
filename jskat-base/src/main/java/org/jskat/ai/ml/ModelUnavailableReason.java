package org.jskat.ai.ml;

/** Stable reasons that a model release cannot be used. */
public enum ModelUnavailableReason {
    NO_ACTIVE_RELEASE,
    ACTIVE_MARKER_INVALID,
    ACTIVE_RELEASE_MISSING,
    MANIFEST_MISSING,
    MANIFEST_INVALID,
    UNSUPPORTED_VERSION,
    RELEASE_TAG_MISMATCH,
    REQUIRED_FILE_MISSING,
    FILE_SIZE_MISMATCH,
    FILE_HASH_MISMATCH
}
