package org.allsparks.contracts.time;

/**
 * Deterministic monotonic clock for tests and replay.
 *
 * <p>This is a mutable instance, not a process-wide singleton. Production
 * code should take a {@link MonotonicClock} parameter instead of reading this
 * type. Time only moves when {@link #advanceNanos(long)} is called.
 */
public final class FakeMonotonicClock implements MonotonicClock {

    private long nanos;

    public FakeMonotonicClock() {
        this(0L);
    }

    public FakeMonotonicClock(long initialNanos) {
        this.nanos = initialNanos;
    }

    @Override
    public long nowNanos() {
        return nanos;
    }

    /**
     * Advance this clock by {@code deltaNanos}.
     *
     * @param deltaNanos non-negative nanoseconds to add
     * @throws IllegalArgumentException if {@code deltaNanos} is negative
     */
    public void advanceNanos(long deltaNanos) {
        if (deltaNanos < 0L) {
            throw new IllegalArgumentException("Clock cannot move backwards: " + deltaNanos);
        }
        this.nanos += deltaNanos;
    }
}
