package org.jskat.ai.ml;

import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ML player creation is tested through the resolved-model factory seam.
 */
public class MLPlayerTest extends AbstractJSkatTest {

    @Test
    public void mlPlayerRequiresStartupResolvedModels() {
        final PlayerCreationResult result = MLPlayerFactory.create(MLPlayer.class.getName(), null);

        assertThat(result.isCreated()).isFalse();
        assertThat(result.unavailable().reason()).isEqualTo(ModelUnavailableReason.NO_ACTIVE_RELEASE);
    }
}
