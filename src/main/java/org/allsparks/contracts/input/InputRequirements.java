package org.allsparks.contracts.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Configuration-time collector of input declarations.
 *
 * <p>Reusable libraries fill this during initialization and apply it to an
 * {@link InputRegistrar}. Duplicate keys are retained so a runtime can union
 * policies; this type does not bind hardware.
 */
public final class InputRequirements {

    private final List<InputRequirement> requirements;
    private final List<CoherentGroup> groups;

    private InputRequirements(List<InputRequirement> requirements, List<CoherentGroup> groups) {
        this.requirements = requirements;
        this.groups = groups;
    }

    public static InputRequirements create() {
        return new InputRequirements(new ArrayList<InputRequirement>(), new ArrayList<CoherentGroup>());
    }

    public InputRequirements require(SignalKey<?> key, SamplingPolicy policy) {
        return require(key, policy, InputPriority.NORMAL);
    }

    public InputRequirements require(SignalKey<?> key, SamplingPolicy policy, InputPriority priority) {
        requirements.add(InputRequirement.of(key, policy, priority));
        return this;
    }

    public InputRequirements requireGroup(String groupId, SamplingPolicy policy, SignalKey<?>... members) {
        return requireGroup(groupId, policy, InputPriority.NORMAL, members);
    }

    public InputRequirements requireGroup(
            String groupId, SamplingPolicy policy, InputPriority priority, SignalKey<?>... members) {
        Objects.requireNonNull(members, "members");
        groups.add(CoherentGroup.of(groupId, policy, priority, Arrays.asList(members)));
        for (SignalKey<?> member : members) {
            requirements.add(InputRequirement.of(member, policy, priority));
        }
        return this;
    }

    public List<InputRequirement> requirements() {
        return Collections.unmodifiableList(requirements);
    }

    public List<CoherentGroup> groups() {
        return Collections.unmodifiableList(groups);
    }

    public void registerWith(InputRegistrar registrar) {
        Objects.requireNonNull(registrar, "registrar");
        for (CoherentGroup group : groups) {
            List<SignalKey<?>> members = group.members();
            SignalKey<?>[] keys = members.toArray(new SignalKey<?>[0]);
            registrar.requireGroup(group.id(), group.policy(), group.priority(), keys);
        }
        for (InputRequirement requirement : requirements) {
            registrar.require(requirement.key(), requirement.policy(), requirement.priority());
        }
    }
}
