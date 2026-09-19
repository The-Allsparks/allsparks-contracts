package org.allsparks.contracts.input;

import java.util.Objects;
import java.util.Optional;
import org.allsparks.contracts.observation.Validity;
import org.allsparks.contracts.status.Reason;

/**
 * One published observation of a signal.
 *
 * <p>{@code captureTimestampNanos} is monotonic process time from the same
 * clock the runtime used for {@code capture}. It is not UTC and not camera
 * capture time.
 *
 * <p>A retained last-known-good numeric value with {@link Validity#INVALID}
 * or {@link Validity#STALE} is not fresh. Callers must inspect
 * {@link #validity()} before treating {@link #value()} as a current
 * measurement.
 *
 * @param <T> boxed Java type of the sampled value
 */
public final class Sample<T> {

    private final T value;
    private final Validity validity;
    private final long captureTimestampNanos;
    private final long captureCycleId;
    private final boolean updatedThisCycle;
    private final Reason fault;

    private Sample(
            T value,
            Validity validity,
            long captureTimestampNanos,
            long captureCycleId,
            boolean updatedThisCycle,
            Reason fault) {
        this.value = value;
        this.validity = validity;
        this.captureTimestampNanos = captureTimestampNanos;
        this.captureCycleId = captureCycleId;
        this.updatedThisCycle = updatedThisCycle;
        this.fault = fault;
    }

    public static <T> Sample<T> missing() {
        return new Sample<>(null, Validity.MISSING, 0L, -1L, false, null);
    }

    public static <T> Sample<T> of(
            T value,
            Validity validity,
            long captureTimestampNanos,
            long captureCycleId,
            boolean updatedThisCycle,
            Reason fault) {
        Objects.requireNonNull(validity, "validity");
        if (validity == Validity.VALID && updatedThisCycle && value == null) {
            throw new IllegalArgumentException("A fresh VALID sample must have a value");
        }
        return new Sample<>(value, validity, captureTimestampNanos, captureCycleId, updatedThisCycle, fault);
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    /**
     * Boxed value, or {@code null} when missing. Prefer {@link #value()} or a
     * primitive getter on the runtime snapshot for encoder-scale reads.
     */
    public T orNull() {
        return value;
    }

    public Validity validity() {
        return validity;
    }

    public long captureTimestampNanos() {
        return captureTimestampNanos;
    }

    public long captureCycleId() {
        return captureCycleId;
    }

    public boolean updatedThisCycle() {
        return updatedThisCycle;
    }

    public Optional<Reason> fault() {
        return Optional.ofNullable(fault);
    }

    public boolean isValid() {
        return validity == Validity.VALID;
    }

    /**
     * True only when this sample was captured during the current cycle and
     * marked {@link Validity#VALID}. Skipped, deferred, and failed reads are
     * never fresh.
     */
    public boolean isFresh() {
        return validity == Validity.VALID && updatedThisCycle;
    }

    /**
     * Age in cycles relative to {@code currentCycleId}. Never-captured samples
     * report {@link Long#MAX_VALUE}.
     */
    public long ageCycles(long currentCycleId) {
        if (captureCycleId < 0L) {
            return Long.MAX_VALUE;
        }
        long age = currentCycleId - captureCycleId;
        return age < 0L ? 0L : age;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Sample)) {
            return false;
        }
        Sample<?> that = (Sample<?>) other;
        return captureTimestampNanos == that.captureTimestampNanos
                && captureCycleId == that.captureCycleId
                && updatedThisCycle == that.updatedThisCycle
                && validity == that.validity
                && Objects.equals(value, that.value)
                && Objects.equals(fault, that.fault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, validity, captureTimestampNanos, captureCycleId, updatedThisCycle, fault);
    }

    @Override
    public String toString() {
        return validity + (updatedThisCycle ? " fresh@" : " stale@") + captureCycleId;
    }
}
