/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
