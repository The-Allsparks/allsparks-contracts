package org.allsparks.contracts.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.allsparks.contracts.identity.ComponentId;
import org.allsparks.contracts.observation.Validity;
import org.allsparks.contracts.status.Reason;
import org.junit.jupiter.api.Test;

class InputContractTest {

    private static final SignalKey<Integer> LEFT = SignalKey.intKey("drive", "leftEncoder");
    private static final SignalKey<Integer> RIGHT = SignalKey.intKey("drive", "rightEncoder");
    private static final SignalKey<Integer> STRAFE = SignalKey.intKey("drive", "strafeEncoder");
    private static final SignalKey<Double> HEADING = SignalKey.doubleKey("imu", "heading");

    @Test
    void signalKeyEqualityUsesNamespacePropertyAndType() {
        SignalKey<Integer> again = SignalKey.intKey(ComponentId.of("drive"), "leftEncoder");
        assertEquals(LEFT, again);
        assertEquals(LEFT.hashCode(), again.hashCode());
        assertNotEquals(LEFT, RIGHT);
        assertNotEquals(LEFT, SignalKey.longKey("drive", "leftEncoder"));
        assertEquals("drive/leftEncoder", LEFT.qualifiedName());
        assertTrue(LEFT.compareTo(RIGHT) < 0);
        assertEquals(
                Objects.hash(LEFT.namespace(), LEFT.property(), LEFT.valueType(), LEFT.objectType()), LEFT.hashCode());
    }

    @Test
    void inputValuesMetadataDefaultsMatchGet() {
        Sample<Integer> published = Sample.of(13, Validity.VALID, 200L, 4L, true, null);
        InputValues values = new FixedValues(published);
        assertEquals(Validity.VALID, values.validity(LEFT));
        assertTrue(values.isFresh(LEFT));
        assertEquals(200L, values.captureTimestampNanos(LEFT));
        assertEquals(4L, values.captureCycleId(LEFT));
        assertEquals(13, values.getInt(LEFT));
    }

    @Test
    void signalKeyRejectsBlankProperty() {
        assertThrows(IllegalArgumentException.class, () -> SignalKey.intKey("drive", "  "));
        assertThrows(IllegalArgumentException.class, () -> SignalKey.of("drive", "pose", int.class));
    }

    @Test
    void evenPlusOddSimplifiesToEveryCycle() {
        SamplingPolicy merged = SamplingPolicy.everyEven().union(SamplingPolicy.everyOdd());
        assertEquals(SamplingPolicy.everyCycle(), merged);
        assertTrue(merged.isDue(0L));
        assertTrue(merged.isDue(1L));
    }

    @Test
    void thirdAPlusBPlusCSimplifiesToEveryCycle() {
        SamplingPolicy merged =
                SamplingPolicy.everyThirdA().union(SamplingPolicy.everyThirdB()).union(SamplingPolicy.everyThirdC());
        assertEquals(SamplingPolicy.everyCycle(), merged);
    }

    @Test
    void thirdAPlusCKeepsPhasesAAndC() {
        SamplingPolicy merged = SamplingPolicy.everyThirdA().union(SamplingPolicy.everyThirdC());
        assertTrue(merged.isDue(0L));
        assertFalse(merged.isDue(1L));
        assertTrue(merged.isDue(2L));
        assertTrue(merged.isDue(3L));
        assertFalse(merged.isDue(4L));
        assertTrue(merged.isDue(5L));
        assertEquals(2, merged.predicateCount());
    }

    @Test
    void mixedPeriodsDoNotRequireUnboundedLcm() {
        SamplingPolicy merged = SamplingPolicy.everyNth(2, 0).union(SamplingPolicy.everyNth(4, 1));
        assertTrue(merged.isDue(0L));
        assertTrue(merged.isDue(1L));
        assertTrue(merged.isDue(2L));
        assertFalse(merged.isDue(3L));
        assertEquals(2, merged.predicateCount());
    }

    @Test
    void spreadUnionUsesGcd() {
        SamplingPolicy merged = SamplingPolicy.spreadAcrossCycles(2).union(SamplingPolicy.spreadAcrossCycles(4));
        assertTrue(merged.isAutoPhase());
        assertEquals(2, merged.spreadPeriod());
        SamplingPolicy assigned = merged.assignPhase(1);
        assertTrue(assigned.isDue(1L));
        assertFalse(assigned.isDue(0L));
    }

    @Test
    void sampleAgeAndFreshness() {
        Sample<Integer> missing = Sample.missing();
        assertEquals(Validity.MISSING, missing.validity());
        assertFalse(missing.isFresh());
        assertEquals(Long.MAX_VALUE, missing.ageCycles(3L));

        Sample<Integer> stale = Sample.of(12, Validity.STALE, 100L, 2L, false, null);
        assertFalse(stale.isFresh());
        assertEquals(1L, stale.ageCycles(3L));
        assertEquals(Integer.valueOf(12), stale.orNull());

        Sample<Integer> failed =
                Sample.of(12, Validity.INVALID, 100L, 3L, false, Reason.of("read-failed", "encoder threw"));
        assertFalse(failed.isFresh());
        assertTrue(failed.fault().isPresent());

        Sample<Integer> fresh = Sample.of(13, Validity.VALID, 200L, 4L, true, null);
        assertTrue(fresh.isFresh());
        assertEquals(0L, fresh.ageCycles(4L));
    }

    @Test
    void requirementsCollectGroupsAndRegister() {
        InputRequirements requirements = InputRequirements.create()
                .require(HEADING, SamplingPolicy.everyCycle(), InputPriority.CRITICAL)
                .requireGroup(
                        "drive-localization",
                        SamplingPolicy.everyCycle(),
                        InputPriority.CRITICAL,
                        LEFT,
                        RIGHT,
                        STRAFE,
                        HEADING);
        assertEquals(1, requirements.groups().size());
        RecordingRegistrar registrar = new RecordingRegistrar();
        requirements.registerWith(registrar);
        assertEquals(1, registrar.groups.size());
        assertEquals("drive-localization", registrar.groups.get(0));
        assertFalse(registrar.requirements.isEmpty());
    }

    @Test
    void autoPhaseIsDueFailsUntilAssigned() {
        SamplingPolicy spread = SamplingPolicy.spreadAcrossCycles(3);
        assertThrows(IllegalStateException.class, () -> spread.isDue(0L));
        assertTrue(spread.assignPhase(2).isDue(2L));
        assertFalse(spread.assignPhase(2).isDue(0L));
    }

    @Test
    void onDemandIsNeverCycleDue() {
        SamplingPolicy onDemand = SamplingPolicy.onDemand();
        assertTrue(onDemand.isOnDemand());
        assertFalse(onDemand.isDue(0L));
        assertFalse(onDemand.isDue(1L, 1_000_000L, -1L));
        assertEquals("onDemand", onDemand.toString());
        assertEquals(SamplingPolicy.everyCycle(), onDemand.union(SamplingPolicy.everyCycle()));
        assertTrue(onDemand.union(SamplingPolicy.onDemand()).isOnDemand());
    }

    @Test
    void motorSignalsRejectBlankDeviceName() {
        assertThrows(IllegalArgumentException.class, () -> MotorSignals.overCurrent("  "));
        assertEquals(
                "motor/front_left_drive/overCurrent",
                MotorSignals.overCurrent("front_left_drive").qualifiedName());
        assertEquals(
                "motor/front_left_drive/currentAmps",
                MotorSignals.currentAmps("front_left_drive").qualifiedName());
    }

    @Test
    void tryGetUnknownKeyIsMissingWithoutThrowing() {
        InputValues values = new EmptyValues();
        SignalKey<Double> unknown = SignalKey.doubleKey("motor", "missing/currentAmps");
        assertFalse(values.contains(unknown));
        Sample<Double> sample = values.tryGet(unknown);
        assertEquals(Validity.MISSING, sample.validity());
        assertFalse(sample.isFresh());
        assertTrue(Double.isNaN(values.tryGetDouble(unknown)));
        assertFalse(values.tryGetBoolean(SignalKey.booleanKey("motor", "missing/overCurrent")));
    }

    @Test
    void timeBasedDueUsesElapsedNanos() {
        SamplingPolicy everyMs = SamplingPolicy.everyNanos(1_000_000L);
        assertTrue(everyMs.isTimeBased());
        assertTrue(everyMs.isDue(1L, 0L, -1L));
        assertFalse(everyMs.isDue(2L, 500_000L, 0L));
        assertTrue(everyMs.isDue(3L, 1_000_000L, 0L));
        SamplingPolicy faster = everyMs.union(SamplingPolicy.everyNanos(2_000_000L));
        assertEquals(1_000_000L, faster.periodNanos());
        SamplingPolicy mixed = SamplingPolicy.everyEven().union(everyMs);
        assertTrue(mixed.isDue(0L, 0L, 0L));
        assertFalse(mixed.isDue(1L, 500_000L, 0L));
        assertTrue(mixed.isDue(1L, 1_000_000L, 0L));
        SamplingPolicy spreadTime = SamplingPolicy.spreadAcrossCycles(3).union(everyMs);
        SamplingPolicy assigned = spreadTime.assignPhase(1);
        assertTrue(assigned.isTimeBased());
        assertEquals(1_000_000L, assigned.periodNanos());
        assertTrue(assigned.isDue(1L, 0L, 0L));
        assertFalse(assigned.isDue(0L, 500_000L, 0L));
    }

    private static final class FixedValues implements InputValues {
        private final Sample<Integer> published;

        FixedValues(Sample<Integer> published) {
            this.published = published;
        }

        @Override
        public long currentCycleId() {
            return published.captureCycleId();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Sample<T> get(SignalKey<T> key) {
            return (Sample<T>) published;
        }

        @Override
        public int getInt(SignalKey<Integer> key) {
            Integer value = published.orNull();
            return value == null ? 0 : value.intValue();
        }

        @Override
        public long getLong(SignalKey<Long> key) {
            return 0L;
        }

        @Override
        public double getDouble(SignalKey<Double> key) {
            return 0.0d;
        }

        @Override
        public boolean getBoolean(SignalKey<Boolean> key) {
            return false;
        }
    }

    private static final class EmptyValues implements InputValues {
        @Override
        public long currentCycleId() {
            return 0L;
        }

        @Override
        public <T> Sample<T> get(SignalKey<T> key) {
            throw new IllegalArgumentException("unknown " + key.qualifiedName());
        }

        @Override
        public int getInt(SignalKey<Integer> key) {
            throw new IllegalArgumentException("unknown");
        }

        @Override
        public long getLong(SignalKey<Long> key) {
            throw new IllegalArgumentException("unknown");
        }

        @Override
        public double getDouble(SignalKey<Double> key) {
            throw new IllegalArgumentException("unknown");
        }

        @Override
        public boolean getBoolean(SignalKey<Boolean> key) {
            throw new IllegalArgumentException("unknown");
        }
    }

    private static final class RecordingRegistrar implements InputRegistrar {
        private final List<InputRequirement> requirements = new ArrayList<>();
        private final List<String> groups = new ArrayList<>();

        @Override
        public void require(SignalKey<?> key, SamplingPolicy policy, InputPriority priority) {
            requirements.add(InputRequirement.of(key, policy, priority));
        }

        @Override
        public void requireGroup(
                String groupId, SamplingPolicy policy, InputPriority priority, SignalKey<?>... members) {
            groups.add(groupId);
        }
    }
}
