package org.jskat.gui.javafx.table

import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import org.jskat.util.JSkatResourceBundle

class IconPanel : HBox() {

    private val strings = JSkatResourceBundle.INSTANCE

    private val chatLabel: ImageView
    private val readyToPlayLabel: ImageView
    private val resignedLabel: ImageView
    private val thinkingLabel: ImageView

    var isShowIssWidgets = false
    var isChatEnabled = false
    var isReadyToPlay = false
    var isResigned = false
    private var isThinking = false

    init {
        val blankIcon = JSkatGraphicRepository.INSTANCE.getImageView(Icon.BLANK, IconSize.SMALL)
        chatLabel = ImageView(blankIcon.image)
        readyToPlayLabel = ImageView(blankIcon.image)
        resignedLabel = ImageView(blankIcon.image)
        thinkingLabel = ImageView(blankIcon.image)

        refreshIcons()

        children.addAll(resignedLabel, chatLabel, readyToPlayLabel, thinkingLabel)
    }

    fun reset() {
        isResigned = false
        refreshIcons()
    }

    fun setResign(isResign: Boolean) {
        isResigned = isResign
        refreshIcons()
    }

    fun setThinking(isThinking: Boolean) {
        this.isThinking = isThinking
        refreshIcons()
    }

    private fun refreshIcons() {
        if (isThinking) {
            thinkingLabel.image = JSkatGraphicRepository.INSTANCE.getImageView(Icon.THINKING, IconSize.SMALL).image
            Tooltip.install(thinkingLabel, Tooltip(strings.getString("player_thinking")))
        } else {
            setBlank(thinkingLabel)
        }

        if (!isShowIssWidgets) {
            setBlank(resignedLabel, chatLabel, readyToPlayLabel)
        } else {
            if (isResigned) {
                resignedLabel.image =
                    JSkatGraphicRepository.INSTANCE.getImageView(Icon.WHITE_FLAG, IconSize.SMALL).image
                Tooltip.install(resignedLabel, Tooltip(strings.getString("iss_player_wants_to_resign")))
            } else {
                setBlank(resignedLabel)
            }

            if (isChatEnabled) {
                chatLabel.image = JSkatGraphicRepository.INSTANCE.getImageView(Icon.CHAT, IconSize.SMALL).image
                Tooltip.install(chatLabel, Tooltip(strings.getString("iss_chat_enabled")))
            } else {
                chatLabel.image = JSkatGraphicRepository.INSTANCE.getImageView(Icon.CHAT_DISABLED, IconSize.SMALL).image
                Tooltip.install(chatLabel, Tooltip(strings.getString("iss_chat_disabled")))
            }

            if (isReadyToPlay) {
                readyToPlayLabel.image = JSkatGraphicRepository.INSTANCE.getImageView(Icon.OK, IconSize.SMALL).image
                Tooltip.install(readyToPlayLabel, Tooltip(strings.getString("iss_ready_to_play")))
            } else {
                readyToPlayLabel.image = JSkatGraphicRepository.INSTANCE.getImageView(Icon.STOP, IconSize.SMALL).image
                Tooltip.install(readyToPlayLabel, Tooltip(strings.getString("iss_not_ready_to_play")))
            }
        }
    }

    private fun setBlank(vararg labels: ImageView) {
        val blankIcon = JSkatGraphicRepository.INSTANCE.getImageView(Icon.BLANK, IconSize.SMALL)
        for (label in labels) {
            label.image = blankIcon.image
        }
    }
}
