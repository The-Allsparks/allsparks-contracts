package org.allsparks.contracts.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.allsparks.contracts.identity.CapabilityId;
import org.allsparks.contracts.identity.ComponentId;

/**
 * Whether a requested action is complete or ready to begin for one capability.
 *
 * <p>{@link State#UNKNOWN} is never ready. This type does not command hardware
 * and does not encode match strategy. {@code evaluatedAtNanos} is monotonic
 * process time from the clock that produced the evaluation.
 */
public final class Readiness {

    public enum State {
        READY,
        NOT_READY,
        DEGRADED,
        UNAVAILABLE,
        UNKNOWN
    }

    private final CapabilityId capability;
    private final State state;
    private final List<Reason> reasons;
    private final long evaluatedAtNanos;
    private final ComponentId source;

    private Readiness(
            CapabilityId capability, State state, List<Reason> reasons, long evaluatedAtNanos, ComponentId source) {
        this.capability = capability;
        this.state = state;
        this.reasons = reasons;
        this.evaluatedAtNanos = evaluatedAtNanos;
        this.source = source;
    }

    public static Readiness of(CapabilityId capability, State state, List<Reason> reasons, long evaluatedAtNanos) {
        return create(capability, state, reasons, evaluatedAtNanos, null);
    }

    public static Readiness of(
            CapabilityId capability, State state, List<Reason> reasons, long evaluatedAtNanos, ComponentId source) {
        Objects.requireNonNull(source, "source");
        return create(capability, state, reasons, evaluatedAtNanos, source);
    }

    private static Readiness create(
            CapabilityId capability, State state, List<Reason> reasons, long evaluatedAtNanos, ComponentId source) {
        Objects.requireNonNull(capability, "capability");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(reasons, "reasons");
        List<Reason> copy = new ArrayList<>(reasons);
        for (Reason reason : copy) {
            Objects.requireNonNull(reason, "reason");
        }
        return new Readiness(capability, state, Collections.unmodifiableList(copy), evaluatedAtNanos, source);
    }

    public CapabilityId capability() {
        return capability;
    }

    public State state() {
        return state;
    }

    public List<Reason> reasons() {
        return reasons;
    }

    public long evaluatedAtNanos() {
        return evaluatedAtNanos;
    }

    public Optional<ComponentId> source() {
        return Optional.ofNullable(source);
    }

    /**
     * {@code true} only when {@link State#READY}. {@link State#UNKNOWN} never
     * returns {@code true}.
     */
    public boolean isReady() {
        return state == State.READY;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Readiness)) {
            return false;
        }
        Readiness that = (Readiness) other;
        return evaluatedAtNanos == that.evaluatedAtNanos
                && capability.equals(that.capability)
                && state == that.state
                && reasons.equals(that.reasons)
                && Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capability, state, reasons, evaluatedAtNanos, source);
    }

    @Override
    public String toString() {
        return capability + "=" + state;
    }
}
