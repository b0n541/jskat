package org.jskat.gui.swing;

import org.jskat.data.JSkatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
