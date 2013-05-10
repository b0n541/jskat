/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.1
 * Copyright (C) 2013-05-10
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
package org.jskat.control.iss;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StreamOutputChannel implements OutputChannel {

	private static Logger log = LoggerFactory
			.getLogger(StreamOutputChannel.class);

	private final PrintWriter output;

	/**
	 * Constructor
	 * 
	 * @param newOutput
	 *            Input stream from ISS
	 */
	StreamOutputChannel(final PrintWriter newOutput) {

		this.output = newOutput;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendMessage(final String message) {
		log.debug("ISS <--|    " + message); //$NON-NLS-1$
		this.output.println(message);
	}
}
