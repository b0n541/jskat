package org.jskat.gui.javafx.main

import org.jskat.control.gui.action.JSkatAction
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.human.*
import org.jskat.gui.action.iss.*
import org.jskat.gui.action.main.*

object JSkatActions {
    val actionMap: Map<JSkatAction, AbstractJSkatAction> by lazy {
        val actions = mutableMapOf<JSkatAction, AbstractJSkatAction>()

        // common actions
        actions[JSkatAction.LOAD_SERIES] = LoadSeriesAction()
        actions[JSkatAction.SAVE_SERIES] = SaveSeriesAction()
        actions[JSkatAction.SAVE_SERIES_AS] = SaveSeriesAsAction()
        actions[JSkatAction.HELP] = HelpAction()
        actions[JSkatAction.LICENSE] = LicenseAction()
        actions[JSkatAction.EXIT_JSKAT] = ExitAction()
        actions[JSkatAction.PREFERENCES] = PreferencesAction()
        actions[JSkatAction.ABOUT_JSKAT] = AboutAction()
        actions[JSkatAction.CHANGE_ACTIVE_TABLE] = ChangeActiveTableAction()
        // skat table actions
        actions[JSkatAction.CREATE_LOCAL_TABLE] = CreateTableAction()
        actions[JSkatAction.START_LOCAL_SERIES] = StartSkatSeriesAction()
        actions[JSkatAction.CONTINUE_LOCAL_SERIES] = ContinueSkatSeriesAction()
        actions[JSkatAction.REPLAY_GAME] = ReplayGameAction()
        actions[JSkatAction.NEXT_REPLAY_STEP] = NextReplayMoveAction()
        // ISS actions
        actions[JSkatAction.REGISTER_ON_ISS] = RegisterAction()
        actions[JSkatAction.OPEN_ISS_HOMEPAGE] = OpenHomepageAction()
        actions[JSkatAction.SHOW_ISS_LOGIN] = ShowLoginPanelAction()
        actions[JSkatAction.CONNECT_TO_ISS] = ConnectAction()
        actions[JSkatAction.DISCONNECT_FROM_ISS] = LogoutAction()
        actions[JSkatAction.SEND_CHAT_MESSAGE] = SendChatMessageAction()
        actions[JSkatAction.CREATE_ISS_TABLE] = CreateIssTableAction()
        actions[JSkatAction.JOIN_ISS_TABLE] = JoinIssTableAction()
        actions[JSkatAction.LEAVE_ISS_TABLE] = LeaveIssTableAction()
        actions[JSkatAction.OBSERVE_ISS_TABLE] = ObserveTableAction()
        actions[JSkatAction.INVITE_ISS_PLAYER] = InvitePlayerAction()
        actions[JSkatAction.READY_TO_PLAY] = ReadyAction()
        actions[JSkatAction.TALK_ENABLED] = TalkEnableAction()
        actions[JSkatAction.CHANGE_TABLE_SEATS] = ChangeTableSeatsAction()
        actions[JSkatAction.RESIGN] = ResignAction()
        actions[JSkatAction.SHOW_CARDS] = ShowCardsAction()
        // Human player actions
        actions[JSkatAction.MAKE_BID] = MakeBidAction()
        actions[JSkatAction.HOLD_BID] = HoldBidAction()
        actions[JSkatAction.PASS_BID] = PassBidAction()
        actions[JSkatAction.PICK_UP_SKAT] = PickUpSkatAction()
        actions[JSkatAction.PLAY_GRAND_HAND] = PlayGrandHandAction()
        actions[JSkatAction.CALL_CONTRA] = CallContraAction()
        actions[JSkatAction.CALL_RE] = CallReAction()
        actions[JSkatAction.PLAY_SCHIEBERAMSCH] = PlaySchiebeRamschAction()
        actions[JSkatAction.SCHIEBEN] = SchiebenAction()
        actions[JSkatAction.PLAY_HAND_GAME] = PlayHandGameAction()
        actions[JSkatAction.ANNOUNCE_GAME] = GameAnnounceAction()
        actions[JSkatAction.PUT_CARD_INTO_SKAT] = PutCardIntoSkatAction()
        actions[JSkatAction.TAKE_CARD_FROM_SKAT] = TakeCardFromSkatAction()
        actions[JSkatAction.DISCARD_CARDS] = DiscardAction()
        actions[JSkatAction.PLAY_CARD] = PlayCardAction()

        // disable some actions
        actions[JSkatAction.LOAD_SERIES]?.setEnabled(false)
        actions[JSkatAction.SAVE_SERIES]?.setEnabled(false)
        actions[JSkatAction.SAVE_SERIES_AS]?.setEnabled(false)
        actions[JSkatAction.START_LOCAL_SERIES]?.setEnabled(false)
        actions[JSkatAction.CREATE_ISS_TABLE]?.setEnabled(false)
        actions[JSkatAction.INVITE_ISS_PLAYER]?.setEnabled(false)
        actions[JSkatAction.REPLAY_GAME]?.setEnabled(false)
        actions[JSkatAction.NEXT_REPLAY_STEP]?.setEnabled(false)

        actions
    }
}
