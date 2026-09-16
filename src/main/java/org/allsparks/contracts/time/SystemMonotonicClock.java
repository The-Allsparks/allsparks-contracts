package org.allsparks.contracts.time;

/**
 * Stateless production clock backed by {@link System#nanoTime()}.
 *
 * <p>{@link #INSTANCE} is safe to share because this type has no mutable
 * state and starts no threads. Tests should inject {@link FakeMonotonicClock}
 * instead of mutating this instance.
 */
public final class SystemMonotonicClock implements MonotonicClock {

    public static final SystemMonotonicClock INSTANCE = new SystemMonotonicClock();

    private SystemMonotonicClock() {}

    @Override
    public long nowNanos() {
        return System.nanoTime();
    }
}
