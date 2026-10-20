package org.jskat.tournament;

import java.util.Comparator;
import java.util.Objects;

/** A stable issue shared by all tournament entry points. */
public record ValidationIssue(Severity severity, String code, String path, String message) {
    public enum Severity {
        ERROR,
        WARNING
    }

    public static final Comparator<ValidationIssue> ORDER =
            Comparator.comparing(ValidationIssue::path)
                    .thenComparing(ValidationIssue::code)
                    .thenComparing(ValidationIssue::message);

    public ValidationIssue {
        Objects.requireNonNull(severity);
        Objects.requireNonNull(code);
        Objects.requireNonNull(path);
        Objects.requireNonNull(message);
    }
}
