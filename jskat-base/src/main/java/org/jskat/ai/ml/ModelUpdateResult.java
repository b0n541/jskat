package org.jskat.ai.ml;

/** Typed result of an explicit remote update check. */
public final class ModelUpdateResult {

    public enum Status {
        AVAILABLE,
        UP_TO_DATE,
        UNSUPPORTED,
        FAILED
    }

    private final Status status;
    private final ModelReleaseCandidate candidate;
    private final ModelUnavailableReason reason;
    private final String message;

    private ModelUpdateResult(final Status status, final ModelReleaseCandidate candidate,
                              final ModelUnavailableReason reason, final String message) {
        this.status = status;
        this.candidate = candidate;
        this.reason = reason;
        this.message = message;
    }

    public static ModelUpdateResult available(final ModelReleaseCandidate candidate) {
        return new ModelUpdateResult(Status.AVAILABLE, candidate, null, "A compatible model update is available");
    }

    public static ModelUpdateResult upToDate(final String message) {
        return new ModelUpdateResult(Status.UP_TO_DATE, null, null, message);
    }

    public static ModelUpdateResult unsupported(final String message) {
        return new ModelUpdateResult(Status.UNSUPPORTED, null, ModelUnavailableReason.UNSUPPORTED_VERSION, message);
    }

    public static ModelUpdateResult failed(final ModelUnavailableReason reason, final String message) {
        return new ModelUpdateResult(Status.FAILED, null, reason, message);
    }

    public Status status() {
        return status;
    }

    public ModelReleaseCandidate candidate() {
        return candidate;
    }

    public ModelUnavailableReason reason() {
        return reason;
    }

    public String message() {
        return message;
    }
}
