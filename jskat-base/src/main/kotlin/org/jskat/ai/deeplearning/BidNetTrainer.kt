package org.jskat.ai.deeplearning

import ai.djl.Model
import ai.djl.metric.Metrics
import ai.djl.modality.Classifications
import ai.djl.ndarray.types.Shape
import ai.djl.nn.Activation
import ai.djl.nn.Parameter
import ai.djl.nn.SequentialBlock
import ai.djl.nn.core.Linear
import ai.djl.training.DefaultTrainingConfig
import ai.djl.training.EasyTrain
import ai.djl.training.evaluator.Accuracy
import ai.djl.training.initializer.NormalInitializer
import ai.djl.training.listener.TrainingListener
import ai.djl.training.loss.Loss
import ai.djl.training.optimizer.Optimizer
import ai.djl.training.tracker.Tracker
import ai.djl.training.util.ProgressBar
import java.nio.file.Paths


fun main() {

    val batchSize = 10_000
    val builder = DataFrameDataSet.Builder()
    builder.filePath = "data/kermit_games.csv"
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
    dataSet.prepare(ProgressBar())

    println("Data set size: ${dataSet.size()}")
    println("Feature count: ${dataSet.features.size}")
    println("Feature ${dataSet.features[0].name}: ")
    println("Label count: ${dataSet.labels.size}")

    val featurizer = dataSet.features[0].featurizer
    println("Featurizer declarer: ${featurizer.dataRequired()}")
    val defeature = featurizer.deFeaturize(floatArrayOf(1.0f, 0.0f, 0.0f))
    println("DeFeature: $defeature")

    val featurizer2 = dataSet.labels[0].featurizer
    println("Featurizer gameType: ${featurizer2.dataRequired()}")
    val defeature2 = featurizer2.deFeaturize(floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7f))
    println("DeFeature: $defeature2")
    if (defeature2 is Classifications) {
        println(defeature2.best<Classifications.Classification?>().className)
    }

    val trainTest = dataSet.randomSplit(8, 2)
    val training = trainTest[0]
    val test = trainTest[1]

    training.prepare()
    test.prepare()

    println("Training data size: ${training.size()}")
    println("Test data size: ${test.size()}")

    val inputSize = 35L
    val outputSize = 6L

    val block = SequentialBlock()
    block.add(Linear.builder().setUnits(inputSize).build())
    block.add(Activation::relu)
//    block.add(Linear.builder().setUnits(2048).build())
//    block.add(Activation::relu)
//    block.add(Linear.builder().setUnits(2048).build())
//    block.add(Activation::relu)
//    block.add(Linear.builder().setUnits(1024).build())
//    block.add(Activation::relu)
//    block.add(Linear.builder().setUnits(512).build())
//    block.add(Activation::relu)
    block.add(Linear.builder().setUnits(256).build())
    block.add(Activation::relu)
    block.add(Linear.builder().setUnits(128).build())
    block.add(Activation::relu)
    block.add(Linear.builder().setUnits(outputSize).build())
    block.setInitializer(NormalInitializer(), Parameter.Type.WEIGHT)

    val model = Model.newInstance("bidnet")
    model.block = block

    val config =
        DefaultTrainingConfig(
            Loss.softmaxCrossEntropyLoss(
                "SoftmaxCrossEntropyLoss",
                1.0f,
                -1,
                false,
                true
            )
        ) //softmaxCrossEntropyLoss is a standard loss for classification problems
            .optOptimizer(Optimizer.sgd().setLearningRateTracker(Tracker.fixed(0.1f)).build())
            .addEvaluator(Accuracy()) // Use accuracy so we humans can understand how accurate the model is
            .addTrainingListeners(*TrainingListener.Defaults.logging())

    val trainer = model.newTrainer(config)
    trainer.initialize(Shape(batchSize.toLong(), inputSize))
    trainer.metrics = Metrics()

    val epoch = 10

    EasyTrain.fit(trainer, epoch, training, test)

    val result = trainer.trainingResult

    println(result)

    model.save(Paths.get("build/model"), "bidnet")
}
