package org.jskat.gui.swing.table;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.swing.CardPanel;
import org.jskat.util.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Panel for showing cards on a hand. The cards can be clicked.
 */
class ClickableCardPanel extends CardPanel {


    private static final Logger log = LoggerFactory
            .getLogger(ClickableCardPanel.class);

    private JPanel parent = null;

    /**
     * Creates a new instance of CardPanel.
     *
     * @param parent       Parent panel
     * @param scaleFactor  Scale factor for cards
     * @param showBackside TRUE if the Card should hide its face
     */
    ClickableCardPanel(final JPanel parent, final Double scaleFactor,
                       final Boolean showBackside) {

        super(scaleFactor, showBackside);

        this.parent = parent;
        setActionMap(parent.getActionMap());

        createClickListener();
    }

    private void createClickListener() {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                // not needed
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                // not needed
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                // not needed
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                // not needed
            }

            @Override
            public void mouseReleased(final MouseEvent e) {

                cardClicked(e);
            }
        });
    }

    /**
     * Tells the JSkatMaster when the panel was clicked by the user.
     *
     * @param event Mouse event
     */
    void cardClicked(final MouseEvent event) {
        // FIXME (jan 04.12.2010) refactor this method, nobody understands it
        final int xPosition = event.getX();
        final int yPosition = event.getY();

        if (xPosition > -1 && xPosition < getWidth() && yPosition > -1
                && yPosition < getHeight()) {

            // get card
            final int cardWidth = bitmaps.getCardImage(Card.CJ).getWidth(this);

            final int cardIndex = getCardIndex(xPosition, cardWidth);

            final Card card = getCard(cardIndex);

            if (card != null) {
                final Action action = getAction();

                if (action != null) {

                    action.actionPerformed(new ActionEvent(Card
                            .getCardFromString(card.getSuit().getShortString()
                                    + card.getRank().getShortString()),
                            ActionEvent.ACTION_PERFORMED, (String) action
                            .getValue(Action.ACTION_COMMAND_KEY)));
                } else {

                    log.debug("Action is null");
                }
            }
        }
    }

    private Action getAction() {
        // send event only, if the card panel shows a card
        Action action = null;

        if (parent instanceof DiscardPanel) {
            // card panel in discard panel was clicked
            action = getActionMap().get(JSkatAction.TAKE_CARD_FROM_SKAT);
        } else if (parent instanceof JSkatUserPanel) {
            // card panel in player panel was clicked

            final GameState state = ((JSkatUserPanel) parent).getGameState();

            if (state == GameState.DISCARDING
                    || state == GameState.SCHIEBERAMSCH) {
                // discarding phase
                action = getActionMap().get(JSkatAction.PUT_CARD_INTO_SKAT);
            } else if (state == GameState.TRICK_PLAYING) {
                // trick playing phase
                action = getActionMap().get(JSkatAction.PLAY_CARD);
            }
        } else {

            log.debug("Other parent " + parent);
        }
        return action;
    }

    private Card getCard(final int cardIndex) {
        Card card = null;
        if (cardIndex > -1 && cardIndex < cards.size()) {
            card = cards.get(cardIndex);
        }
        return card;
    }

    private int getCardIndex(final int clickPositionX, final int cardWidth) {
        int cardIndex = -1;
        if (cards.size() == 1) {
            log.debug("only one card on hand");
            if (clickPositionX < cardWidth) {
                cardIndex = 0;
            }
        } else if (cards.size() > 1) {

            final int distanceBetweenCards = (getWidth() - cardWidth)
                    / (cards.size() - 1);

            if (cardWidth > distanceBetweenCards) {
                log.debug("cards with overlaping");
                cardIndex = 0;
                while (cardIndex * distanceBetweenCards < activeCardMinXPosition) {
                    cardIndex++;
                }
            } else {
                log.debug("cards without overlaping");
                cardIndex = (int) (clickPositionX * (1 / scaleFactor) / cardWidth);
            }
        }
        return cardIndex;
    }
}
