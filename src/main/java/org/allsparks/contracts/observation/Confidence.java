package org.allsparks.contracts.observation;

import java.util.Objects;
import java.util.OptionalDouble;

/**
 * Evidence strength in {@code [0, 1]} or explicit unknown.
 *
 * <p>Unknown is not {@code 0.0} and is not a missing field. This type does
 * not decide whether a score is actionable.
 */
public final class Confidence {

    private final boolean known;
    private final double value;

    private Confidence(boolean known, double value) {
        this.known = known;
        this.value = value;
    }

    public static Confidence unknown() {
        return new Confidence(false, Double.NaN);
    }

    public static Confidence of(double value) {
        if (Double.isNaN(value) || value < 0.0d || value > 1.0d) {
            throw new IllegalArgumentException("Confidence must be in [0, 1], not " + value);
        }
        return new Confidence(true, value);
    }

    public boolean isKnown() {
        return known;
    }

    public OptionalDouble value() {
        return known ? OptionalDouble.of(value) : OptionalDouble.empty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Confidence)) {
            return false;
        }
        Confidence that = (Confidence) other;
        if (known != that.known) {
            return false;
        }
        return !known || Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(known, known ? value : 0.0d);
    }

    @Override
    public String toString() {
        return known ? Double.toString(value) : "unknown";
    }
}
