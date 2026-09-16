package org.allsparks.contracts.health;

import java.util.Objects;
import org.allsparks.contracts.identity.ComponentId;

/**
 * Immutable health finding.
 *
 * <p>{@code timestampNanos} is monotonic process time. This type does not
 * carry stack traces, logging transport, or recovery actions, and it does not
 * mutate after construction.
 */
public final class HealthFinding {

    private final ComponentId source;
    private final String code;
    private final HealthSeverity severity;
    private final String message;
    private final long timestampNanos;

    private HealthFinding(
            ComponentId source, String code, HealthSeverity severity, String message, long timestampNanos) {
        this.source = source;
        this.code = code;
        this.severity = severity;
        this.message = message;
        this.timestampNanos = timestampNanos;
    }

    public static HealthFinding of(
            ComponentId source, String code, HealthSeverity severity, String message, long timestampNanos) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(severity, "severity");
        return new HealthFinding(
                source,
                requireText(code, "HealthFinding code"),
                severity,
                requireText(message, "HealthFinding message"),
                timestampNanos);
    }

    public ComponentId source() {
        return source;
    }

    public String code() {
        return code;
    }

    public HealthSeverity severity() {
        return severity;
    }

    public String message() {
        return message;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthFinding)) {
            return false;
        }
        HealthFinding that = (HealthFinding) other;
        return timestampNanos == that.timestampNanos
                && source.equals(that.source)
                && code.equals(that.code)
                && severity == that.severity
                && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, code, severity, message, timestampNanos);
    }

    @Override
    public String toString() {
        return source + " " + severity + " " + code + ": " + message;
    }

    private static String requireText(String value, String kind) {
        if (value == null) {
            throw new IllegalArgumentException(kind + " must not be null");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(kind + " must not be blank");
        }
        return trimmed;
    }
}
