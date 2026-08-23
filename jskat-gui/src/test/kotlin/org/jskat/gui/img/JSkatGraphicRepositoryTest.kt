package org.jskat.gui.img

import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.util.Card
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class JSkatGraphicRepositoryTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
        }
    }

    @Test
    fun `loads active graphics as JavaFX images`() {
        val graphics = JSkatGraphicRepository.INSTANCE

        val images = listOf(
            graphics.getCardImageFX(Card.CA),
            graphics.getFlagImageFX(JSkatGraphicRepository.Flag.GERMAN),
            graphics.getImageView(JSkatGraphicRepository.Icon.CLOSE, JSkatGraphicRepository.IconSize.SMALL).image,
            graphics.getLeftBidBubbleFX(),
            graphics.getSkatTableImageFX(),
            graphics.getJSkatLogoImageFX(),
        )

        assertThat(images).allSatisfy { image ->
            assertThat(image.isError).isFalse()
            assertThat(image.width).isPositive()
            assertThat(image.height).isPositive()
        }
    }
}
