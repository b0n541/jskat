package org.jskat.ai.ml;

/** Result of side-effect-free local Active-release inspection. */
public final class ModelInspection {

    private final ResolvedModelSet resolvedModelSet;
    private final ModelUnavailable unavailable;

    private ModelInspection(final ResolvedModelSet resolvedModelSet, final ModelUnavailable unavailable) {
        this.resolvedModelSet = resolvedModelSet;
        this.unavailable = unavailable;
    }

    public static ModelInspection available(final ResolvedModelSet resolvedModelSet) {
        return new ModelInspection(resolvedModelSet, null);
    }

    public static ModelInspection unavailable(final ModelUnavailable unavailable) {
        return new ModelInspection(null, unavailable);
    }

    public boolean isAvailable() {
        return resolvedModelSet != null;
    }

    public ResolvedModelSet resolvedModelSet() {
        return resolvedModelSet;
    }

    public ModelUnavailable unavailable() {
        return unavailable;
    }

    public String installedVersion() {
        return isAvailable() ? resolvedModelSet.version() : unavailable.installedVersion();
    }
}
