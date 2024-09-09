package org.jskat.ai.deeplearning

import ai.djl.ndarray.NDManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DataFrameDataSetTest {
    @Test
    fun creation() {
        val builder = DataFrameDataSet.Builder()
        builder.filePath = "data/kermit_games_test.csv"
        builder.setSampling(10, true)
        builder.addCategoricalFeature("declarer", true)
        listOf(
            "♣A", "♣T", "♣K", "♣Q", "♣J", "♣9", "♣8", "♣7",
            "♠A", "♠T", "♠K", "♠Q", "♠J", "♠9", "♠8", "♠7",
            "♥A", "♥T", "♥K", "♥Q", "♥J", "♥9", "♥8", "♥7",
            "♦A", "♦T", "♦K", "♦Q", "♦J", "♦9", "♦8", "♦7"
        ).forEach {
            builder.addNumericFeature(it)
        }
        builder.addCategoricalLabel("gameType", true)
        val dataSet = builder.build()
        dataSet.prepare()

        assertThat(dataSet.size()).isEqualTo(10)
        assertThat(dataSet.featureSize).isEqualTo(33)
        assertThat(dataSet.labelSize).isEqualTo(1)

        val ndManager = NDManager.newBaseManager()
        val inputs = dataSet.getRowFeatures(ndManager, 0, dataSet.features)

        assertThat(inputs[0].size()).isEqualTo(35)

        val outputs = dataSet.getRowFeatures(ndManager, 0, dataSet.labels)

        assertThat(outputs[0].size()).isEqualTo(6)

        val splitData = dataSet.randomSplit(80, 20)
        assertThat(splitData.size).isEqualTo(2)

        val training = splitData[0]
        assertThat(training.size()).isEqualTo(8)

        val record = training.get(ndManager, 0)
        assertThat(record.data[0].size()).isEqualTo(35)
        assertThat(record.labels[0].size()).isEqualTo(6)

        val test = splitData[1]
        assertThat(test.size()).isEqualTo(2)
    }
}