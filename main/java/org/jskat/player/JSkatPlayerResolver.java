package org.jskat.player;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

/**
 * Resolves all implementations of {@link JSkatPlayer} that inherit from
 * {@link AbstractJSkatPlayer} and are not abstract
 */
public class JSkatPlayerResolver {

	public final static String HUMAN_PLAYER_CLASS = "org.jskat.gui.human.SwingHumanPlayer";

	public final static Set<String> EXCLUDED_PLAYER_CLASSES;

	static {
		EXCLUDED_PLAYER_CLASSES = new HashSet<String>();
		EXCLUDED_PLAYER_CLASSES.add("org.jskat.ai.mjl.AIPlayerMJL");
	}

	/**
	 * Gets all class names including package names of AI player implementations
	 */
	public static Set<String> getAllAIPlayerImplementations() {

		Set<String> result = getAllImplementations();

		result.removeAll(EXCLUDED_PLAYER_CLASSES);

		return result;
	}

	private static Set<String> getAllImplementations() {
		Set<String> result = new HashSet<String>();
		Reflections reflections = new Reflections("org.jskat");

		Set<Class<? extends AbstractJSkatPlayer>> subTypes = reflections
				.getSubTypesOf(AbstractJSkatPlayer.class);
		for (Class<? extends AbstractJSkatPlayer> jskatPlayer : subTypes) {
			if (isNotAbstract(jskatPlayer) && isNotHumanPlayer(jskatPlayer)) {
				result.add(jskatPlayer.getName());
			}
		}
		return result;
	}

	private static boolean isNotHumanPlayer(
			Class<? extends AbstractJSkatPlayer> jskatPlayer) {
		return !HUMAN_PLAYER_CLASS.equals(jskatPlayer.getName());
	}

	private static boolean isNotAbstract(
			Class<? extends AbstractJSkatPlayer> jskatPlayer) {
		return !Modifier.isAbstract(jskatPlayer.getModifiers());
	}
}
