package org.jskat.ai.newalgorithm;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;

import java.util.ArrayList;

/**
 * Situation
 *
 * @author Daniel Loreck
 */
public class Situation {
    private Suit oTrumpSuit;
    private GameType oGameType;
    private ArrayList<Suit> oFreeSuitsLeftPlayer;
    private ArrayList<Suit> oFreeSuitsRightPlayer;
    private boolean oHasTrumpLeftPlayer;
    private boolean oHasTrumpRightPlayer;

    private Suit oLongestSuit;
    private ArrayList<Suit> oBlankSuits;
    private int oRandomInt;

    /**
     * Performs situation.
     * @param pKnowledge p knowledge value.
     * @param pGameType p game type value.
     */
    public Situation(ImmutablePlayerKnowledge pKnowledge, GameType pGameType) {
        oTrumpSuit = pGameType.getTrumpSuit();
        oGameType = pGameType;
        oFreeSuitsLeftPlayer = new ArrayList<Suit>();
        oFreeSuitsRightPlayer = new ArrayList<Suit>();
        oHasTrumpLeftPlayer = true;
        oHasTrumpRightPlayer = true;

        setCardsAfterDiscarding(pKnowledge.getOwnCards());

        oRandomInt = (int) (Math.random() * 3);
    }

    /**
     * Performs situation.
     * @param pTrumpSuit p trump suit value.
     * @param pGameType p game type value.
     * @param pLongestSuit p longest suit value.
     * @param pBlankSuit p blank suit value.
     * @param pRandomInt p random int value.
     * @param hasTrumpLeftPlayer has trump left player value.
     * @param hasTrumpRightPlayer has trump right player value.
     * @param pFreeSuitsLeftPlayer p free suits left player value.
     * @param pFreeSuitsRightPlayer p free suits right player value.
     */
    public Situation(Suit pTrumpSuit, GameType pGameType, Suit pLongestSuit, ArrayList<Suit> pBlankSuit, int pRandomInt, boolean hasTrumpLeftPlayer, boolean hasTrumpRightPlayer
            , ArrayList<Suit> pFreeSuitsLeftPlayer, ArrayList<Suit> pFreeSuitsRightPlayer) {
        oTrumpSuit = pTrumpSuit;
        oGameType = pGameType;
        oLongestSuit = pLongestSuit;
        oBlankSuits = pBlankSuit;
        oRandomInt = pRandomInt;
        oHasTrumpLeftPlayer = hasTrumpLeftPlayer;
        oHasTrumpRightPlayer = hasTrumpRightPlayer;
        oFreeSuitsLeftPlayer = pFreeSuitsLeftPlayer;
        oFreeSuitsRightPlayer = pFreeSuitsRightPlayer;
    }


    // -------------------------
// Methods
    /**
     * Sets cards after discarding.
     * @param pOwnCards p own cards value.
     */
    public void setCardsAfterDiscarding(CardList pOwnCards) {
        oLongestSuit = pOwnCards.getMostFrequentSuit();
        oBlankSuits = new ArrayList<Suit>();
        for (Suit s : Suit.values()) {
            if (pOwnCards.getSuitCount(s, false) == 0)
                oBlankSuits.add(s);
        }
    }

    // -------------------------
// GETTER
    /**
     * Returns or indicates trump suit.
     * @return get trump suit result.
     */
    public Suit getTrumpSuit() {
        return oTrumpSuit;
    }

    /**
     * Returns or indicates game type.
     * @return get game type result.
     */
    public GameType getGameType() {
        return oGameType;
    }

    /**
     * Returns or indicates longest suit.
     * @return get longest suit result.
     */
    public Suit getLongestSuit() {
        return oLongestSuit;
    }

    /**
     * Returns or indicates blank suits.
     * @return get blank suits result.
     */
    public ArrayList<Suit> getBlankSuits() {
        return oBlankSuits;
    }

    /**
     * Returns or indicates random int.
     * @return get random int result.
     */
    public int getRandomInt() {
        return oRandomInt;
    }

    // -------------------------
// LEFT PLAYER
    /**
     * Sets left player blank color.
     * @param pSuit p suit value.
     */
    public void setLeftPlayerBlankColor(Suit pSuit) {
        oFreeSuitsLeftPlayer.add(pSuit);
    }

    /**
     * Sets left player blank on trump.
     */
    public void setLeftPlayerBlankOnTrump() {
        oHasTrumpLeftPlayer = true;
    }

    /**
     * Returns or indicates left player blank suits.
     * @return get left player blank suits result.
     */
    public ArrayList<Suit> getLeftPlayerBlankSuits() {
        return oFreeSuitsLeftPlayer;
    }

    /**
     * Returns or indicates left player blank on color.
     * @param pSuit p suit value.
     * @return is left player blank on color result.
     */
    public boolean isLeftPlayerBlankOnColor(Suit pSuit) {
        return oFreeSuitsLeftPlayer.contains(pSuit);
    }

    /**
     * Performs left player has trump.
     * @return left player has trump result.
     */
    public boolean leftPlayerHasTrump() {
        return oHasTrumpLeftPlayer;
    }

    // -------------------------
// RIGHT PLAYER
    /**
     * Sets right player blank color.
     * @param pSuit p suit value.
     */
    public void setRightPlayerBlankColor(Suit pSuit) {
        oFreeSuitsRightPlayer.add(pSuit);
    }

    /**
     * Sets right player blank on trump.
     */
    public void setRightPlayerBlankOnTrump() {
        oHasTrumpRightPlayer = true;
    }

    /**
     * Returns or indicates right player blank suits.
     * @return get right player blank suits result.
     */
    public ArrayList<Suit> getRightPlayerBlankSuits() {
        return oFreeSuitsRightPlayer;
    }

    /**
     * Returns or indicates right player blank on color.
     * @param pSuit p suit value.
     * @return is right player blank on color result.
     */
    public boolean isRightPlayerBlankOnColor(Suit pSuit) {
        return oFreeSuitsRightPlayer.contains(pSuit);
    }

    /**
     * Performs right player has trump.
     * @return right player has trump result.
     */
    public boolean rightPlayerHasTrump() {
        return oHasTrumpRightPlayer;
    }
}
