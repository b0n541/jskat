/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.player;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves all implementations of {@link JSkatPlayer} that inherit from
 * {@link AbstractJSkatPlayer} and are not abstract
 */
public class JSkatPlayerResolver {

	private final static Logger LOG = LoggerFactory.getLogger(JSkatPlayerResolver.class);

	public static final String HUMAN_PLAYER_CLASS = "org.jskat.gui.human.SwingHumanPlayer";

	public static final Set<String> EXCLUDED_PLAYER_CLASSES;

	public static final Set<String> UNIT_TEST_PLAYER_CLASSES;

	static {
		EXCLUDED_PLAYER_CLASSES = new HashSet<String>();
		EXCLUDED_PLAYER_CLASSES.add("org.jskat.ai.mjl.AIPlayerMJL");
		EXCLUDED_PLAYER_CLASSES.add("org.jskat.ai.algorithmic.AlgorithmicAIPlayer");

		UNIT_TEST_PLAYER_CLASSES = new HashSet<String>();
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.UnitTestPlayer");
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.RamschTestPlayer");
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.NoBiddingTestPlayer");
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.ContraCallingTestPlayer");
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.ContraReCallingTestPlayer");
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.ExceptionTestPlayer");
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.PlayNonPossessingCardTestPlayer");
		UNIT_TEST_PLAYER_CLASSES.add("org.jskat.ai.test.PlayNotAllowedCardTestPlayer");
	}

	/**
	 * Gets all class names including package names of AI player implementations
	 *
	 * @return A set of all player implementation classes
	 */
	public static Set<String> getAllAIPlayerImplementations() {

		final Set<String> result = getAllImplementations();

		result.removeAll(EXCLUDED_PLAYER_CLASSES);
		result.removeAll(UNIT_TEST_PLAYER_CLASSES);

		LOG.info("Found {} implementations of AbstractJSkatPlayer.", result.size());

		return result;
	}

	private static Set<String> getAllImplementations() {
		final Set<String> result = new HashSet<String>();
		final Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("org.jskat.ai"))
				.filterInputsBy(new FilterBuilder().include(".*\\.class")));

		final Set<Class<? extends AbstractJSkatPlayer>> subTypes = reflections.getSubTypesOf(AbstractJSkatPlayer.class);
		for (final Class<? extends AbstractJSkatPlayer> jskatPlayer : subTypes) {
			if (isNotAbstract(jskatPlayer) && isNotHumanPlayer(jskatPlayer)) {
				result.add(jskatPlayer.getName());
			}
		}

		return result;
	}

	private static boolean isNotHumanPlayer(
			final Class<? extends AbstractJSkatPlayer> jskatPlayer) {
		return !HUMAN_PLAYER_CLASS.equals(jskatPlayer.getName());
	}

	private static boolean isNotAbstract(
			final Class<? extends AbstractJSkatPlayer> jskatPlayer) {
		return !Modifier.isAbstract(jskatPlayer.getModifiers());
	}
}
