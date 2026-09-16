package org.allsparks.contracts.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class IdentifierTest {

    @Test
    void componentIdRejectsNullEmptyAndBlank() {
        assertThrows(IllegalArgumentException.class, () -> ComponentId.of(null));
        assertThrows(IllegalArgumentException.class, () -> ComponentId.of(""));
        assertThrows(IllegalArgumentException.class, () -> ComponentId.of("   "));
    }

    @Test
    void capabilityIdRejectsNullEmptyAndBlank() {
        assertThrows(IllegalArgumentException.class, () -> CapabilityId.of(null));
        assertThrows(IllegalArgumentException.class, () -> CapabilityId.of(""));
        assertThrows(IllegalArgumentException.class, () -> CapabilityId.of("\t"));
    }

    @Test
    void identifiersTrimAndHaveStableEquality() {
        ComponentId left = ComponentId.of("  lift-encoder  ");
        ComponentId right = ComponentId.of("lift-encoder");
        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
        assertEquals("lift-encoder", left.toString());
        assertEquals("lift-encoder", left.value());
        assertNotEquals(ComponentId.of("other"), left);
    }

    @Test
    void capabilityEqualityIsCaseSensitive() {
        CapabilityId mixed = CapabilityId.of("drive.translation");
        assertEquals(mixed, CapabilityId.of("drive.translation"));
        assertNotEquals(mixed, CapabilityId.of("Drive.translation"));
        assertEquals("drive.translation", mixed.toString());
    }
}
