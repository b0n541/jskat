package org.jskat.data.iss;

/**
 * Holds login credentials for ISS
 */
/**
 * Credentials used to sign in to an Internet Skat Server.
 *
 * @param userName account name
 * @param password account password
 */
public record LoginCredentials(String userName, String password) {
}
