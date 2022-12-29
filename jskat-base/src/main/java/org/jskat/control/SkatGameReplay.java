package org.jskat.control;

import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.skatgame.*;
import org.jskat.control.event.table.SkatGameStateChangedEvent;
import org.jskat.control.event.table.TableGameMoveEvent;
import org.jskat.control.event.table.TrickCompletedEvent;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for replaying skat games
 */
public class SkatGameReplay {

    private final static Logger LOG = LoggerFactory.getLogger(SkatGameReplay.class);

    private final String tableName;
    private SkatGameData data;
    private final List<SkatGameEvent> gameMoves = new ArrayList<>();
    private int currentMove = 0;

    public SkatGameReplay(String tableName, List<SkatGameEvent> gameMoves) {
        this.tableName = tableName;
        this.gameMoves.addAll(gameMoves);
        resetReplay();
    }

    public void toStart() {
        resetReplay();
    }

    public void oneMoveBackward() {
        if (currentMove > 1) {
            currentMove--;
            gameMoves.get(currentMove).processBackward(data);
        }
    }

    public void oneMoveForward() {
        if (hasMoreMoves()) {
            oneStepForward();
        }
    }

    public void toEnd() {
        while (hasMoreMoves()) {
            oneStepForward();
        }
    }

    private void resetReplay() {
        currentMove = 0;
        data = new SkatGameData();
        // game start
        oneStepForward();
        // dealing
        oneStepForward();
    }

    private boolean hasMoreMoves() {
        return currentMove < gameMoves.size();
    }

    private void setGameState(SkatGameEvent event) {
        if (event instanceof GameStartEvent) {
            JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
        } else if (event instanceof BidEvent) {
            JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
        } else if (event instanceof GameAnnouncementEvent) {
            JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.DECLARING));
        } else if (event instanceof TrickCardPlayedEvent) {
            JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING));
        } else if (event instanceof GameFinishEvent) {
            JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.GAME_OVER));
        }
    }

    private void oneStepForward() {
        SkatGameEvent event = gameMoves.get(currentMove);

        setGameState(event);

        // TODO: code duplication with SkatGame.playCard()
        if (event instanceof TrickCardPlayedEvent
                && data.getCurrentTrick() != null
                && data.getCurrentTrick().getFirstCard() == null) {
            JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new TrickCompletedEvent(data.getLastCompletedTrick()));
        } else if (event instanceof GameFinishEvent) {
            JSkatEventBus.INSTANCE.post(new ShowCardsCommand(tableName, data.getCardsAfterDiscard(), data.getSkat()));
        }

        event.processForward(data);

        JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, event));

        currentMove++;
    }
}
