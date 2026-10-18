package org.jskat.ai.ml;

/**
 * Typed explanation for an unavailable Active model release.
 *
 * @param reason why the release is unavailable
 * @param message human-readable explanation for the unavailable release
 * @param installedVersion version currently installed, if any
 */
public record ModelUnavailable(ModelUnavailableReason reason, String message, String installedVersion) {
}
