package org.jskat.ai.nn.data;

public enum GamePhase {
	OPENING, MIDDLEGAME, ENDGAME;

	public final static GamePhase of(int trickNoInGame) {
		switch (trickNoInGame) {
		case 0:
		case 1:
		case 2:
			return OPENING;
		case 3:
		case 4:
		case 5:
		case 6:
			return MIDDLEGAME;
		case 7:
		case 8:
		case 9:
			return ENDGAME;
		}
		return null;
	}
}
