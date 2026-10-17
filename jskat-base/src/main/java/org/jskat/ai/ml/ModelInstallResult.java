package org.jskat.ai.ml;

/** Typed result of an explicit model installation. */
public final class ModelInstallResult {

    public enum Status {
        INSTALLED,
        CANCELLED,
        FAILED
    }

    private final Status status;
    private final ModelInspection inspection;
    private final String message;

    private ModelInstallResult(final Status status, final ModelInspection inspection, final String message) {
        this.status = status;
        this.inspection = inspection;
        this.message = message;
    }

    public static ModelInstallResult installed(final ModelInspection inspection) {
        return new ModelInstallResult(Status.INSTALLED, inspection, "Model release installed");
    }

    public static ModelInstallResult cancelled(final ModelInspection inspection) {
        return new ModelInstallResult(Status.CANCELLED, inspection, "Model installation was cancelled");
    }

    public static ModelInstallResult failed(final ModelInspection inspection, final String message) {
        return new ModelInstallResult(Status.FAILED, inspection, message);
    }

    public Status status() {
        return status;
    }

    public ModelInspection inspection() {
        return inspection;
    }

    public String message() {
        return message;
    }
}
