/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.newalgorithm;

import java.util.ArrayList;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;

/**
 * Situation
 * 
 * @author Daniel Loreck
 * 
 */
public class Situation {
	private Suit			oTrumpSuit;
	private GameType		oGameType;
	private ArrayList<Suit>	oFreeSuitsLeftPlayer;
	private ArrayList<Suit>	oFreeSuitsRightPlayer;
	private boolean			oHasTrumpLeftPlayer;
	private boolean			oHasTrumpRightPlayer;
	
	private Suit			oLongestSuit;
	private ArrayList<Suit>	oBlankSuits;
	private int				oRandomInt;
	
	public Situation(ImmutablePlayerKnowledge pKnowledge, GameType pGameType) {
		oTrumpSuit				= pGameType.getTrumpSuit();
		oGameType				= pGameType;
		oFreeSuitsLeftPlayer	= new ArrayList<Suit>();
		oFreeSuitsRightPlayer	= new ArrayList<Suit>();
		oHasTrumpLeftPlayer		= true;
		oHasTrumpRightPlayer	= true;
		
		setCardsAfterDiscarding(pKnowledge.getOwnCards());
		
		oRandomInt				= (int)(Math.random()*3);
	}
	
	public Situation(Suit pTrumpSuit, GameType pGameType, Suit pLongestSuit, ArrayList<Suit> pBlankSuit, int pRandomInt, boolean hasTrumpLeftPlayer, boolean hasTrumpRightPlayer
			, ArrayList<Suit> pFreeSuitsLeftPlayer, ArrayList<Suit> pFreeSuitsRightPlayer) {
		oTrumpSuit				= pTrumpSuit;
		oGameType				= pGameType;
		oLongestSuit			= pLongestSuit;
		oBlankSuits				= pBlankSuit;
		oRandomInt				= pRandomInt;
		oHasTrumpLeftPlayer		= hasTrumpLeftPlayer;
		oHasTrumpRightPlayer	= hasTrumpRightPlayer;
		oFreeSuitsLeftPlayer	= pFreeSuitsLeftPlayer;
		oFreeSuitsRightPlayer	= pFreeSuitsRightPlayer;
	}


// -------------------------
// Methods
	public void setCardsAfterDiscarding(CardList pOwnCards) {
		oLongestSuit			= pOwnCards.getMostFrequentSuit();
		oBlankSuits				= new ArrayList<Suit>();
		for(Suit s : Suit.values()) {
			if(pOwnCards.getSuitCount(s, false) == 0)
				oBlankSuits.add(s);
		}
	}

// -------------------------
// GETTER
	public Suit getTrumpSuit() {
		return oTrumpSuit;
	}
	public GameType getGameType() {
		return oGameType;
	}
	public Suit getLongestSuit() {
		return oLongestSuit;
	}
	public ArrayList<Suit> getBlankSuits() {
		return oBlankSuits;
	}
	public int getRandomInt() {
		return oRandomInt;
	}
	
// -------------------------
// LEFT PLAYER
	public void setLeftPlayerBlankColor(Suit pSuit) {
		oFreeSuitsLeftPlayer.add(pSuit);
	}
	public void setLeftPlayerBlankOnTrump() {
		oHasTrumpLeftPlayer = true;
	}
	public ArrayList<Suit> getLeftPlayerBlankSuits() {
		return oFreeSuitsLeftPlayer;
	}
	public boolean isLeftPlayerBlankOnColor(Suit pSuit) {
		return oFreeSuitsLeftPlayer.contains(pSuit);
	}
	public boolean leftPlayerHasTrump() {
		return oHasTrumpLeftPlayer;
	}

// -------------------------
// RIGHT PLAYER
	public void setRightPlayerBlankColor(Suit pSuit) {
		oFreeSuitsRightPlayer.add(pSuit);
	}
	public void setRightPlayerBlankOnTrump() {
		oHasTrumpRightPlayer = true;
	}
	public ArrayList<Suit> getRightPlayerBlankSuits() {
		return oFreeSuitsRightPlayer;
	}
	public boolean isRightPlayerBlankOnColor(Suit pSuit) {
		return oFreeSuitsRightPlayer.contains(pSuit);
	}
	public boolean rightPlayerHasTrump() {
		return oHasTrumpRightPlayer;
	}
}
