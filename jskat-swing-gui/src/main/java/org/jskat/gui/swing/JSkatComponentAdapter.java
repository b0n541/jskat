/**
 * This file is part of JSkat.
 * <p>
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
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
package org.jskat.gui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.jskat.data.JSkatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSkatComponentAdapter extends ComponentAdapter {

	private final static Logger LOG = LoggerFactory.getLogger(JSkatComponentAdapter.class);

	@Override
	public void componentShown(ComponentEvent e) {
		LOG.debug(e.getComponent().getClass() + " shown");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point mainFramePosition = JSkatOptions.instance().getMainFramePosition();
		if (screenSize.getWidth() > mainFramePosition.getX() && screenSize.getHeight() > mainFramePosition.getY()) {
			LOG.debug("Set position: " + mainFramePosition);
			e.getComponent().setLocation(mainFramePosition);
			e.getComponent().setSize(JSkatOptions.instance().getMainFrameSize());
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		LOG.debug(e.getComponent().getClass() + " resized: " + e.getComponent().getSize());
		LOG.debug(e.paramString());
//		JSkatOptions.instance().setMainFrameSize(e.getComponent().getSize());
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		LOG.debug(e.getComponent().getClass() + " moved: " + e.getComponent().getLocationOnScreen());
		LOG.debug(e.paramString());
//		JSkatOptions.instance().setMainFramePosition(e.getComponent().getLocationOnScreen());
	}
}
