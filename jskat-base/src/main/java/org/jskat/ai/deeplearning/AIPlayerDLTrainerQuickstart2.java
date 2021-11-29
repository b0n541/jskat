package org.jskat.ai.deeplearning;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderMultiDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.IEvaluation;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AIPlayerDLTrainerQuickstart2 {
    private final static Logger LOG = LoggerFactory.getLogger(AIPlayerDLTrainerQuickstart2.class);

    public static void main(final String[] args) throws Exception {

        final Random random = new Random();
        random.setSeed(0xC0FFEE);
        final FileSplit inputSplit = new FileSplit(
                Paths.get(ClassLoader.getSystemResource("org/jskat/ai/deeplearning/bidding/train/").toURI()).toFile(),
                random);

        final CSVRecordReader recordReader = new CSVRecordReader();
        recordReader.initialize(inputSplit);

        final Schema schema = new Schema.Builder()
                .addColumnCategorical("Player position", "FOREHAND", "MIDDLEHAND", "REARHAND")
                .addColumnCategorical("Deal CA card", "0", "1")
                .addColumnCategorical("Deal CT card", "0", "1")
                .addColumnCategorical("Deal CK card", "0", "1")
                .addColumnCategorical("Deal CQ card", "0", "1")
                .addColumnCategorical("Deal CJ card", "0", "1")
                .addColumnCategorical("Deal C9 card", "0", "1")
                .addColumnCategorical("Deal C8 card", "0", "1")
                .addColumnCategorical("Deal C7 card", "0", "1")
                .addColumnCategorical("Deal SA card", "0", "1")
                .addColumnCategorical("Deal ST card", "0", "1")
                .addColumnCategorical("Deal SK card", "0", "1")
                .addColumnCategorical("Deal SQ card", "0", "1")
                .addColumnCategorical("Deal SJ card", "0", "1")
                .addColumnCategorical("Deal S9 card", "0", "1")
                .addColumnCategorical("Deal S8 card", "0", "1")
                .addColumnCategorical("Deal S7 card", "0", "1")
                .addColumnCategorical("Deal HA card", "0", "1")
                .addColumnCategorical("Deal HT card", "0", "1")
                .addColumnCategorical("Deal HK card", "0", "1")
                .addColumnCategorical("Deal HQ card", "0", "1")
                .addColumnCategorical("Deal HJ card", "0", "1")
                .addColumnCategorical("Deal H9 card", "0", "1")
                .addColumnCategorical("Deal H8 card", "0", "1")
                .addColumnCategorical("Deal H7 card", "0", "1")
                .addColumnCategorical("Deal DA card", "0", "1")
                .addColumnCategorical("Deal DT card", "0", "1")
                .addColumnCategorical("Deal DK card", "0", "1")
                .addColumnCategorical("Deal DQ card", "0", "1")
                .addColumnCategorical("Deal DJ card", "0", "1")
                .addColumnCategorical("Deal D9 card", "0", "1")
                .addColumnCategorical("Deal D8 card", "0", "1")
                .addColumnCategorical("Deal D7 card", "0", "1")
                .addColumnsInteger("Bid value fore hand", "Bid value middle hand", "Bid value rear hand")
                .addColumnCategorical("Deal skat CA card", "0", "1")
                .addColumnCategorical("Deal skat CT card", "0", "1")
                .addColumnCategorical("Deal skat CK card", "0", "1")
                .addColumnCategorical("Deal skat CQ card", "0", "1")
                .addColumnCategorical("Deal skat CJ card", "0", "1")
                .addColumnCategorical("Deal skat C9 card", "0", "1")
                .addColumnCategorical("Deal skat C8 card", "0", "1")
                .addColumnCategorical("Deal skat C7 card", "0", "1")
                .addColumnCategorical("Deal skat SA card", "0", "1")
                .addColumnCategorical("Deal skat ST card", "0", "1")
                .addColumnCategorical("Deal skat SK card", "0", "1")
                .addColumnCategorical("Deal skat SQ card", "0", "1")
                .addColumnCategorical("Deal skat SJ card", "0", "1")
                .addColumnCategorical("Deal skat S9 card", "0", "1")
                .addColumnCategorical("Deal skat S8 card", "0", "1")
                .addColumnCategorical("Deal skat S7 card", "0", "1")
                .addColumnCategorical("Deal skat HA card", "0", "1")
                .addColumnCategorical("Deal skat HT card", "0", "1")
                .addColumnCategorical("Deal skat HK card", "0", "1")
                .addColumnCategorical("Deal skat HQ card", "0", "1")
                .addColumnCategorical("Deal skat HJ card", "0", "1")
                .addColumnCategorical("Deal skat H9 card", "0", "1")
                .addColumnCategorical("Deal skat H8 card", "0", "1")
                .addColumnCategorical("Deal skat H7 card", "0", "1")
                .addColumnCategorical("Deal skat DA card", "0", "1")
                .addColumnCategorical("Deal skat DT card", "0", "1")
                .addColumnCategorical("Deal skat DK card", "0", "1")
                .addColumnCategorical("Deal skat DQ card", "0", "1")
                .addColumnCategorical("Deal skat DJ card", "0", "1")
                .addColumnCategorical("Deal skat D9 card", "0", "1")
                .addColumnCategorical("Deal skat D8 card", "0", "1")
                .addColumnCategorical("Deal skat D7 card", "0", "1")
                .addColumnCategorical("Discard skat CA card", "0", "1")
                .addColumnCategorical("Discard skat CT card", "0", "1")
                .addColumnCategorical("Discard skat CK card", "0", "1")
                .addColumnCategorical("Discard skat CQ card", "0", "1")
                .addColumnCategorical("Discard skat CJ card", "0", "1")
                .addColumnCategorical("Discard skat C9 card", "0", "1")
                .addColumnCategorical("Discard skat C8 card", "0", "1")
                .addColumnCategorical("Discard skat C7 card", "0", "1")
                .addColumnCategorical("Discard skat SA card", "0", "1")
                .addColumnCategorical("Discard skat ST card", "0", "1")
                .addColumnCategorical("Discard skat SK card", "0", "1")
                .addColumnCategorical("Discard skat SQ card", "0", "1")
                .addColumnCategorical("Discard skat SJ card", "0", "1")
                .addColumnCategorical("Discard skat S9 card", "0", "1")
                .addColumnCategorical("Discard skat S8 card", "0", "1")
                .addColumnCategorical("Discard skat S7 card", "0", "1")
                .addColumnCategorical("Discard skat HA card", "0", "1")
                .addColumnCategorical("Discard skat HT card", "0", "1")
                .addColumnCategorical("Discard skat HK card", "0", "1")
                .addColumnCategorical("Discard skat HQ card", "0", "1")
                .addColumnCategorical("Discard skat HJ card", "0", "1")
                .addColumnCategorical("Discard skat H9 card", "0", "1")
                .addColumnCategorical("Discard skat H8 card", "0", "1")
                .addColumnCategorical("Discard skat H7 card", "0", "1")
                .addColumnCategorical("Discard skat DA card", "0", "1")
                .addColumnCategorical("Discard skat DT card", "0", "1")
                .addColumnCategorical("Discard skat DK card", "0", "1")
                .addColumnCategorical("Discard skat DQ card", "0", "1")
                .addColumnCategorical("Discard skat DJ card", "0", "1")
                .addColumnCategorical("Discard skat D9 card", "0", "1")
                .addColumnCategorical("Discard skat D8 card", "0", "1")
                .addColumnCategorical("Discard skat D7 card", "0", "1")
                .addColumnInteger("Multiplier")
                .addColumnCategorical("Game type", "CLUBS", "SPADES", "HEARTS", "DIAMONDS", "GRAND", "NULL")
                .addColumnCategorical("Hand announced", "0", "1")
                .addColumnCategorical("Ouvert announced", "0", "1")
                .addColumnCategorical("Schneider announced", "0", "1")
                .addColumnCategorical("Schwarz announced", "0", "1")
                .addColumnsInteger("Declarer score")
                .addColumnCategorical("Result Schneider", "0", "1")
                .addColumnCategorical("Result Schwarz", "0", "1")
                .build();

        LOG.info(schema.toString());

//        final DataAnalysis analysis = AnalyzeLocal.analyze(schema, recordReader);
//        HtmlAnalysis.createHtmlAnalysisFile(analysis, new File("/home/jan/Projects/jskat/iss/kermit_won_games_analysis.html"));

        final TransformProcess transformProcess = new TransformProcess.Builder(schema)
                .removeColumns("Bid value fore hand", "Bid value middle hand", "Bid value rear hand")
                .removeColumns("Deal skat CA card", "Deal skat CT card", "Deal skat CK card", "Deal skat CQ card", "Deal skat CJ card", "Deal skat C9 card", "Deal skat C8 card", "Deal skat C7 card")
                .removeColumns("Deal skat SA card", "Deal skat ST card", "Deal skat SK card", "Deal skat SQ card", "Deal skat SJ card", "Deal skat S9 card", "Deal skat S8 card", "Deal skat S7 card")
                .removeColumns("Deal skat HA card", "Deal skat HT card", "Deal skat HK card", "Deal skat HQ card", "Deal skat HJ card", "Deal skat H9 card", "Deal skat H8 card", "Deal skat H7 card")
                .removeColumns("Deal skat DA card", "Deal skat DT card", "Deal skat DK card", "Deal skat DQ card", "Deal skat DJ card", "Deal skat D9 card", "Deal skat D8 card", "Deal skat D7 card")
                .removeColumns("Discard skat CA card", "Discard skat CT card", "Discard skat CK card", "Discard skat CQ card", "Discard skat CJ card", "Discard skat C9 card", "Discard skat C8 card", "Discard skat C7 card")
                .removeColumns("Discard skat SA card", "Discard skat ST card", "Discard skat SK card", "Discard skat SQ card", "Discard skat SJ card", "Discard skat S9 card", "Discard skat S8 card", "Discard skat S7 card")
                .removeColumns("Discard skat HA card", "Discard skat HT card", "Discard skat HK card", "Discard skat HQ card", "Discard skat HJ card", "Discard skat H9 card", "Discard skat H8 card", "Discard skat H7 card")
                .removeColumns("Discard skat DA card", "Discard skat DT card", "Discard skat DK card", "Discard skat DQ card", "Discard skat DJ card", "Discard skat D9 card", "Discard skat D8 card", "Discard skat D7 card")
                .removeColumns("Declarer score", "Result Schneider", "Result Schwarz")
                .categoricalToOneHot("Player position")
                .categoricalToInteger("Deal CA card", "Deal CT card", "Deal CK card", "Deal CQ card", "Deal CJ card", "Deal C9 card", "Deal C8 card", "Deal C7 card")
                .categoricalToInteger("Deal SA card", "Deal ST card", "Deal SK card", "Deal SQ card", "Deal SJ card", "Deal S9 card", "Deal S8 card", "Deal S7 card")
                .categoricalToInteger("Deal HA card", "Deal HT card", "Deal HK card", "Deal HQ card", "Deal HJ card", "Deal H9 card", "Deal H8 card", "Deal H7 card")
                .categoricalToInteger("Deal DA card", "Deal DT card", "Deal DK card", "Deal DQ card", "Deal DJ card", "Deal D9 card", "Deal D8 card", "Deal D7 card")
                .categoricalToInteger("Game type")
                .categoricalToInteger("Hand announced", "Ouvert announced", "Schneider announced", "Schwarz announced")
                .build();

        LOG.info(transformProcess.toString());

        final Schema finalSchema = transformProcess.getFinalSchema();

        LOG.info(finalSchema.toString());

        final int multiplierClasses = 11;
        final int gameTypeClasses = 6;

        final ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(0xC0FFEE)
                .weightInit(WeightInit.XAVIER)
                .activation(Activation.RELU)
                .updater(new Adam.Builder().learningRate(0.0001).build())
                .graphBuilder()
                .addInputs("input")
                .addLayer("L1", new DenseLayer.Builder().nIn(finalSchema.getIndexOfColumn("Multiplier")).nOut(1024).build(), "input")
                .addLayer("L2", new DenseLayer.Builder().nIn(1024).nOut(1024).build(), "L1")
                .addLayer("L3", new DenseLayer.Builder().nIn(1024).nOut(1024).dropOut(0.6).build(), "L2")
                .addLayer("L4", new DenseLayer.Builder().nIn(1024).nOut(1024).dropOut(0.6).build(), "L3")
                .addLayer("L5", new DenseLayer.Builder().nIn(1024).nOut(512).dropOut(0.6).build(), "L4")
                .addLayer("L6_multiplier", new DenseLayer.Builder().nIn(512).nOut(512).dropOut(0.6).build(), "L5")
                .addLayer("L7_multiplier", new DenseLayer.Builder().nIn(512).nOut(512).dropOut(0.6).build(), "L6_multiplier")
                .addLayer("L8_multiplier", new DenseLayer.Builder().nIn(512).nOut(512).dropOut(0.6).build(), "L7_multiplier")
                .addLayer("L9_multiplier", new DenseLayer.Builder().nIn(512).nOut(256).build(), "L8_multiplier")
                .addLayer("L10_multiplier", new DenseLayer.Builder().nIn(256).nOut(256).build(), "L9_multiplier")
                .addLayer("L11_multiplier", new DenseLayer.Builder().nIn(256).nOut(256).build(), "L10_multiplier")
                .addLayer("L12_multiplier", new DenseLayer.Builder().nIn(256).nOut(256).build(), "L11_multiplier")
                .addLayer("multiplier", new OutputLayer.Builder().nIn(256).nOut(multiplierClasses).activation(Activation.SOFTMAX).build(), "L12_multiplier")
                .addLayer("L6_gametype", new DenseLayer.Builder().nIn(512).nOut(512).dropOut(0.6).build(), "L5")
                .addLayer("L7_gametype", new DenseLayer.Builder().nIn(512).nOut(512).dropOut(0.6).build(), "L6_gametype")
                .addLayer("L8_gametype", new DenseLayer.Builder().nIn(512).nOut(512).dropOut(0.6).build(), "L7_gametype")
                .addLayer("L9_gametype", new DenseLayer.Builder().nIn(512).nOut(256).build(), "L8_gametype")
                .addLayer("L10_gametype", new DenseLayer.Builder().nIn(256).nOut(256).build(), "L9_gametype")
                .addLayer("L11_gametype", new DenseLayer.Builder().nIn(256).nOut(256).build(), "L10_gametype")
                .addLayer("L12_gametype", new DenseLayer.Builder().nIn(256).nOut(256).build(), "L11_gametype")
                .addLayer("gametype", new OutputLayer.Builder().nIn(256).nOut(gameTypeClasses).activation(Activation.SOFTMAX).build(), "L12_gametype")
                .setOutputs("multiplier", "gametype")
                .build();

//        final ComputationGraph model = new ComputationGraph(config);
//        model.init();
        final ComputationGraph model = ComputationGraph.load(Paths.get(ClassLoader.getSystemResource("org/jskat/ai/deeplearning/bidding/bidding_epoch254.nn").toURI()).toFile(), true);
        model.setLearningRate(0.00001);
        model.addListeners(new ScoreIterationListener(50));

        final UIServer uiServer = UIServer.getInstance();
        final StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        model.addListeners(new StatsListener(statsStorage, 50));

        final TransformProcessRecordReader trainRecordReader = new TransformProcessRecordReader(new CSVRecordReader(), transformProcess);
        trainRecordReader.initialize(inputSplit);

        final int batchSize = 100;

        final RecordReaderMultiDataSetIterator trainIterator = new RecordReaderMultiDataSetIterator.Builder(batchSize)
                .addReader("train", trainRecordReader)
                .addInput("train", 0, finalSchema.getIndexOfColumn("Deal D7 card"))
                .addOutputOneHot("train", finalSchema.getIndexOfColumn("Multiplier"), multiplierClasses)
                .addOutputOneHot("train", finalSchema.getIndexOfColumn("Game type"), gameTypeClasses)
                .build();

        final long startTime = System.currentTimeMillis();

        int epoch = 254;
        do {
            epoch++;
            LOG.info("Training epoch " + epoch);
            model.fit(trainIterator);
            model.save(new File("/home/jan/git/jskat-multimodule/jskat-base/src/main/resources/org/jskat/ai/deeplearning/bidding/bidding_epoch" + epoch + ".nn"));
        } while (model.score() > 0.05);

        final long stopTime = System.currentTimeMillis();

        LOG.info("Reached score < 0.05 after " + epoch + " epochs in " + (stopTime - startTime) + " milliseconds.");

        model.save(new File("/home/jan/git/jskat-multimodule/jskat-base/src/main/resources/org/jskat/ai/deeplearning/bidding/bidding.nn"), false);

        final TransformProcessRecordReader testRecordReader = new TransformProcessRecordReader(new CSVRecordReader(), transformProcess);
        testRecordReader.initialize(new FileSplit(Paths.get(ClassLoader.getSystemResource("org/jskat/ai/deeplearning/bidding/test/").toURI()).toFile()));
        final RecordReaderMultiDataSetIterator testIterator = new RecordReaderMultiDataSetIterator.Builder(10000)
                .addReader("test", testRecordReader)
                .addInput("test", 0, finalSchema.getIndexOfColumn("Deal D7 card"))
                .addOutputOneHot("test", finalSchema.getIndexOfColumn("Multiplier"), 11)
                .addOutputOneHot("test", finalSchema.getIndexOfColumn("Game type"), 6)
                .build();

        final Map<Integer, IEvaluation[]> m = new HashMap<>();
        m.put(0, new IEvaluation[]{new Evaluation()});
        m.put(1, new IEvaluation[]{new Evaluation()});

        model.evaluate(testIterator, m);
        m.values().forEach(it -> System.out.println(it[0].stats()));
    }
}
