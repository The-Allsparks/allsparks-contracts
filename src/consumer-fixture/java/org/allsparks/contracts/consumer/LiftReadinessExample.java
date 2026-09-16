package org.allsparks.contracts.consumer;

import java.util.Collections;
import org.allsparks.contracts.identity.CapabilityId;
import org.allsparks.contracts.identity.ComponentId;
import org.allsparks.contracts.status.Readiness;
import org.allsparks.contracts.status.Reason;
import org.allsparks.contracts.time.MonotonicClock;
import org.allsparks.contracts.time.SystemMonotonicClock;

/** Plain Java 11 consumer compiled against the contracts JAR only. */
public final class LiftReadinessExample {
    private LiftReadinessExample() {}

    public static Readiness liftReadiness(MonotonicClock clock) {
        CapabilityId lift = CapabilityId.of("mechanism.lift");
        ComponentId source = ComponentId.of("lift-encoder");
        return Readiness.of(lift, Readiness.State.READY, Collections.emptyList(), clock.nowNanos(), source);
    }

    public static void main(String[] args) {
        Readiness readiness = liftReadiness(SystemMonotonicClock.INSTANCE);
        if (!readiness.isReady()) {
            for (Reason reason : readiness.reasons()) {
                System.out.println(reason.code() + ": " + reason.message());
            }
            return;
        }
        System.out.println(readiness.capability() + " is ready");
    }
}
