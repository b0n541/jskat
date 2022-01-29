package org.jskat.control;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jskat.control.command.table.CreateTableCommand;
import org.jskat.control.command.table.RemoveTableCommand;
import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Central event bus for JSkat.
 */
public class JSkatEventBus {

    private final static Logger LOG = LoggerFactory
            .getLogger(JSkatEventBus.class);

    private final EventBus mainEventBus;

    public final static JSkatEventBus INSTANCE = new JSkatEventBus();
    public final static Map<String, EventBus> TABLE_EVENT_BUSSES = new HashMap<>();

    private JSkatEventBus() {
        mainEventBus = new EventBus("JSkat");
        mainEventBus.register(this);
    }

    public void register(Object listener) {
        mainEventBus.register(listener);
    }

    public void post(Object event) {
        mainEventBus.post(event);
    }

    @Subscribe
    public void on(DeadEvent event) {
        LOG.error("Recieved dead event: " + event.getEvent());
    }

    @Subscribe
    public void createTableEventBusOn(final CreateTableCommand command) {

        if (!JSkatEventBus.TABLE_EVENT_BUSSES.containsKey(command.tableName)) {
            EventBus eventBus = new EventBus("Table " + command.tableName);
            JSkatEventBus.TABLE_EVENT_BUSSES.put(command.tableName, eventBus);

            post(new TableCreatedEvent(command.tableType, command.tableName));
        }
    }

    @Subscribe
    public void removeTableEventBusOn(final RemoveTableCommand command) {
        JSkatEventBus.TABLE_EVENT_BUSSES.remove(command.tableName);

        post(new TableRemovedEvent(command.tableName, command.tableType));
    }

    @Subscribe
    public void dispatchTableEventOn(ActivePlayerChangedEvent event) {
        JSkatEventBus.TABLE_EVENT_BUSSES.get(event.tableName).post(event);
    }

    @Subscribe
    public void dispatchTableEventOn(TableGameMoveEvent event) {
        LOG.info("Forwarding game event " + event.gameEvent + " to table "
                + event.tableName);
        JSkatEventBus.TABLE_EVENT_BUSSES.get(event.tableName)
                .post(event.gameEvent);
    }

    @Subscribe
    public void dispatchTableEventOn(SkatSeriesStartedEvent event) {
        LOG.info("Forwarding table event " + event + " to table "
                + event.tableName);
        JSkatEventBus.TABLE_EVENT_BUSSES.get(event.tableName).post(event);
    }

    @Subscribe
    public void dispatchTableCommandOn(ShowCardsCommand command) {
        LOG.info("Forwarding command " + command + " to table "
                + command.tableName);
        JSkatEventBus.TABLE_EVENT_BUSSES.get(command.tableName).post(command);
    }
}
