/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
