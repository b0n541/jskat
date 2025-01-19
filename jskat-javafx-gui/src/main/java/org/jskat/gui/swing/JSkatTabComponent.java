package org.jskat.gui.swing;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.table.TableRemovedEvent;
import org.jskat.data.JSkatViewType;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tab component with title and close button<br>
 * inspired by Java tutorial from Sun
 */
public class JSkatTabComponent extends JPanel {


    final JTabbedPane pane;

    /**
     * Constructor
     *
     * @param newPane Tab pane
     * @param bitmaps JSkat bitmaps
     */
    public JSkatTabComponent(final JTabbedPane newPane,
                             JSkatGraphicRepository bitmaps) {

        // unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        if (newPane == null) {
            throw new IllegalArgumentException("TabbedPane is null");
        }
        this.pane = newPane;
        setOpaque(false);

        // make JLabel read titles from JTabbedPane
        JLabel label = new JLabel() {


            @Override
            public String getText() {
                int i = JSkatTabComponent.this.pane
                        .indexOfTabComponent(JSkatTabComponent.this);
                if (i != -1) {
                    return JSkatTabComponent.this.pane.getTitleAt(i);
                }
                return null;
            }
        };

        add(label);
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // tab button
        JButton button = new TabButton(bitmaps);
        add(button);
        // add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    private class TabButton extends JButton implements ActionListener {


        JSkatGraphicRepository bitmaps;

        public TabButton(JSkatGraphicRepository newBitmaps) {

            this.bitmaps = newBitmaps;

            int size = 22;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            // Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            // Make it transparent
            setContentAreaFilled(false);
            // No need to be focusable
            setFocusable(false);
            // Close the proper tab by clicking the button
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            int i = JSkatTabComponent.this.pane
                    .indexOfTabComponent(JSkatTabComponent.this);
            if (i != -1) {
                // FIXME: it could also be an ISS table
                JSkatEventBus.INSTANCE.post(new TableRemovedEvent(pane
                        .getTitleAt(i), JSkatViewType.LOCAL_TABLE));
            }
        }

        // paint the cross
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            // shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.drawImage(this.bitmaps.getIconImage(Icon.CLOSE, IconSize.SMALL),
                    null, null);
            g2.dispose();
        }
    }
}
