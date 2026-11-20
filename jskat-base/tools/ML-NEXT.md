# Comparing ML next

Select **ML next** in the new-series player list, or use tournament implementation
`ml-next`. ML and ML Pro retain their existing identities. ML next uses the same
bidding and card-play models as ML Pro, batches eligible contracts per discard
pair, and shares the Null/Null Ouvert prediction while scoring their distinct
values separately. Candidate order and strict `>` tie selection are retained.

Existing releases work using scalar evaluation. The v1.4.0 transformer evaluator
contains a fixed batch-one attention reshape despite its dynamic input metadata.
To exercise batching, create a separate development release; the tool verifies
all source artifacts and only accepts the known v1.4.0 evaluator graph/weights.
It never activates a release or overwrites an existing destination.

```sh
python3 -m venv /tmp/jskat-onnx-tools
/tmp/jskat-onnx-tools/bin/pip install onnx==1.23.2
/tmp/jskat-onnx-tools/bin/python jskat-base/tools/prepare_ml_next_release.py \
  "$HOME/.jskat/models/releases/v1.4.0" \
  "$PWD/.scratch/tournament-performance/ml-next/models"
```

Choose that directory as a development model source in the tournament editor,
or add a competitor to a tournament configuration:

```json
{
  "id": "ca207ce9-3ca4-4ce1-8443-81e081b8344e",
  "name": "ML next batch",
  "implementation": "ml-next",
  "model": {
    "source": "development",
    "path": "/absolute/path/to/.scratch/tournament-performance/ml-next/models"
  }
}
```

Use different UUIDs and names for other competitors. ML Pro with installed
v1.4.0 supplies the scalar baseline; ML next with installed v1.4.0 isolates
prediction reuse; ML next with the development release adds batching. Model
snapshots and content fingerprints distinguish the transformed release.

Batch execution can introduce small floating-point differences: the initial
varied-input probe observed absolute probability differences up to 1.2e-7.
Treat tournament decision/outcome comparisons as measured evidence, not a
universal promise of identical play. Batch execution failures are reported as
inference fallbacks before scalar recovery, disabling further batch attempts
for that player. Such fallbacks remain visible in tournament reproducibility
reports. Fixed-one metadata uses scalar inference without a failed batch probe.

The card-play graph already runs with ONNX Runtime's default full optimization.
The initial optimization-level experiment found no additional useful setting;
this player does not silently quantize or replace that model.
