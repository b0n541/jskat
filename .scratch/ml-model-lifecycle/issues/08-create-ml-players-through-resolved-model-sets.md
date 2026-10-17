# 08: Create ML players through resolved model sets

**What to build:** ML Player and ML Player Pro can be created only through a dedicated factory from a Resolved model set. Series creation and headless callers receive typed unavailable outcomes; implicit path lookup, arbitrary-path constructors, and build-time model downloads are removed.

**Blocked by:** 05: Inspect the Active model release and resolve model sets.

**Status:** resolved

- [x] Both ML player variants receive only their required artifacts from a Resolved model set and cannot browse the model store or initiate lifecycle operations.
- [x] The player-resolution and series-start path creates ML players through the factory and reports a typed model-unavailable result instead of a reflective construction failure.
- [x] Tests provide resolved test model sets through the same factory, existing feature-interface parity remains protected, and build and test execution no longer fetch model data implicitly.

## Answer

Implemented `ResolvedModelSet`, `MLPlayerFactory`, and model-set constructors for both ML player variants. Removed path browsing and no-argument constructors, routed series creation through the factory with typed unavailable results, adapted the ML test to the factory seam, and removed the Gradle model download task and test dependency.
