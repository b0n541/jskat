package org.jskat.ai.ml;

/**
 * Aggregate and current-file progress reported during model installation.
 *
 * @param currentFile name of the file currently being downloaded, if any
 * @param completedFiles number of files fully downloaded
 * @param totalFiles total number of files in the release
 * @param downloadedBytes number of bytes downloaded so far
 * @param totalBytes total number of bytes to download, if known
 * @param indeterminate whether the total amount of work is unknown
 */
public record ModelDownloadProgress(String currentFile, int completedFiles, int totalFiles,
                                    long downloadedBytes, long totalBytes, boolean indeterminate) {
}
