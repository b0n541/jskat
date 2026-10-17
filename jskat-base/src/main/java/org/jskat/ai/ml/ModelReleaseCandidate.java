package org.jskat.ai.ml;

/** A remote release whose manifest passed discovery and compatibility checks. */
public record ModelReleaseCandidate(ModelReleaseService.RemoteRelease release,
                                    ModelReleaseManifest manifest) {
}
