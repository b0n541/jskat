package org.jskat.ai.deeplearning

import ai.djl.Model
import ai.djl.nn.Activation
import ai.djl.nn.SequentialBlock
import ai.djl.nn.core.Linear
import java.nio.file.Paths

fun main() {

    val block = SequentialBlock()
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

    var model = Model.newInstance("bidnet")
    model.block = block
    model.load(Paths.get("data/model"))

    var gameTypeTranslator = GameTypeTranslator()
    var predictor = model.newPredictor(gameTypeTranslator)

    var clubs = predictor.predict(
        floatArrayOf(
            1.0f, 0.0f, 0.0f, // declarer
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // clubs
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // spades
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // hearts
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f // diamonds
        )
    )
    println("Predictor for CLUBS: $clubs")

    var spades = predictor.predict(
        floatArrayOf(
            1.0f, 0.0f, 0.0f, // declarer
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // clubs
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // spades
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // hearts
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f // diamonds
        )
    )
    println("Predictor for SPADES: $spades")

    var hearts = predictor.predict(
        floatArrayOf(
            1.0f, 0.0f, 0.0f, // declarer
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // clubs
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // spades
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // hearts
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f // diamonds
        )
    )
    println("Predictor for HEARTS: $hearts")

    var diamonds = predictor.predict(
        floatArrayOf(
            1.0f, 0.0f, 0.0f, // declarer
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // clubs
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // spades
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // hearts
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f // diamonds
        )
    )
    println("Predictor for DIAMONDS: $diamonds")

    var nullGame = predictor.predict(
        floatArrayOf(
            1.0f, 0.0f, 0.0f, // declarer
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, // clubs
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, // spades
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, // hearts
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f // diamonds
        )
    )
    println("Predictor for NULL: $nullGame")

    var grand = predictor.predict(
        floatArrayOf(
            1.0f, 0.0f, 0.0f, // declarer
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, // clubs
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, // spades
            1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // hearts
            1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f // diamonds
        )
    )
    println("Predictor for GRAND: $grand")

    var perfectHand = predictor.predict(
        floatArrayOf(
            1.0f, 0.0f, 0.0f, // declarer
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // clubs
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // spades
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, // hearts
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f // diamonds
        )
    )
    println("Predictor for perfect hand: $perfectHand")
}
