package org.jskat.ai.ml;

/** Aggregate and current-file progress reported during model installation. */
public record ModelDownloadProgress(String currentFile, int completedFiles, int totalFiles,
                                    long downloadedBytes, long totalBytes, boolean indeterminate) {
}
