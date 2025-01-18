package org.jskat.ai.deeplearning

import com.google.common.eventbus.EventBus
import org.jskat.AbstractJSkatTest
import org.jskat.ai.rnd.AIPlayerRND
import org.jskat.control.JSkatEventBus
import org.jskat.control.SkatGame
import org.jskat.data.JSkatOptions
import org.jskat.util.Card
import org.jskat.util.GameVariant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AIPlayerDLTest : AbstractJSkatTest() {

    val TABLE_NAME = "Table 1"

    @BeforeEach
    fun setUp() {
        JSkatOptions.instance().resetToDefault()
        JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, EventBus())
    }

    @Test
    fun testBidding() {
        val game = SkatGame(
            TABLE_NAME,
            GameVariant.STANDARD,
            AIPLayerDL(),
            AIPlayerRND(),
            AIPlayerRND()
        )
        game.run()
    }

    @Test
    fun testPermutation() {
        val subsets = getSubsets(Card.values().toList().subList(0, 12), 10)
        println("Size: ${subsets.size}")
        subsets.forEach { println(it) }
    }
}

fun <T> getSubsets(cards: List<T>, size: Int): List<List<T>> {
    val result = mutableListOf<List<T>>()
    val combination = IntArray(size)

    fun generate(index: Int, start: Int) {
        if (index == size) {
            result.add(combination.map { cards[it] })
            return
        }
        for (i in start until cards.size) {
            combination[index] = i
            generate(index + 1, i + 1)
        }
    }

    generate(0, 0)
    return result
}