package org.allsparks.contracts.observation;

/**
 * Whether a sample or observation can be trusted as a measurement.
 *
 * <p>This enum does not invent numeric values and has no side effects.
 * Domain leftovers such as skipped-loop or disagreeing-redundant-sensor map
 * to {@link #INVALID} at adapters.
 */
public enum Validity {
    VALID,
    STALE,
    MISSING,
    OUT_OF_RANGE,
    UNSUPPORTED,
    INVALID
}
