package org.jskat.ai.deeplearning

import ai.djl.basicdataset.tabular.TabularDataset
import ai.djl.util.Progress
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.size

class DataFrameDataSet(builder: Builder<*>) : TabularDataset(builder) {

    private val filePath: String = builder.filePath!!
    private var dataFrame: DataFrame<Any?> =
        DataFrame.readCSV(filePath).fillNulls("gameType").with { "NULL_GAME" }

    override fun prepare(progress: Progress?) {
        prepareFeaturizers()
    }

    override fun availableSize(): Long {
        return dataFrame.size().nrow.toLong()
    }

    override fun getCell(rowIndex: Long, featureName: String?): String {
        return dataFrame.get(Math.toIntExact(rowIndex)).get(featureName!!).toString()
    }

    class Builder<T : Builder<T>?> : BaseBuilder<T>() {
        var filePath: String? = null

        /** {@inheritDoc}  */
        override fun self(): T {
            return this as T
        }

        fun setFilePath(path: String): T {
            this.filePath = path
            return self()
        }

        fun build(): DataFrameDataSet {
            return DataFrameDataSet(this)
        }
    }
}