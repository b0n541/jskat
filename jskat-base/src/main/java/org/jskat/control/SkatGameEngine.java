package org.jskat.control;

import org.jskat.control.event.skatgame.*;
import org.jskat.data.*;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatTableOptions.ContraCallingTime;
import org.jskat.data.SkatTableOptions.RamschSkatOwner;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.*;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls a skat game.
 */
public class SkatGameEngine {

    private Logger log = LoggerFactory.getLogger(SkatGameEngine.class);
    private int maxSleep;
    protected final SkatGameData data;
    private final GameVariant variant;
    private CardDeck deck;
    protected final Map<Player, JSkatPlayer> player;
    private Player activePlayer;
    private final RulesPolicy policy;
    protected final Presentation presentation;
    private final boolean strict;
    private final java.util.function.Consumer<SkatGameEvent> moves;
    private final java.util.function.BiConsumer<String,String> facts;
    private SkatRule rules;

    /**
     * Constructor
     *
     * @param newTableName  Table name
     * @param variant       Game variant
     * @param newForeHand   Forehand player
     * @param newMiddleHand Middle hand player
     * @param newRearHand   Rear hand player
     */
    public SkatGameEngine(RulesPolicy policy, List<JSkatPlayer> players, Presentation presentation,
                          boolean strict, boolean resetPlayers,
                          java.util.function.Consumer<SkatGameEvent> moves,
                          java.util.function.BiConsumer<String,String> facts) {
        this.policy = policy;
        this.variant = policy.variant();
        this.presentation = presentation;
        this.strict = strict;
        this.moves = moves;
        this.facts = facts;
        data = new SkatGameData();
        player = new java.util.EnumMap<>(Player.class);
        for (Player seat : Player.getOrderedList()) {
            JSkatPlayer instance = players.get(seat.getOrder());
            player.put(seat, instance);
            data.setPlayerName(seat, instance.getPlayerName());
            if (resetPlayers) instance.newGame(seat);
        }
        setGameState(GameState.GAME_START);
    }

    public record RulesPolicy(GameVariant variant, boolean ramschAllPass, boolean schieberamsch,
                              boolean jacksInSkat, boolean contra, ContraCallingTime contraTime,
                              RamschSkatOwner ramschSkatOwner) {
        public static RulesPolicy standard() {
            return new RulesPolicy(GameVariant.STANDARD, false, false, false, false,
                    ContraCallingTime.AFTER_GAME_ANNOUNCEMENT, RamschSkatOwner.LAST_TRICK);
        }
    }

    /** Presentation operations are synchronous but have no effect on the rules. */
    public interface Presentation {
        default void move(SkatGameEvent move) {}
        default void phase(GameState phase, SkatGameData data) {}
        default void active(Player seat) {}
        default void declarer(Player seat) {}
        default void pickup(CardList cards) {}
        default void discard(CardList cards) {}
        default void trick(Trick trick) {}
        default void geschoben(Player seat) {}
        default void illegalCard(JSkatPlayer player, Card card) {}
        default void illegalDiscard(JSkatPlayer player, CardList cards) {}
        default void invalidContract(GameContract contract) {}
        default void pause(int milliseconds) {}
        default void afterTrick() {}
    }

    public void recordExternalMove(SkatGameEvent move) { data.apply(move); }
    private void emitMove(SkatGameEvent move) {
        data.apply(move);
        moves.accept(move);
        presentation.move(move);
    }
    public SkatGameData gameData() { return data; }
    private static GameContract copyContract(GameContract contract) {
        return new GameContract(contract.gameType(), contract.hand(), contract.schneider(), contract.schwarz(),
                contract.ouvert(), new CardList(contract.ouvertCards()));
    }
    private static boolean sameCards(CardList a, CardList b) {
        java.util.Set<Card> left = new java.util.HashSet<>(), right = new java.util.HashSet<>();
        a.forEach(left::add); b.forEach(right::add);
        return left.size() == 10 && a.size() == 10 && left.equals(right);
    }
    private void illegal(String action) {
        throw new IllegalArgumentException("Illegal " + action + " by " + activePlayer + " in " + getGameState());
    }

    /**
     * Runs the skat game.
     *
     * @return Result of the skat game
     */
    public SkatGameResult run() {
        // FIXME jan 11.07.2013: this method is too long, break it down to smaller
        // methods or implement it in another way
        do {
            log.info("Game state: " + data.getGameState());

            switch (data.getGameState()) {
                case GAME_START:
                    setGameState(GameState.DEALING);
                    break;
                case DEALING:
                    dealCards();
                    setGameState(GameState.BIDDING);
                    break;
                case BIDDING:
                    setActivePlayer(Player.MIDDLEHAND);

                    if (variant == GameVariant.FORCED_RAMSCH) {
                        log.info("Playing forced ramsch");
                        setGameState(GameState.RAMSCH_GRAND_HAND_ANNOUNCING);
                    } else {
                        // "normal" game (i.e. no ramsch)
                        bidding();

                        if (data.getDeclarer() == null) {
                            // FIXME (jansch 02.01.2012) use cloned rule options here (see MantisBT: 0000037)


                            if (policy.ramschAllPass()) {
                                log.info("Playing ramsch due to no bid");
                                setGameState(GameState.RAMSCH_GRAND_HAND_ANNOUNCING);
                            } else {
                                setGameAnnouncement(new GameAnnouncement(new GameContract(GameType.PASSED_IN)));
                                setGameState(GameState.PRELIMINARY_GAME_END);
                            }
                        } else {
                            setGameState(GameState.PICKING_UP_SKAT);
                        }
                    }
                    break;
                case RAMSCH_GRAND_HAND_ANNOUNCING:
                    setActivePlayer(Player.FOREHAND);

                    final boolean grandHandAnnounced = grandHand();

                    if (grandHandAnnounced) {
                        log.debug(data.getDeclarer() + " is playing grand hand");
                        setGameAnnouncement(new GameAnnouncement(new GameContract(GameType.GRAND, true)));
                        setGameState(GameState.TRICK_PLAYING);
                        log.debug("grand hand game started");
                        break;
                    } else {
                        if (policy.schieberamsch()) {
                            log.debug("no grand hand - initiating schieberamsch");
                            setGameState(GameState.SCHIEBERAMSCH);
                        } else {
                            log.debug("no grand hand and no schieberamsch - play ramsch");
                            setGameAnnouncement(new GameAnnouncement(new GameContract(GameType.RAMSCH)));
                            setGameState(GameState.TRICK_PLAYING);
                        }
                    }
                    break;
                case SCHIEBERAMSCH:
                    schieberamsch();
                    setGameAnnouncement(new GameAnnouncement(new GameContract(GameType.RAMSCH)));
                    setGameState(GameState.TRICK_PLAYING);
                    break;
                case PICKING_UP_SKAT:
                    setActivePlayer(data.getDeclarer());
                    if (pickUpSkat()) {
                        setGameState(GameState.DISCARDING);
                    } else {
                        setGameState(GameState.DECLARING);
                    }
                    break;
                case DISCARDING:
                    setActivePlayer(data.getDeclarer());
                    discarding();
                    if (!GameState.PRELIMINARY_GAME_END.equals(data.getGameState())) {
                        log.info("Transitioning to DECLARING state after discarding.");
                        setGameState(GameState.DECLARING);
                    }
                    break;
                case DECLARING:
                    log.info("Entering announceGame() method.");
                    announceGame();
                    if (isContraPlayEnabled(ContraCallingTime.AFTER_GAME_ANNOUNCEMENT, 0)) {
                        setGameState(GameState.CONTRA);
                    } else {
                        setGameState(GameState.TRICK_PLAYING);
                    }
                    break;
                case CONTRA:
                    for (final Player player : Player.getOrderedList()) {
                        if (isContraEnabledForPlayer(player, ContraCallingTime.AFTER_GAME_ANNOUNCEMENT, 0)) {
                            setActivePlayer(player);
                            contraRe();
                        }
                    }
                    setGameState(GameState.TRICK_PLAYING);
                    break;
                case TRICK_PLAYING:
                    playTricks();
                    setGameState(GameState.CALCULATING_GAME_VALUE);
                    break;
                case PRELIMINARY_GAME_END:
                    setGameState(GameState.CALCULATING_GAME_VALUE);
                    break;
                case CALCULATING_GAME_VALUE:
                    calculateGameValue();
                    setGameState(GameState.GAME_OVER);
                    break;
                case GAME_OVER:
                    break;
            }

        } while (data.getGameState() != GameState.GAME_OVER);


        log.debug(data.getGameState().name());
        log.debug("Game moves:");
        for (final SkatGameEvent event : data.getGameMoves()) {
            log.debug(event.toString());
        }

        return getGameResult();
    }

    private void contraRe() {
        if (getActivePlayerInstance().callContra()) {
            emitMove(new ContraEvent(activePlayer));
            final Player activePlayerBeforeContraRe = activePlayer;
            setActivePlayer(data.getDeclarer());
            if (getActivePlayerInstance().callRe()) {
                emitMove(new ReEvent(activePlayer));
            }
            setActivePlayer(activePlayerBeforeContraRe);
        }
    }

    private boolean grandHand() {
        boolean grandHandAnnounced = false;

        for (final Player currPlayer : Player.getOrderedList()) {
            setActivePlayer(currPlayer);
            if (!grandHandAnnounced && playGrandHand()) {
                log.info("Player " + activePlayer + " is playing grand hand.");
                setDeclarer(activePlayer);
                grandHandAnnounced = true;
            } else {
                log.info("Player " + activePlayer + " doesn't want to play grand hand.");
            }
        }
        return grandHandAnnounced;
    }

    private void schieberamsch() {
        for (final Player currPlayer : Player.getOrderedList()) {
            setActivePlayer(currPlayer);
            if (!pickUpSkat()) {
                log.info("Player " + currPlayer + " does schieben.");
                data.addGeschoben();
                presentation.geschoben(activePlayer);
            } else {
                log.info("Player " + currPlayer + " wants to look into skat.");
                discarding();
            }
        }
    }

    private void setActivePlayer(final Player newPlayer) {
        activePlayer = newPlayer;
        facts.accept("active", activePlayer.name());
        presentation.active(activePlayer);
    }

    private boolean playGrandHand() {
        return getActivePlayerInstance().playGrandHand();
    }

    private boolean pickUpSkat() {
        return getActivePlayerInstance().pickUpSkat();
    }

    /**
     * Deals the cards to the players and the skat
     */
    public void dealCards() {

        final Map<Player, CardList> dealtCards = new HashMap<>();
        for (final Player player : Player.values()) {
            dealtCards.put(player, new CardList());
        }

        if (deck == null) {
            // Skat game has no cards, yet
            deck = new CardDeck();

            log.debug(deck.toString());
        }

        log.info("shuffling...");
        deck.shuffle();

        doSleep(maxSleep);

        log.debug("dealing...");

        // deal three rounds of cards
        // deal three cards
        dealCards(3, dealtCards);
        // and put two cards into the skat
        final CardList skat = new CardList(deck.remove(0), deck.remove(0));
        // deal four cards
        dealCards(4, dealtCards);
        // deal three cards
        dealCards(3, dealtCards);

        emitMove(new CardDealEvent(dealtCards, skat));

        doSleep(maxSleep);

        log.debug("Fore hand: " + data.getPlayerCards(Player.FOREHAND));
        log.debug("Middle hand: "
                + data.getPlayerCards(Player.MIDDLEHAND));
        log.debug("Rear hand: " + data.getPlayerCards(Player.REARHAND));
        log.debug("Skat: " + data.getSkat());
    }

    /**
     * Deals a given number of cards to the players
     *
     * @param cardCount Number of cards to be dealt to a player
     */
    private void dealCards(final int cardCount, final Map<Player, CardList> dealtCards) {

        for (final Player hand : Player.getOrderedList()) {
            final CardList cards = new CardList();
            for (int j = 0; j < cardCount; j++) {
                // deal amount of cards
                cards.add(deck.remove(0));
            }
            // player can get original card object because Card is immutable
            dealtCards.get(hand).addAll(cards);
            getPlayerInstance(hand).takeCards(cards);
        }
    }

    /**
     * Controls the bidding of all players
     */
    public void bidding() {

        int bidValue = 0;

        log.debug("ask middle and fore hand...");

        bidValue = twoPlayerBidding(Player.MIDDLEHAND, Player.FOREHAND, bidValue);

        log.debug("Bid value after first bidding: " + bidValue);

        final Player firstWinner = getBiddingWinner(Player.MIDDLEHAND, Player.FOREHAND);

        log.debug("First bidding winner: " + firstWinner);
        log.debug("ask rear hand and first winner...");

        bidValue = twoPlayerBidding(Player.REARHAND, firstWinner, bidValue);

        log.debug("Bid value after second bidding: " + bidValue);

        // get second winner
        Player secondWinner = getBiddingWinner(Player.REARHAND, firstWinner);

        if (secondWinner == Player.FOREHAND && bidValue == 0) {

            log.debug("Check whether fore hand holds at least one bid");

            setActivePlayer(Player.FOREHAND);

            // check whether forehand holds at least one bid
            final int foreHandBidValue = getPlayerInstance(Player.FOREHAND).bidMore(18);
            if (strict && foreHandBidValue != 0 && (foreHandBidValue < 18 || !SkatConstants.bidOrder.contains(foreHandBidValue))) illegal("bid " + foreHandBidValue);
            if (foreHandBidValue > 0) {
                log.debug("Fore hand holds 18");
                emitMove(new BidEvent(secondWinner, foreHandBidValue));
            } else {
                log.debug("Fore hand passes too");
                emitMove(new PassBidEvent(Player.FOREHAND, data.getNextBidValue()));
                secondWinner = null;
            }
        }

        if (secondWinner != null) {
            // there is a winner of the bidding
            setDeclarer(secondWinner);
            setActivePlayer(secondWinner);

            log.debug("Player " + data.getDeclarer() + " wins the bidding.");
        }

        doSleep(maxSleep);
    }

    private void informPlayersAboutBid(final Player bidPlayer, final int bidValue) {
        // inform all players about the last bid
        for (final JSkatPlayer playerInstance : player.values()) {
            playerInstance.bidByPlayer(bidPlayer, bidValue);
        }
    }

    /**
     * Controls the bidding between two players
     *
     * @param announcer     Announcing player
     * @param hearer        Hearing player
     * @param startBidValue Bid value to start from
     * @return the final bid value
     */
    private int twoPlayerBidding(final Player announcer, final Player hearer, final int startBidValue) {

        int currBidValue = startBidValue;
        boolean announcerPassed = false;
        boolean hearerPassed = false;

        while (!announcerPassed && !hearerPassed) {

            // get bid value
            final int nextBidValue = SkatConstants.getNextBidValue(currBidValue);
            // ask player
            setActivePlayer(announcer);
            final int announcerBidValue = getPlayerInstance(announcer).bidMore(nextBidValue);

            if (strict && announcerBidValue != 0 && (announcerBidValue < nextBidValue || !SkatConstants.bidOrder.contains(announcerBidValue))) illegal("bid " + announcerBidValue);
            if (announcerBidValue > 0 && SkatConstants.bidOrder.contains(Integer.valueOf(announcerBidValue))) {

                log.debug("announcer bids " + announcerBidValue);

                // announcing hand holds bid
                currBidValue = announcerBidValue;


                informPlayersAboutBid(announcer, announcerBidValue);
                emitMove(new BidEvent(announcer, announcerBidValue));

                setActivePlayer(hearer);
                if (getPlayerInstance(hearer).holdBid(currBidValue)) {

                    log.debug("hearer holds " + currBidValue);

                    // hearing hand holds bid

                    informPlayersAboutBid(hearer, announcerBidValue);
                    emitMove(new HoldBidEvent(hearer, announcerBidValue));

                } else {

                    log.debug("hearer passed at " + announcerBidValue);

                    // hearing hand passed
                    hearerPassed = true;

                    emitMove(new PassBidEvent(hearer, data.getNextBidValue()));
                }
            } else {

                log.debug("announcer passed at " + nextBidValue);

                // announcing hand passes
                announcerPassed = true;

                emitMove(new PassBidEvent(announcer, data.getNextBidValue()));
            }
        }

        return currBidValue;
    }

    private Player getBiddingWinner(final Player announcer, final Player hearer) {

        Player biddingWinner = null;

        if (data.isPlayerPass(announcer)) {
            biddingWinner = hearer;
        } else if (data.isPlayerPass(hearer)) {
            biddingWinner = announcer;
        }

        return biddingWinner;
    }

    private void discarding() {

        final JSkatPlayer activePlayerInstance = getActivePlayerInstance();

        log.info("Player " + activePlayer + " looks into the skat...");
        log.info("Skat before discarding: " + data.getSkat());

        final CardList skatBefore = data.getSkat().getImmutableCopy();
        presentation.pickup(skatBefore);

        // create a clone of the skat before sending it to the player
        // otherwise the player could change the skat after discarding
        activePlayerInstance.takeSkat(new CardList(skatBefore));

        emitMove(new PickUpSkatEvent(activePlayer, skatBefore));

        // ask player for the cards to be discarded
        // cloning is done to prevent the player
        // from manipulating the skat afterward
        final CardList returnedSkat = activePlayerInstance.discardSkat();
        final CardList discardedSkat = returnedSkat == null ? null : returnedSkat.getImmutableCopy();

        if (!checkDiscardedCards(activePlayer, discardedSkat)) {
            if (strict) illegal("discard");
            presentation.illegalDiscard(activePlayerInstance, discardedSkat);
            endGameBecauseOfSchwarzPlaying(activePlayer);
        } else {
            log.info("Discarded cards: " + discardedSkat);

            emitMove(new DiscardSkatEvent(activePlayer, discardedSkat));
            presentation.discard(discardedSkat);
        }
    }

    private boolean checkDiscardedCards(final Player player, final CardList discardedSkat) {

        boolean result = discardedSkat != null && discardedSkat.size() == 2
                && discardedSkat.get(0) != null && discardedSkat.get(1) != null
                && discardedSkat.get(0) != discardedSkat.get(1);
        if (!result) return false;
        if (strict && (!data.getPlayerCards(player).contains(discardedSkat.get(0))
                || !data.getPlayerCards(player).contains(discardedSkat.get(1)))) return false;

        if (GameState.SCHIEBERAMSCH == getGameState()
                && !policy.jacksInSkat()
                && (discardedSkat.get(0).getRank() == Rank.JACK || discardedSkat.get(1).getRank() == Rank.JACK)) {
            log.error("Player is fooling!!! Jacks are not allowed to be discarded.");
            result = false;
        }

        return result;
    }

    private void announceGame() {
        log.info("Entering announceGame() method.");
        log.debug("declaring game...");

        final var contract = getPlayerInstance(data.getDeclarer()).announceGame();
        if (contract != null) {
            if (strict && (!GameType.GRAND_SUIT_NULL.contains(contract.gameType())
                    || contract.hand() == data.isSkatPickedUp()
                    || (contract.ouvert() && !sameCards(contract.ouvertCards(), data.getPlayerCards(data.getDeclarer()))))) illegal("contract");
            if (contract.hand()) {
                setGameAnnouncement(new GameAnnouncement(contract));
            } else {
                setGameAnnouncement(new GameAnnouncement(contract, data.getSkat()));
            }
        } else {
            if (strict) illegal("contract");
            presentation.invalidContract(contract);
        }

        doSleep(maxSleep);
    }

    private void playTricks() {

        setGameState(GameState.TRICK_PLAYING);
        for (int trickNo = 0; trickNo < 10; trickNo++) {

            log.debug("=============== Play trick " + (trickNo + 1) + " ===============");
            doSleep(maxSleep);

            final Player trickForehand = getTrickForeHand(trickNo);
            setActivePlayer(trickForehand);

            informPlayersAboutNewTrick(trickNo, trickForehand);

            // Ask players for their cards
            log.debug("fore hand plays");
            if (isContraEnabledForPlayer(activePlayer, ContraCallingTime.BEFORE_FIRST_CARD, trickNo)) {
                setGameState(GameState.CONTRA);
                contraRe();
                setGameState(GameState.TRICK_PLAYING);
            }

            playCard(trickForehand, null, activePlayer);

            if (data.isGameFinished()) {
                break;
            }

            doSleep(maxSleep);

            log.debug("middle hand plays");
            setActivePlayer(activePlayer.getLeftNeighbor());
            if (isContraEnabledForPlayer(activePlayer, ContraCallingTime.BEFORE_FIRST_CARD, trickNo)) {
                setGameState(GameState.CONTRA);
                contraRe();
                setGameState(GameState.TRICK_PLAYING);
            }

            playCard(trickForehand, data.getCurrentTrick().getFirstCard(), activePlayer);

            if (data.isGameFinished()) {
                break;
            }

            doSleep(maxSleep);

            log.debug("rear hand plays");
            setActivePlayer(activePlayer.getLeftNeighbor());
            if (isContraEnabledForPlayer(activePlayer, ContraCallingTime.BEFORE_FIRST_CARD, trickNo)) {
                setGameState(GameState.CONTRA);
                contraRe();
                setGameState(GameState.TRICK_PLAYING);
            }

            playCard(trickForehand, data.getCurrentTrick().getFirstCard(), activePlayer);

            if (data.isGameFinished()) {
                break;
            }

            doSleep(maxSleep);

            final Trick lastTrick = data.getLastCompletedTrick();

            informPlayersAboutCompletedTrick(lastTrick);

            // Check for preliminary ending of a null game
            if (GameType.NULL.equals(data.getGameType())) {

                if (lastTrick.getTrickWinner() == data.getDeclarer()) {
                    // declarer has won a trick
                    setGameState(GameState.PRELIMINARY_GAME_END);
                }
            }

            log.debug("Trick cards: " + lastTrick.getCardList());
            logPlayerPoints();

            if (getActivePlayerInstance().isAIPlayer()) {
                presentation.afterTrick();
            }

            if (data.isGameFinished()) {
                break;
            }
        }

        addSkatPointsToPlayerPoints();

        // set schneider/schwarz/jungfrau/durchmarsch flags
        switch (data.getGameType()) {
            case CLUBS:
            case SPADES:
            case HEARTS:
            case DIAMONDS:
            case GRAND:
                data.setSchneiderSchwarz();
                break;
            case RAMSCH:
                data.setJungfrauDurchmarsch();
                break;
            case NULL:
            case PASSED_IN:
                // do nothing
                break;
        }
    }

    private Boolean isContraPlayEnabled(final ContraCallingTime gameTime, final int trickNo) {

        if (!GameVariant.FORCED_RAMSCH.equals(variant) && policy.contra()
                && policy.contraTime() == gameTime && isGameWithDeclarer()) {
            if (ContraCallingTime.AFTER_GAME_ANNOUNCEMENT == gameTime) {
                return true;
            } else {
                return ContraCallingTime.BEFORE_FIRST_CARD == gameTime && trickNo == 0;
            }
        }
        return false;
    }

    private Boolean isGameWithDeclarer() {
        final GameType gameType = data.getGameType();
        return gameType == GameType.CLUBS || gameType == GameType.SPADES || gameType == GameType.HEARTS
                || gameType == GameType.DIAMONDS || gameType == GameType.GRAND || gameType == GameType.NULL;
    }

    private Boolean isContraEnabledForPlayer(final Player player, final ContraCallingTime gameTime, final int trickNo) {
        return isContraPlayEnabled(gameTime, trickNo) && isNoContraCalledYet() && isPlayerOpponent(player)
                && isPlayerBidHighEnoughForContra(player);
    }

    private boolean isPlayerBidHighEnoughForContra(final Player player) {

        if (false && data.getMaxPlayerBid(player) > 0) {
            return true;
        }
        return true;
    }

    private boolean isPlayerOpponent(final Player player) {
        return player != data.getDeclarer();
    }

    private boolean isNoContraCalledYet() {
        return !data.isContra();
    }

    private void informPlayersAboutCompletedTrick(final Trick trick) {
        for (final Player currPosition : Player.getOrderedList()) {
            getPlayerInstance(currPosition).showTrick((Trick) trick.clone());
        }
    }

    private void informPlayersAboutNewTrick(final int trickNo, final Player trickForehand) {
        for (final Player currPosition : Player.getOrderedList()) {
            getPlayerInstance(currPosition).newTrick(trickNo, trickForehand);
        }
    }

    private Player getTrickForeHand(final int currentTrickNo) {
        Player trickForeHand = null;
        if (currentTrickNo == 0) {
            // first trick
            trickForeHand = Player.FOREHAND;
        } else {
            // get last trick winner as fore hand of next trick
            trickForeHand = data.getTrickWinner(currentTrickNo - 1);
        }
        return trickForeHand;
    }

    private void logPlayerPoints() {
        log.debug("Points: fore hand: " + data.getPlayerPoints(Player.FOREHAND) +
                " middle hand: "
                + data.getPlayerPoints(Player.MIDDLEHAND) + " rear hand: "
                + data.getPlayerPoints(Player.REARHAND));
    }

    private void addSkatPointsToPlayerPoints() {
        log.debug("Skat: " + data.getSkat());
        if (data.getGameType() == GameType.RAMSCH) {
            addSkatPointsToPlayerPointsInRamschGames();
        } else {
            // for all the other games, points to the declarer
            data.addPlayerPoints(data.getDeclarer(), data.getSkat().getTotalValue());
        }
        logPlayerPoints();
    }

    private void addSkatPointsToPlayerPointsInRamschGames() {
        if (policy.ramschSkatOwner() == RamschSkatOwner.LAST_TRICK) {
            try {
                final Player lastTrickWinner = data.getLastTrickWinner();
                if (lastTrickWinner != null) {
                    log.debug("Skat cards (" + data.getSkat().getTotalValue() + " points) are added to player @ "
                            + lastTrickWinner + " (= last trick)");
                    data.addPlayerPoints(lastTrickWinner, data.getSkat().getTotalValue());
                }
            } catch (final IllegalArgumentException exception) {
                // IllegalArgumentException can be thrown if a game was ended
                // preliminary by a player playing Schwarz
                log.warn("Skat cards cannot be added to winner of final trick - trick winner is unknown");
            }
        } else if (policy.ramschSkatOwner() == RamschSkatOwner.LOSER) {
            int maxPoints = -1;
            Player looser = null;
            for (final Player player : Player.values()) {
                final int playerPoints = data.getPlayerPoints(player);
                if (playerPoints > maxPoints) {
                    maxPoints = playerPoints;
                    looser = player;
                }
            }
            data.addPlayerPoints(looser, data.getSkat().getTotalValue());
        }
    }

    private void playCard(final Player trickForeHand, final Card firstTrickCard, final Player currPlayer) {

        Card playedCard = null;
        final JSkatPlayer skatPlayer = getActivePlayerInstance();

        boolean cardAccepted = false;
        boolean aiPlayerPlayedSchwarz = false;

        while (!cardAccepted && !aiPlayerPlayedSchwarz) {

            try {
                // ask player for the next card
                playedCard = skatPlayer.playCard();
            } catch (final Exception exp) {
                if (strict) throw exp;
                log.error("Exception thrown by player " + skatPlayer + " playing " + currPlayer + ": " + exp);
                if (!skatPlayer.isHumanPlayer()) {
                    aiPlayerPlayedSchwarz = true;
                }
            }

            log.debug(playedCard + " " + data);

            if (isCardSchwarzPlay(skatPlayer, currPlayer, firstTrickCard, playedCard)) {
                if (strict) illegal("card " + playedCard);
                if (skatPlayer.isHumanPlayer()) {
                    presentation.illegalCard(skatPlayer, playedCard);
                } else {
                    presentation.illegalCard(skatPlayer, playedCard);
                    aiPlayerPlayedSchwarz = true;
                }
            } else {
                cardAccepted = true;
            }
        }

        if (playedCard != null) {
            // TODO: code duplication with SkatGameReplayer.oneStepForward()
            if (data.getCurrentTrick() != null && data.getCurrentTrick().getFirstCard() == null) {
                // TODO: post it into the main event bus
                presentation.trick(data.getLastCompletedTrick());
            }

            emitMove(new TrickCardPlayedEvent(currPlayer, playedCard));

            for (final JSkatPlayer playerInstance : player.values()) {
                // inform all players
                // cloning of card is not necessary, because Card is immutable
                playerInstance.cardPlayed(currPlayer, playedCard);
            }

            log.debug("playing card " + playedCard);
        }

        if (aiPlayerPlayedSchwarz) {
            // end game immediately
            endGameBecauseOfSchwarzPlaying(currPlayer);
        }
    }

    private void endGameBecauseOfSchwarzPlaying(final Player schwarzPlayer) {
        data.getResult().setSchwarz(true);
        // declarer played schwarz
        // opponent played schwarz
        data.getResult().setWon(!schwarzPlayer.equals(data.getDeclarer()));
        data.setGameState(GameState.PRELIMINARY_GAME_END);
    }

    private boolean isCardSchwarzPlay(final JSkatPlayer skatPlayer, final Player position, final Card firstTrickCard,
                                      final Card playedCard) {
        boolean isSchwarz = false;
        if (playedCard == null) {

            log.error("Player is fooling!!! Did not play a card!");
            isSchwarz = true;

        } else if (!playerHasCard(position, playedCard)) {

            log.error("Player (" + skatPlayer + ") is fooling!!! Doesn't have card " + playedCard + "!");
            isSchwarz = true;

        } else if (!rules.isCardAllowed(data.getGameType(), firstTrickCard, data.getPlayerCards(position),
                playedCard)) {

            log.error(
                    "Player " + skatPlayer.getClass() + " card not allowed: " + playedCard + " game type: "
                            + data.getGameType() + " first trick card: "
                            + firstTrickCard + " player cards: "
                            + data.getPlayerCards(position));
            isSchwarz = true;
        }

        return isSchwarz;
    }

    private JSkatPlayer getActivePlayerInstance() {
        return player.get(activePlayer);
    }

    private JSkatPlayer getPlayerInstance(final Player position) {
        return player.get(position);
    }

    /**
     * Checks whether a player has the card on it's hand or not
     *
     * @param card Card to check
     * @return TRUE if the card is on player's hand
     */
    private boolean playerHasCard(final Player player, final Card card) {

        boolean result = false;

        log.info("Player {} has card: player cards: {} card to check: {}", player, data.getPlayerCards(player), card);

        for (final Card handCard : data.getPlayerCards(player)) {

            if (handCard.equals(card)) {

                result = true;
                break;
            }
        }

        return result;
    }

    private void calculateGameValue() {

        log.info("Calculating game value");

        // FIXME (jan 07.12.2010) don't let a data class calculate it's values
        data.calcResult();

        log.info("game value={}, bid value={}", data.getResult(), data.getMaxBidValue());
        log.info("Final game result: lost:{} game value: {}", data.isGameLost(), data.getResult());
        log.info("Final result: {}/{}", data.getDeclarerScore(), data.getOpponentScore());

        for (final JSkatPlayer playerInstance : player.values()) {
            GameSummary summary = data.getGameSummary();
            if (strict) {
                summary.gameResult = summary.gameResult.clone();
                summary.tricks = summary.tricks.stream().map(t -> (Trick) t.clone()).toList();
            }
            playerInstance.setGameSummary(summary);
            playerInstance.finalizeGame();
        }

        doSleep(maxSleep);
    }

    private void doSleep(int milliseconds) { presentation.pause(milliseconds); }

    /**
     * Sets a new logger for the skat game
     *
     * @param newLogger New logger
     */
    public void setLogger(final Logger newLogger) {
        log = newLogger;
    }

    /**
     * Sets the cards from outside
     *
     * @param newDeck Card deck
     */
    public void setCardDeck(final CardDeck newDeck) {

        deck = newDeck;
    }

    /**
     * Sets the game announcement from the outside
     *
     * @param announcement Game announcement
     */
    public void setGameAnnouncement(final GameAnnouncement announcement) {

        GameAnnouncement retained = strict
                ? new GameAnnouncement(copyContract(announcement.contract()), new CardList(announcement.discardedCards()))
                : announcement;
        emitMove(new GameAnnouncementEvent(data.getDeclarer(), retained));
        rules = SkatRuleFactory.getSkatRules(data.getGameType());

        // inform all players
        for (final JSkatPlayer playerInstance : player.values()) {
            playerInstance.startGame(data.getDeclarer(), strict ? copyContract(retained.contract()) : retained.contract());
        }

        log.info(".setGameAnnouncement(): " + data.getAnnouncement() + " by " + data.getDeclarer() + ", rules=" + rules);
    }

    /**
     * Sets the game state.
     *
     * @param newState Game state
     */
    public void setGameState(final GameState newState) {

        data.setGameState(newState);
        facts.accept("phase", newState.name());

        for (final JSkatPlayer playerInstance : player.values()) {
            playerInstance.setGameState(newState);
        }

        presentation.phase(newState, data);
    }

    /**
     * Sets the single player from outside
     *
     * @param declarer Declarer
     */
    public void setDeclarer(final Player declarer) {
        data.setDeclarer(declarer);
        facts.accept("declarer", declarer.name());
        presentation.declarer(declarer);
    }

    /**
     * Gets the single player
     *
     * @return Single player
     */
    public Player getDeclarer() {
        return data.getDeclarer();
    }

    /**
     * Gets whether a game was won or not
     *
     * @return TRUE if the game was won
     */
    public boolean isGameWon() {

        return data.isGameWon();
    }

    /**
     * Gets the maximum sleep time
     *
     * @return Maximum sleep time
     */
    public int getMaxSleep() {

        return maxSleep;
    }

    /**
     * Sets the maximum sleep time
     *
     * @param newMaxSleep Maximum sleep time
     */
    public void setMaxSleep(final int newMaxSleep) {

        maxSleep = newMaxSleep;
    }

    /**
     * Gets the game result
     *
     * @return Game result
     */
    public SkatGameResult getGameResult() {

        return data.getGameResult();
    }

    /**
     * Gets the game moves.
     *
     * @return List of game moves
     */
    public List<SkatGameEvent> getGameMoves() {
        return data.getGameMoves();
    }

    /**
     * Gets a summary of the game
     *
     * @return Game summary
     */
    public GameSummary getGameSummary() {
        return data.getGameSummary();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {

        return data.getGameState().toString();
    }

    /**
     * Gets the game announcement
     *
     * @return Game announcement
     */
    public GameAnnouncement getGameAnnouncement() {
        return data.getAnnouncement();
    }

    /**
     * Gets the game state
     *
     * @return Game state
     */
    public GameState getGameState() {
        return data.getGameState();
    }

}
