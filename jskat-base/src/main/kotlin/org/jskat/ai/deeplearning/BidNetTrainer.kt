package org.jskat.ai.deeplearning

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dl.dataset.OnHeapDataset
import org.jskat.util.Card
import java.nio.file.Path

fun main() {

    val displayConfig = DisplayConfiguration(rowsLimit = 100, useDarkColorScheme = true)

    val dataFrame = DataFrame.readCSV("kermit_games.csv")
        .fillNulls("gameType").with { "NULL" }

    dataFrame.describe().rows().forEach { println(it) }
    println(dataFrame.head())
    println("Raw size: ${dataFrame.size()}")

    val pivoted = dataFrame.pivotMatches("declarer", "gameType", inward = false)
    println(pivoted.head())
    println("Pivoted size: ${pivoted.size()}")

    println(
        pivoted.describe(
            "FOREHAND",
            "MIDDLEHAND",
            "REARHAND",
            "CLUBS",
            "SPADES",
            "HEARTS",
            "NULL",
            "DIAMONDS"
        )
    )

    val dataFrame2 = pivoted.select(
        "maxBidForehand",
        "maxBidMiddlehand",
        "maxBidRearhand",
        "FOREHAND",
        "MIDDLEHAND",
        "REARHAND",
        "CLUBS",
        "SPADES",
        "HEARTS",
        "DIAMONDS",
        "NULL",
        "GRAND",
        Card.CJ.toString(),
        Card.SJ.toString(),
        Card.HJ.toString(),
        Card.DJ.toString(),
        "won",
        "declarerScore",
        "schneider"
    ).convert { cols { false || !it.isColumnGroup() }.recursively() }.toDouble()

    println(dataFrame2.head())
    println("Size: ${dataFrame2.size()}")

    val dataFrame3 = dataFrame2.flatten()

    println(dataFrame3.head())
    println("Size: ${dataFrame3.size()}")

    dataFrame3.corr()
        .format { colsOf<Double>() }.with { linearBg(it, -1.0 to red, 1.0 to green) }
        .toStandaloneHTML(displayConfig).writeHTML(Path.of("kermit_corr_gameType_jacks.html"))

    val (train, test) = dataFrame3
        .convert { colsOf<Double>() }.toFloat()
        .toOnHeapDataset("won")
        .split(0.8)

    println(train.x[0].size.toLong())
}

fun <T> DataFrame<T>.toOnHeapDataset(labelColumnName: String): OnHeapDataset {
    return OnHeapDataset.create(
        dataframe = this,
        yColumn = labelColumnName
    )
}

fun OnHeapDataset.Companion.create(
    dataframe: DataFrame<Any?>,
    yColumn: String
): OnHeapDataset {
    fun extractX(): Array<FloatArray> =
        dataframe.remove(yColumn).rows()
            .map { (it.values() as List<Float>).toFloatArray() }.toTypedArray()

    fun extractY(): FloatArray =
        dataframe.get { yColumn<Float>() }.toList().toFloatArray()

    return create(
        ::extractX,
        ::extractY
    )
}
