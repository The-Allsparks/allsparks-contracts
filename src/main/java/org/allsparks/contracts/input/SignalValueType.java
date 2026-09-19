package org.allsparks.contracts.input;

/**
 * Discriminator for a {@link SignalKey} value representation.
 *
 * <p>Primitive kinds exist so runtimes can cache encoder, switch, and voltage
 * reads without boxing. {@link #OBJECT} is the escape hatch for structured
 * values.
 */
public enum SignalValueType {
    INT,
    LONG,
    DOUBLE,
    BOOLEAN,
    OBJECT
}
