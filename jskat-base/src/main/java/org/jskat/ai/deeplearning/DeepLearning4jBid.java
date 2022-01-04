package org.jskat.ai.deeplearning;

import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class DeepLearning4jBid {

    public static void main(final String[] args) throws Exception {

        final TransformProcess transformProcess = new TransformProcess.Builder(DeepLearning4JSchema.INPUT_SCHEMA)
                .removeColumns("Bid value fore hand", "Bid value middle hand", "Bid value rear hand")
                .removeColumns("Deal skat CA card", "Deal skat CT card", "Deal skat CK card", "Deal skat CQ card", "Deal skat CJ card", "Deal skat C9 card", "Deal skat C8 card", "Deal skat C7 card")
                .removeColumns("Deal skat SA card", "Deal skat ST card", "Deal skat SK card", "Deal skat SQ card", "Deal skat SJ card", "Deal skat S9 card", "Deal skat S8 card", "Deal skat S7 card")
                .removeColumns("Deal skat HA card", "Deal skat HT card", "Deal skat HK card", "Deal skat HQ card", "Deal skat HJ card", "Deal skat H9 card", "Deal skat H8 card", "Deal skat H7 card")
                .removeColumns("Deal skat DA card", "Deal skat DT card", "Deal skat DK card", "Deal skat DQ card", "Deal skat DJ card", "Deal skat D9 card", "Deal skat D8 card", "Deal skat D7 card")
                .removeColumns("Discard skat CA card", "Discard skat CT card", "Discard skat CK card", "Discard skat CQ card", "Discard skat CJ card", "Discard skat C9 card", "Discard skat C8 card", "Discard skat C7 card")
                .removeColumns("Discard skat SA card", "Discard skat ST card", "Discard skat SK card", "Discard skat SQ card", "Discard skat SJ card", "Discard skat S9 card", "Discard skat S8 card", "Discard skat S7 card")
                .removeColumns("Discard skat HA card", "Discard skat HT card", "Discard skat HK card", "Discard skat HQ card", "Discard skat HJ card", "Discard skat H9 card", "Discard skat H8 card", "Discard skat H7 card")
                .removeColumns("Discard skat DA card", "Discard skat DT card", "Discard skat DK card", "Discard skat DQ card", "Discard skat DJ card", "Discard skat D9 card", "Discard skat D8 card", "Discard skat D7 card")
                .removeColumns("Hand announced", "Multiplier", "Ouvert announced", "Schneider announced", "Schwarz announced")
                .removeColumns("Declarer score", "Result Schneider", "Result Schwarz")
                .categoricalToOneHot("Player position")
                .categoricalToInteger("Deal CA card", "Deal CT card", "Deal CK card", "Deal CQ card", "Deal CJ card", "Deal C9 card", "Deal C8 card", "Deal C7 card")
                .categoricalToInteger("Deal SA card", "Deal ST card", "Deal SK card", "Deal SQ card", "Deal SJ card", "Deal S9 card", "Deal S8 card", "Deal S7 card")
                .categoricalToInteger("Deal HA card", "Deal HT card", "Deal HK card", "Deal HQ card", "Deal HJ card", "Deal H9 card", "Deal H8 card", "Deal H7 card")
                .categoricalToInteger("Deal DA card", "Deal DT card", "Deal DK card", "Deal DQ card", "Deal DJ card", "Deal D9 card", "Deal D8 card", "Deal D7 card")
                .categoricalToInteger("Game type")
                .build();

        final Schema finalSchema = transformProcess.getFinalSchema();

        final int classesCount = 6;

        final MultiLayerConfiguration multiLayerConfig = new NeuralNetConfiguration.Builder()
                .seed(0xC0FFEE)
                .weightInit(WeightInit.RELU)
                .activation(Activation.RELU)
                .updater(new Adam.Builder().learningRate(0.001).build())
//                .l1(0.0001)
                .l2(0.0001)
                .list(
                        new DenseLayer.Builder().nIn(finalSchema.numColumns() - 1).nOut(1024).build(),
                        new DenseLayer.Builder().nOut(1024).dropOut(0.6).build(),
                        new DenseLayer.Builder().nOut(512).dropOut(0.6).build(),
                        new DenseLayer.Builder().nOut(512).dropOut(0.6).build(),
                        new DenseLayer.Builder().nOut(256).build(),
                        new OutputLayer.Builder().nOut(classesCount)
                                .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                                .activation(Activation.SOFTMAX).build()
                )
                .build();

        final DeepLearning4JTrainer trainer = new DeepLearning4JTrainer(
                System.getProperty("user.home") + "/.jskat/deeplearning/kermit_won_games_100000.csv",
                100000,
                DeepLearning4JSchema.INPUT_SCHEMA,
                true,
                System.getProperty("user.home") + "/.jskat/deeplearning/kermit_won_games_data_analysis.html",
                transformProcess,
                finalSchema,
                "Game type",
                classesCount,
                multiLayerConfig,
                true);

        trainer.train();
    }
}
