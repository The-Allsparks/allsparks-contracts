package org.allsparks.contracts.input;

import java.util.Arrays;
import java.util.Objects;

/**
 * Period-and-phase capture schedule, or an automatic-spread request.
 *
 * <p>Canonical cycle schedules:
 *
 * <pre>
 * Every cycle: period 1, phase 0
 * Every-even:  period 2, phase 0
 * Every-odd:   period 2, phase 1
 * Every-3rd-A: period 3, phase 0
 * Every-3rd-B: period 3, phase 1
 * Every-3rd-C: period 3, phase 2
 * </pre>
 *
 * <p>A union of requests for the same signal is another {@code SamplingPolicy}.
 * When the union covers every cycle it simplifies to {@link #everyCycle()}.
 * Predicate sets are used when a simple period/phase pair is not enough, so
 * mixed periods do not require an unbounded least-common-multiple expansion.
 *
 * <p>Time-based policies ({@link #everyNanos(long)}) keep signal identity
 * unchanged. A runtime honors {@link #isDue(long, long, long)} or rejects the
 * policy at compile time.
 *
 * <p>This type is a declaration envelope. It does not read hardware and is not
 * AMPER's current-read budget or TRACE's change-threshold recorder policy.
 */
public final class SamplingPolicy {

    public static final int AUTO_PHASE = -1;
    public static final int MAX_PERIOD = 64;

    private final int[] periods;
    private final int[] phases;
    private final boolean autoPhase;
    private final int spreadPeriod;
    private final int cost;
    private final long periodNanos;

    private SamplingPolicy(
            int[] periods, int[] phases, boolean autoPhase, int spreadPeriod, int cost, long periodNanos) {
        this.periods = periods;
        this.phases = phases;
        this.autoPhase = autoPhase;
        this.spreadPeriod = spreadPeriod;
        this.cost = cost;
        this.periodNanos = periodNanos;
    }

    public static SamplingPolicy everyCycle() {
        return cycle(1, 0);
    }

    /**
     * Never cycle-due and never time-due. A runtime captures the signal only
     * when a loop-time {@link InputDemand#requestOnce(SignalKey)} marks it for
     * the next capture. Bind-only signals must use this so freeze does not
     * default them to {@link #everyCycle()}.
     */
    public static SamplingPolicy onDemand() {
        return new SamplingPolicy(empty(), empty(), false, 0, 1, 0L);
    }

    public static SamplingPolicy everyEven() {
        return cycle(2, 0);
    }

    public static SamplingPolicy everyOdd() {
        return cycle(2, 1);
    }

    public static SamplingPolicy everyNth(int period, int phase) {
        return cycle(period, phase);
    }

    public static SamplingPolicy everyThirdA() {
        return cycle(3, 0);
    }

    public static SamplingPolicy everyThirdB() {
        return cycle(3, 1);
    }

    public static SamplingPolicy everyThirdC() {
        return cycle(3, 2);
    }

    /**
     * Request a read every {@code period} cycles without choosing A/B/C.
     * A compiler assigns a deterministic phase later.
     */
    public static SamplingPolicy spreadAcrossCycles(int period) {
        if (period < 1 || period > MAX_PERIOD) {
            throw new IllegalArgumentException("spread period must be in 1.." + MAX_PERIOD + ": " + period);
        }
        if (period == 1) {
            return everyCycle();
        }
        return new SamplingPolicy(empty(), empty(), true, period, 1, 0L);
    }

    /**
     * Capture when at least {@code periodNanos} monotonic time has elapsed since
     * the last successful sample. Signal identity is unchanged. May be unioned
     * with cycle predicates: the signal is due if either schedule matches.
     */
    public static SamplingPolicy everyNanos(long periodNanos) {
        if (periodNanos <= 0L) {
            throw new IllegalArgumentException("periodNanos must be positive: " + periodNanos);
        }
        return new SamplingPolicy(empty(), empty(), false, 0, 1, periodNanos);
    }

    public boolean isTimeBased() {
        return periodNanos > 0L;
    }

    /**
     * True when this policy has no cycle predicates, no automatic spread, and
     * no time period. {@link #isDue(long)} is then always false.
     */
    public boolean isOnDemand() {
        return !autoPhase && !isTimeBased() && periods.length == 0;
    }

    public boolean isAutoPhase() {
        return autoPhase;
    }

    public int spreadPeriod() {
        return spreadPeriod;
    }

    public int cost() {
        return cost;
    }

    public long periodNanos() {
        return periodNanos;
    }

    public int predicateCount() {
        return periods.length;
    }

    public int periodAt(int index) {
        return periods[index];
    }

    public int phaseAt(int index) {
        return phases[index];
    }

    /**
     * Copy of compiled cycle predicates. Empty when this policy is only an
     * automatic-spread request.
     */
    public int[] periods() {
        return Arrays.copyOf(periods, periods.length);
    }

    public int[] phases() {
        return Arrays.copyOf(phases, phases.length);
    }

    public boolean isDue(long cycleId) {
        if (autoPhase && periods.length == 0) {
            throw new IllegalStateException("Auto-phase policy is not due until a phase is assigned");
        }
        if (isTimeBased() && periods.length == 0) {
            throw new IllegalStateException("Time-based policy requires nowNanos and last capture time");
        }
        return cycleDue(cycleId);
    }

    /**
     * Due decision for mixed cycle and time schedules.
     *
     * @param lastCaptureNanos monotonic timestamp of the last successful sample,
     *     or a negative value if the signal has never been captured
     */
    public boolean isDue(long cycleId, long nowNanos, long lastCaptureNanos) {
        if (autoPhase && periods.length == 0) {
            throw new IllegalStateException("Auto-phase policy is not due until a phase is assigned");
        }
        boolean cycleDue = periods.length > 0 && cycleDue(cycleId);
        if (!isTimeBased()) {
            return cycleDue;
        }
        boolean timeDue = lastCaptureNanos < 0L || nowNanos - lastCaptureNanos >= periodNanos;
        if (periods.length == 0) {
            return timeDue;
        }
        return timeDue || cycleDue;
    }

    private boolean cycleDue(long cycleId) {
        for (int i = 0; i < periods.length; i++) {
            if (Math.floorMod(cycleId, (long) periods[i]) == phases[i]) {
                return true;
            }
        }
        return false;
    }

    public SamplingPolicy withCost(int declaredCost) {
        if (declaredCost < 1) {
            throw new IllegalArgumentException("cost must be >= 1: " + declaredCost);
        }
        return new SamplingPolicy(periods, phases, autoPhase, spreadPeriod, declaredCost, periodNanos);
    }

    /**
     * Bind an automatic-spread request to an explicit phase of its spread
     * period, then union any already-explicit predicates and time period.
     */
    public SamplingPolicy assignPhase(int phase) {
        if (!autoPhase) {
            throw new IllegalStateException("Policy does not request automatic phase assignment");
        }
        if (phase < 0 || phase >= spreadPeriod) {
            throw new IllegalArgumentException("assigned phase must be in 0.." + (spreadPeriod - 1) + ": " + phase);
        }
        SamplingPolicy assigned = cycle(spreadPeriod, phase).withCost(cost);
        if (periodNanos > 0L) {
            assigned = assigned.union(everyNanos(periodNanos).withCost(cost));
        }
        if (periods.length == 0) {
            return assigned;
        }
        return assigned.union(new SamplingPolicy(periods, phases, false, 0, cost, 0L));
    }

    /**
     * Union two requests for the same signal. Explicit phases are preserved.
     * Automatic-spread periods combine by gcd so the result remains at least
     * as frequent as either request without an unbounded LCM expansion.
     */
    public SamplingPolicy union(SamplingPolicy other) {
        Objects.requireNonNull(other, "other");
        int mergedCost = Math.max(cost, other.cost);
        long mergedNanos = 0L;
        if (isTimeBased() && other.isTimeBased()) {
            mergedNanos = Math.min(periodNanos, other.periodNanos);
        } else if (isTimeBased()) {
            mergedNanos = periodNanos;
        } else if (other.isTimeBased()) {
            mergedNanos = other.periodNanos;
        }
        boolean mergedAuto = autoPhase || other.autoPhase;
        int mergedSpread = 0;
        if (autoPhase && other.autoPhase) {
            mergedSpread = gcd(spreadPeriod, other.spreadPeriod);
            if (mergedSpread == 1) {
                mergedAuto = false;
            }
        } else if (autoPhase) {
            mergedSpread = spreadPeriod;
        } else if (other.autoPhase) {
            mergedSpread = other.spreadPeriod;
        }

        int[] mergedPeriods = concat(periods, other.periods);
        int[] mergedPhases = concat(phases, other.phases);
        int count = dedupe(mergedPeriods, mergedPhases);
        mergedPeriods = Arrays.copyOf(mergedPeriods, count);
        mergedPhases = Arrays.copyOf(mergedPhases, count);

        if (!mergedAuto && coversEveryCycle(mergedPeriods, mergedPhases)) {
            return everyCycle().withCost(mergedCost);
        }
        if (mergedAuto && mergedSpread == 1 && mergedPeriods.length == 0 && mergedNanos == 0L) {
            return everyCycle().withCost(mergedCost);
        }
        return new SamplingPolicy(mergedPeriods, mergedPhases, mergedAuto, mergedSpread, mergedCost, mergedNanos);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SamplingPolicy)) {
            return false;
        }
        SamplingPolicy that = (SamplingPolicy) other;
        return autoPhase == that.autoPhase
                && spreadPeriod == that.spreadPeriod
                && cost == that.cost
                && periodNanos == that.periodNanos
                && Arrays.equals(periods, that.periods)
                && Arrays.equals(phases, that.phases);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(autoPhase, spreadPeriod, cost, periodNanos);
        result = 31 * result + Arrays.hashCode(periods);
        result = 31 * result + Arrays.hashCode(phases);
        return result;
    }

    @Override
    public String toString() {
        if (isOnDemand()) {
            return cost == 1 ? "onDemand" : "onDemand(cost=" + cost + ")";
        }
        if (isTimeBased() && periods.length == 0 && !autoPhase) {
            return "everyNanos(" + periodNanos + ")";
        }
        if (autoPhase && periods.length == 0) {
            if (isTimeBased()) {
                return "spreadAcrossCycles(" + spreadPeriod + ")+everyNanos(" + periodNanos + ")";
            }
            return "spreadAcrossCycles(" + spreadPeriod + ")";
        }
        if (periods.length == 1 && periods[0] == 1 && !isTimeBased()) {
            return "everyCycle";
        }
        StringBuilder text = new StringBuilder("period/phase");
        if (autoPhase) {
            text.append("+spread(").append(spreadPeriod).append(')');
        }
        text.append('[');
        for (int i = 0; i < periods.length; i++) {
            if (i > 0) {
                text.append(',');
            }
            text.append(periods[i]).append('@').append(phases[i]);
        }
        text.append(']');
        if (isTimeBased()) {
            text.append("+everyNanos(").append(periodNanos).append(')');
        }
        return text.toString();
    }

    private static SamplingPolicy cycle(int period, int phase) {
        if (period < 1 || period > MAX_PERIOD) {
            throw new IllegalArgumentException("period must be in 1.." + MAX_PERIOD + ": " + period);
        }
        if (phase < 0 || phase >= period) {
            throw new IllegalArgumentException("phase must be in 0.." + (period - 1) + ": " + phase);
        }
        return new SamplingPolicy(new int[] {period}, new int[] {phase}, false, 0, 1, 0L);
    }

    private static int[] empty() {
        return new int[0];
    }

    private static int[] concat(int[] left, int[] right) {
        int[] out = Arrays.copyOf(left, left.length + right.length);
        System.arraycopy(right, 0, out, left.length, right.length);
        return out;
    }

    private static int dedupe(int[] periods, int[] phases) {
        int count = 0;
        for (int i = 0; i < periods.length; i++) {
            boolean seen = false;
            for (int j = 0; j < count; j++) {
                if (periods[j] == periods[i] && phases[j] == phases[i]) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                periods[count] = periods[i];
                phases[count] = phases[i];
                count++;
            }
        }
        return count;
    }

    static boolean coversEveryCycle(int[] periods, int[] phases) {
        if (periods.length == 0) {
            return false;
        }
        for (int i = 0; i < periods.length; i++) {
            if (periods[i] == 1) {
                return true;
            }
        }
        int span = 1;
        for (int i = 0; i < periods.length; i++) {
            span = lcm(span, periods[i]);
            if (span > MAX_PERIOD) {
                return false;
            }
        }
        boolean[] covered = new boolean[span];
        for (int i = 0; i < periods.length; i++) {
            int period = periods[i];
            int phase = phases[i];
            for (int cycle = phase; cycle < span; cycle += period) {
                covered[cycle] = true;
            }
        }
        for (int i = 0; i < span; i++) {
            if (!covered[i]) {
                return false;
            }
        }
        return true;
    }

    static int gcd(int a, int b) {
        int x = a;
        int y = b;
        while (y != 0) {
            int next = x % y;
            x = y;
            y = next;
        }
        return x;
    }

    static int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }
}
