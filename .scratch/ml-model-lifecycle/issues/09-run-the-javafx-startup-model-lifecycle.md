# 09: Run the JavaFX startup model lifecycle

**What to build:** JavaFX performs one startup-only check, asks consent for a missing release or compatible update, shows cancellable aggregate and per-file progress, and presents Continue/Retry failures while preserving any usable Active model release.

**Blocked by:** 06: Discover supported model-release updates; 07: Install and activate a verified model release.

**Status:** resolved

- [x] Startup inspects local availability, runs one background update check, and lets the application open while a usable Active model release remains available.
- [x] Missing data prompts for download; compatible updates offer Update now or Later; download progress is cancellable and reports the required aggregate and current-file state.
- [x] Check and manifest failures name the problem and whether an installed release remains usable, with Continue and Retry repeating only the startup flow.

## Answer

Implemented `ModelStartupLifecycle` as a JavaFX adapter. Startup publishes the local inspection immediately, performs one background update check, offers consent for missing data or updates, shows cancellable progress, and presents Continue/Retry outcomes while preserving an installed Active release.
