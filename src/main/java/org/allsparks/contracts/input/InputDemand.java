package org.allsparks.contracts.input;

/**
 * Loop-time request to capture a bound signal on the next sampling cycle.
 *
 * <p>This is not a configuration-time {@link InputRegistrar} and not a
 * scheduler. Implementations must not read hardware from
 * {@link #requestOnce(SignalKey)}. They only mark the key due for the
 * <em>next</em> capture. Calling this during or after the current capture
 * does not add work to that capture.
 *
 * <p>Unknown or unbound keys fail fast. Duplicate requests for the same key
 * collapse to one physical read. {@link #isRequested(SignalKey)} is a cache-only
 * peek of that mark; it must not read hardware.
 */
public interface InputDemand {

    void requestOnce(SignalKey<?> key);

    /**
     * True when {@code key} is already marked for the next capture.
     * Unknown keys fail fast. Must not read hardware.
     */
    boolean isRequested(SignalKey<?> key);
}
