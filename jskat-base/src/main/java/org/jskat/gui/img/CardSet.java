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
	XSKAT_GERMAN("XSkat", CardFace.GERMAN, "png");

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
