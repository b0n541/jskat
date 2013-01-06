package org.jskat.player;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jskat.AbstractJSkatTest;
import org.junit.Before;
import org.junit.Test;

public class JSkatPlayerResolverTest extends AbstractJSkatTest {

	Set<String> testImplementations;

	@Before
	public void setUp() {
		testImplementations = new HashSet<String>();
		testImplementations.add("org.jskat.player.UnitTestPlayer");
		testImplementations.add("org.jskat.ai.test.RamschTestPlayer");
		testImplementations.add("org.jskat.ai.test.NoBiddingTestPlayer");
	}

	@Test
	public void testGetAllAIPlayerImplementations() {

		Set<String> implementations = JSkatPlayerResolver
				.getAllAIPlayerImplementations();
		implementations.removeAll(testImplementations);

		assertEquals(4, implementations.size());
	}
}
