package org.allsparks.contracts.input;

import java.util.Objects;

/**
 * One library's request that a signal be sampled.
 */
public final class InputRequirement {

    private final SignalKey<?> key;
    private final SamplingPolicy policy;
    private final InputPriority priority;

    private InputRequirement(SignalKey<?> key, SamplingPolicy policy, InputPriority priority) {
        this.key = key;
        this.policy = policy;
        this.priority = priority;
    }

    public static InputRequirement of(SignalKey<?> key, SamplingPolicy policy, InputPriority priority) {
        return new InputRequirement(
                Objects.requireNonNull(key, "key"),
                Objects.requireNonNull(policy, "policy"),
                Objects.requireNonNull(priority, "priority"));
    }

    public SignalKey<?> key() {
        return key;
    }

    public SamplingPolicy policy() {
        return policy;
    }

    public InputPriority priority() {
        return priority;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InputRequirement)) {
            return false;
        }
        InputRequirement that = (InputRequirement) other;
        return key.equals(that.key) && policy.equals(that.policy) && priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, policy, priority);
    }

    @Override
    public String toString() {
        return key + " " + policy + " " + priority;
    }
}
