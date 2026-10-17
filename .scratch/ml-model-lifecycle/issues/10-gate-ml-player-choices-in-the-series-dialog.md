# 10: Gate ML player choices in the series dialog

**What to build:** The series dialog enables ML Player and ML Player Pro only when startup established a Resolved model set, explains unavailability otherwise, and starts ML series through the factory without another availability check or network request.

**Blocked by:** 08: Create ML players through resolved model sets; 09: Run the JavaFX startup model lifecycle.

**Status:** resolved

- [x] ML Player and ML Player Pro are selectable only when the startup state supplies a Resolved model set; disabled controls state why ML models are unavailable.
- [x] Confirming a series that uses an ML player reaches the dedicated factory, while non-ML player selection continues to work normally.
- [x] Series-dialog coverage proves selecting a player type never repeats an availability check or triggers network activity.

## Answer

The series dialog now receives the startup `ModelInspection`, disables ML player cells with the typed unavailable reason when no `ResolvedModelSet` exists, and passes the established set to `JSkatMaster.startSeries()`. Series creation uses `MLPlayerFactory`; player selection performs no manager or network operation.
