/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
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
package org.jskat.util;

/**
 * Defines all modes for a skat list
 */
public enum SkatListMode {
	
	/**
	 * Normal mode: plus and minus points for declarer
	 */
	NORMAL,
	/**
	 * Tournament mode: like normal mode plus extra points used in tournaments
	 *                  after Seeger-Fabian system
	 */
	TOURNAMENT,
	/**
	 * Bierlachs mode: Only minus points are listed
	 */
	BIERLACHS;
}
