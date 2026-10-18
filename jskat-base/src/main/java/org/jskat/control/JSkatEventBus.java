package org.jskat.control;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jskat.control.command.table.AbstractTableCommand;
import org.jskat.control.command.table.CreateTableCommand;
import org.jskat.control.command.table.RemoveTableCommand;
import org.jskat.control.event.table.AbstractTableEvent;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.control.event.table.TableGameMoveEvent;
import org.jskat.control.event.table.TableRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Central event bus for JSkat.
 */
public class JSkatEventBus {

    private final static Logger LOG = LoggerFactory.getLogger(JSkatEventBus.class);

    private final EventBus mainEventBus;

    /**
     * Shared event bus for application events.
     */
    public final static JSkatEventBus INSTANCE = new JSkatEventBus();
    // TODO should be private
    /**
     * Event buses used for table-specific events, keyed by table name.
     */
    public final static Map<String, EventBus> TABLE_EVENT_BUSSES = new HashMap<>();

    private JSkatEventBus() {
        mainEventBus = new EventBus("JSkat");
        mainEventBus.register(this);
    }

    /**
     * Performs register.
     * @param listener listener value.
     */
    public void register(Object listener) {
        mainEventBus.register(listener);
    }

    /**
     * Performs post.
     * @param event event value.
     */
    public void post(Object event) {
        mainEventBus.post(event);
    }

    /**
     * Logs events that have no registered subscribers.
     * @param event unmatched event wrapper
     */
    @Subscribe
    public void on(DeadEvent event) {
        LOG.error("Received dead event: " + event.getEvent());
    }

    /**
     * Creates an event bus for a newly created table.
     * @param command table creation command
     */
    @Subscribe
    public void createTableEventBusOn(final CreateTableCommand command) {
        if (!JSkatEventBus.TABLE_EVENT_BUSSES.containsKey(command.tableName)) {
            EventBus eventBus = new EventBus("Table " + command.tableName);
            JSkatEventBus.TABLE_EVENT_BUSSES.put(command.tableName, eventBus);

            post(new TableCreatedEvent(command.tableType, command.tableName));
        }
    }

    /**
     * Removes the event bus for a deleted table.
     * @param command table removal command
     */
    @Subscribe
    public void removeTableEventBusOn(final RemoveTableCommand command) {
        JSkatEventBus.TABLE_EVENT_BUSSES.remove(command.tableName);

        post(new TableRemovedEvent(command.tableName, command.tableType));
    }

    /**
     * Dispatches a table event to the bus for its table.
     * @param event table event to dispatch
     */
    @Subscribe
    public void dispatchTableEventOn(AbstractTableEvent event) {
        if (event instanceof TableGameMoveEvent tableGameMoveEvent) {
            LOG.info("Forwarding game move event " + tableGameMoveEvent.gameEvent + " to table " + tableGameMoveEvent.tableName);
            JSkatEventBus.TABLE_EVENT_BUSSES.get(tableGameMoveEvent.tableName).post(tableGameMoveEvent.gameEvent);
        } else {
            LOG.info("Forwarding table event " + event + " to table " + event.tableName);
            JSkatEventBus.TABLE_EVENT_BUSSES.get(event.tableName).post(event);
        }
    }

    /**
     * Dispatches a table command to the bus for its table.
     * @param command table command to dispatch
     */
    @Subscribe
    public void dispatchTableCommandOn(AbstractTableCommand command) {
        if (JSkatEventBus.TABLE_EVENT_BUSSES.containsKey(command.tableName)) {
            LOG.info("Forwarding command " + command + " to table " + command.tableName);
            JSkatEventBus.TABLE_EVENT_BUSSES.get(command.tableName).post(command);
        } else {
            LOG.warn("No event bus found for table " + command.tableName);
        }
    }
}
