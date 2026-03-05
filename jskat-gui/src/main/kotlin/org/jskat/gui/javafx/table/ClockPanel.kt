package org.jskat.gui.javafx.table

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.util.Duration
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import java.text.DecimalFormat
import java.text.NumberFormat

class ClockPanel : HBox() {

    private val timeLabel: Label
    private var playerTimeInSeconds: Double = 0.0

    private var countDownTimeline: Timeline? = null

    init {
        val clockImageView = JSkatGraphicRepository.INSTANCE.getImageView(Icon.CLOCK, IconSize.SMALL)
        this.timeLabel = Label(getPlayerTimeString())

        children.addAll(clockImageView, this.timeLabel)
    }

    fun setActive() {
        countDownTimeline?.stop() // Stop any existing timeline
        countDownTimeline = Timeline(
            KeyFrame(Duration.seconds(1.0), EventHandler<ActionEvent> {
                playerTimeInSeconds -= 1.0
                if (playerTimeInSeconds < 0.0) {
                    playerTimeInSeconds = 0.0
                }
                refreshTimeLabel()
                if (playerTimeInSeconds == 0.0) {
                    countDownTimeline?.stop()
                }
            })
        )
        countDownTimeline?.cycleCount = Timeline.INDEFINITE
        countDownTimeline?.play()
    }

    fun setInactive() {
        countDownTimeline?.stop()
    }

    fun setPlayerTime(newPlayerTime: Double) {
        this.playerTimeInSeconds = newPlayerTime
        refreshTimeLabel()
    }

    private fun refreshTimeLabel() {
        this.timeLabel.text = getPlayerTimeString()
    }

    private fun getPlayerTimeString(): String {
        val minutes = (this.playerTimeInSeconds / 60).toInt()
        val seconds = (this.playerTimeInSeconds - (minutes * 60)).toInt()

        val format = NumberFormat.getInstance() as DecimalFormat
        format.applyPattern("00")
        return "${format.format(minutes)}:${format.format(seconds)}"
    }
}
