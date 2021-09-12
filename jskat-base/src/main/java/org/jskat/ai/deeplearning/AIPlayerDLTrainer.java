package org.jskat.ai.deeplearning;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class AIPlayerDLTrainer {

    private final static Logger log = LoggerFactory.getLogger(AIPlayerDLTrainer.class);

    public static void main(final String[] args) throws IOException, InterruptedException {

        final RecordReader recordReader = new CSVRecordReader(0, ",");
        recordReader.initialize(new FileSplit(new File("/home/jan/Projects/jskat/iss/kermit_won_games.cvs")));

        //Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
        final int batchSize = 100; // 128 records per batch
        final int numFeatures = 35;
        final int labelIndex = numFeatures; // 39 values in each row of the kermit CSV: 38 input features followed by a label (class) index.
        final int numClasses = 6;  // 6 classes (types of skat game) in the kermit data set. Classes have string values CLUBS, SPADES, HEARTS, DIAMONDS, GRAND, NULL

        final DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);

//        iterator.setPreProcessor(new NormalizerMinMaxScaler());

        final DataSet allData = iterator.next();
        allData.shuffle();
        final SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training

        final DataSet trainingData = testAndTrain.getTrain();
        final DataSet testData = testAndTrain.getTest();

        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
//        final DataNormalization normalizer = new NormalizerStandardize();
        final DataNormalization normalizer = new NormalizerMinMaxScaler();
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set

        log.info("Build model....");

        final int numInputs = numFeatures;
        final int outputNum = numClasses;
        final long seed = 1234567890L;

        final MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(0.1))
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(1024).build())
                .layer(new DenseLayer.Builder().nIn(1024).nOut(1024).build())
                .layer(new DenseLayer.Builder().nIn(1024).nOut(1024).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX) //Override the global TANH activation with softmax for this layer
                        .nIn(1024).nOut(outputNum).build())
                .build();

        //run the model
        final MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        //record score once every 100 iterations
        model.setListeners(new ScoreIterationListener(100));

        do {
            model.fit(trainingData);
        } while (model.score() > 0.05);

        //evaluate the model on the test set
        final Evaluation eval = new Evaluation(numClasses);
        final INDArray output = model.output(testData.getFeatures());
        eval.eval(testData.getLabels(), output);

        log.info(eval.stats());

        model.save(Paths.get("/home/jan/Projects/jskat/iss/kermit_bidding.nn").toFile());
    }
}
