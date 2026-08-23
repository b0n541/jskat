package org.jskat.gui.javafx

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class JavaFxHostDocumentOpenerTest {

    @Test
    fun `opens a link through JavaFX host services`() {
        var openedLink: String? = null
        val opener = JavaFxHostDocumentOpener { openedLink = it }

        opener.open("https://www.skatgame.net/iss/register")

        assertThat(openedLink).isEqualTo("https://www.skatgame.net/iss/register")
    }

    @Test
    fun `opens only external HTTP links when handling web view navigation`() {
        var openedLink: String? = null
        val opener = JavaFxHostDocumentOpener { openedLink = it }

        assertThat(opener.openIfExternal("file:/help/contents.html")).isFalse()
        assertThat(opener.openIfExternal("https://www.skatgame.net/iss/register")).isTrue()

        assertThat(openedLink).isEqualTo("https://www.skatgame.net/iss/register")
    }

    @Test
    fun `logs failures from JavaFX host services`() {
        val failure = IllegalStateException("No browser available")
        val log = LoggerFactory.getLogger(JavaFxHostDocumentOpener::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        log.addAppender(appender)

        try {
            JavaFxHostDocumentOpener { throw failure }.open("https://www.skatgame.net/iss/register")

            assertThat(appender.list).hasSize(1)
            val event = appender.list.single()
            assertThat(event.formattedMessage).isEqualTo("Error opening web page: https://www.skatgame.net/iss/register")
            assertThat(event.throwableProxy.message).isEqualTo("No browser available")
        } finally {
            log.detachAppender(appender)
        }
    }
}
