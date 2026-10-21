# Tournament acceptance evidence

This records ticket #19 / issue #265 verification on 2026-10-21. Contract IDs
A01–A18 refer to `.scratch/ai-model-tournament/spec.md` in the local issue tracker.
The shipped behavior and format definitions are linked from [TOURNAMENT.md](TOURNAMENT.md).
These are production JavaFX and application entry-point tests; the planning
prototype is not implementation evidence.

## Executed environment and commands

Integration baseline: `d500ca6175c7f25e1717c9b1091b75d16d6e2848`.
The tested packaged identity was that commit plus dirty source digest
`5e714565e68a650acaebe5875e4f952bbabc4210dbc06dfbf1bd20e59a1cef66`
(the app acceptance test dependency contributes to build inputs). All separate
application JVM comparisons used that same packaged identity. Test/doc edits do
not contribute to the source digest. A subsequent commit changes the build
identity; retained test runs from this execution must use their original build.

Environment: Linux, Temurin JDK 25+36, JavaFX 27, ONNX Runtime 1.31.0,
actual installed `~/.jskat/models/releases/v1.4.0` nine-artifact release.
JavaFX used `DISPLAY=:0`. Child headless application JVMs explicitly removed
DISPLAY and WAYLAND_DISPLAY and used an isolated user home with no model store.
`findmnt -T` identified `/dev/mapper/system-root` as ext4 for the selected test
root. `/tmp` is tmpfs and is intentionally refused by production run storage.

```bash
JAVA_HOME=/home/jan/.sdkman/candidates/java/current \
JSKAT_TOURNAMENT_EXT4_TEST_ROOT=/home/jan/git/jskat/.scratch/ai-model-tournament/test-runs \
DISPLAY=:0 ./gradlew test
```

Full root result: **BUILD SUCCESSFUL**, 1m25s. Base: 385 tests, zero failures or
errors, three existing skips. GUI: 90 tests, zero failures/errors/skips. App: nine
tests, zero failures/errors/skips. All tournament tests, including ONNX-fixture
and filesystem scenarios, executed. The three skipped tests belong to
`org.jskat.control.iss.MessageParserTest`:
`testParseGameSummary_IncompleteGrandOuvertAnnouncement`,
`testParseGameSummary_NullOuvertTwoResigns`, and
`testParseGameSummary_CorruptData`.

After adding explicit CLI lock/build/version fixtures and native invalid-record
inspection, the affected adapters were rerun with the same environment:

```bash
./gradlew :app:test :jskat-gui:test --tests org.jskat.gui.javafx.tournament.TournamentWorkflowTest
```

Final adapter result: **BUILD SUCCESSFUL**, 1m2s, app ten tests and the three native
Workflow tests, zero failures/errors/skips. The native-ownership suite was then
rerun (five tests, zero failures/errors/skips) with alternating valid ML/ML-Pro
competitors to force pool eviction; app test imports were recompiled. The earlier full-root GUI count
remains 90 (the new invalid-record scenario extends an existing method). XML evidence is in
`<module>/build/test-results/test/TEST-*.xml`; HTML is in
`<module>/build/reports/tests/test/index.html`. These generated outputs are not
source artifacts. The retained durability fixtures remain under the selected
ext4 root for audit; no ordinary active model or original release was changed.

## Acceptance matrix

Base test classes below are under
[`src/test/java/org/jskat/tournament`](src/test/java/org/jskat/tournament).
`Entry` means [`TournamentEntryTest`](../app/src/test/java/org/jskat/TournamentEntryTest.java)
launching the actual `org.jskat.JSkatKt` main in child JVMs. GUI classes are under
[`jskat-gui/src/test/kotlin/org/jskat/gui/javafx/tournament`](../jskat-gui/src/test/kotlin/org/jskat/gui/javafx/tournament).
Names without class suffix abbreviate `Tournament…Test`.

| ID | Executed public-boundary evidence |
| --- | --- |
| A01 | `StrictValidation` unsigned seed boundaries, exact epochs, duplicate keys/trailing content/malformed UTF-8, Unicode/path normalization, source variants and schedule representation bounds; `Configuration.aggregatesOrderedSchemaAndRosterIssues`; `Identity.canonicalRequestIgnoresFormattingAndRosterOrderingButRetainsSeedPresenceAndOutput`. `Entry.actualCliPreservesOrderedStructuralAndSourcePreflightIssues` and `Workflow` **native import and real source preflight preserve complete shared ordered issues** compare complete ordered issue values from real reader/preflight against adapters, including output and missing-model failures. |
| A02 | `Inputs.modelFreeRosterDoesNotNeedModelStore`, `sourcePreflightDoesNotCreateOutputOrSnapshots`, `activeAndInstalledAliasesRejectDuplicateEffectiveCompetitors`, `discoveryChecksOnlyTheSelectedImplementationsSignatures`; `Service.sharedPreflightReturnsBuildAndReadOnlyCompetitorIdentities`. `Entry.runsAndExplicitlyResumesCompletedRunWithoutModelStoreOrDisplay` checks isolated model home remains absent; real `Workflow` model-free Validate/Start and `Editor` invalid discovery control exercise native adapters. Model-backed tests resolve/probe actual ONNX files without activating/installing them. |
| A03 | `Inputs.snapshotsSurviveSourceRemovalAndRejectRetainedTampering`, `hashValidArtifactsWithWrongTensorSignatureAreRejected`, and `StoreDurability.missingModelSourceRefusesBeforeCreatingOutput`. `Entry.modelBackedRunUsesRelativeDevelopmentSourceAndCompletedResumeUsesSnapshot` completes ML-Pro play then deletes its test development source before resume. `Workflow` **retained native inspection verifies snapshots after original source disappears and corruption stays inspectable** uses retained model snapshots after source removal. |
| A04 | `Entry.freshJvmWorkersAndVerboseResumeReproduceEveryRetainedSemanticResult` executes **both model-free and actual ML-Pro variants** in separate application JVMs: three epochs, workers1 quiet, workers3 verbose, then SIGINT after a durable commit and workers4 verbose resume. It compares full retained schedule/decks/seeds, canonical game IDs/outcomes/scores/ordered events/fallbacks, and all semantic summary fields (block totals, observations, ratings, 5,000-replicate intervals, predictive diagnostics, descriptive/pairwise data and interpretation). Every result has no fallback and reports reproducible=true. Only run/output/config provenance, checksums and session/attempt identities differ. `RegisteredGame.registeredModelFreeLeasesPlayLegalRepeatableRetainedDeals` and `PlayerPool.leasesThreeIndependentSeatsAndResetsReusedRandomStream` additionally exercise reset/reuse. |
| A05 | `Schedule.exactGoldenSchedulesUseIndependentPythonFixtures` freezes two/three-competitor vectors; `twoCompetitorsHaveCanonicalSixMixedAssignmentsAndOneDeck` checks AAB…BBA. `fourCompetitorsCoverEveryTripleWithOneSharedDeckAndBalancedSeats` covers 48 games over two epochs, four blocks per epoch, all six permutations, shared epoch deck and balanced seat participation, unique game/player seeds. `Identity.versionOneSeedAndArtifactGoldenVectors` independently freezes purpose-separated encodings. |
| A06 | `GameCore` exact all-pass, Grand-Hand-Schwarz and Null-Hand fixtures verify legal values and Seeger–Fabian scores; callback/finalize failures are unscored. `Reports.duplicatedSeatScoresSumEveryInstanceBeforeBlockOrdering` covers repeated competitors. `RegisteredGame.fallbacksAreObservedAfterCallbacksBeforeResultFreeze` and `Runner.failedAttemptFallbackEvidenceSurvivesResumeAndRemainsInReports` preserve rateable legal fallback and permanent warning evidence. CLI output and native `Presentation` consume shared summaries; successful integrated runs above check fallback-free audit. |
| A07 | `Ratings.priorAndEloScaleMatchIndependentScalarRoot`, `wholeEpochBootstrapAndOrderingMatchIndependentTwoEpochFixture`, `independentFiveEpochOracleFreezesBootstrapRngAndPercentiles` use independent numerical expectations. `Reports` verifies partial epochs, exact all-pass ties/intervals and held-out log loss/calibration. Fresh-JVM `Entry` compares the complete published numerical outputs. Native `Presentation` **published ratings show values and unavailable uncertainty without changing shared ordering** covers adapters' unavailable intervals and order. |
| A08 | `Entry.malformedTournamentArgumentsNeverInitializeGraphics`, `runsAndExplicitlyResumesCompletedRunWithoutModelStoreOrDisplay`, and actual ML-Pro entry tests run main without DISPLAY/WAYLAND and without ordinary model startup/download. `GameCore` and `RegisteredGame` execute the shared headless engine. Full base suite also passes existing `control.SkatGameTest` ordinary local-game regressions, including bid notifications, pass-in, Ramsch/contra options and complete/predefined play; the local adapter and tournament use `SkatGameEngine`. |
| A09 | `Runner.cancellationDrainsAdmittedGamesAndGateRemainsOwnedThroughCleanup`, `processGateIsHeldDuringPreparationAndReleasedAfterPreparationFailure`, `observerFailureDoesNotChangeDurableCompletion`, `callbackCancellationExceptionIsAnUnscoredFailureEvenDuringRequestedCancellation`, `publicationOnlyRecoveryAndCancellationAfterLastCommitNeedNoNewGames`; `Service.cancelledNewInvocationCleansUpBeforeTerminalAndReleasesProcessGate`. `Entry.signalsDrainAdmittedGamesAndPublishTerminalStatus` runs actual SIGINT and SIGTERM, verifies drain/publication and JVM exits130/143. Native controller/host tests cover matching cancellation ownership. |
| A10 | `NativeOwnership` injects failures at the external ONNX API during partial construction and close, verifies all earlier sessions close and Pro's fourth session cannot silently disappear. `repeatedNativePlayerEvictionNeverExceedsTwelveSessionsPerWorkerAndClosesEverything` drives the real pool and ML-Pro constructors through 12 changing-roster rounds with workers3: peak exactly36 external sessions, eviction before replacement, all sessions closed at end. `PlayerPool.concurrentGamesStayWithinCapacityAndCancelledWaitersDoNotAcquire` covers distinct leases, capacity and cancelled waiters. `Runner.preFreezeFailureIsUnscoredAndStopsAdmissionWhilePostFreezeCloseRetainsScore` and cleanup error fixtures cover terminal ownership/failure precedence. Actual ONNX construction/play is exercised by app and GUI fixtures; resource-failure injection counts the native API boundary rather than process RSS/threads. |
| A11 | `StoreDurability.readyCrashWindowsNeverCountTemporaryFilesAsReadiness`, `gameBeforeRenameFailureRemainsPendingAndExplicitResumeUsesNewAttempt`, `gameCommitAcknowledgesOnlyAfterDirectoryFlushAndResumeSkipsUncertainPublication`, `reportPointerCrashExposesOneCompleteGeneration`; `Runner.postRenameCommitUncertaintyDoesNotInflateAcknowledgedCountsAndResumeFindsTheGame`. `DurableTournamentRunStore.unsupportedTmpfsFailsBeforeResolverAndNeverPublishesReady` checks refusal. All run on explicitly selected real ext4; these are injected process/syscall publication failures, not destructive hardware power cuts. |
| A12 | `Record.recordsBindRunTypeVersionAndChecksum`, `BuildIdentity.packagedIdentityRetainsFullCommitAndOptionalDirtyInputDigest`, strict config/digest fixtures, `DurableTournamentRunStore.readyRunResumesOnlyRetainedInputsAndRejectsLiveLockAndTampering`, `StoreDurability` malformed/forged-score/actual-second-process lock/stale-audit tests. `Entry.actualCliRefusesWrongBuildAndLiveLockAndRequiresAuditedStaleRecovery` exercises exact-build refusal, a real independently launched CLI denied a live OS lock even with recovery, stale refusal without authorization, successful explicit recovery preserving exact previous bytes, and unsupported ready-record version refusal without repair. Existing `Entry` checks changed seed/output refusal. Native workflow reopens an unsupported ready record with Start/Resume disabled and leaves bytes unchanged. |
| A13 | `Runner.explicitResumeRetriesOnlyPendingGamesAndMatchesSequentialOutcome`, `actualParallelRunCompletesAndCompletedResumeConstructsNoPlayers`, `publicationOnlyRecoveryAndCancellationAfterLastCommitNeedNoNewGames`; `StoreDurability.completedVerificationIsReadOnlyAndMissingExportsRequireOnlyPublicationRecovery`. Actual `Entry` interrupted/resumed variant preserves full semantics; `runsAndExplicitlyResumesCompletedRunWithoutModelStoreOrDisplay` compares every retained file byte after completed resume. `Entry.actualCliRecoversMissingDerivedExportWithoutReplacingCommittedGames` and native `Workflow` real Open/Verify/Resume remove a derived export, regenerate with zero new admissions, and preserve the exact committed set including attempt IDs/checksums. |
| A14 | `Reports.durableGenerationPublishesAndReadsOneFixedObservation`, `partialEpochIsProgressOnlyAndNoEvidenceDoesNotInventRating`; `StoreDurability.reportPointerCrashExposesOneCompleteGeneration` checks both pointer boundaries and `failureHistoriesSurviveRetryAndOnlyCompleteEpochsEnterFixedSet` checks audit/population. `Runner.failedAttemptFallbackEvidenceSurvivesResumeAndRemainsInReports` retains failure/fallback evidence. Actual CLI and native Workflow recover exports and capture generation IDs; native Presentation renders the captured shared generation rather than recomputing ratings. |
| A15 | `Command.rejectsMalformedControlsWithoutReadingConfiguration`, `printsEverySharedConfigurationIssueInStablePathOrder`, `coalescesFastPublicationUpdatesButShowsStallsAndFailuresImmediately`, `standaloneHelpIsDisplayFreeAndSuccessful`; all `Entry` cases exercise production main, exits0/1/2/130/143, normalized output, terminal state, session controls, explicit resume/recovery and signal hook draining. `Runner.aStallWarnsOnceUsingMonotonicTimeAndNeverInterruptsTheGame` tests the single nonfatal stall boundary with injected monotonic time. |
| A16 | Real native `Workflow` covers import/Validate/Start/Open/Verify/Resume against default backend, source-free retained snapshot verification, negative issue parity and corrupt inspection. `Editor` **native roster edits and export preserve UUIDs and imported relative path context**; `Host` **ordinary menu hides tournament and developer action reattaches one monitor**. `Panel` latches read-only preflight while an FX heartbeat executes, rejects simultaneous input ownership and renders ordered issues on actual fields. Controller/Host latches keep FX responsive through run/report/finalization/cleanup while native controls follow ownership. |
| A17 | Native `Host` **inspection resume keeps shared run context when another inspection closes and monitoring reopens** and **ordinary menu hides tournament and developer action reattaches one monitor** exercise actual native tab/window/menu routing. `Controller` **detaching a cancelled monitor keeps ownership until background cleanup ends**, **Continue preserves monitoring and finalizing approval detaches without new cancellation**, **broken monitor cannot prevent runner cleanup and application exit** test detachment, Continue/Escape-equivalent dismissal, process gate, reopening, exit waiting and listener isolation. Latches represent the external execution boundary; real Workflow separately executes the shared service/native probes to terminal cleanup. |
| A18 | `Runner.epochPublicationBlocksNextAdmissionAndFailureClosesTheBarrier`, `aWorkerFailureStopsFurtherAdmissionBeforeItsFreedSlotCanBeReused`, `aStallWarnsOnceUsingMonotonicTimeAndNeverInterruptsTheGame`; pool/native bound stress above; four-roster scheduling; actual fresh-JVM Entry changed-worker resume and verbosity equivalence above. Publication blocks later-epoch admissions without reducing bootstrap replicates. `Command` verifies routine ≤1/s versus immediate stall/failure; native controller monotonic immutable progress prevents older observations replacing newer lifecycle state. |

## Support boundaries

Reproducibility evidence is for the same packaged build and fallback-free retained
inputs. ML evidence uses the installed v1.4.0 fixture and both real ML/ML-Pro
construction paths; it is not a cross-build, cross-hardware or arbitrary future
ONNX model guarantee. Fallback is legal and rateable but permanently makes a run
non-reproducible. Actual fresh-JVM equality includes ML-Pro inference and pool
reuse, not only a synthetic all-pass player.

Durability evidence establishes the Linux/ext4 software boundary: OS locks,
file/directory force, same-filesystem atomic rename, immutable publication and
failure recovery. It does not examine other filesystems, power removal or drive
cache honesty. Production rejects unsupported providers/types. There is no
salvage/migration/authoritative repair or live-lock takeover. Cooperative
cancellation and stall warnings cannot terminate a hung callback. Native counts
bound owned player sessions, not total process threads or memory consumption.
