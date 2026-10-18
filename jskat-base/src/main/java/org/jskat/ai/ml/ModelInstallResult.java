package org.jskat.ai.ml;

/**
 * Typed result of an explicit model installation.
 */
public final class ModelInstallResult {

    /**
     * Defines the status type.
     */
    public enum Status {
        /**
         * Installation completed successfully.
         */
        INSTALLED,
        /**
         * Installation was cancelled by the user.
         */
        CANCELLED,
        /**
         * Installation failed.
         */
        FAILED
    }

    private final Status status;
    private final ModelInspection inspection;
    private final String message;

    /**
     * Performs model install result.
     * @param status status value.
     * @param inspection inspection value.
     * @param message message value.
     */
    private ModelInstallResult(final Status status, final ModelInspection inspection, final String message) {
        this.status = status;
        this.inspection = inspection;
        this.message = message;
    }

    /**
     * Performs installed.
     * @param inspection inspection value.
     * @return installed result.
     */
    public static ModelInstallResult installed(final ModelInspection inspection) {
        return new ModelInstallResult(Status.INSTALLED, inspection, "Model release installed");
    }

    /**
     * Returns or indicates cancelled.
     * @param inspection inspection value.
     * @return cancelled result.
     */
    public static ModelInstallResult cancelled(final ModelInspection inspection) {
        return new ModelInstallResult(Status.CANCELLED, inspection, "Model installation was cancelled");
    }

    /**
     * Performs failed.
     * @param inspection inspection value.
     * @param message message value.
     * @return failed result.
     */
    public static ModelInstallResult failed(final ModelInspection inspection, final String message) {
        return new ModelInstallResult(Status.FAILED, inspection, message);
    }

    /**
     * Performs status.
     * @return status result.
     */
    public Status status() {
        return status;
    }

    /**
     * Performs inspection.
     * @return inspection result.
     */
    public ModelInspection inspection() {
        return inspection;
    }

    /**
     * Performs message.
     * @return message result.
     */
    public String message() {
        return message;
    }
}
