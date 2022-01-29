package org.jskat.data;

/**
 * Resolves the default save path for a specific platform
 */
public interface SavePathResolver {
	/**
	 * Resolves the default save path
	 *
	 * @return Default save path
	 */
	public String getDefaultSavePath();

	/**
	 * Resolves the current working directory
	 *
	 * @return Current working directory
	 */
	public String getCurrentWorkingDirectory();
}
