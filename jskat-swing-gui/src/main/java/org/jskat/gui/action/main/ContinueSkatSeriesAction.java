/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.action.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.ReadyForNextGameCommand;
import org.jskat.data.JSkatApplicationData;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for continuing a local skat series
 */
public class ContinueSkatSeriesAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public ContinueSkatSeriesAction() {

		putValue(NAME, STRINGS.getString("continue_series")); //$NON-NLS-1$
		putValue(SHORT_DESCRIPTION, STRINGS.getString("continue_series_tooltip")); //$NON-NLS-1$

		setIcon(Icon.PLAY);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		JSkatEventBus.TABLE_EVENT_BUSSES.get(
				JSkatApplicationData.INSTANCE.getActiveTable()).post(
						new ReadyForNextGameCommand());
	}
}
