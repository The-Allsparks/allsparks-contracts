package org.allsparks.contracts.time;

/**
 * Injectable monotonic elapsed time.
 *
 * <p>Values are nanoseconds since an arbitrary origin. This is not UTC, not
 * wall-clock time, not camera capture time, and not synchronized across
 * devices. Subtracting values from different clock instances is undefined.
 *
 * <p>{@link System#nanoTime()} wraps on the order of 292 years. Treat wrap as
 * out of scope for a match-length session.
 *
 * <p>Implementations must not start threads or own hardware. Tests inject a
 * {@link FakeMonotonicClock} or a lambda.
 */
@FunctionalInterface
public interface MonotonicClock {

    /**
     * Current monotonic time in nanoseconds.
     *
     * @return nanoseconds since this clock's origin
     */
    long nowNanos();
}
