package org.jskat.ai.deeplearning

import org.jskat.data.GameContract
import org.jskat.player.AbstractJSkatPlayer
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameType
import org.jskat.util.SkatConstants
import org.jskat.util.rule.GrandRule
import org.jskat.util.rule.SuitRule
import org.slf4j.LoggerFactory
import kotlin.random.Random

class AIPLayerDL(val name: String = "AIPlayerDL") : AbstractJSkatPlayer() {

    val logger = LoggerFactory.getLogger(javaClass)

    val random = Random.Default

    val biddingModel = BiddingModel()

    val grandRule = GrandRule()
    val suitRule = SuitRule()

    override fun prepareForNewGame() {
        // nothing to do for AIPLayerDL
    }

    override fun isAIPlayer(): Boolean {
        return true
    }

    override fun startGame() {
        // nothing to do for AIPLayerDL
    }

    override fun bidMore(nextBidValue: Int): Int {
        if (nextBidValue <= maxBid()) {
            return nextBidValue
        }

        return 0
    }

    override fun holdBid(currBidValue: Int): Boolean {
        return currBidValue <= maxBid()
    }

    private fun maxBid(): Int {
        val gameType = biddingModel.predictGameType(knowledge.playerPosition, knowledge.ownCards)

        logger.info("Bidding model prediction: $gameType")

        if (gameType != GameType.PASSED_IN) {
            var matadors = when (gameType) {
                GameType.GRAND -> grandRule.getMatadors(knowledge.ownCards, gameType)
                GameType.CLUBS, GameType.SPADES, GameType.HEARTS, GameType.DIAMONDS ->
                    suitRule.getMatadors(
                        knowledge.ownCards, gameType
                    )

                else -> 0
            }

            logger.info("Matadors: $matadors")

            // TODO use calculations from skat rules and SkatConstants
            // TODO take hand and ouvert into account
            return (matadors + 1) * SkatConstants.getGameBaseValue(gameType, false, false)
        }

        return 0
    }

    override fun pickUpSkat(): Boolean {
        return !biddingModel.handGamePossible(knowledge.playerPosition, knowledge.ownCards)
    }

    override fun getCardsToDiscard(): CardList {
        TODO("Not yet implemented")
    }

    override fun announceGame(): GameContract {
        val gameType = biddingModel.predictGameType(knowledge.playerPosition, knowledge.ownCards)
        return GameContract(gameType)
    }

    override fun playGrandHand(): Boolean {
        // TODO
        return false
    }

    override fun callContra(): Boolean {
        // TODO
        return false
    }

    override fun callRe(): Boolean {
        // TODO
        return false
    }

    override fun playCard(): Card {
        val possibleCards = getPlayableCards(knowledge.trickCards)
        val index = random.nextInt(possibleCards.size())
        return possibleCards[index]
    }

    override fun finalizeGame() {
        // nothing to do for AIPLayerDL
    }
}