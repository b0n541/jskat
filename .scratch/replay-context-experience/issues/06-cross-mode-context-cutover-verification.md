# 06 — Complete the cross-mode cutover and acceptance verification

**What to build:** Local, ISS, and replay all use the shared context structure with no duplicated or split controls. JavaFX integration coverage validates transitions, placement, dispatch, replay lifecycle, and the final visual/manual acceptance sweep.

**Blocked by:** 02 — Move local-play commands into the shared action area; 03 — Move applicable ISS trick-play commands into the shared action area; 04 — Deliver replay navigation through the shared action area; 05 — Present and clear replay skat in the lower-left context slot.

**Status:** resolved

- [x] JavaFX integration tests drive local, ISS, and replay transitions and verify observable placement, action dispatch, replay-skat lifecycle, and prohibited controls.
- [x] The user-visible cutover leaves every context with one reliable command surface and preserves toolbar continuity.
- [x] Manual acceptance covers local phases, applicable ISS trick play, replay pickup/discard through completion, reset or replay exit, and a subsequent game.

## Comments

Resolved in the accompanying `#194` commit. JavaFX acceptance coverage includes local bidding placement and dispatch, ISS trick-play placement and table-name routing, replay navigation, and replay-skat lifecycle boundaries. Verified with `./gradlew test`.
