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
