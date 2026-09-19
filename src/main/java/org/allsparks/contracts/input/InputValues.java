package org.allsparks.contracts.input;

import org.allsparks.contracts.observation.Validity;

/**
 * Read-only view of published input samples.
 *
 * <p>Implementations must never invoke physical hardware from these methods.
 * Primitive getters exist so encoder-scale consumers can avoid boxing.
 * {@link #validity(SignalKey)}, {@link #isFresh(SignalKey)}, and the capture
 * timestamp / cycle accessors exist so consumers that need freshness can skip
 * {@link #get(SignalKey)} and its {@link Sample} allocation.
 *
 * <p>{@link #contains(SignalKey)}, {@link #tryGet(SignalKey)},
 * {@link #tryGetDouble(SignalKey)}, and {@link #tryGetBoolean(SignalKey)} are
 * the cache-only peek: unknown keys are absent, never a hardware read, and never
 * a throw. Primitive {@link #getInt} / {@link #getDouble} stay fail-fast for
 * unknown keys so encoder-scale consumers still catch bind mistakes.
 *
 * <p>Default implementations of the metadata methods call {@link #get(SignalKey)}
 * and therefore allocate. Sampling runtimes override them to read primitives.
 *
 * <p>This is not a decision world model and not a logging snapshot. Missing
 * keys on primitive getters are programmer errors after freeze and must fail
 * fast.
 */
public interface InputValues {

    long currentCycleId();

    <T> Sample<T> get(SignalKey<T> key);

    int getInt(SignalKey<Integer> key);

    long getLong(SignalKey<Long> key);

    double getDouble(SignalKey<Double> key);

    boolean getBoolean(SignalKey<Boolean> key);

    /**
     * Published validity for {@code key}. Same value as
     * {@code get(key).validity()} without requiring a {@link Sample}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default Validity validity(SignalKey<?> key) {
        return get((SignalKey) key).validity();
    }

    /**
     * True only when the published sample is {@link Validity#VALID} and was
     * updated this cycle. Same value as {@code get(key).isFresh()}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default boolean isFresh(SignalKey<?> key) {
        return get((SignalKey) key).isFresh();
    }

    /**
     * Monotonic capture timestamp of the published sample, or {@code 0} when
     * the signal has never been captured.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default long captureTimestampNanos(SignalKey<?> key) {
        return get((SignalKey) key).captureTimestampNanos();
    }

    /**
     * Cycle id when the published sample was captured, or {@code -1} when the
     * signal has never been captured.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default long captureCycleId(SignalKey<?> key) {
        return get((SignalKey) key).captureCycleId();
    }

    /**
     * True when this snapshot has a slot for {@code key}, even if the sample
     * is {@link Validity#MISSING} or was not captured this cycle. Unknown keys
     * are false. Must not read hardware.
     */
    default boolean contains(SignalKey<?> key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        try {
            get(key);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    /**
     * Cache-only sample for {@code key}. Unknown keys return
     * {@link Sample#missing()} without throwing and without reading hardware.
     * Known keys that were not captured this cycle are not {@link Sample#isFresh()}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <T> Sample<T> tryGet(SignalKey<T> key) {
        if (!contains(key)) {
            return Sample.missing();
        }
        return get(key);
    }

    /**
     * Cache-only double. Unknown keys return {@link Double#NaN} without
     * throwing and without reading hardware. Known keys return
     * {@link #getDouble(SignalKey)} even when the sample is stale. Check
     * {@link #isFresh(SignalKey)} before treating the value as this cycle.
     */
    default double tryGetDouble(SignalKey<Double> key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (!contains(key)) {
            return Double.NaN;
        }
        return getDouble(key);
    }

    /**
     * Cache-only boolean. Unknown keys return {@code false} without throwing
     * and without reading hardware. Known keys return
     * {@link #getBoolean(SignalKey)} even when the sample is stale. Check
     * {@link #contains(SignalKey)} and {@link #isFresh(SignalKey)} before
     * treating {@code false} as a published flag.
     */
    default boolean tryGetBoolean(SignalKey<Boolean> key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (!contains(key)) {
            return false;
        }
        return getBoolean(key);
    }
}
