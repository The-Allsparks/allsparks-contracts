package org.allsparks.contracts.input;

/**
 * Capture importance for a declared input.
 *
 * <p>Runtimes must not skip {@link #CRITICAL} inputs that are due.
 * {@link #OPTIONAL} inputs may be deferred when a cycle budget is exhausted.
 * {@link #NORMAL} inputs are scheduled work that still runs when due unless a
 * runtime documents a stricter rule.
 */
public enum InputPriority {
    CRITICAL,
    NORMAL,
    OPTIONAL
}
