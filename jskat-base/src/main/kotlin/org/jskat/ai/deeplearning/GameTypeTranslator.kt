package org.jskat.ai.deeplearning

import ai.djl.ndarray.NDList
import ai.djl.translate.Translator
import ai.djl.translate.TranslatorContext
import org.jskat.util.GameType

class GameTypeTranslator : Translator<FloatArray, GameType> {
    override fun processInput(
        ctx: TranslatorContext?,
        input: FloatArray?
    ): NDList? {
        return NDList(ctx?.ndManager?.create(input))
    }

    override fun processOutput(
        ctx: TranslatorContext?,
        list: NDList?
    ): GameType? {
        var probabilities = list?.singletonOrThrow()
        var probs = probabilities?.toFloatArray()
        var maxIndex = 0
        var max = probs!![0]

        for (i in 1 until probs!!.size) {
            if (probs!![i] > max) {
                max = probs!![i]
                maxIndex = i
            }
        }

        return listOf(
            GameType.CLUBS,
            GameType.DIAMONDS,
            GameType.GRAND,
            GameType.HEARTS,
            GameType.NULL,
            GameType.SPADES
        )[maxIndex]
    }
}