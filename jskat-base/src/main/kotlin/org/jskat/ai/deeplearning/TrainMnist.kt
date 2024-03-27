package org.jskat.ai.deeplearning

import ai.djl.Model
import ai.djl.basicdataset.cv.classification.Mnist
import ai.djl.basicmodelzoo.basic.Mlp
import ai.djl.metric.Metrics
import ai.djl.ndarray.types.Shape
import ai.djl.nn.Block
import ai.djl.training.DefaultTrainingConfig
import ai.djl.training.EasyTrain
import ai.djl.training.TrainingResult
import ai.djl.training.dataset.Dataset
import ai.djl.training.dataset.RandomAccessDataset
import ai.djl.training.evaluator.Accuracy
import ai.djl.training.listener.TrainingListener
import ai.djl.training.loss.Loss
import ai.djl.training.util.ProgressBar
import java.io.IOException


/**
 * An example of training an image classification (MNIST) model.
 *
 * See this [doc](https://github.com/deepjavalibrary/djl/blob/master/examples/docs/train_mnist_mlp.md)
 * for information about this example.
 */
fun main(args: Array<String>) {
    runExample(args)
}

//    @Throws(IOException::class, TranslateException::class)
fun runExample(args: Array<String>?): TrainingResult {
    // Construct neural network
    val block: Block = Mlp(
        Mnist.IMAGE_HEIGHT * Mnist.IMAGE_WIDTH,
        Mnist.NUM_CLASSES,
        intArrayOf(128, 64)
    )

    Model.newInstance("mlp").use { model ->
        model.block = block
        // get training and validation dataset
        val trainingSet = getDataset(Dataset.Usage.TRAIN)
        val validateSet = getDataset(Dataset.Usage.TEST)

        // setup training configuration
        val config = setupTrainingConfig()

        model.newTrainer(config).use { trainer ->
            trainer.metrics = Metrics()
            /*
            * MNIST is 28x28 grayscale image and pre processed into 28 * 28 NDArray.
            * 1st axis is batch axis, we can use 1 for initialization.
            */
            val inputShape = Shape(1, (Mnist.IMAGE_HEIGHT * Mnist.IMAGE_WIDTH).toLong())

            // initialize trainer with proper input shape
            trainer.initialize(inputShape)

            EasyTrain.fit(trainer, 2, trainingSet, validateSet)
            return trainer.trainingResult
        }
    }
}

private fun setupTrainingConfig(): DefaultTrainingConfig {
    return DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
        .addEvaluator(Accuracy())
        .addTrainingListeners(*TrainingListener.Defaults.logging())
}

@Throws(IOException::class)
private fun getDataset(usage: Dataset.Usage): RandomAccessDataset {
    val mnist = Mnist.builder()
        .optUsage(usage)
        .setSampling(32, true)
        .build()
    mnist.prepare(ProgressBar())
    return mnist
}
