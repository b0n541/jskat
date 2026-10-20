package org.jskat.tournament;

import ai.onnxruntime.*;

import java.nio.file.Path;
import java.util.*;

/** Tensor metadata accepted by the built-in inference wrappers; batch is dynamic or one. */
final class TournamentModelSignatures {
    private record Tensor(OnnxJavaType type, long[] shape) {}

    private static Tensor f(long... shape) {
        return new Tensor(OnnxJavaType.FLOAT, shape);
    }

    private static Tensor i(long... shape) {
        return new Tensor(OnnxJavaType.INT64, shape);
    }

    private static Tensor b(long... shape) {
        return new Tensor(OnnxJavaType.BOOL, shape);
    }

    static void verify(Path directory, PlayerImplementation implementation) throws OrtException {
        check(
                directory,
                "bidding_dense.onnx",
                Map.of("features", f(-1, 35)),
                Map.of("pickup_probs", f(-1, 63), "hand_probs", f(-1, 63)));
        check(
                directory,
                "card_play_transformer.onnx",
                Map.ofEntries(
                        Map.entry("game_type", i(-1)),
                        Map.entry("declarer", i(-1)),
                        Map.entry("is_ouvert", i(-1)),
                        Map.entry("hand_len", i(-1)),
                        Map.entry("ouvert_hand_len", i(-1)),
                        Map.entry("history_len", i(-1)),
                        Map.entry("trick_len", i(-1)),
                        Map.entry("hand", i(-1, 10)),
                        Map.entry("ouvert_hand", i(-1, 10)),
                        Map.entry("history", i(-1, 27, 2)),
                        Map.entry("trick", i(-1, 2, 2)),
                        Map.entry("legal_mask", b(-1, 32))),
                Map.of("logits", f(-1, 32)));
        if (implementation == PlayerImplementation.ML)
            check(
                    directory,
                    "game_eval_dense.onnx",
                    Map.of("features", f(-1, 75)),
                    Map.of("win_prob", f(-1)));
        else {
            check(
                    directory,
                    "bidding_transformer.onnx",
                    Map.of("hand_cards", i(-1, 10), "position", i(-1)),
                    Map.of("pickup_probs", f(-1, 63), "hand_probs", f(-1, 63)));
            check(
                    directory,
                    "game_eval_transformer.onnx",
                    Map.ofEntries(
                            Map.entry("hand_cards", i(-1, 10)),
                            Map.entry("skat_cards", i(-1, 2)),
                            Map.entry("skat_len", i(-1)),
                            Map.entry("game_type", i(-1)),
                            Map.entry("position", i(-1)),
                            Map.entry("is_hand", i(-1)),
                            Map.entry("bid", f(-1))),
                    Map.of("win_prob", f(-1)));
        }
    }

    private static void check(
            Path directory,
            String filename,
            Map<String, Tensor> inputs,
            Map<String, Tensor> outputs)
            throws OrtException {
        try (var options = new OrtSession.SessionOptions()) {
            options.setIntraOpNumThreads(1);
            options.setInterOpNumThreads(1);
            try (var session =
                    OrtEnvironment.getEnvironment()
                            .createSession(directory.resolve(filename).toString(), options)) {
                checkMap(filename, "inputs", session.getInputInfo(), inputs);
                checkMap(filename, "outputs", session.getOutputInfo(), outputs);
            }
        }
    }

    private static void checkMap(
            String filename,
            String direction,
            Map<String, NodeInfo> actual,
            Map<String, Tensor> expected) {
        if (!actual.keySet().equals(expected.keySet()))
            throw new IllegalArgumentException(
                    filename + " incompatible " + direction + " names: " + actual.keySet());
        for (var entry : expected.entrySet()) {
            if (!(actual.get(entry.getKey()).getInfo() instanceof TensorInfo info))
                throw new IllegalArgumentException(filename + " non-tensor " + entry.getKey());
            var wanted = entry.getValue();
            long[] shape = info.getShape();
            // Both scalar probability wrapper layouts [N] and [N,1] are explicitly supported.
            boolean probability =
                    entry.getKey().equals("win_prob") && shape.length == 2 && shape[1] == 1;
            boolean valid =
                    info.type == wanted.type
                            && (shape.length == wanted.shape.length || probability);
            if (valid)
                for (int axis = 0; axis < wanted.shape.length; axis++)
                    valid &=
                            axis == 0
                                    ? (shape[axis] < 0 || shape[axis] == 1)
                                    : shape[axis] == wanted.shape[axis];
            if (!valid)
                throw new IllegalArgumentException(
                        filename + " incompatible tensor " + entry.getKey() + ": " + info);
        }
    }
}
