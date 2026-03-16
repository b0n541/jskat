package org.jskat.gui.javafx.main

import org.jskat.control.gui.action.JSkatAction
import org.jskat.gui.action.human.*
import org.jskat.gui.action.iss.*
import org.jskat.gui.action.main.*
import javax.swing.ActionMap

object JSkatActions {
    fun createActionMap(): ActionMap {
        val actions = ActionMap()

        // common actions
        actions.put(JSkatAction.LOAD_SERIES, LoadSeriesAction())
        actions.put(JSkatAction.SAVE_SERIES, SaveSeriesAction())
        actions.put(JSkatAction.SAVE_SERIES_AS, SaveSeriesAsAction())
        actions.put(JSkatAction.HELP, HelpAction())
        actions.put(JSkatAction.LICENSE, LicenseAction())
        actions.put(JSkatAction.EXIT_JSKAT, ExitAction())
        actions.put(JSkatAction.PREFERENCES, PreferencesAction())
        actions.put(JSkatAction.ABOUT_JSKAT, AboutAction())
        actions.put(JSkatAction.CHANGE_ACTIVE_TABLE, ChangeActiveTableAction())
        // skat table actions
        actions.put(JSkatAction.CREATE_LOCAL_TABLE, CreateTableAction())
        actions.put(JSkatAction.START_LOCAL_SERIES, StartSkatSeriesAction())
        actions.put(JSkatAction.CONTINUE_LOCAL_SERIES, ContinueSkatSeriesAction())
        actions.put(JSkatAction.REPLAY_GAME, ReplayGameAction())
        actions.put(JSkatAction.NEXT_REPLAY_STEP, NextReplayMoveAction())
        // ISS actions
        actions.put(JSkatAction.REGISTER_ON_ISS, RegisterAction())
        actions.put(JSkatAction.OPEN_ISS_HOMEPAGE, OpenHomepageAction())
        actions.put(JSkatAction.SHOW_ISS_LOGIN, ShowLoginPanelAction())
        actions.put(JSkatAction.CONNECT_TO_ISS, ConnectAction())
        actions.put(JSkatAction.DISCONNECT_FROM_ISS, LogoutAction())
        actions.put(JSkatAction.SEND_CHAT_MESSAGE, SendChatMessageAction())
        actions.put(JSkatAction.CREATE_ISS_TABLE, CreateIssTableAction())
        actions.put(JSkatAction.JOIN_ISS_TABLE, JoinIssTableAction())
        actions.put(JSkatAction.LEAVE_ISS_TABLE, LeaveIssTableAction())
        actions.put(JSkatAction.OBSERVE_ISS_TABLE, ObserveTableAction())
        actions.put(JSkatAction.INVITE_ISS_PLAYER, InvitePlayerAction())
        actions.put(JSkatAction.READY_TO_PLAY, ReadyAction())
        actions.put(JSkatAction.TALK_ENABLED, TalkEnableAction())
        actions.put(JSkatAction.CHANGE_TABLE_SEATS, ChangeTableSeatsAction())
        actions.put(JSkatAction.RESIGN, ResignAction())
        actions.put(JSkatAction.SHOW_CARDS, ShowCardsAction())
        // Human player actions
        actions.put(JSkatAction.MAKE_BID, MakeBidAction())
        actions.put(JSkatAction.HOLD_BID, HoldBidAction())
        actions.put(JSkatAction.PASS_BID, PassBidAction())
        actions.put(JSkatAction.PICK_UP_SKAT, PickUpSkatAction())
        actions.put(JSkatAction.PLAY_GRAND_HAND, PlayGrandHandAction())
        actions.put(JSkatAction.CALL_CONTRA, CallContraAction())
        actions.put(JSkatAction.CALL_RE, CallReAction())
        actions.put(JSkatAction.PLAY_SCHIEBERAMSCH, PlaySchiebeRamschAction())
        actions.put(JSkatAction.SCHIEBEN, SchiebenAction())
        actions.put(JSkatAction.PLAY_HAND_GAME, PlayHandGameAction())
        actions.put(JSkatAction.ANNOUNCE_GAME, GameAnnounceAction())
        actions.put(JSkatAction.PUT_CARD_INTO_SKAT, PutCardIntoSkatAction())
        actions.put(JSkatAction.TAKE_CARD_FROM_SKAT, TakeCardFromSkatAction())
        actions.put(JSkatAction.DISCARD_CARDS, DiscardAction())
        actions.put(JSkatAction.PLAY_CARD, PlayCardAction())

        // disable some actions
        actions.get(JSkatAction.LOAD_SERIES).isEnabled = false
        actions.get(JSkatAction.SAVE_SERIES).isEnabled = false
        actions.get(JSkatAction.SAVE_SERIES_AS).isEnabled = false
        actions.get(JSkatAction.START_LOCAL_SERIES).isEnabled = false
        actions.get(JSkatAction.CREATE_ISS_TABLE).isEnabled = false
        actions.get(JSkatAction.INVITE_ISS_PLAYER).isEnabled = false
        actions.get(JSkatAction.REPLAY_GAME).isEnabled = false
        actions.get(JSkatAction.NEXT_REPLAY_STEP).isEnabled = false

        return actions
    }
}
