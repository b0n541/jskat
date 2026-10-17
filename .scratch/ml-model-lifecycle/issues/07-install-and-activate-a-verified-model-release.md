# 07: Install and activate a verified model release

**What to build:** A caller that has obtained consent can download a supported release with progress and cancellation, validate it in a Staged model release, atomically make it Active, retain one rollback release, and safely recover under concurrent or interrupted installation.

**Blocked by:** 05: Inspect the Active model release and resolve model sets; 06: Discover supported model-release updates.

**Status:** resolved

- [x] Download progress reports the current file, completed files, and aggregate bytes, beginning indeterminate until the release service confirms totals; cancellation stops the operation promptly.
- [x] Only a complete, manifest-verified supported candidate becomes the Active model release; transfer failure, corruption, cancellation, and interruption preserve the previous Active model release.
- [x] Store locking serializes concurrent operations, cleanup safely removes abandoned Staged model releases, and successful activations retain exactly the immediately prior valid release for rollback.

## Answer

Implemented in `ModelManager.install()`. Downloads emit initial indeterminate and aggregate/per-file progress, cancellation and transfer failures remove only staged data, verification happens before activation, the store lock serializes installation, and activation updates the marker atomically while retaining the prior Active release.
