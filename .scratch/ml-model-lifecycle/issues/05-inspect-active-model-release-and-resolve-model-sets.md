# 05: Inspect the Active model release and resolve model sets

**What to build:** Headless callers can inspect the sole model store without network access and receive either a validated, immutable Resolved model set or a typed reason that ML models are unavailable.

**Blocked by:** None (can start immediately).

**Status:** resolved

- [x] Local inspection validates the Active model release against the supported release and required-variant rules without any network operation.
- [x] A valid Active model release resolves to an immutable Resolved model set; missing, corrupt, unsupported, or inconsistent releases return typed unavailable reasons.
- [x] Boundary tests use temporary model stores and cover valid resolution plus every unavailable outcome without relying on a developer machine.

## Answer

Implemented in `ModelManager.inspectLocalAvailability()` with temporary-store boundary coverage in `ModelManagerTest`. The manager validates the Active marker, supported version, manifest file set, file sizes, and SHA-256 values before returning an immutable `ResolvedModelSet` or a typed `ModelUnavailable` reason.
