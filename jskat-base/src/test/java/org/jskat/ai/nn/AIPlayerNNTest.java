/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn;

import java.util.Arrays;
import java.util.List;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.junit.Ignore;

/**
 * Test class for {@link AIPlayerNN}
 */
@Ignore
public class AIPlayerNNTest extends AbstractJSkatTest {

	List<Card> nullOrder = Arrays.asList(Card.D7, Card.D8, Card.D9, Card.DT,
			Card.DJ, Card.DQ, Card.DK, Card.DA, Card.H7, Card.H8, Card.H9,
			Card.HT, Card.HJ, Card.HQ, Card.HK, Card.HA, Card.S7, Card.S8,
			Card.S9, Card.ST, Card.SJ, Card.SQ, Card.SK, Card.SA, Card.C7,
			Card.C8, Card.C9, Card.CT, Card.CJ, Card.CQ, Card.CK, Card.CA);

	List<Card> grandOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ,
			Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT, Card.DA,
			Card.H7, Card.H8, Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA,
			Card.S7, Card.S8, Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA,
			Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA);

	List<Card> clubsOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ,
			Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA,
			Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT, Card.DA,
			Card.H7, Card.H8, Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA,
			Card.S7, Card.S8, Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA);

	List<Card> spadesOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ,
			Card.S7, Card.S8, Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA,
			Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT, Card.DA,
			Card.H7, Card.H8, Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA,
			Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA);

	List<Card> heartsOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ,
			Card.H7, Card.H8, Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA,
			Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT, Card.DA,
			Card.S7, Card.S8, Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA,
			Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA);

	List<Card> diamondsOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ,
			Card.DJ, Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT,
			Card.DA, Card.H7, Card.H8, Card.H9, Card.HQ, Card.HK, Card.HT,
			Card.HA, Card.S7, Card.S8, Card.S9, Card.SQ, Card.SK, Card.ST,
			Card.SA, Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT,
			Card.CA);

	// /**
	// * Tests the calculation of the net input index
	// */
	// @Test
	// public void testGetNetInputIndexForNullGame() {
	//
	// for (Card card : Card.values()) {
	//
	// int index = AIPlayerNN.getNetInputIndex(GameType.NULL, card);
	// assertEquals(
	//					"Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
	// }
	// }
	//
	// /**
	// * Tests the calculation of the net input index
	// */
	// @Test
	// public void testGetNetInputIndexForGrandGame() {
	//
	// for (Card card : Card.values()) {
	//
	// int index = AIPlayerNN.getNetInputIndex(GameType.GRAND, card);
	// assertEquals(
	//					"Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
	// }
	// }
	//
	// /**
	// * Tests the calculation of the net input index
	// */
	// @Test
	// public void testGetNetInputIndexForRamschGame() {
	//
	// for (Card card : Card.values()) {
	//
	// int index = AIPlayerNN.getNetInputIndex(GameType.RAMSCH, card);
	// assertEquals(
	//					"Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
	// }
	// }
	//
	// /**
	// * Tests the calculation of the net input index
	// */
	// @Test
	// public void testGetNetInputIndexForClubsGame() {
	//
	// for (Card card : Card.values()) {
	//
	// int index = AIPlayerNN.getNetInputIndex(GameType.CLUBS, card);
	// assertEquals(
	//					"Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
	// }
	// }
	//
	// /**
	// * Tests the calculation of the net input index
	// */
	// @Test
	// public void testGetNetInputIndexForSpadesGame() {
	//
	// for (Card card : Card.values()) {
	//
	// int index = AIPlayerNN.getNetInputIndex(GameType.SPADES, card);
	// assertEquals(
	//					"Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
	// }
	// }
	//
	// /**
	// * Tests the calculation of the net input index
	// */
	// @Test
	// public void testGetNetInputIndexForHeartsGame() {
	//
	// for (Card card : Card.values()) {
	//
	// int index = AIPlayerNN.getNetInputIndex(GameType.HEARTS, card);
	// assertEquals(
	//					"Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
	// }
	// }
	//
	// /**
	// * Tests the calculation of the net input index
	// */
	// @Test
	// public void testGetNetInputIndexForDiamondssGame() {
	//
	// for (Card card : Card.values()) {
	//
	// int index = AIPlayerNN.getNetInputIndex(GameType.DIAMONDS, card);
	// assertEquals(
	//					"Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
	// }
	// }
	//
	// /**
	// * Tests the setting of the input values for a declarer net
	// */
	// @Test
	// public void testSetDeclarerKnowCards_DeclarerNeurons() {
	//
	// GameAnnouncementFactory factory = GameAnnouncement.getFactory();
	// factory.setGameType(GameType.GRAND);
	// factory.setDiscardedCards(new CardList(null, null));
	// GameAnnouncement announcement = factory.getAnnouncement();
	//
	// AIPlayerNN player = new AIPlayerNN();
	// player.preparateForNewGame();
	// player.newGame(Player.FOREHAND);
	// player.startGame();
	//
	// player.startGame(Player.FOREHAND, announcement);
	// player.newTrick(new Trick(0, Player.FOREHAND));
	//
	// double[] inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[363], 0.0);
	//
	// player.startGame(Player.MIDDLEHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[0], 0.0);
	//
	// player.startGame(Player.REARHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[726], 0.0);
	//
	// player.newGame(Player.MIDDLEHAND);
	//
	// player.startGame(Player.FOREHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[726], 0.0);
	//
	// player.startGame(Player.MIDDLEHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[363], 0.0);
	//
	// player.startGame(Player.REARHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[0], 0.0);
	//
	// player.newGame(Player.REARHAND);
	//
	// player.startGame(Player.FOREHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[0], 0.0);
	//
	// player.startGame(Player.MIDDLEHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[726], 0.0);
	//
	// player.startGame(Player.REARHAND, announcement);
	// inputs = player.getNetInputs(Card.CJ);
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[363], 0.0);
	// }
	//
	// /**
	// * Tests the setting of the input values for a declarer net
	// */
	// @Test
	// @Ignore
	// public void testSetDeclarerKnowCards_PlayedCards() {
	//
	// GameAnnouncementFactory factory = GameAnnouncement.getFactory();
	// factory.setGameType(GameType.GRAND);
	// factory.setDiscardedCards(new CardList(null, null));
	// GameAnnouncement announcement = factory.getAnnouncement();
	//
	// AIPlayerNN player = new AIPlayerNN();
	// player.preparateForNewGame();
	// player.newGame(Player.REARHAND);
	// player.startGame();
	//
	// player.startGame(Player.FOREHAND, announcement);
	// player.newTrick(new Trick(0, Player.FOREHAND));
	// player.cardPlayed(Player.FOREHAND, Card.SJ);
	// player.cardPlayed(Player.MIDDLEHAND, Card.HJ);
	//
	// double[] inputs = player.getNetInputs(Card.CJ);
	//
	// // game declarer for player FOREHAND
	// assertEquals(AIPlayerNN.ACTIVE, inputs[0], 0.0);
	// // trick fore hand for player FOREHAND
	// assertEquals(AIPlayerNN.ACTIVE, inputs[1], 0.0);
	// // Club Jack for player FOREHAND
	// assertEquals(0.0, inputs[2], 0.0);
	// // Spade Jack for player FOREHAND
	// assertEquals(AIPlayerNN.PLAYED_CARD, inputs[3], 0.0);
	// // Heart Jack for player FOREHAND
	// assertEquals(0.0, inputs[4], 0.0);
	// // Diamond Jack for player FOREHAND
	// assertEquals(0.0, inputs[5], 0.0);
	//
	// // game declarer for player MIDDLEHAND
	// assertEquals(0.0, inputs[726], 0.0);
	// // trick fore hand for player MIDDLEHAND
	// assertEquals(0.0, inputs[1 + 726], 0.0);
	// // Club Jack for player MIDDLEHAND
	// assertEquals(0.0, inputs[2 + 726], 0.0);
	// // Spade Jack for player MIDDLEHAND
	// assertEquals(0.0, inputs[3 + 726], 0.0);
	// // Heart Jack for player MIDDLEHAND
	// assertEquals(AIPlayerNN.PLAYED_CARD, inputs[4 + 726], 0.0);
	// // Diamond Jack for player MIDDLEHAND
	// assertEquals(0.0, inputs[5 + 726], 0.0);
	//
	// // game declarer for player REARHAND
	// assertEquals(0.0, inputs[363], 0.0);
	// // trick fore hand for player MIDDLEHAND
	// assertEquals(0.0, inputs[1 + 363], 0.0);
	// // Club Jack for player MIDDLEHAND
	// assertEquals(AIPlayerNN.PLAYED_CARD_IN_TRICK, inputs[2 + 363], 0.0);
	// // Spade Jack for player MIDDLEHAND
	// assertEquals(0.0, inputs[3 + 363], 0.0);
	// // Heart Jack for player MIDDLEHAND
	// assertEquals(0.0, inputs[4 + 363], 0.0);
	// // Diamond Jack for player MIDDLEHAND
	// assertEquals(0.0, inputs[5 + 363], 0.0);
	// }
	//
	// /**
	// * Tests the setting of the input values for a declarer net
	// */
	// @Test
	// @Ignore
	// public void testSetDeclarerKnowCards_PlayedCardsTwoTricks() {
	//
	// GameAnnouncementFactory factory = GameAnnouncement.getFactory();
	// factory.setGameType(GameType.GRAND);
	// factory.setDiscardedCards(new CardList(null, null));
	// GameAnnouncement announcement = factory.getAnnouncement();
	//
	// AIPlayerNN player = new AIPlayerNN();
	// player.preparateForNewGame();
	// player.newGame(Player.REARHAND);
	// player.startGame();
	//
	// player.startGame(Player.FOREHAND, announcement);
	// player.newTrick(new Trick(0, Player.FOREHAND));
	// player.cardPlayed(Player.FOREHAND, Card.SJ);
	// player.cardPlayed(Player.MIDDLEHAND, Card.HJ);
	// player.cardPlayed(Player.REARHAND, Card.CJ);
	// Trick completeTrick = new Trick(0, Player.FOREHAND);
	// completeTrick.addCard(Card.SJ);
	// completeTrick.addCard(Card.HJ);
	// completeTrick.addCard(Card.CJ);
	// completeTrick.setTrickWinner(Player.REARHAND);
	// player.showTrick(completeTrick);
	// player.newTrick(new Trick(1, Player.REARHAND));
	//
	// double[] inputs = player.getNetInputs(Card.DJ);
	//
	// // game declarer for player FOREHAND
	// assertEquals(AIPlayerNN.ACTIVE, inputs[0], 0.0);
	// // trick fore hand for player FOREHAND trick 1
	// assertEquals(AIPlayerNN.ACTIVE, inputs[1], 0.0);
	// // Club Jack for player FOREHAND trick 1
	// assertEquals(0.0, inputs[2], 0.0);
	// // Spade Jack for player FOREHAND trick 1
	// assertEquals(AIPlayerNN.PLAYED_CARD, inputs[3], 0.0);
	// // Heart Jack for player FOREHAND trick 1
	// assertEquals(0.0, inputs[4], 0.0);
	// // Diamond Jack for player FOREHAND trick 1
	// assertEquals(0.0, inputs[5], 0.0);
	// // trick fore hand for player FOREHAND trick 2
	// assertEquals(0.0, inputs[34], 0.0);
	// // Club Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[35], 0.0);
	// // Spade Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[36], 0.0);
	// // Heart Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[37], 0.0);
	// // Diamond Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[38], 0.0);
	//
	// // game declarer for player MIDDLEHAND
	// assertEquals(0.0, inputs[726], 0.0);
	// // trick fore hand for player MIDDLEHAND trick 1
	// assertEquals(0.0, inputs[1 + 726], 0.0);
	// // Club Jack for player MIDDLEHAND trick 1
	// assertEquals(0.0, inputs[2 + 726], 0.0);
	// // Spade Jack for player MIDDLEHAND trick 1
	// assertEquals(0.0, inputs[3 + 726], 0.0);
	// // Heart Jack for player MIDDLEHAND trick 1
	// assertEquals(AIPlayerNN.PLAYED_CARD, inputs[4 + 726], 0.0);
	// // Diamond Jack for player MIDDLEHAND trick 1
	// assertEquals(0.0, inputs[5 + 726], 0.0);
	// // trick fore hand for player FOREHAND trick 2
	// assertEquals(0.0, inputs[34 + 726], 0.0);
	// // Club Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[35 + 726], 0.0);
	// // Spade Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[36 + 726], 0.0);
	// // Heart Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[37 + 726], 0.0);
	// // Diamond Jack for player FOREHAND trick 2
	// assertEquals(0.0, inputs[38 + 726], 0.0);
	//
	// // game declarer for player REARHAND
	// assertEquals(0.0, inputs[363], 0.0);
	// // trick fore hand for player REARHAND trick 1
	// assertEquals(0.0, inputs[1 + 363], 0.0);
	// // Club Jack for player REARHAND trick 1
	// assertEquals(AIPlayerNN.PLAYED_CARD, inputs[2 + 363], 0.0);
	// // Spade Jack for player REARHAND trick 1
	// assertEquals(0.0, inputs[3 + 363], 0.0);
	// // Heart Jack for player REARHAND trick 1
	// assertEquals(0.0, inputs[4 + 363], 0.0);
	// // Diamond Jack for player REARHAND trick 1
	// assertEquals(0.0, inputs[5 + 363], 0.0);
	// // trick fore hand for player REARHAND trick 2
	// assertEquals(AIPlayerNN.HAS_CARD, inputs[34 + 363], 0.0);
	// // Club Jack for player REARHAND trick 2
	// assertEquals(0.0, inputs[35 + 363], 0.0);
	// // Spade Jack for player REARHAND trick 2
	// assertEquals(0.0, inputs[36 + 363], 0.0);
	// // Heart Jack for player REARHAND trick 2
	// assertEquals(0.0, inputs[37 + 363], 0.0);
	// // Diamond Jack for player REARHAND trick 2
	// assertEquals(AIPlayerNN.PLAYED_CARD_IN_TRICK, inputs[38 + 363], 0.0);
	// }
}
