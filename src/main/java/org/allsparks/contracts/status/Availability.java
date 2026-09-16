package org.allsparks.contracts.status;

/**
 * Whether a capability can currently exist or operate.
 *
 * <p>This is not readiness: it does not say whether a requested action is
 * complete or ready to begin. {@link #UNKNOWN} is not available. Freshness
 * belongs on {@code Validity} or an age field, not as a fifth availability
 * value.
 *
 * <p>This enum encodes no coordinator policy and does not command hardware.
 */
public enum Availability {
    AVAILABLE,
    DEGRADED,
    UNAVAILABLE,
    UNKNOWN;

    public boolean isAvailable() {
        return this == AVAILABLE;
    }
}
