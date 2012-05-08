package org.jskat.control;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles each thread in JSkat
 */
public class ThreadManager {

	private static List<JSkatThread> threads = new ArrayList<JSkatThread>();
	private static volatile boolean terminateAllThreads = false;

	/**
	 * Registers a thread
	 * 
	 * @param thread
	 *            Thread to be registered
	 * @return TRUE, if the thread was registered
	 */
	public static boolean registerThread(final JSkatThread thread) {
		return threads.add(thread);
	}

	/**
	 * Unregister a thread
	 * 
	 * @param thread
	 *            Thread to be unregistered
	 * @return TRUE, if the thread was unregistered
	 */
	public static boolean unregisterThread(final JSkatThread thread) {
		return threads.remove(thread);
	}

	/**
	 * Terminates all threads
	 */
	public static void terminateAllThreads() {
		terminateAllThreads = true;
		for (JSkatThread tThread : threads) {
			tThread.terminate();
		}
		threads.clear();
	}

	/**
	 * Checks whether all threads will be terminated
	 * 
	 * @return TRUE, if all threads will be terminated
	 */
	public static boolean isTerminating() {
		return terminateAllThreads;
	}

	/**
	 * Initializes the thread manager
	 * 
	 * @return TRUE, if the initializing is done
	 */
	public static boolean init() {
		return terminateAllThreads = false;
	}
}
