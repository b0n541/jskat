package org.jskat.gui.javafx.iss

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.image.ImageView
import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.data.iss.PlayerData
import org.jskat.gui.javafx.JavaFxTestSupport
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class IssPlayerInvitationDialogTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeToolkit() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `AI player remains available and can fill both invitation places`() {
        val dialog = onFxThread { IssPlayerInvitationDialog(listOf(player("SkatBot", isAI = true))) }

        onFxThread {
            invitationButton(dialog, "SkatBot").fire()
            invitationButton(dialog, "SkatBot").fire()
        }

        assertThat(onFxThread { dialog.resultConverter.call(ButtonType.OK) })
            .containsExactly("SkatBot", "SkatBot")
    }

    @Test
    fun `human player is removed after being placed`() {
        val dialog = onFxThread { IssPlayerInvitationDialog(listOf(player("Marta"))) }

        onFxThread { invitationButton(dialog, "Marta").fire() }

        assertThat(onFxThread { availableInvitationButtons(dialog) })
            .noneMatch { it.id == "invite-player-Marta" }
        assertThat(onFxThread { dialog.resultConverter.call(ButtonType.OK) })
            .containsExactly("Marta")
    }

    @Test
    fun `invitation places keep their size and show a remove icon`() {
        val dialog = onFxThread { IssPlayerInvitationDialog(listOf(player("SkatBot", isAI = true))) }
        val slotWidth = onFxThread { invitationPlaceButtons(dialog).first().prefWidth }
        val slotHeight = onFxThread { invitationPlaceButtons(dialog).first().prefHeight }

        onFxThread { invitationButton(dialog, "SkatBot").fire() }

        assertThat(onFxThread { invitationPlaceButtons(dialog).map(Button::getPrefWidth) })
            .containsOnly(slotWidth)
        assertThat(onFxThread { invitationPlaceButtons(dialog).map(Button::getPrefHeight) })
            .containsOnly(slotHeight)
        assertThat(onFxThread { availableInvitationButtons(dialog).single().prefHeight })
            .isEqualTo(slotHeight)
        assertThat(onFxThread { invitationPlaceButtons(dialog).first().prefWidth })
            .isEqualTo(onFxThread { availableInvitationButtons(dialog).single().prefWidth })
        assertThat(onFxThread { invitationPlaceButtons(dialog).single { it.graphic != null }.graphic })
            .isInstanceOf(ImageView::class.java)
    }

    private fun player(login: String, isAI: Boolean = false) = PlayerData().apply {
        this.login = login
        strength = 1234.56
        isKIPlayer = isAI
    }

    private fun invitationButton(dialog: IssPlayerInvitationDialog, login: String): Button =
        availableInvitationButtons(dialog).single { it.id == "invite-player-$login" }

    private fun availableInvitationButtons(dialog: IssPlayerInvitationDialog): List<Button> =
        descendantsOf(dialog.dialogPane.content).filterIsInstance<Button>()
            .filter { it.id?.startsWith("invite-player-") == true }
            .toList()

    private fun invitationPlaceButtons(dialog: IssPlayerInvitationDialog): List<Button> =
        descendantsOf(dialog.dialogPane.content).filterIsInstance<Button>()
            .filter { it.id?.startsWith("invitation-place-") == true }
            .toList()

    private fun descendantsOf(node: Node): Sequence<Node> = sequence {
        yield(node)
        if (node is Parent) {
            node.childrenUnmodifiable.forEach { child -> yieldAll(descendantsOf(child)) }
        }
    }

    private fun <T> onFxThread(action: () -> T): T {
        val result = arrayOfNulls<Any>(1)
        val failure = arrayOfNulls<Throwable>(1)
        val completed = CountDownLatch(1)
        Platform.runLater {
            try {
                result[0] = action()
            } catch (error: Throwable) {
                failure[0] = error
            } finally {
                completed.countDown()
            }
        }
        check(completed.await(1, TimeUnit.SECONDS))
        failure[0]?.let { throw it }
        @Suppress("UNCHECKED_CAST")
        return result[0] as T
    }
}
