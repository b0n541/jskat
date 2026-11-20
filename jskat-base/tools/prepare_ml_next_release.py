#!/usr/bin/env python3
"""Create a separate batch-capable development release from pinned v1.4.0 models.

Requires onnx==1.23.2. The destination must not exist; no release is activated.
See ML-NEXT.md for reproduction and tournament comparison instructions.
"""

import argparse
import hashlib
import json
import pathlib
import shutil

import numpy as np
import onnx
from onnx import numpy_helper

PINNED_GRAPH = "44be5760e3eae9e9be3cd1964aa5cb1cec951f272a39d192edff7288e5898cde"
PINNED_WEIGHTS = "7252b59e2b3dc224496413e019fc7c32d69023cbe3d96190b43d6ca993b5f8df"
FILES = {
    "bidding_dense.onnx", "bidding_dense.onnx.data",
    "bidding_transformer.onnx", "bidding_transformer.onnx.data",
    "game_eval_dense.onnx", "game_eval_dense.onnx.data",
    "game_eval_transformer.onnx", "game_eval_transformer.onnx.data",
    "card_play_transformer.onnx",
}


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source", type=pathlib.Path)
    parser.add_argument("destination", type=pathlib.Path)
    parser.add_argument("--version", default="1.4.0-ml-next-batch")
    args = parser.parse_args()
    source = args.source.resolve()
    if args.destination.is_symlink():
        raise ValueError("Destination must not be a symbolic link")
    destination = args.destination.resolve()
    if destination.exists():
        raise ValueError("Destination must not exist")
    if source == destination or source in destination.parents:
        raise ValueError("Destination must be outside source release")

    manifest = json.loads((source / "models.json").read_text())
    if manifest["version"] != "1.4.0" or not args.version.strip() or args.version == "1.4.0":
        raise ValueError("Requires v1.4.0 source and a distinct nonempty version")
    assets = [
        asset
        for family in manifest["models"].values()
        for variant in family["variants"].values()
        for asset in variant["files"]
    ]
    if len(assets) != len(FILES) or {asset["name"] for asset in assets} != FILES:
        raise ValueError("Unexpected release inventory")
    for asset in assets:
        path = source / asset["name"]
        if path.is_symlink() or not path.is_file():
            raise ValueError("Source artifact must be a regular file: " + asset["name"])
        if path.stat().st_size != asset["size_bytes"] or digest(path) != asset["sha256"]:
            raise ValueError("Source integrity failure: " + asset["name"])
    if (digest(source / "game_eval_transformer.onnx") != PINNED_GRAPH
            or digest(source / "game_eval_transformer.onnx.data") != PINNED_WEIGHTS):
        raise ValueError("Unsupported transformer export")

    model = onnx.load(source / "game_eval_transformer.onnx", load_external_data=False)
    shape = next(tensor for tensor in model.graph.initializer if tensor.name == "val_75")
    if not np.array_equal(numpy_helper.to_array(shape), [17, 1, 3, 256]):
        raise ValueError("Unexpected attention shape")
    consumers = sum(
        node.op_type == "Reshape" and len(node.input) > 1 and node.input[1] == "val_75"
        for node in model.graph.node
    )
    if consumers != 4:
        raise ValueError("Unexpected attention reshape consumers")
    shape.CopyFrom(numpy_helper.from_array(
        np.array([17, -1, 3, 256], dtype=np.int64), name="val_75"))
    # Stale batch-one intermediates otherwise produce fixed-one Gemm reshapes in ORT.
    del model.graph.value_info[:]
    if len(model.graph.output) != 1 or model.graph.output[0].name != "win_prob":
        raise ValueError("Unexpected output")
    model.graph.output[0].type.tensor_type.shape.dim[0].dim_param = "batch"

    destination.mkdir(parents=True)
    try:
        for name in sorted(FILES):
            shutil.copyfile(source / name, destination / name)
        onnx.save(model, destination / "game_eval_transformer.onnx")
        # Checking by path resolves the unchanged external weight file beside the graph.
        onnx.checker.check_model(str(destination / "game_eval_transformer.onnx"))
        for asset in assets:
            path = destination / asset["name"]
            asset["sha256"] = digest(path)
            asset["size_bytes"] = path.stat().st_size
        manifest["version"] = args.version
        manifest["base_url"] = destination.as_uri()
        (destination / "models.json").write_text(json.dumps(manifest, indent=2) + "\n")
    except BaseException:
        shutil.rmtree(destination)
        raise

    print(json.dumps({
        "release_directory": str(destination),
        "source_version": "1.4.0",
        "version": args.version,
        "source_graph_sha256": PINNED_GRAPH,
        "source_weights_sha256": PINNED_WEIGHTS,
        "transformed_graph_sha256": digest(destination / "game_eval_transformer.onnx"),
        "onnx_version": onnx.__version__,
        "transformation": (
            "val_75 [17,1,3,256] -> [17,-1,3,256]; "
            "clear intermediate value_info; output dimension -> batch"
        ),
    }, indent=2))


if __name__ == "__main__":
    main()
