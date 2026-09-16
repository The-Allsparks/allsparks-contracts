package org.allsparks.contracts.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.allsparks.contracts.health.HealthSeverity;
import org.allsparks.contracts.identity.CapabilityId;
import org.allsparks.contracts.identity.ComponentId;
import org.junit.jupiter.api.Test;

class StatusTest {

    private static final CapabilityId LIFT = CapabilityId.of("mechanism.lift");

    @Test
    void availabilityUnknownIsNotAvailable() {
        assertTrue(Availability.AVAILABLE.isAvailable());
        assertFalse(Availability.DEGRADED.isAvailable());
        assertFalse(Availability.UNAVAILABLE.isAvailable());
        assertFalse(Availability.UNKNOWN.isAvailable());
    }

    @Test
    void readinessDistinguishesStatesAndUnknownIsNeverReady() {
        assertTrue(readiness(Readiness.State.READY).isReady());
        assertFalse(readiness(Readiness.State.NOT_READY).isReady());
        assertFalse(readiness(Readiness.State.DEGRADED).isReady());
        assertFalse(readiness(Readiness.State.UNAVAILABLE).isReady());
        assertFalse(readiness(Readiness.State.UNKNOWN).isReady());
    }

    @Test
    void readinessReasonsAreImmutable() {
        List<Reason> mutable = new ArrayList<>();
        mutable.add(Reason.of("COLD", "lift is cold"));
        Readiness readiness = Readiness.of(LIFT, Readiness.State.NOT_READY, mutable, 10L);
        mutable.clear();
        assertEquals(1, readiness.reasons().size());
        assertThrows(
                UnsupportedOperationException.class, () -> readiness.reasons().add(Reason.of("X", "x")));
    }

    @Test
    void readinessKeepsOptionalSource() {
        ComponentId source = ComponentId.of("lift-encoder");
        Readiness readiness = Readiness.of(LIFT, Readiness.State.READY, Collections.emptyList(), 5L, source);
        assertEquals(source, readiness.source().orElseThrow());
        assertEquals(5L, readiness.evaluatedAtNanos());
        assertFalse(readiness(Readiness.State.READY).source().isPresent());
    }

    @Test
    void reasonRequiresCodeAndMessage() {
        assertThrows(IllegalArgumentException.class, () -> Reason.of(null, "msg"));
        assertThrows(IllegalArgumentException.class, () -> Reason.of(" ", "msg"));
        assertThrows(IllegalArgumentException.class, () -> Reason.of("CODE", ""));
        Reason reason = Reason.of("STALE_INPUT", "snapshot too old", HealthSeverity.WARNING);
        assertEquals("STALE_INPUT", reason.code());
        assertEquals(HealthSeverity.WARNING, reason.severity().orElseThrow());
        assertFalse(reason.source().isPresent());
    }

    private static Readiness readiness(Readiness.State state) {
        return Readiness.of(LIFT, state, Collections.emptyList(), 0L);
    }
}
