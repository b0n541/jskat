package org.jskat.control.event;

import com.google.common.eventbus.EventBus;


/**
 * Central event bus for JSkat.
 */
public class JSkatEventBus {

	public final static EventBus INSTANCE = new EventBus();

	private JSkatEventBus() {
		
	}
}
