package org.jskat.control;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.command.table.NextReplayMoveCommand;
import org.jskat.control.command.table.ReadyForNextGameCommand;
import org.jskat.control.command.table.ReplayGameCommand;
import org.jskat.control.command.table.PracticeWithSameCardsCommand;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.control.event.skatgame.GameStartEvent;
import org.jskat.control.event.table.*;
import org.jskat.control.gui.JSkatView;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

/**
 * Controls a series of skat games
 */
public class SkatSeries {

    private static final Logger LOG = LoggerFactory.getLogger(SkatSeries.class);

    private static final Random RANDOM = new Random();

    private int maxSleep = 0;
    private final SkatSeriesData data;
    private int roundsToGo = 0;
    private boolean unlimitedRounds = false;
    private boolean onlyPlayRamsch = false;
    private boolean readyForNextGame = false;
    private boolean practiceRequested = false;
    private CardDeck practiceDeck = null;
    private final Map<Player, JSkatPlayer> players;
    private SkatGame currSkatGame;
    private SkatGameReplay currReplayGame;

    private JSkatView view;

    /**
     * Constructor
     *
     * @param tableName Table name
     */
    public SkatSeries(final String tableName) {

        data = new SkatSeriesData();
        data.setState(SeriesState.WAITING);
        data.setTableName(tableName);

        view = JSkatMaster.INSTANCE.getView();

        JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).register(this);

        players = new HashMap<>();
    }

    @Subscribe
    public void startReplayGameOn(final ReplayGameCommand command) {

        JSkatEventBus.TABLE_EVENT_BUSSES.get(data.getTableName()).post(
                new SkatGameReplayStartedEvent());

        currReplayGame = new SkatGameReplay(data.getTableName(), currSkatGame.getGameMoves());
    }

    @Subscribe
    public void replayNextMoveOn(final NextReplayMoveCommand command) {
        currReplayGame.oneMoveForward();
    }

    @Subscribe
    public void readyForNextGameOn(final ReadyForNextGameCommand command) {

        JSkatEventBus.TABLE_EVENT_BUSSES.get(data.getTableName()).post(new SkatGameReplayFinishedEvent());
        readyForNextGame = true;
    }

    @Subscribe
    public void practiceWithSameCardsOn(final PracticeWithSameCardsCommand command) {

        LOG.debug("Practice with same cards requested");

        JSkatEventBus.TABLE_EVENT_BUSSES.get(data.getTableName()).post(new SkatGameReplayFinishedEvent());

        // Build the deck from the current game's dealt cards
        if (currSkatGame != null) {
            final Map<Player, CardList> dealtCards = currSkatGame.getDealtCards();
            final CardList dealtSkat = currSkatGame.getDealtSkat();

            // Convert CardList to List<Card> for the CardDeck constructor
            practiceDeck = new CardDeck(
                    StreamSupport.stream(dealtCards.get(Player.FOREHAND).spliterator(), false).toList(),
                    StreamSupport.stream(dealtCards.get(Player.MIDDLEHAND).spliterator(), false).toList(),
                    StreamSupport.stream(dealtCards.get(Player.REARHAND).spliterator(), false).toList(),
                    StreamSupport.stream(dealtSkat.spliterator(), false).toList());

            practiceRequested = true;
        } else {
            LOG.warn("No current game available for practice");
        }
    }

    /**
     * Sets the skat players
     *
     * @param newPlayers New skat series player
     */
    public void setPlayers(final List<JSkatPlayer> newPlayers) {

        if (newPlayers.size() != 3) {
            throw new IllegalArgumentException("Only three players are allowed at the moment.");
        }

        JSkatEventBus.INSTANCE.post(new PlayerNamesChangedEvent(data.getTableName(),
                newPlayers.get(0).getPlayerName(), newPlayers.get(0).isAIPlayer(),
                newPlayers.get(1).getPlayerName(), newPlayers.get(1).isAIPlayer(),
                newPlayers.get(2).getPlayerName(), newPlayers.get(2).isAIPlayer()));

        // memorize third player to find it again after shuffling the players
        final JSkatPlayer thirdPlayer = newPlayers.get(2);

        // set players in random order
        // simple Collection.shuffle doesn't work here, because the order of
        // players should be the same like in start skat series dialog
        final int startPlayer = RANDOM.nextInt(3);
        players.put(Player.FOREHAND, newPlayers.get(startPlayer));
        players.put(Player.MIDDLEHAND, newPlayers.get((startPlayer + 1) % 3));
        players.put(Player.REARHAND, newPlayers.get((startPlayer + 2) % 3));

        // if an human player is playing, always show him/her at the bottom
        // FIXME (jansch 09.05.2012) this is GUI logic, move it to the GUI
        // package
        for (final Player hand : Player.values()) {
            if (players.get(hand).isHumanPlayer()
                    || players.get(hand) == thirdPlayer) {
                data.setBottomPlayer(hand);
            }
        }

        LOG.debug("Player order: " + players);
    }

    /**
     * Checks whether a series is running
     *
     * @return TRUE if the series is running
     */
    public boolean isRunning() {

        return SeriesState.RUNNING.equals(data.getState());
    }

    /**
     * Starts the series
     *
     * @param rounds            Number of rounds to be played
     * @param newUnlimitedRound TRUE, if the number of rounds is not limited
     */
    public void setMaxRounds(final int rounds, final boolean newUnlimitedRound) {

        roundsToGo = rounds;
        unlimitedRounds = newUnlimitedRound;
        data.setState(SeriesState.RUNNING);
    }

    public void run() {

        int roundsPlayed = 0;
        int gameNumber = 0;
        int gamesPerRound = 3;

        while (roundsToGo > 0 || unlimitedRounds) {

            LOG.debug("Playing round " + (roundsPlayed + 1));

            for (int gameInRound = 0; gameInRound < gamesPerRound; gameInRound++) {

                if (gameInRound > 0 || roundsPlayed > 0) {
                    // change player positions after first game
                    final JSkatPlayer helper = players.get(Player.REARHAND);
                    players.put(Player.REARHAND, players.get(Player.FOREHAND));
                    players.put(Player.FOREHAND, players.get(Player.MIDDLEHAND));
                    players.put(Player.MIDDLEHAND, helper);

                    data.setBottomPlayer(data.getBottomPlayer().getRightNeighbor());
                }

                gameNumber++;

                GameVariant gameVariant = GameVariant.STANDARD;
                if (onlyPlayRamsch) {
                    gameVariant = GameVariant.FORCED_RAMSCH;
                }

                currSkatGame = new SkatGame(data.getTableName(), gameVariant,
                        players.get(Player.FOREHAND),
                        players.get(Player.MIDDLEHAND),
                        players.get(Player.REARHAND));

                JSkatEventBus.INSTANCE.post(
                        new TableGameMoveEvent(data.getTableName(),
                                new GameStartEvent(gameNumber, gameVariant,
                                        data.getBottomPlayer().getLeftNeighbor(),
                                        data.getBottomPlayer().getRightNeighbor(),
                                        data.getBottomPlayer())));

                currSkatGame.setView(view);
                currSkatGame.setMaxSleep(maxSleep);

                LOG.debug("Playing game " + (gameInRound + 1) + " of " + gamesPerRound + " for round " + (roundsPlayed + 1));

                data.addGame(currSkatGame);

                CompletableFuture.runAsync(() -> currSkatGame.run()).join();

                LOG.debug("Game ended: join");

                // Wait for user action, handling practice-with-same-cards requests
                do {
                    readyForNextGame = false;
                    practiceRequested = false;
                    while (isHumanPlayerInvolved() && !readyForNextGame && !practiceRequested) {
                        try {
                            Thread.sleep(200);
                        } catch (final InterruptedException e) {
                            LOG.warn("Interrupted while waiting for next game", e);
                            Thread.currentThread().interrupt();
                        }
                    }

                    if (practiceRequested && practiceDeck != null) {
                        playPracticeGame(gameNumber, gameVariant);
                    }
                } while (practiceRequested);
            }

            roundsToGo--;
            roundsPlayed++;
        }

        data.setState(SeriesState.SERIES_FINISHED);

        JSkatEventBus.INSTANCE.post(new SkatSeriesFinishedEvent(data.getTableName()));

        LOG.debug(data.getState().name());
    }

    private boolean isHumanPlayerInvolved() {

        boolean result = false;

        for (final JSkatPlayer currPlayer : players.values()) {
            if (currPlayer.isHumanPlayer()) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Plays a practice game with the same card deal. This game does not count
     * towards the series score and does not affect player rotation.
     *
     * @param gameNumber  The display game number (for UI purposes)
     * @param gameVariant The game variant to use
     */
    private void playPracticeGame(final int gameNumber, final GameVariant gameVariant) {

        LOG.info("Playing practice game with same cards");

        final SkatGame practiceGame = new SkatGame(data.getTableName(), gameVariant,
                players.get(Player.FOREHAND),
                players.get(Player.MIDDLEHAND),
                players.get(Player.REARHAND));

        // Set the pre-dealt deck so cards will be the same
        practiceGame.setCardDeck(practiceDeck);

        JSkatEventBus.INSTANCE.post(
                new TableGameMoveEvent(data.getTableName(),
                        new GameStartEvent(gameNumber, gameVariant,
                                data.getBottomPlayer().getLeftNeighbor(),
                                data.getBottomPlayer().getRightNeighbor(),
                                data.getBottomPlayer(),
                                true)));

        practiceGame.setView(view);
        practiceGame.setMaxSleep(maxSleep);

        // Note: We intentionally do NOT call data.addGame(practiceGame)
        // because this is a practice game that should not count for score

        CompletableFuture.runAsync(() -> practiceGame.run()).join();

        LOG.info("Practice game ended");

        // Store the practice game as currSkatGame so that another practice can be requested
        currSkatGame = practiceGame;

        // Clear the practice deck
        practiceDeck = null;
    }

    /**
     * Gets the state of the series
     *
     * @return State of the series
     */
    public SeriesState getSeriesState() {

        return data.getState();
    }

    /**
     * Gets the game state of the current game
     *
     * @return Game state
     */
    public GameState getGameState() {
        return data.getGameState();
    }

    /**
     * Gets the ID of the current game
     *
     * @return ID of the current game
     */
    public int getCurrentGameID() {

        return data.getCurrentGameID();
    }

    /**
     * Sets the view for the series
     *
     * @param newView View
     */
    public void setView(final JSkatView newView) {

        view = newView;
    }

    /**
     * Sets whether only ramsch games are played or not
     *
     * @param isOnlyPlayRamsch TRUE, if only ramsch games should be played
     */
    public void setOnlyPlayRamsch(final boolean isOnlyPlayRamsch) {
        onlyPlayRamsch = isOnlyPlayRamsch;
    }

    /**
     * Sets max sleep between actions during the skat series, this must only be set
     * in skat series that are run with a GUI, otherwise the default value of 0 is
     * used
     *
     * @param newMaxSleep New value for maximum sleep time in milliseconds
     */
    public void setMaxSleep(final int newMaxSleep) {

        maxSleep = newMaxSleep;
    }
}
