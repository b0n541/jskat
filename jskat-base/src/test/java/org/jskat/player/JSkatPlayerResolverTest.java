
package org.jskat.player;


import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JSkatPlayerResolverTest extends AbstractJSkatTest {

    @Test
    public void testGetAllAIPlayerImplementations() {

        final Set<String> implementations = JSkatPlayerResolver.getAllAIPlayerImplementations();

        assertThat(implementations).hasSize(2);
    }
}
