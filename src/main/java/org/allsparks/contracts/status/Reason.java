package org.allsparks.contracts.status;

import java.util.Objects;
import java.util.Optional;
import org.allsparks.contracts.health.HealthSeverity;
import org.allsparks.contracts.identity.ComponentId;

/**
 * Interoperability envelope for why something was rejected, degraded, or
 * unavailable.
 *
 * <p>Domain projects keep their own fault codes. This type is not a universal
 * error taxonomy and has no metadata map.
 */
public final class Reason {

    private final String code;
    private final String message;
    private final HealthSeverity severity;
    private final ComponentId source;

    private Reason(String code, String message, HealthSeverity severity, ComponentId source) {
        this.code = code;
        this.message = message;
        this.severity = severity;
        this.source = source;
    }

    public static Reason of(String code, String message) {
        return new Reason(requireText(code, "Reason code"), requireText(message, "Reason message"), null, null);
    }

    public static Reason of(String code, String message, HealthSeverity severity) {
        Objects.requireNonNull(severity, "severity");
        return new Reason(requireText(code, "Reason code"), requireText(message, "Reason message"), severity, null);
    }

    public static Reason of(String code, String message, ComponentId source) {
        Objects.requireNonNull(source, "source");
        return new Reason(requireText(code, "Reason code"), requireText(message, "Reason message"), null, source);
    }

    public static Reason of(String code, String message, HealthSeverity severity, ComponentId source) {
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(source, "source");
        return new Reason(requireText(code, "Reason code"), requireText(message, "Reason message"), severity, source);
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public Optional<HealthSeverity> severity() {
        return Optional.ofNullable(severity);
    }

    public Optional<ComponentId> source() {
        return Optional.ofNullable(source);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Reason)) {
            return false;
        }
        Reason that = (Reason) other;
        return code.equals(that.code)
                && message.equals(that.message)
                && Objects.equals(severity, that.severity)
                && Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, severity, source);
    }

    @Override
    public String toString() {
        return code + ": " + message;
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
