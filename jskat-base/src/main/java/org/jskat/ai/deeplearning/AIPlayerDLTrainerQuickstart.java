package org.jskat.ai.deeplearning;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.impl.LossNegativeLogLikelihood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AIPlayerDLTrainerQuickstart {
    private final static Logger LOG = LoggerFactory.getLogger(AIPlayerDLTrainerQuickstart.class);

    public static void main(final String[] args) throws Exception {

        // TODO Fix NullPointerException
//        final Random random = new Random();
//        random.setSeed(0xC0FFEE);
//        final FileSplit inputSplit = new FileSplit(new File("/home/jan/Projects/jskat/iss/kermit_won_games.cvs"), random);
        final FileSplit inputSplit = new FileSplit(new File("/home/jan/Projects/jskat/iss/kermit_won_games.cvs"));

        final CSVRecordReader recordReader = new CSVRecordReader();
        recordReader.initialize(inputSplit);

        final Schema schema = new Schema.Builder()
                .addColumnCategorical("Player position", "FOREHAND", "MIDDLEHAND", "REARHAND")
                .addColumnCategorical("Has CA card", "0", "1")
                .addColumnCategorical("Has CT card", "0", "1")
                .addColumnCategorical("Has CK card", "0", "1")
                .addColumnCategorical("Has CQ card", "0", "1")
                .addColumnCategorical("Has CJ card", "0", "1")
                .addColumnCategorical("Has C9 card", "0", "1")
                .addColumnCategorical("Has C8 card", "0", "1")
                .addColumnCategorical("Has C7 card", "0", "1")
                .addColumnCategorical("Has SA card", "0", "1")
                .addColumnCategorical("Has ST card", "0", "1")
                .addColumnCategorical("Has SK card", "0", "1")
                .addColumnCategorical("Has SQ card", "0", "1")
                .addColumnCategorical("Has SJ card", "0", "1")
                .addColumnCategorical("Has S9 card", "0", "1")
                .addColumnCategorical("Has S8 card", "0", "1")
                .addColumnCategorical("Has S7 card", "0", "1")
                .addColumnCategorical("Has HA card", "0", "1")
                .addColumnCategorical("Has HT card", "0", "1")
                .addColumnCategorical("Has HK card", "0", "1")
                .addColumnCategorical("Has HQ card", "0", "1")
                .addColumnCategorical("Has HJ card", "0", "1")
                .addColumnCategorical("Has H9 card", "0", "1")
                .addColumnCategorical("Has H8 card", "0", "1")
                .addColumnCategorical("Has H7 card", "0", "1")
                .addColumnCategorical("Has DA card", "0", "1")
                .addColumnCategorical("Has DT card", "0", "1")
                .addColumnCategorical("Has DK card", "0", "1")
                .addColumnCategorical("Has DQ card", "0", "1")
                .addColumnCategorical("Has DJ card", "0", "1")
                .addColumnCategorical("Has D9 card", "0", "1")
                .addColumnCategorical("Has D8 card", "0", "1")
                .addColumnCategorical("Has D7 card", "0", "1")
                .addColumnsInteger("Bid value fore hand", "Bid value middle hand", "Bid value rear hand")
                .addColumnCategorical("Game type", "GRAND", "CLUBS", "SPADES", "HEARTS", "DIAMONDS", "NULL")
                .addColumnCategorical("Hand announced", "0", "1")
                .addColumnCategorical("Ouvert announced", "0", "1")
                .addColumnCategorical("Schneider announced", "0", "1")
                .addColumnCategorical("Schwarz announced", "0", "1")
                .addColumnsInteger("Declarer score")
                .addColumnCategorical("Result Schneider", "0", "1")
                .addColumnCategorical("Result Schwarz", "0", "1")
                .build();

        LOG.info(schema.toString());

        // TODO why do Null games have declarer score > 0?
//        final DataAnalysis analysis = AnalyzeLocal.analyze(schema, recordReader);
//        HtmlAnalysis.createHtmlAnalysisFile(analysis, new File("/home/jan/Projects/jskat/iss/kermin_won_games_analysis.html"));

        final TransformProcess transformProcess = new TransformProcess.Builder(schema)
                .removeColumns("Bid value fore hand", "Bid value middle hand", "Bid value rear hand")
                .removeColumns("Hand announced", "Ouvert announced", "Schneider announced", "Schwarz announced")
                .removeColumns("Declarer score", "Result Schneider", "Result Schwarz")
                .categoricalToOneHot("Player position")
                .categoricalToInteger("Has CA card", "Has CT card", "Has CK card", "Has CQ card", "Has CJ card", "Has C9 card", "Has C8 card", "Has C7 card")
                .categoricalToInteger("Has SA card", "Has ST card", "Has SK card", "Has SQ card", "Has SJ card", "Has S9 card", "Has S8 card", "Has S7 card")
                .categoricalToInteger("Has HA card", "Has HT card", "Has HK card", "Has HQ card", "Has HJ card", "Has H9 card", "Has H8 card", "Has H7 card")
                .categoricalToInteger("Has DA card", "Has DT card", "Has DK card", "Has DQ card", "Has DJ card", "Has D9 card", "Has D8 card", "Has D7 card")
                .categoricalToInteger("Game type")
                .build();

        LOG.info(transformProcess.toString());

        final Schema finalSchema = transformProcess.getFinalSchema();

        LOG.info(finalSchema.toString());

        final int batchSize = 100;
        final int totalExampleCount = 100;
        final int epochs = 100;
        final double l2 = Math.sqrt((batchSize * 1.0) / (totalExampleCount * epochs));

        LOG.info("Recommendation for L2: {}", l2);

        final TransformProcessRecordReader trainRecordReader = new TransformProcessRecordReader(new CSVRecordReader(), transformProcess);
        trainRecordReader.initialize(inputSplit);

        final RecordReaderDataSetIterator trainIterator = new RecordReaderDataSetIterator.Builder(trainRecordReader, batchSize)
                .classification(finalSchema.getIndexOfColumn("Game type"), 6)
                .build();

        final MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(0xC0FFEE)
                .weightInit(WeightInit.XAVIER)
                .activation(Activation.RELU)
                .updater(new Adam.Builder().learningRate(0.01).build())
                .l2(l2 * 0.1 * 0.1)
                .list(
                        new DenseLayer.Builder().nOut(1024).build(),
                        new DenseLayer.Builder().nOut(1024).build(),
                        new DenseLayer.Builder().nOut(1024).build(),
                        new OutputLayer.Builder(new LossNegativeLogLikelihood()).nOut(6).activation(Activation.SOFTMAX).build()
                )
                .setInputType(InputType.feedForward(finalSchema.numColumns() - 1))
                .build();

        final MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        model.addListeners(new ScoreIterationListener(50));

        final UIServer uiServer = UIServer.getInstance();
        final StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        model.addListeners(new StatsListener(statsStorage, 50));

        model.fit(trainIterator, epochs);

        final TransformProcessRecordReader testRecordReader = new TransformProcessRecordReader(new CSVRecordReader(), transformProcess);
        testRecordReader.initialize(new FileSplit(new File("/home/jan/Projects/jskat/iss/kermit_won_games.cvs")));
        final RecordReaderDataSetIterator testIterator = new RecordReaderDataSetIterator.Builder(testRecordReader, batchSize)
                .classification(finalSchema.getIndexOfColumn("Game type"), 6)
                .build();

        final Evaluation evaluate = model.evaluate(testIterator);
        LOG.info(evaluate.stats());
    }
}
