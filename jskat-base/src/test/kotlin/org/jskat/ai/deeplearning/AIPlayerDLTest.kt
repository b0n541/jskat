package org.jskat.ai.deeplearning

import com.google.common.eventbus.EventBus
import org.jskat.AbstractJSkatTest
import org.jskat.ai.rnd.AIPlayerRND
import org.jskat.control.JSkatEventBus
import org.jskat.control.SkatGame
import org.jskat.data.JSkatOptions
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
}