package org.jskat.ai.deeplearning

import ai.djl.Model
import ai.djl.basicdataset.tabular.CsvDataset
import ai.djl.metric.Metrics
import ai.djl.modality.Classifications
import ai.djl.nn.Activation
import ai.djl.nn.Parameter
import ai.djl.nn.SequentialBlock
import ai.djl.nn.core.Linear
import ai.djl.training.DefaultTrainingConfig
import ai.djl.training.EasyTrain
import ai.djl.training.evaluator.Accuracy
import ai.djl.training.initializer.NormalInitializer
import ai.djl.training.listener.TrainingListener
import ai.djl.training.loss.SoftmaxCrossEntropyLoss
import ai.djl.training.optimizer.Optimizer
import ai.djl.training.util.ProgressBar
import org.apache.commons.csv.CSVFormat
import java.io.File
import java.nio.file.Paths


fun main() {

    val batchSize = 10_000
    val builder = CsvDataset.CsvBuilder()
        .optCsvFile(File("data/kermit_games.csv").toPath())
        .setCsvFormat(
            CSVFormat.DEFAULT
                .builder()
                .setHeader(
                    "declarer",
                    "♣A",
                    "♣T",
                    "♣K",
                    "♣Q",
                    "♣J",
                    "♣9",
                    "♣8",
                    "♣7",
                    "♠A",
                    "♠T",
                    "♠K",
                    "♠Q",
                    "♠J",
                    "♠9",
                    "♠8",
                    "♠7",
                    "♥A",
                    "♥T",
                    "♥K",
                    "♥Q",
                    "♥J",
                    "♥9",
                    "♥8",
                    "♥7",
                    "♦A",
                    "♦T",
                    "♦K",
                    "♦Q",
                    "♦J",
                    "♦9",
                    "♦8",
                    "♦7",
                    "maxBidForehand",
                    "maxBidMiddlehand",
                    "maxBidRearhand",
                    "gameType",
                    "hand",
                    "ouvert",
                    "annSchneider",
                    "annSchwarz",
                    "won",
                    "declarerScore",
                    "schneider",
                    "schwarz"
                )
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build()
        )
        .setSampling(batchSize, true)
        .addCategoricalFeature("declarer", true)

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
    println("Feature ${dataSet.features[0].name}:")
    val featurizer = dataSet.features[0].featurizer
    println("Featurizer data required: ${featurizer.dataRequired()}")
    val defeature = featurizer.deFeaturize(floatArrayOf(1.0f, 0.0f, 0.0f))
    println("DeFeature: $defeature")

    println("Label count: ${dataSet.labels.size}")
    println("Label ${dataSet.labels[0].name}:")

    val featurizer2 = dataSet.labels[0].featurizer
    println("Featurizer data required: ${featurizer2.dataRequired()}")
    val defeature2 = featurizer2.deFeaturize(floatArrayOf(1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f))
    println("DeFeature: $defeature2")
    if (defeature2 is Classifications) {
        println(defeature2.best<Classifications.Classification?>().className)
    }

    val trainTest = dataSet.randomSplit(80, 20)
    val training = trainTest[0]
    val test = trainTest[1]

    training.prepare()
    test.prepare()

    println("Training data size: ${training.size()}")
    println("Test data size: ${test.size()}")

    val inputSize = 33
    val outputSize = 6
    //Mlp(inputSize, outputSize, intArrayOf(128, 128, 64, 64))
    val block = SequentialBlock()
        .add(Linear.builder().setUnits(inputSize.toLong()).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(128).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(128).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(64).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(64).build())
        .add(Activation::relu)
        .add(Linear.builder().setUnits(outputSize.toLong()).build())
    block.setInitializer(NormalInitializer(), Parameter.Type.WEIGHT)

    val model = Model.newInstance("bidnet")
    model.block = block

    val config = DefaultTrainingConfig(
        SoftmaxCrossEntropyLoss(
            "SoftmaxCrossEntropyLossOneHotEncodedLabels",
            1.0f,
            -1,
            false,
            true
        )
    )
        .optOptimizer(Optimizer.adam().build())
        .addEvaluator(Accuracy()) // Use accuracy so we humans can understand how accurate the model is
        .addTrainingListeners(*TrainingListener.Defaults.logging())

    val trainer = model.newTrainer(config)
    //trainer.initialize(Shape(batchSize.toLong(), outputSize.toLong()))
    trainer.metrics = Metrics()

    val epoch = 20

    EasyTrain.fit(trainer, epoch, training, test)

//    println("Model input: ${model.describeInput()}")
//    println("Model output: ${model.describeOutput()}")

    val result = trainer.trainingResult

    println(result)

    model.save(Paths.get("data/model"), "bidnet")
}
