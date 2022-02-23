package org.jskat.control.command.table;

import org.jskat.util.CardList;
import org.jskat.util.Player;

import java.util.HashMap;
import java.util.Map;

public class ShowCardsCommand extends AbstractTableCommand {

    public final Map<Player, CardList> cards = new HashMap<>();
    public final CardList skat = new CardList();

    public ShowCardsCommand(String tableName, Player player, CardList cards) {
        this(tableName, Map.of(player, cards), null);
    }

    public ShowCardsCommand(String tableName, Map<Player, CardList> cards, CardList skat) {
        super(tableName);
        this.cards.putAll(cards);
        if (skat != null) {
            this.skat.addAll(skat);
        }
    }
}
