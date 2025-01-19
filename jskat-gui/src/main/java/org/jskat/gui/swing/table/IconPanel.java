package org.jskat.gui.swing.table;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;
import org.jskat.util.JSkatResourceBundle;

import javax.swing.*;

/**
 * Holds information icons for the player.
 */
public class IconPanel extends JPanel {


    private static final ImageIcon CHAT_ENABLED_ICON = new ImageIcon(
            JSkatGraphicRepository.INSTANCE.getIconImage(Icon.CHAT, IconSize.SMALL));

    private static final ImageIcon CHAT_DISABLED_ICON = new ImageIcon(
            JSkatGraphicRepository.INSTANCE.getIconImage(Icon.CHAT_DISABLED, IconSize.SMALL));

    private static final ImageIcon READY_TO_PLAY_ICON = new ImageIcon(
            JSkatGraphicRepository.INSTANCE.getIconImage(Icon.OK, IconSize.SMALL));

    private static final ImageIcon NOT_READY_TO_PLAY_ICON = new ImageIcon(
            JSkatGraphicRepository.INSTANCE.getIconImage(Icon.STOP, IconSize.SMALL));

    private static final ImageIcon RESIGNED_ICON = new ImageIcon(
            JSkatGraphicRepository.INSTANCE.getIconImage(Icon.WHITE_FLAG, IconSize.SMALL));

    private static final ImageIcon THINKING_ICON = new ImageIcon(
            JSkatGraphicRepository.INSTANCE.getIconImage(Icon.THINKING, IconSize.SMALL));

    private static final ImageIcon BLANK_ICON = new ImageIcon(
            JSkatGraphicRepository.INSTANCE.getIconImage(Icon.BLANK, IconSize.SMALL));

    private boolean showIssIcons = false;
    private boolean chatEnabled = false;
    private final JLabel chatLabel;
    private boolean readyToPlay = false;
    private final JLabel readyToPlayLabel;
    private boolean resigned = false;
    private final JLabel resignedLabel;
    private boolean thinking = false;
    private final JLabel thinkingLabel;

    private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

    /**
     * Constructor
     */
    public IconPanel() {

        chatLabel = new JLabel(BLANK_ICON);
        readyToPlayLabel = new JLabel(BLANK_ICON);
        resignedLabel = new JLabel(BLANK_ICON);
        thinkingLabel = new JLabel(BLANK_ICON);

        refreshIcons();

        add(resignedLabel);
        add(chatLabel);
        add(readyToPlayLabel);
        add(thinkingLabel);
    }

    /**
     * Resets the icon panel
     */
    public void reset() {

        resigned = false;
        refreshIcons();
    }

    public void setShowIssWidgets(boolean isShowIssIcons) {
        this.showIssIcons = isShowIssIcons;
    }

    /**
     * Sets the flag for enabling chat
     *
     * @param isChatEnabled TRUE, if chatting is enabled
     */
    public void setChatEnabled(boolean isChatEnabled) {

        chatEnabled = isChatEnabled;
        refreshIcons();
    }

    /**
     * Sets the flag for "Ready to play"
     *
     * @param isReadyToPlay TRUE, if the player is ready to play
     */
    public void setReadyToPlay(boolean isReadyToPlay) {

        readyToPlay = isReadyToPlay;
        refreshIcons();
    }

    /**
     * Sets the flag for "Resign"
     *
     * @param isResign TRUE, if the player wants to resign
     */
    public void setResign(boolean isResign) {

        resigned = isResign;
        refreshIcons();
    }

    /**
     * Sets the flag for "Thinking"
     *
     * @param isThinking TRUE, if the player is thinking
     */
    public void setThinking(boolean isThinking) {
        thinking = isThinking;
        refreshIcons();
    }

    private void refreshIcons() {

        if (thinking) {
            thinkingLabel.setIcon(THINKING_ICON);
            thinkingLabel.setToolTipText(strings.getString("player_thinking"));
        } else {
            setBlank(thinkingLabel);
        }

        if (!showIssIcons) {
            setBlank(resignedLabel, chatLabel, readyToPlayLabel);
        } else {
            if (resigned) {
                resignedLabel.setIcon(RESIGNED_ICON);
                resignedLabel.setToolTipText(strings.getString("iss_player_wants_to_resign"));
            } else {
                setBlank(resignedLabel);
            }

            if (chatEnabled) {
                chatLabel.setIcon(CHAT_ENABLED_ICON);
                chatLabel.setToolTipText(strings.getString("iss_chat_enabled"));
            } else {
                chatLabel.setIcon(CHAT_DISABLED_ICON);
                chatLabel.setToolTipText(strings.getString("iss_chat_disabled"));
            }

            if (readyToPlay) {
                readyToPlayLabel.setIcon(READY_TO_PLAY_ICON);
                readyToPlayLabel.setToolTipText(strings.getString("iss_ready_to_play"));
            } else {
                readyToPlayLabel.setIcon(NOT_READY_TO_PLAY_ICON);
                readyToPlayLabel.setToolTipText(strings.getString("iss_not_ready_to_play"));
            }
        }
    }

    private void setBlank(JLabel... labels) {
        for (JLabel label : labels) {
            label.setIcon(BLANK_ICON);
            label.setToolTipText("");
        }
    }
}
