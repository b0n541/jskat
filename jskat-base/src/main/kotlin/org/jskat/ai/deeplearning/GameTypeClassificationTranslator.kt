package org.jskat.ai.deeplearning

import ai.djl.modality.Classifications
import ai.djl.ndarray.NDList
import ai.djl.translate.Translator
import ai.djl.translate.TranslatorContext

class GameTypeClassificationTranslator : Translator<FloatArray, Classifications> {
    override fun processInput(
        ctx: TranslatorContext,
        input: FloatArray
    ): NDList? {
        return NDList(ctx.ndManager?.create(input))
    }

    override fun processOutput(
        ctx: TranslatorContext,
        list: NDList
    ): Classifications {
        return Classifications(
            listOf("CLUBS", "DIAMONDS", "GRAND", "HEARTS", "NULL", "SPADES"),
            list.singletonOrThrow().softmax(0)
        )
    }
}