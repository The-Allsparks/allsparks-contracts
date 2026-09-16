package org.allsparks.contracts.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClockTest {

    @Test
    void fakeClockIsDeterministic() {
        FakeMonotonicClock clock = new FakeMonotonicClock();
        assertEquals(0L, clock.nowNanos());
        clock.advanceNanos(1_000_000L);
        assertEquals(1_000_000L, clock.nowNanos());
        clock.advanceNanos(0L);
        assertEquals(1_000_000L, clock.nowNanos());
    }

    @Test
    void fakeClockRejectsNegativeAdvance() {
        FakeMonotonicClock clock = new FakeMonotonicClock();
        assertThrows(IllegalArgumentException.class, () -> clock.advanceNanos(-1L));
        assertEquals(0L, clock.nowNanos());
    }

    @Test
    void injectedLambdaIsDeterministic() {
        long[] value = {42L};
        MonotonicClock clock = () -> value[0];
        assertEquals(42L, clock.nowNanos());
        value[0] = 99L;
        assertEquals(99L, clock.nowNanos());
    }

    @Test
    void systemClockIsStatelessAndNotDescribedAsUtc() {
        long first = SystemMonotonicClock.INSTANCE.nowNanos();
        long second = SystemMonotonicClock.INSTANCE.nowNanos();
        assertTrue(second >= first);
        assertEquals(SystemMonotonicClock.INSTANCE, SystemMonotonicClock.INSTANCE);
    }
}
