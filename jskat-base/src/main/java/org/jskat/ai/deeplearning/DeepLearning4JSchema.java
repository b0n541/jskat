package org.jskat.ai.deeplearning;

import org.datavec.api.transform.schema.Schema;

public class DeepLearning4JSchema {

    public final static Schema INPUT_SCHEMA = new Schema.Builder()
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
}
