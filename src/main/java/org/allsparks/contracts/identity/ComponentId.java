package org.allsparks.contracts.identity;

/**
 * Immutable identifier for a producing component.
 *
 * <p>There is no global registry and no season-specific constant list. Custom
 * identifiers are valid without changing this library.
 */
public final class ComponentId {

    private final String value;

    private ComponentId(String value) {
        this.value = value;
    }

    /**
     * Create an identifier from a human-readable value.
     *
     * @param value non-null, non-blank text; leading and trailing whitespace is
     *     removed
     */
    public static ComponentId of(String value) {
        return new ComponentId(Identifiers.requireNonBlank(value, "ComponentId"));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ComponentId)) {
            return false;
        }
        return value.equals(((ComponentId) other).value);
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
