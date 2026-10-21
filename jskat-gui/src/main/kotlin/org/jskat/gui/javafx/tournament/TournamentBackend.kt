package org.jskat.gui.javafx.tournament

import org.jskat.tournament.*
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path

/** Blocking service boundary. The presentation calls every operation on a background executor. */
open class TournamentBackend(private val service: TournamentService = TournamentService()) {
    open fun read(file: Path) = service.read(file)
    open fun read(json: String, file: Path) = service.read(json, file)
    open fun preflight(configuration: TournamentConfiguration, cancellation: CancellationToken) = service.preflightNew(configuration, cancellation)
    open fun discover(implementation: PlayerImplementation) = DefaultTournamentInputResolver().discover(implementation)
    open fun open(directory: Path) = service.open(directory)
    open fun verify(configuration: TournamentConfiguration, cancellation: CancellationToken) = service.verify(configuration, cancellation)
    open fun run(configuration: TournamentConfiguration, invocation: TournamentInvocation, options: TournamentSessionOptions,
                 cancellation: CancellationToken, observer: TournamentProgress.Observer) = service.run(configuration, invocation, options, cancellation, observer)
    open fun audit(directory: Path): String = buildString {
        // Tolerant evidence viewer: no lock, no mutation, and no execution authority.
        for (name in listOf("sessions", "failed-attempts", "diagnostics", "lock-history")) {
            append("\n=== $name ===\n")
            val folder = directory.resolve(name)
            if (!Files.isDirectory(folder, NOFOLLOW_LINKS)) continue
            Files.list(folder).use { paths -> paths.sorted().forEach { file ->
                if (Files.isRegularFile(file, NOFOLLOW_LINKS) && file.fileName.toString().endsWith(".json")) {
                    append("\n${file.fileName}\n")
                    try {
                        if (Files.size(file) <= 10_000_000) append(Files.readString(file)) else append("Record too large to display; inspect directory")
                    } catch (e: Exception) { append(e.toString()) }
                    append('\n')
                }
            } }
        }
    }
}
