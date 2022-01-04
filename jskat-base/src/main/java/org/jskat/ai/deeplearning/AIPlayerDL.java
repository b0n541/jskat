/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.deeplearning;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jetbrains.annotations.Nullable;
import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

/**
 * Random player for testing purposes and driving the other players nuts.
 */
public class AIPlayerDL extends AbstractAIPlayer {

    private static final Logger LOG = LoggerFactory.getLogger(AIPlayerDL.class);

    private MultiLayerNetwork biddingNetwork;

    /**
     * Random generator for decision making.
     */
    private final Random random = new Random();

    /**
     * Creates a new instance of AIPlayerDL.
     */
    public AIPlayerDL() {

        this("unknown"); //$NON-NLS-1$

        try {
            final Path path = Paths.get(ClassLoader.getSystemResource("org/jskat/ai/deeplearning/bidding/bidding.nn").toURI());
            biddingNetwork = MultiLayerNetwork.load(path.toFile(), false);
        } catch (final IOException | URISyntaxException exception) {
            LOG.error("File not found.");
        }
    }

    /**
     * Creates a new instance of AIPlayerDL.
     *
     * @param newPlayerName Player's name
     */
    public AIPlayerDL(final String newPlayerName) {

        LOG.debug("Constructing new AIPlayerDL"); //$NON-NLS-1$
        setPlayerName(newPlayerName);
    }

    @Override
    public Boolean pickUpSkat() {
        return random.nextBoolean();
    }

    @Override
    public Boolean playGrandHand() {
        return random.nextBoolean();
    }

    @Override
    public GameAnnouncement announceGame() {
        LOG.debug("position: " + knowledge.getPlayerPosition()); //$NON-NLS-1$
        LOG.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) + //$NON-NLS-1$
                " " + knowledge.getHighestBid(Player.MIDDLEHAND) + //$NON-NLS-1$
                " " + knowledge.getHighestBid(Player.REARHAND)); //$NON-NLS-1$

        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

        factory.setGameType(getBestGameType());

        return factory.getAnnouncement();
    }

    @Override
    public Integer bidMore(final int nextBidValue) {

        return possibleBidValue() >= nextBidValue ? nextBidValue : -1;
    }

    @Nullable
    private Integer possibleBidValue() {
        final GameType bestGameType = getBestGameType();
        return getHighestBidValue(bestGameType, knowledge.getOwnCards());
    }

    @Nullable
    private GameType getBestGameType() {
        GameType bestGameType = null;
        final int prediction = biddingNetwork.predict(getNetworkInputBidding())[0];
        switch (prediction) {
            case 0:
                bestGameType = GameType.GRAND;
                break;
            case 1:
                bestGameType = GameType.CLUBS;
                break;
            case 2:
                bestGameType = GameType.SPADES;
                break;
            case 3:
                bestGameType = GameType.HEARTS;
                break;
            case 4:
                bestGameType = GameType.DIAMONDS;
                break;
            case 5:
                bestGameType = GameType.NULL;
                break;
        }
        return bestGameType;
    }

    private INDArray getNetworkInputBidding() {
        final INDArray input = Nd4j.zeros(1, 35);

        if (knowledge.getPlayerPosition() == Player.FOREHAND) {
            input.putScalar(0, 1);
        } else if (knowledge.getPlayerPosition() == Player.MIDDLEHAND) {
            input.putScalar(1, 1);
        } else if (knowledge.getPlayerPosition() == Player.REARHAND) {
            input.putScalar(2, 1);
        }

        final CardList ownCards = knowledge.getOwnCards();
        Arrays.stream(Card.values()).forEach(it -> {
            if (ownCards.contains(it)) {
                input.putScalar(it.ordinal() + 3, 1);
            }
        });

        return input;
    }

    private static int getHighestBidValue(final GameType bestGameType, final CardList ownCards) {
        if (bestGameType == GameType.NULL) {
            return 23;
        }

        int jacks = 0;
        if (ownCards.contains(Card.CJ)) {
            jacks++;
            if (ownCards.contains(Card.SJ)) {
                jacks++;
                if (ownCards.contains(Card.HJ)) {
                    jacks++;
                    if (ownCards.contains(Card.DJ)) {
                        jacks++;
                    }
                }
            }
        }
//        if (jacks == 0) {
//            if (!ownCards.contains(Card.CJ)) {
//                jacks++;
//                if (!ownCards.contains(Card.SJ)) {
//                    jacks++;
//                    if (!ownCards.contains(Card.HJ)) {
//                        jacks++;
//                        if (!ownCards.contains(Card.DJ)) {
//                            jacks++;
//                        }
//                    }
//                }
//            }
//        }

        return jacks * getGameTypeBaseValue(bestGameType);
    }

    private static int getGameTypeBaseValue(final GameType bestGameType) {
        switch (bestGameType) {
            case GRAND:
                return 24;
            case CLUBS:
                return 12;
            case SPADES:
                return 11;
            case HEARTS:
                return 10;
            case DIAMONDS:
                return 9;
        }
        return 0;
    }

    @Override
    public Boolean holdBid(final int currBidValue) {
        return possibleBidValue() >= currBidValue ? true : false;
    }

    @Override
    public void startGame() {
        // do nothing
    }

    @Override
    public Card playCard() {

        int index = -1;

        LOG.debug('\n' + knowledge.toString());

        // first find all possible cards
        final CardList possibleCards = getPlayableCards(knowledge
                .getTrickCards());

        LOG.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

        // then choose a random one
        index = random.nextInt(possibleCards.size());

        LOG.debug("choosing card " + index); //$NON-NLS-1$
        LOG.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(index)); //$NON-NLS-1$//$NON-NLS-2$

        return possibleCards.get(index);
    }

    @Override
    public CardList getCardsToDiscard() {
        final CardList result = new CardList();

        final CardList discardableCards = new CardList(knowledge.getOwnCards());

        // just discard two random cards
        result.add(discardableCards.remove(random.nextInt(discardableCards
                .size())));
        result.add(discardableCards.remove(random.nextInt(discardableCards
                .size())));

        return result;
    }

    @Override
    public void preparateForNewGame() {
        // nothing to do for AIPlayerRND
    }

    @Override
    public void finalizeGame() {
        // nothing to do for AIPlayerRND
    }

    @Override
    public Boolean callContra() {
        return random.nextBoolean();
    }

    @Override
    public Boolean callRe() {
        return random.nextBoolean();
    }
}