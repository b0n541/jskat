package org.jskat.ai.deeplearning

import ai.djl.Model
import ai.djl.inference.Predictor
import ai.djl.modality.Classifications
import ai.djl.modality.Classifications.Classification
import ai.djl.nn.Activation
import ai.djl.nn.SequentialBlock
import ai.djl.nn.core.Linear
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameType
import org.jskat.util.Player
import org.slf4j.LoggerFactory
import java.nio.file.Paths

class BiddingModel() {

    private val logger = LoggerFactory.getLogger(BiddingModel::class.java)

    private val block = SequentialBlock()
        .add(Linear.builder().setUnits(33).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(128).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(128).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(64).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(64).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(6).build())

    private val model = Model.newInstance("bidnet")

    private val gameTypeTranslator = GameTypeClassificationTranslator()

    private val predictor: Predictor<FloatArray, Classifications>

    init {
        model.block = block
        model.load(Paths.get(BiddingModel::class.java.classLoader.getResource("data/model").toURI()))
        predictor = model.newPredictor(gameTypeTranslator)
    }

    fun predictGameType(position: Player, hand: CardList): GameType {

        var best = bestGameType(position, hand)

        if (best.probability > 0.5) {
            return GameType.valueOf(best.className)
        }
        return GameType.PASSED_IN
    }

    fun handGamePossible(position: Player, hand: CardList): Boolean {
        return bestGameType(position, hand).probability >= 0.9
    }

    private fun bestGameType(position: Player, hand: CardList): Classification {
        val classes = predictor.predict(toFloatArray(position) + toFloatArray(hand))
        val best = classes.best<Classification>()

        logger.info("Best game type ${best.className} with probability of ${best.probability}")

        return best
    }

    private fun toFloatArray(postion: Player): FloatArray {
        val result = floatArrayOf(0.0f, 0.0f, 0.0f)
        when (postion) {
            Player.FOREHAND -> result[0] = 1.0f
            Player.MIDDLEHAND -> result[1] = 1.0f
            Player.REARHAND -> result[2] = 1.0f
        }
        return result
    }

    private fun toFloatArray(hand: CardList): FloatArray {
        val result = FloatArray(32) { 0.0f }

        for (card in Card.values()) {
            if (hand.contains(card)) {
                result[card.ordinal] = 1.0f
            }
        }

        return result
    }
}