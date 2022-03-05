package org.jskat.ai.deeplearning;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.ui.HtmlAnalysis;
import org.datavec.local.transforms.AnalyzeLocal;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

public class DeepLearning4JTrainer {

    private final static Logger LOG = LoggerFactory.getLogger(DeepLearning4JTrainer.class);

    private final String dataFileName;
    private final int dataSize;
    private final Schema schema;
    private final boolean analyzeData;
    private final String htmlAnalysisFileName;
    private final TransformProcess transformProcess;
    private final Schema finalSchema;
    private final String label;
    private final int classesCount;
    private final MultiLayerConfiguration multiLayerConfig;
    private final boolean startUiServer;

    public DeepLearning4JTrainer(
            final String dataFileName,
            final int dataSize,
            final Schema schema,
            final boolean analyzeData,
            final String htmlAnalysisFileName,
            final TransformProcess transformProcess,
            final Schema finalSchema,
            final String label,
            final int classesCount,
            final MultiLayerConfiguration multiLayerConfig,
            final boolean startUiServer) {

        this.dataFileName = dataFileName;
        this.dataSize = dataSize;
        this.schema = schema;
        this.analyzeData = analyzeData;
        this.htmlAnalysisFileName = htmlAnalysisFileName;
        this.transformProcess = transformProcess;
        this.finalSchema = finalSchema;
        this.label = label;
        this.classesCount = classesCount;
        this.multiLayerConfig = multiLayerConfig;
        this.startUiServer = startUiServer;
    }

    public void train() throws Exception {

        LOG.info(DeepLearning4JSchema.INPUT_SCHEMA.toString());

        if (analyzeData) {
            final CSVRecordReader recordReader = new CSVRecordReader();
            recordReader.initialize(new FileSplit(new File(dataFileName)));
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
        trainRecordReader.initialize(new FileSplit(new File(dataFileName)));

        final RecordReaderDataSetIterator iterator = new RecordReaderDataSetIterator.Builder(trainRecordReader, dataSize)
                .classification(finalSchema.getIndexOfColumn(label), classesCount)
                .collectMetaData(true)
                .build();

        DataSet allData = iterator.next();
        allData.shuffle();
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.8);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        final long startTime = System.currentTimeMillis();
        final double threshold = 0.1;

        ListDataSetIterator trainingIterator = new ListDataSetIterator(trainingData.asList(), 128);
        int epoch = 1;
        do {
            LOG.info("Training epoch " + epoch);
            model.fit(trainingIterator);
            if (epoch % 50 == 0) {
                model.save(new File(System.getProperty("user.home") + "/.jskat/deeplearning/bid_" + epoch + ".nn"));
            }
            evaluateData(model, trainingData);
            evaluateData(model, testData);
            epoch++;
        } while (model.score() >= threshold);

        Duration duration = Duration.ofMillis(System.currentTimeMillis() - startTime);

        LOG.info("Reached score < " + threshold + " after "
                + epoch + " epochs "
                + model.getIterationCount() + " iterations in "
                + duration.toDays() + " days "
                + duration.toHoursPart() + " hours "
                + duration.toMinutesPart() + " minutes "
                + duration.toSecondsPart() + " seconds.");

        model.save(new File(System.getProperty("user.home") + "/.jskat/deeplearning/bid.nn"), false);

        evaluateData(model, trainingData);
        evaluateData(model, testData);
    }

    private void evaluateData(MultiLayerNetwork model, DataSet testData) {
        DataSetIterator dataSetIterator = new TestDataSetIterator(testData, 128);
        final Evaluation evaluation = new Evaluation(classesCount);
        while (dataSetIterator.hasNext()) {
            DataSet test = dataSetIterator.next();
            INDArray predict2 = model.output(test.getFeatures());
            evaluation.eval(test.getLabels(), predict2);
        }

        LOG.info(evaluation.stats());
    }
}
