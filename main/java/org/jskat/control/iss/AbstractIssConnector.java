package org.jskat.control.iss;

import org.jskat.data.JSkatOptions;
import org.jskat.util.JSkatResourceBundle;

/**
 * Abstract implementation of {@link IssConnector}
 */
abstract class AbstractIssConnector implements IssConnector {

	protected static final JSkatResourceBundle strings = JSkatResourceBundle
			.instance();
	protected static final JSkatOptions options = JSkatOptions.instance();

	protected String loginName;
	protected String password;

	/**
	 * Sets login credentials
	 * 
	 * @param newLoginName
	 *            Login name
	 * @param newPassword
	 *            Password
	 */
	@Override
	public void setConnectionData(final String newLoginName,
			final String newPassword) {
		loginName = newLoginName;
		password = newPassword;
	}

}
