package org.jskat.ai.deeplearning;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.ui.HtmlAnalysis;
import org.datavec.local.transforms.AnalyzeLocal;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DeepLearning4JTrainer {

    private final static Logger LOG = LoggerFactory.getLogger(DeepLearning4JTrainer.class);

    private final InputSplit trainingData;
    private final InputSplit testData;
    private final Schema schema;
    private final boolean analyzeData;
    private final String htmlAnalysisFileName;
    private final TransformProcess transformProcess;
    private final Schema finalSchema;
    private final int classesCount;
    private final MultiLayerConfiguration multiLayerConfig;
    private final boolean startUiServer;

    public DeepLearning4JTrainer(
            final InputSplit trainingData,
            final InputSplit testData,
            final Schema schema,
            final boolean analyzeData,
            final String htmlAnalysisFileName,
            final TransformProcess transformProcess,
            final Schema finalSchema,
            final int classesCount,
            final MultiLayerConfiguration multiLayerConfig,
            final boolean startUiServer) {

        this.trainingData = trainingData;
        this.testData = testData;
        this.schema = schema;
        this.analyzeData = analyzeData;
        this.htmlAnalysisFileName = htmlAnalysisFileName;
        this.transformProcess = transformProcess;
        this.finalSchema = finalSchema;
        this.classesCount = classesCount;
        this.multiLayerConfig = multiLayerConfig;
        this.startUiServer = startUiServer;
    }

    public void train() throws Exception {

        LOG.info(DeepLearning4JSchema.INPUT_SCHEMA.toString());

        if (analyzeData) {
            final CSVRecordReader recordReader = new CSVRecordReader();
            recordReader.initialize(trainingData);
            final DataAnalysis analysis = AnalyzeLocal.analyze(schema, recordReader);
            HtmlAnalysis.createHtmlAnalysisFile(analysis, new File(htmlAnalysisFileName));
        }

        LOG.info(finalSchema.toString());

        final MultiLayerNetwork model = new MultiLayerNetwork(multiLayerConfig);
        model.init();
        model.addListeners(new ScoreIterationListener(50));

        if (startUiServer) {
            final UIServer uiServer = UIServer.getInstance();
            final StatsStorage statsStorage = new InMemoryStatsStorage();
            uiServer.attach(statsStorage);
            model.addListeners(new StatsListener(statsStorage, 50));
        }

        final TransformProcessRecordReader trainRecordReader = new TransformProcessRecordReader(new CSVRecordReader(), transformProcess);
        trainRecordReader.initialize(trainingData);

        final int batchSize = 100;

        final RecordReaderDataSetIterator trainIterator = new RecordReaderDataSetIterator.Builder(trainRecordReader, batchSize)
                .classification(finalSchema.getIndexOfColumn("Hand announced"), classesCount)
                .collectMetaData(true)
                .build();

        final long startTime = System.currentTimeMillis();

        final double threshold = 0.01;

        int epoch = 0;
        do {
            epoch++;
            LOG.info("Training epoch " + epoch);
            model.fit(trainIterator);
            model.save(new File("/home/jan/git/jskat-multimodule/jskat-base/src/main/resources/org/jskat/ai/deeplearning/takeskat/takeskat_epoch" + epoch + ".nn"));
        } while (model.score() >= threshold);

        final long stopTime = System.currentTimeMillis();

        LOG.info("Reached score < " + threshold + " after " + epoch + " epochs in " + (stopTime - startTime) + " milliseconds.");

        model.save(new File("/home/jan/git/jskat-multimodule/jskat-base/src/main/resources/org/jskat/ai/deeplearning/takeskat/takeskat.nn"), false);

        final TransformProcessRecordReader testRecordReader = new TransformProcessRecordReader(new CSVRecordReader(), transformProcess);
        testRecordReader.initialize(testData);
        final RecordReaderDataSetIterator testIterator = new RecordReaderDataSetIterator.Builder(testRecordReader, batchSize)
                .classification(finalSchema.getIndexOfColumn("Hand announced"), classesCount)
                .build();

        final Evaluation evaluation = model.evaluate(testIterator);
        LOG.info(evaluation.stats());
    }
}
