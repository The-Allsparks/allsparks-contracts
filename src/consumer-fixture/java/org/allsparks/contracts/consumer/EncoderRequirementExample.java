package org.allsparks.contracts.consumer;

import java.util.function.IntSupplier;
import org.allsparks.contracts.input.InputPriority;
import org.allsparks.contracts.input.InputRegistrar;
import org.allsparks.contracts.input.InputRequirements;
import org.allsparks.contracts.input.SamplingPolicy;
import org.allsparks.contracts.input.SignalKey;

/**
 * Plain Java 11 consumer compiled against the contracts JAR only. Shows a
 * library declaring an encoder without depending on a sampling runtime.
 */
public final class EncoderRequirementExample {
    public static final SignalKey<Integer> ELEVATOR_POSITION = SignalKey.intKey("elevator", "position");

    private EncoderRequirementExample() {}

    public static InputRequirements declare() {
        return InputRequirements.create().require(ELEVATOR_POSITION, SamplingPolicy.everyCycle(), InputPriority.NORMAL);
    }

    public static IntSupplier standaloneOrCached(IntSupplier source) {
        return source;
    }

    public static void register(InputRegistrar registrar) {
        declare().registerWith(registrar);
    }
}
