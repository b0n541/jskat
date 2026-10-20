package org.jskat.tournament;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;

/** Filesystem system-call boundary for durability checks and fault-injection acceptance runs. */
public interface TournamentPublicationIO {
    void forceFile(FileChannel file) throws IOException;

    void atomicMove(Path temporary, Path target, boolean replace) throws IOException;

    void forceDirectory(Path directory) throws IOException;

    TournamentPublicationIO LOCAL =
            new TournamentPublicationIO() {
                public void forceFile(FileChannel file) throws IOException {
                    file.force(true);
                }

                public void atomicMove(Path temporary, Path target, boolean replace)
                        throws IOException {
                    if (replace)
                        Files.move(
                                temporary,
                                target,
                                StandardCopyOption.ATOMIC_MOVE,
                                StandardCopyOption.REPLACE_EXISTING);
                    else Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
                }

                public void forceDirectory(Path directory) throws IOException {
                    try (var channel = FileChannel.open(directory, StandardOpenOption.READ)) {
                        channel.force(true);
                    }
                }
            };
}
