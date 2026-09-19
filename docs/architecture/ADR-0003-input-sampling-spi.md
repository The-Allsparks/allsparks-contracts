# ADR-0003: Input sampling declaration contracts

- Status: Accepted
- Date: 2026-09-17
- Deciders: The Allsparks maintainers

## Context

Independently adoptable libraries (MIMIC, AMPER, Pedro, HELM, TRACE, ViDAR)
need to name the physical inputs they read and how often those inputs must be
captured. A shared sampling *runtime* would force every library to depend on
that runtime. [ADR-0001](ADR-0001-contract-boundary.md) forbids a mandatory
center, threads, hardware ownership, and OpMode lifecycle.

PULSE (Prioritized Unified Loop Sampling Engine) is the optional runtime that
compiles those declarations, deduplicates physical reads, and publishes a
cycle snapshot. Libraries must remain usable with a raw `IntSupplier` when
PULSE is absent.

AMPER already has a local `SamplingPolicy` that means current-read budget.
TRACE already has a local `SamplingPolicy` that means recorder interval /
change threshold. Those meanings stay in their projects. The shared type is
period-and-phase *input capture* cadence.

## Admission rule

| Criterion | Result |
| --------- | ------ |
| 1. At least three projects, or two plus TeamCode | Six independently adoptable libraries plus TeamCode composition need a shared signal identity and declaration SPI so they do not depend on PULSE. |
| 2. Identical semantics | The shared types are new declaration envelopes, not a merge of AMPER/TRACE local `SamplingPolicy` types. Those remain domain-specific. |
| 3. Sharing reduces adapters | Libraries declare `SignalKey` + `SamplingPolicy` once. PULSE, tests, and TRACE metrics can speak the same names without a PULSE compile dependency in MIMIC/AMPER/HELM cores. |
| 4. No FTC/Android/hardware | Keys, policies, samples, and registrar methods are pure Java. No Hub, motor, or OpMode types. |
| 5. Unlikely to change frequently | Period/phase identity is stable. Time-based scheduling is `everyNanos` without changing `SignalKey`. |
| 6. Clear invariants and tests | Equality, union simplification, freshness, and group membership are unit-tested. |
| 7. Does not force functional projects to depend on one another | Libraries depend on this JAR only. They must not depend on `org.allsparks.pulse`. |
| 8. Does not introduce runtime authority | No compiled read plan, cache, Hub bulk-cache clearing, cycle budget, or `start`/`stop`/`capture` lives here. PULSE owns those. |

The admission rule passes for **declaration and observation envelopes**, not for a scheduler.

## Decision

Add package `org.allsparks.contracts.input`:

- `SignalKey` / `SignalValueType` — stable identity (namespace via `ComponentId`, property, value type)
- `SamplingPolicy` — period/phase, automatic spread, union, time-based form (`everyNanos`)
- `InputPriority` — `CRITICAL` / `NORMAL` / `OPTIONAL`
- `Sample` — value, `Validity`, capture timestamp, cycle id, updated-this-cycle, fault `Reason`
- `InputRequirement` / `CoherentGroup` / `InputRequirements` — configuration-time collector
- `InputRegistrar` — SPI a runtime may implement
- `InputDemand` — loop-time `requestOnce` / `isRequested`; implementations must not read hardware
- `InputValues` — read-only published samples; primitive getters must not call hardware; `contains` / `tryGet` / `tryGetDouble` / `tryGetBoolean` peek without throwing
- `MotorSignals` — shared over-current flag and motor-current amp keys

Reuse existing envelopes: `ComponentId` for namespace, `Validity` for sample quality, `Reason` for read faults, `MonotonicClock` nanoseconds for capture time. Do not add a second clock, a lifecycle interface, or Hub types.

## What stays out

- Compiled read plans, per-cycle caches, double-buffer publication
- REV bulk-cache control
- Cycle budget and reader disabling
- Derived-signal execution
- `Pulse.capture`, freeze, start, stop
- Output arbitration
- Replacing AMPER or TRACE local `SamplingPolicy` types

Those require PULSE (or the owning project), not this JAR. `InputDemand.requestOnce` is a mark for the next capture, not a capture loop.

## Consequences

- Public API allowlist grows. japicmp fails until `./gradlew updateApiBaseline` is run with this ADR and a CHANGELOG migration note.
- PULSE implements `InputRegistrar`, `InputValues`, and `InputDemand`.
- Functional libraries may depend on these types without depending on PULSE.
- `ProductionSafetyTest` continues to forbid `start` / `stop` / `close` / `periodic` / `attach` / `detach` on production interfaces. `InputRegistrar` does not declare those methods.
