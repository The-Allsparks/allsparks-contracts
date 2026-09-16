package org.allsparks.contracts.identity;

/**
 * Immutable identifier for a capability exposed to a consumer or coordinator.
 *
 * <p>Examples of custom values: {@code drive.translation},
 * {@code mechanism.lift}, {@code vision.game-piece-observation},
 * {@code power.observation}. This library does not ship a central list.
 * Custom capabilities are valid without changing this repository.
 */
public final class CapabilityId {

    private final String value;

    private CapabilityId(String value) {
        this.value = value;
    }

    /**
     * Create an identifier from a human-readable value.
     *
     * @param value non-null, non-blank text; leading and trailing whitespace is
     *     removed
     */
    public static CapabilityId of(String value) {
        return new CapabilityId(Identifiers.requireNonBlank(value, "CapabilityId"));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CapabilityId)) {
            return false;
        }
        return value.equals(((CapabilityId) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
