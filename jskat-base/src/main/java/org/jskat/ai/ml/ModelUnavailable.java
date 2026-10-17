package org.jskat.ai.ml;

/** Typed explanation for an unavailable Active model release. */
public record ModelUnavailable(ModelUnavailableReason reason, String message, String installedVersion) {
}
