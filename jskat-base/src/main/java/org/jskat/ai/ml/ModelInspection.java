package org.jskat.ai.ml;

/**
 * Result of side-effect-free local Active-release inspection.
 */
public final class ModelInspection {

    private final ResolvedModelSet resolvedModelSet;
    private final ModelUnavailable unavailable;

    private ModelInspection(final ResolvedModelSet resolvedModelSet, final ModelUnavailable unavailable) {
        this.resolvedModelSet = resolvedModelSet;
        this.unavailable = unavailable;
    }

    /**
     * Performs available.
     * @param resolvedModelSet resolved model set value.
     * @return available result.
     */
    public static ModelInspection available(final ResolvedModelSet resolvedModelSet) {
        return new ModelInspection(resolvedModelSet, null);
    }

    /**
     * Performs unavailable.
     * @param unavailable unavailable value.
     * @return unavailable result.
     */
    public static ModelInspection unavailable(final ModelUnavailable unavailable) {
        return new ModelInspection(null, unavailable);
    }

    /**
     * Returns or indicates available.
     * @return is available result.
     */
    public boolean isAvailable() {
        return resolvedModelSet != null;
    }

    /**
     * Resolved model set.
     * @return resolved model set result.
     */
    public ResolvedModelSet resolvedModelSet() {
        return resolvedModelSet;
    }

    /**
     * Performs unavailable.
     * @return unavailable result.
     */
    public ModelUnavailable unavailable() {
        return unavailable;
    }

    /**
     * Performs installed version.
     * @return installed version result.
     */
    public String installedVersion() {
        return isAvailable() ? resolvedModelSet.version() : unavailable.installedVersion();
    }
}
