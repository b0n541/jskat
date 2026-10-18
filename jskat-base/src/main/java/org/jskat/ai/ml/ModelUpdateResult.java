package org.jskat.ai.ml;

/**
 * Typed result of an explicit remote update check.
 */
public final class ModelUpdateResult {

    /**
     * Defines the status type.
     */
    public enum Status {
        /**
         * A compatible update is available.
         */
        AVAILABLE,
        /**
         * The installed release is already current.
         */
        UP_TO_DATE,
        /**
         * No supported update is available.
         */
        UNSUPPORTED,
        /**
         * The update check failed.
         */
        FAILED
    }

    private final Status status;
    private final ModelReleaseCandidate candidate;
    private final ModelUnavailableReason reason;
    private final String message;

    /**
     * Performs model update result.
     * @param status status value.
     * @param candidate candidate value.
     * @param reason reason value.
     * @param message message value.
     */
    private ModelUpdateResult(final Status status, final ModelReleaseCandidate candidate,
                              final ModelUnavailableReason reason, final String message) {
        this.status = status;
        this.candidate = candidate;
        this.reason = reason;
        this.message = message;
    }

    /**
     * Performs available.
     * @param candidate candidate value.
     * @return available result.
     */
    public static ModelUpdateResult available(final ModelReleaseCandidate candidate) {
        return new ModelUpdateResult(Status.AVAILABLE, candidate, null, "A compatible model update is available");
    }

    /**
     * Performs up to date.
     * @param message message value.
     * @return up to date result.
     */
    public static ModelUpdateResult upToDate(final String message) {
        return new ModelUpdateResult(Status.UP_TO_DATE, null, null, message);
    }

    /**
     * Performs unsupported.
     * @param message message value.
     * @return unsupported result.
     */
    public static ModelUpdateResult unsupported(final String message) {
        return new ModelUpdateResult(Status.UNSUPPORTED, null, ModelUnavailableReason.UNSUPPORTED_VERSION, message);
    }

    /**
     * Performs failed.
     * @param reason reason value.
     * @param message message value.
     * @return failed result.
     */
    public static ModelUpdateResult failed(final ModelUnavailableReason reason, final String message) {
        return new ModelUpdateResult(Status.FAILED, null, reason, message);
    }

    /**
     * Performs status.
     * @return status result.
     */
    public Status status() {
        return status;
    }

    /**
     * Returns or indicates candidate.
     * @return candidate result.
     */
    public ModelReleaseCandidate candidate() {
        return candidate;
    }

    /**
     * Performs reason.
     * @return reason result.
     */
    public ModelUnavailableReason reason() {
        return reason;
    }

    /**
     * Performs message.
     * @return message result.
     */
    public String message() {
        return message;
    }
}
