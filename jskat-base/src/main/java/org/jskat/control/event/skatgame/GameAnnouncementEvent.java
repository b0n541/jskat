package org.jskat.control.event.skatgame;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

import java.util.Objects;

/**
 * Event for game announcement.
 */
public final class GameAnnouncementEvent extends AbstractPlayerMoveEvent {

    public final GameAnnouncement announcement;

    public GameAnnouncementEvent(Player player, GameAnnouncement announcement) {
        super(player);
        this.announcement = announcement;
    }

    @Override
    public void processForward(SkatGameData data) {
        data.setDeclarer(player);
        data.setAnnouncement(announcement);
    }

    @Override
    public void processBackward(SkatGameData data) {
        data.setAnnouncement(null);
        data.setDeclarer(null);
    }

    @Override
    protected String getMoveDetails() {
        return announcement.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(announcement);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameAnnouncementEvent other = (GameAnnouncementEvent) obj;

        return Objects.equals(announcement, other.announcement);
    }
}
