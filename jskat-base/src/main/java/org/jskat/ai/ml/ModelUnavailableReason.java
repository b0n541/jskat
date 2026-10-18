package org.jskat.ai.ml;

/**
 * Stable reasons that a model release cannot be used.
 */
public enum ModelUnavailableReason {
    /**
     * Provides this API member.
     */
    NO_ACTIVE_RELEASE,
    /**
     * Provides this API member.
     */
    ACTIVE_MARKER_INVALID,
    /**
     * Provides this API member.
     */
    ACTIVE_RELEASE_MISSING,
    /**
     * Provides this API member.
     */
    MANIFEST_MISSING,
    /**
     * Provides this API member.
     */
    MANIFEST_INVALID,
    /**
     * Provides this API member.
     */
    UNSUPPORTED_VERSION,
    /**
     * Provides this API member.
     */
    RELEASE_TAG_MISMATCH,
    /**
     * Provides this API member.
     */
    REQUIRED_FILE_MISSING,
    /**
     * Provides this API member.
     */
    FILE_SIZE_MISMATCH,
    /**
     * F i l e  h a s h  m i s m a t c h value.
     */
    FILE_HASH_MISMATCH
}
