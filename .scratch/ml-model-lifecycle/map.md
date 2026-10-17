# ML model lifecycle map

## Destination

Specify the complete ML-model lifecycle for JSkat: a reusable base-layer manager using the sole store at `~/.jskat/models`, JavaFX consent and progress for downloads and optional updates, and a clear headless outcome when models are unavailable.

## Notes

Domain: ML players and their externally released ONNX model data. Consult `grilling` and `domain-modeling` for human decisions. The JavaFX application checks model availability and updates once at application startup; it does not repeat the check when a player type is selected. Model release version 1.4.0 publishes `models.json` beside its assets. A compatible installed version remains usable when an update is offered.

## Decisions so far

- [Discover the model release manifest contract](issues/01-model-release-manifest-contract.md): The release manifest supplies the file inventory, byte sizes, and hashes; JSkat owns compatibility rules.
- [Specify the ML model startup download and update experience](issues/02-startup-download-and-update-experience.md): A startup-only, user-consented flow makes ML availability explicit, preserves a usable installed release, and visibly reports check failures with retry.
- [Decide safe model installation and activation](issues/03-safe-model-installation-contract.md): Validate a supported candidate in staging, atomically activate it, retain one rollback release, and serialize access with a store lock.
- [Define the base model-manager boundary](issues/04-base-model-manager-boundary.md): The manager resolves a typed, immutable model set before player creation; players receive that set and never access the store or lifecycle manager.
- [Inspect the Active model release and resolve model sets](issues/05-inspect-active-model-release-and-resolve-model-sets.md): Implemented by the JavaFX-free manager and temporary-store boundary tests.
- [Discover supported model-release updates](issues/06-discover-supported-model-release-updates.md): Implemented by the explicit update check and GitHub service boundary.
- [Install and activate a verified model release](issues/07-install-and-activate-a-verified-model-release.md): Implemented by staged, locked, verified activation with progress and cancellation.
- [Create ML players through resolved model sets](issues/08-create-ml-players-through-resolved-model-sets.md): Implemented by the resolved-set constructors and dedicated factory.
- [Run the JavaFX startup model lifecycle](issues/09-run-the-javafx-startup-model-lifecycle.md): Implemented by the startup adapter and background check flow.
- [Gate ML player choices in the series dialog](issues/10-gate-ml-player-choices-in-the-series-dialog.md): Implemented by carrying startup inspection into the dialog and series start.

## Not yet specified

None. The lifecycle contract is fully specified.

## Out of scope

- Training, changing, or publishing the ML models themselves.
- Automatic installation of a newer compatible release without the user's agreement.
- Implementing the migration, tests, and packaging changes; those are execution work following this lifecycle specification.
