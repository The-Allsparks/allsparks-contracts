package org.allsparks.contracts.input;

/**
 * Configuration-time sink for input declarations.
 *
 * <p>Functional libraries depend on this interface, not on a sampling
 * runtime. A runtime such as PULSE may implement it. Implementations must not
 * read hardware from these methods.
 *
 * <p>This is not a loop or session lifecycle. There is no {@code start},
 * {@code stop}, or {@code periodic} here.
 */
public interface InputRegistrar {

    void require(SignalKey<?> key, SamplingPolicy policy, InputPriority priority);

    void requireGroup(String groupId, SamplingPolicy policy, InputPriority priority, SignalKey<?>... members);
}
