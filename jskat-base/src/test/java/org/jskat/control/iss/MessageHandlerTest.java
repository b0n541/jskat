package org.jskat.control.iss;

import com.google.common.eventbus.Subscribe;
import org.jskat.AbstractJSkatTest;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.iss.IssDisconnectedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MessageHandlerTest extends AbstractJSkatTest {

    @Test
    void disconnectsAfterRejectedLoginSoTheUserCanRetry() {
        final DisconnectionEvents events = new DisconnectionEvents();
        JSkatEventBus.INSTANCE.register(events);

        new MessageHandler(mock(IssController.class)).handleErrorMessage(List.of("_id_pw_mismatch"));

        assertThat(events.disconnected).isTrue();
    }

    private static final class DisconnectionEvents {
        private boolean disconnected;

        @Subscribe
        public void on(final IssDisconnectedEvent event) {
            disconnected = true;
        }
    }
}
