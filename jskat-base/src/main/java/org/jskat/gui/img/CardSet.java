/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.img;

public enum CardSet {

	/**
	 * German ISS card set
	 */
	ISS_GERMAN("ISS", CardFace.GERMAN, "gif"),
	/**
	 * French ISS card set
	 */
	ISS_FRENCH("ISS", CardFace.FRENCH, "gif"),
	/**
	 * Tournament ISS card set
	 */
	ISS_TOURNAMENT("ISS", CardFace.TOURNAMENT, "gif"),
	/**
	 * German Dondorf card set
	 */
	DONDORF_GERMAN("Dondorf", CardFace.GERMAN, "png"),
	/**
	 * French Dondorf card set
	 */
	DONDORF_FRENCH("Dondorf", CardFace.FRENCH, "png"),
	/**
	 * Tournament Dondorf card set
	 */
	DONDORF_TOURNAMENT("Dondorf", CardFace.TOURNAMENT, "png"),
	/**
	 * French Ornamental card set
	 */
	ORNAMENTAL_FRENCH("Ornamental", CardFace.FRENCH, "png"),
	/**
	 * French Tango card set
	 */
	TANGO_FRENCH("Tango", CardFace.FRENCH, "png"),
	/**
	 * German William Tell card set
	 */
	WILLIAMTELL_GERMAN("William Tell", CardFace.GERMAN, "png"),
	/**
	 * German XSkat card set
	 */
	XSKAT_GERMAN("XSkat", CardFace.GERMAN, "png"),
	/**
	 * Tigullio Bridge card set
	 */
	TIGULLIOBRIDGE_FRENCH("Tigullio Bridge", CardFace.FRENCH, "png");

	private String name = null;
	private CardFace cardFace = null;
	private String fileType = null;

	CardSet(String name, CardFace cardFace, String fileType) {
		this.name = name;
		this.cardFace = cardFace;
		this.fileType = fileType;
	}

	public String getName() {
		return name;
	}

	public CardFace getCardFace() {
		return cardFace;
	}

	public String getFileType() {
		return fileType;
	}
}
