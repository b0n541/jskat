package org.jskat.ai.ml;

/**
 * A remote release whose manifest passed discovery and compatibility checks.
 *
 * @param release remote release metadata
 * @param manifest validated model release manifest
 */
public record ModelReleaseCandidate(ModelReleaseService.RemoteRelease release,
                                    ModelReleaseManifest manifest) {
}
