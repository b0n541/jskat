package org.jskat.control;

import com.google.common.eventbus.Subscribe;
import org.jskat.ai.AIPlayerDelayWrapper;
import org.jskat.control.command.table.*;
import org.jskat.control.event.skatgame.*;
import org.jskat.control.event.table.*;
import org.jskat.control.gui.JSkatView;
import org.jskat.data.*;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.*;
import org.slf4j.Logger;
import java.util.List;

/** Interactive compatibility facade over the shared one-game rules engine. */
public class SkatGame {
    private final String tableName;
    private final SkatGameEngine engine;
    private JSkatView view;
    private final JSkatEventBus eventBus = JSkatEventBus.INSTANCE;

    public SkatGame(String tableName, GameVariant variant, JSkatPlayer fore, JSkatPlayer middle, JSkatPlayer rear) {
        this.tableName = tableName;
        JSkatOptions options = JSkatOptions.instance();
        var policy = new SkatGameEngine.RulesPolicy(variant,
                options.isPlayRamsch() && options.isRamschEventNoBid(), options.isSchieberamsch(true),
                options.isSchieberamschJacksInSkat(), options.isPlayContra(true), options.getContraCallingTime(),
                options.getRamschSkatOwner());
        engine = new SkatGameEngine(policy, List.of(fore, middle, rear), new InteractivePresentation(),
                false, true, move -> {}, (type, value) -> {});
        JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).register(this);
    }
    public SkatGameResult run() {
        try { return engine.run(); }
        finally { JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).unregister(this); }
    }
    @Subscribe public void gameStartedOn(GameStartedEvent event) { engine.recordExternalMove(event); }
    @Subscribe public void takeCardFromSkatOn(TakeCardFromSkatCommand command) {
        eventBus.post(new SkatCardTakenEvent(tableName, command.card));
    }
    @Subscribe public void putCardIntoSkatOn(PutCardIntoSkatCommand command) {
        eventBus.post(new SkatCardPutEvent(tableName, command.card));
    }
    @Deprecated public void setView(JSkatView view) {
        this.view = view;
        if (view != null && view.usesAiActionDelay()) {
            for (Player seat : Player.getOrderedList()) {
                JSkatPlayer current = engine.player.get(seat);
                if (current.isAIPlayer() && !(current instanceof AIPlayerDelayWrapper))
                    engine.player.put(seat, new AIPlayerDelayWrapper(current));
            }
        }
    }
    public void dealCards() { engine.dealCards(); }
    private void bidding() { engine.bidding(); }
    public void setLogger(Logger logger) { engine.setLogger(logger); }
    public void setCardDeck(CardDeck deck) { engine.setCardDeck(deck); }
    public void setGameAnnouncement(GameAnnouncement announcement) { engine.setGameAnnouncement(announcement); }
    public void setGameState(GameState state) { engine.setGameState(state); }
    public void setDeclarer(Player seat) { engine.setDeclarer(seat); }
    public Player getDeclarer() { return engine.getDeclarer(); }
    public boolean isGameWon() { return engine.isGameWon(); }
    public int getMaxSleep() { return engine.getMaxSleep(); }
    public void setMaxSleep(int milliseconds) { engine.setMaxSleep(milliseconds); }
    public SkatGameResult getGameResult() { return engine.getGameResult(); }
    public List<SkatGameEvent> getGameMoves() { return engine.getGameMoves(); }
    public GameSummary getGameSummary() { return engine.getGameSummary(); }
    public GameAnnouncement getGameAnnouncement() { return engine.getGameAnnouncement(); }
    public GameState getGameState() { return engine.getGameState(); }
    @Override public String toString() { return engine.toString(); }

    private final class InteractivePresentation implements SkatGameEngine.Presentation {
        @Override public void move(SkatGameEvent move) { eventBus.post(new TableGameMoveEvent(tableName, move)); }
        @Override public void phase(GameState phase, SkatGameData data) {
            if (view == null) return;
            eventBus.post(new SkatGameStateChangedEvent(tableName, phase));
            if (phase == GameState.GAME_OVER) {
                var finish = new GameFinishEvent(data.getPlayerName(data.getDeclarer()), data.getGameSummary());
                data.apply(finish);
                move(finish);
                eventBus.post(new ShowCardsCommand(tableName, data.getCardsAfterDiscard(), data.getSkat()));
            }
        }
        @Override public void active(Player seat) { eventBus.post(new ActivePlayerChangedEvent(tableName, seat)); }
        @Override public void declarer(Player seat) { eventBus.post(new DeclarerChangedEvent(tableName, seat)); }
        @Override public void pickup(CardList cards) { JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new SkatCardsPickedUpEvent(tableName, cards)); }
        @Override public void discard(CardList cards) { eventBus.post(new SkatCardsChangedEvent(tableName, cards)); }
        @Override public void trick(Trick trick) { JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new TrickCompletedEvent(trick)); }
        @Override public void geschoben(Player seat) { if (view != null) view.setGeschoben(tableName, seat); }
        @Override public void illegalCard(JSkatPlayer player, Card card) {
            if (player.isHumanPlayer()) eventBus.post(new CardNotAllowedToPlayEvent(card));
            else if (view != null) view.showAIPlayedSchwarzMessageCardPlay(player.getPlayerName(), card);
        }
        @Override public void illegalDiscard(JSkatPlayer player, CardList cards) {
            if (view != null) view.showAIPlayedSchwarzMessageDiscarding(player.getPlayerName(), cards);
        }
        @Override public void invalidContract(GameContract contract) {
            if (view != null) {
                var strings = JSkatResourceBundle.INSTANCE;
                view.showErrorMessage(strings.getString("invalidGameAnnouncementTitle"), strings.getString("invalidGameAnnouncementMessage", contract));
            }
        }
        @Override public void pause(int milliseconds) {
            if (milliseconds > 0) try { Thread.sleep(milliseconds); }
            catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        }
        @Override public void afterTrick() { pause(JSkatOptions.instance().getWaitTimeAfterTrick() * 1000); }
    }
}
