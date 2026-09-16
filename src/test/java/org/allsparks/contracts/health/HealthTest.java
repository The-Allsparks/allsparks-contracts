package org.allsparks.contracts.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.allsparks.contracts.identity.ComponentId;
import org.junit.jupiter.api.Test;

class HealthTest {

    @Test
    void severityHasNoSideEffects() {
        assertEquals(6, HealthSeverity.values().length);
        assertEquals(HealthSeverity.INFO, HealthSeverity.valueOf("INFO"));
        assertEquals(HealthSeverity.STOP_ROBOT, HealthSeverity.valueOf("STOP_ROBOT"));
    }

    @Test
    void findingIsImmutableAndRejectsBlankCode() {
        ComponentId source = ComponentId.of("beacon-link");
        HealthFinding finding = HealthFinding.of(source, "LINK_STALE", HealthSeverity.WARNING, "link aged out", 42L);
        assertEquals(source, finding.source());
        assertEquals("LINK_STALE", finding.code());
        assertEquals(HealthSeverity.WARNING, finding.severity());
        assertEquals("link aged out", finding.message());
        assertEquals(42L, finding.timestampNanos());
        assertThrows(
                IllegalArgumentException.class, () -> HealthFinding.of(source, " ", HealthSeverity.INFO, "msg", 0L));
        assertEquals(finding, HealthFinding.of(source, "LINK_STALE", HealthSeverity.WARNING, "link aged out", 42L));
    }
}
