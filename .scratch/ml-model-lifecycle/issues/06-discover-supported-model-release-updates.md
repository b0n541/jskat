# 06: Discover supported model-release updates

**What to build:** An explicit update check identifies the newest published, stable, allowlisted candidate by validating its manifest and release tag; failures have typed explanations and no local model state changes.

**Blocked by:** 05: Inspect the Active model release and resolve model sets.

**Status:** resolved

- [x] Release discovery ignores drafts and prereleases and compares parsed manifest versions rather than release creation order.
- [x] A candidate is eligible only when its manifest is valid, agrees with the release tag, and appears in JSkat's built-in supported-version allowlist.
- [x] Update checks return a typed available, up-to-date, unsupported, or failed outcome and leave the Active model release unchanged.

## Answer

Implemented in `ModelManager.checkForUpdates()` and `GitHubModelReleaseService`. Stable release metadata and manifest versions are validated against the built-in allowlist and release tag, with typed update outcomes and no local activation during checking.
