package org.jskat.tournament;

import java.util.List;

public record ValidationResult<T>(T value, List<ValidationIssue> issues) {
    public ValidationResult {
        issues = issues.stream().sorted(ValidationIssue.ORDER).toList();
    }

    public boolean valid() {
        return value != null
                && issues.stream().noneMatch(i -> i.severity() == ValidationIssue.Severity.ERROR);
    }
}
