# 05 — Preserve JavaFX table lifecycle

**What to build:** A player can create a correctly named table, activate the intended table when selecting a table tab, and close a local or ISS table through the matching table lifecycle action rather than merely hiding the tab.

**Blocked by:** None — can start immediately.

**Status:** done

- [x] Table naming, validation, selection, focus, and close behavior conform to the compatibility boundary for local and ISS tables.
- [x] Deterministic contract tests demonstrate the table lifecycle without relying on a desktop window.
