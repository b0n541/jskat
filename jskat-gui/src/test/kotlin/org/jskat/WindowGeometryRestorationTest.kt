package org.jskat

import javafx.geometry.Rectangle2D
import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.WindowGeometry
import org.junit.jupiter.api.Test

class WindowGeometryRestorationTest {

    private val screenBounds = Rectangle2D(10.5, 20.5, 1919.5, 1079.5)

    @Test
    fun `saved window geometry is restored`() {
        val restored = restoreWindowGeometry(WindowGeometry(100, 200, 800, 600), screenBounds)

        assertThat(restored).isEqualTo(Rectangle2D(100.0, 200.0, 800.0, 600.0))
    }

    @Test
    fun `unset window geometry uses the full visual screen bounds`() {
        val restored = restoreWindowGeometry(WindowGeometry.unset(), screenBounds)

        assertThat(restored).isEqualTo(screenBounds)
    }

    @Test
    fun `position and size fall back independently`() {
        val restoredPosition = restoreWindowGeometry(
            WindowGeometry(100, 200, Int.MIN_VALUE, Int.MIN_VALUE),
            screenBounds,
        )
        val restoredSize = restoreWindowGeometry(
            WindowGeometry(Int.MIN_VALUE, Int.MIN_VALUE, 800, 600),
            screenBounds,
        )

        assertThat(restoredPosition).isEqualTo(Rectangle2D(100.0, 200.0, screenBounds.width, screenBounds.height))
        assertThat(restoredSize).isEqualTo(Rectangle2D(screenBounds.minX, screenBounds.minY, 800.0, 600.0))
    }

    @Test
    fun `partially unset pairs use the corresponding screen bounds`() {
        val restoredPosition = restoreWindowGeometry(WindowGeometry(100, Int.MIN_VALUE, 800, 600), screenBounds)
        val restoredSize = restoreWindowGeometry(WindowGeometry(100, 200, 800, Int.MIN_VALUE), screenBounds)

        assertThat(restoredPosition).isEqualTo(Rectangle2D(screenBounds.minX, screenBounds.minY, 800.0, 600.0))
        assertThat(restoredSize).isEqualTo(Rectangle2D(100.0, 200.0, screenBounds.width, screenBounds.height))
    }
}
