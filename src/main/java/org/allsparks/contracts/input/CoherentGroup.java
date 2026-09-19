package org.allsparks.contracts.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Inputs that must share one due decision.
 *
 * <p>Runtimes must not independently phase-shift members of the same group.
 * Conflicting member schedules are unioned; the group is due when any merged
 * predicate matches, so related localization or safety inputs are captured
 * together.
 */
public final class CoherentGroup {

    private final String id;
    private final SamplingPolicy policy;
    private final InputPriority priority;
    private final List<SignalKey<?>> members;

    private CoherentGroup(String id, SamplingPolicy policy, InputPriority priority, List<SignalKey<?>> members) {
        this.id = id;
        this.policy = policy;
        this.priority = priority;
        this.members = members;
    }

    public static CoherentGroup of(
            String id, SamplingPolicy policy, InputPriority priority, List<SignalKey<?>> members) {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(members, "members");
        if (members.size() < 2) {
            throw new IllegalArgumentException("A coherent group needs at least two members");
        }
        List<SignalKey<?>> copy = new ArrayList<>(members.size());
        for (SignalKey<?> member : members) {
            Objects.requireNonNull(member, "member");
            copy.add(member);
        }
        return new CoherentGroup(requireId(id), policy, priority, Collections.unmodifiableList(copy));
    }

    public String id() {
        return id;
    }

    public SamplingPolicy policy() {
        return policy;
    }

    public InputPriority priority() {
        return priority;
    }

    public List<SignalKey<?>> members() {
        return members;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CoherentGroup)) {
            return false;
        }
        CoherentGroup that = (CoherentGroup) other;
        return id.equals(that.id)
                && policy.equals(that.policy)
                && priority == that.priority
                && members.equals(that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, policy, priority, members);
    }

    @Override
    public String toString() {
        return id + members;
    }

    private static String requireId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("group id must not be null");
        }
        String trimmed = id.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("group id must not be blank");
        }
        return trimmed;
    }
}
