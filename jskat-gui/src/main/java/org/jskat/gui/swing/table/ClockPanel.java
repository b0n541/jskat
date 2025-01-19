package org.jskat.gui.swing.table;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Holds the clock icon and the time<br>
 * does clock countdown too
 */
public class ClockPanel extends JPanel {


    private final JLabel timeLabel;
    double playerTimeInSeconds;
    CountDownThread countDownThread;

    /**
     * Constructor
     */
    public ClockPanel() {

        ImageIcon clock = new ImageIcon(
                JSkatGraphicRepository.INSTANCE.getIconImage(Icon.CLOCK,
                        IconSize.SMALL));
        JLabel clockLabel = new JLabel(clock);
        this.playerTimeInSeconds = 0.0d;
        this.timeLabel = new JLabel(getPlayerTimeString());

        add(clockLabel);
        add(this.timeLabel);
    }

    /**
     * Sets the {@link ClockPanel} to active state
     */
    public void setActive() {
        if (this.countDownThread != null) {
            // there is an old count down thread running
            // stop it at first
            this.countDownThread.stopCountDown();
        }
        this.countDownThread = new CountDownThread();
        this.countDownThread.start();
    }

    /**
     * Sets the {@link ClockPanel} to inactive state
     */
    public void setInactive() {
        if (this.countDownThread != null) {
            this.countDownThread.stopCountDown();
        }
    }

    /**
     * Sets the player time
     *
     * @param newPlayerTime Player time
     */
    public void setPlayerTime(final double newPlayerTime) {

        this.playerTimeInSeconds = newPlayerTime;
        refreshTimeLabel();
    }

    void refreshTimeLabel() {
        this.timeLabel.setText(getPlayerTimeString());
    }

    private String getPlayerTimeString() {

        int minutes = (int) Math.floor(this.playerTimeInSeconds / 60);
        int seconds = (int) (this.playerTimeInSeconds - (minutes * 60));

        DecimalFormat format = ((DecimalFormat) NumberFormat.getInstance());
        format.applyPattern("00");
        return format.format(minutes) + ":" + format.format(seconds);
    }

    private class CountDownThread extends Thread {

        private volatile boolean isActive = true;

        /**
         * Constructor
         */
        CountDownThread() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            while (this.isActive) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (this.isActive) {
                    // thread could be inactive meanwhile
                    if (ClockPanel.this.playerTimeInSeconds < 1.0) {
                        setPlayerTime(0.0);
                    } else {
                        setPlayerTime(ClockPanel.this.playerTimeInSeconds - 1.0);
                    }
                }
            }
        }

        void stopCountDown() {
            this.isActive = false;
        }
    }
}
