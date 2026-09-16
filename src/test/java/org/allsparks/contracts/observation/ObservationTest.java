package org.allsparks.contracts.observation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ObservationTest {

    @Test
    void unknownConfidenceIsNotZero() {
        Confidence unknown = Confidence.unknown();
        assertFalse(unknown.isKnown());
        assertFalse(unknown.value().isPresent());
        assertEquals("unknown", unknown.toString());
        assertEquals(unknown, Confidence.unknown());
    }

    @Test
    void confidenceRejectsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> Confidence.of(-0.01d));
        assertThrows(IllegalArgumentException.class, () -> Confidence.of(1.01d));
        assertThrows(IllegalArgumentException.class, () -> Confidence.of(Double.NaN));
        assertTrue(Confidence.of(0.0d).isKnown());
        assertTrue(Confidence.of(1.0d).isKnown());
        assertEquals(0.5d, Confidence.of(0.5d).value().getAsDouble());
    }

    @Test
    void validityHasNoSideEffects() {
        Validity[] values = Validity.values();
        assertEquals(6, values.length);
        assertEquals(Validity.VALID, Validity.valueOf("VALID"));
        assertEquals(Validity.INVALID, Validity.valueOf("INVALID"));
    }
}
