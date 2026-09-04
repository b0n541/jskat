package org.jskat.gui.javafx.iss

import javafx.scene.image.ImageView
import org.jskat.gui.img.JSkatGraphicRepository

internal fun languageFlagImageViews(
    languages: String,
    bitmaps: JSkatGraphicRepository
): List<ImageView> = languages.mapNotNull { language ->
    JSkatGraphicRepository.Flag.valueOf(language)?.let { flag ->
        ImageView(bitmaps.getFlagImageFX(flag))
    }
}
