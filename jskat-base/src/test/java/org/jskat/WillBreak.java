package org.jskat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WillBreak {
    @Test
    void willBreak() {
        assertThat(false).isTrue();
    }
}
