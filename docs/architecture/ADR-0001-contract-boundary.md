# ADR-0001: Contract boundary

- Status: Accepted
- Date: 2026-09-16
- Deciders: The Allsparks maintainers

## Acceptance

Maintainers accepted this ADR on 2026-09-16. The v0 public allowlist is unchanged. Lifecycle interfaces remain deferred. Closes #1.

## Context

The Allsparks maintains independently adoptable FTC libraries: AMPER, ViDAR, MIMIC, TRACE, BEACON, HELM, ECHO, and SHIFT when available. RELAY does not exist. Those libraries were generated separately and now contain the same clocks, identifiers, validity, confidence, availability, readiness, reasons, and health envelopes under project prefixes.

A large shared runtime would make every library require every other library. That violates independent adoption.

## Decision

Publish a tiny pure-Java artifact `org.allsparks:allsparks-contracts`. It is not a robot framework, scheduler, logger, hardware layer, or dependency-injection system. Installing it does nothing by itself.

Duplicated prefixes, extra enum constants, Java 8 versus Java 11, SHIFT milliseconds versus nanoseconds, and Fake/Manual/Simulated clock names are code-generation drift. v0 therefore shares one envelope per duplicated concept. Functional projects keep payloads this library must not own.

## 1. Why a small contracts artifact is justified

Six libraries already inject a monotonic nanosecond clock. HELM, AMPER, and ECHO already talk about capabilities. TRACE, HELM, and BEACON already emit finding-shaped records. Without a shared JAR, every adapter re-translates the same words and TeamCode cannot name a capability once.

The artifact stays small enough to audit (13 public production types, plus nested `Readiness.State`).

## 2. Why a large shared runtime is rejected

A runtime would own threads, OpMode lifecycle, hardware, logging, and feature flags. That forces AMPER to depend on TRACE, ViDAR to depend on HELM, and so on. Independent adoption would end. The name `SystemCore` is rejected for the same reason: it implies a mandatory center.

## 3. Concepts identical across at least three projects

After correcting drift:

- Monotonic process time in nanoseconds
- Component identity strings
- Capability identity strings
- Measurement validity shared states
- Confidence in `[0, 1]` or unknown
- Availability
- Readiness
- Structured reason envelopes
- Health severity as recommended impact
- Health findings

See [cross-project-contract-audit.md](../audits/cross-project-contract-audit.md).

## 4. Similar-looking concepts that remain project-specific

- ViDAR camera capture time and camera attach/detach
- BEACON `LinkHealth` topology and failure domains
- MIMIC `MOVING` / `HOLDING` and calibration
- AMPER `PowerGrant` and feature flags
- TRACE recorder health and wall-clock
- Snapshots, logging records, schedulers, pathing, season types

Those map into the envelopes. They are not copied here.

## 5. Are lifecycle interfaces identical?

No. `initialize` / `periodic` / `tick` / `attachVision` / `stop` / `close` / `mute` are different operations. `AutoCloseable.close()` is not emergency stop. `stop()` sometimes implies actuator authority and sometimes does not. Shared lifecycle contracts are deferred until two consumers exist (issue: evaluate lifecycle after two consumers).

## 6. What timestamps represent

`MonotonicClock.nowNanos()` is monotonic process elapsed time. It is not UTC, not wall-clock, not camera capture time, and not synchronized across devices.

TRACE wall-clock milliseconds, ViDAR `captureTimeNanos`, and SHIFT gamepad milliseconds stay outside this SPI. v0 has no `Timestamp` wrapper and no `TimeDomain` enum. Callers pass `long` nanos and document the clock on the field. Mixing capture time with `nowNanos()` is a consumer bug.

`System.nanoTime()` wraps on the order of 292 years. Subtracting values from different clock instances is undefined.

## 7. How version conflicts are avoided

Semantic versioning. One artifact coordinate: `org.allsparks:allsparks-contracts`. Consumers depend on it directly. Functional libraries must not shade or relocate it. An optional Gradle version catalog may align TeamCode stack versions; that is not this repository's runtime and does not create compile-time edges between functional libraries. See [version-catalog.md](../version-catalog.md).

Before 1.0, avoid unnecessary breaks and document migrations. After 1.0, removals and semantic changes require a major version. Adding enum constants is a compatibility event because consumers may switch exhaustively.

## 8. How consumers adapt local types without leaking contracts internally

Keep domain types inside the functional project. Convert at the edge:

```text
local HelmClock / TraceClock  ->  MonotonicClock
local Capability              ->  CapabilityId
local MeasurementValidity     ->  Validity
```

Internal packages may keep project names until a later cleanup PR. TeamCode and optional adapters are the composition root.

## 9. How optional adapters integrate functional projects

Preferred direction:

```text
allsparks-contracts
        ^
functional project cores
        ^
optional integration adapters
        ^
TeamCode / OpMode composition root
```

Examples (not implemented here):

```text
trace-mimic-adapter  -> TRACE + MIMIC
helm-vidar-adapter   -> HELM + ViDAR
amper-mimic-adapter  -> AMPER + MIMIC
echo-helm-adapter    -> ECHO + HELM
```

Avoid `MIMIC -> TRACE` or `ViDAR -> HELM` compile-time edges.

## 10. Criteria for adding a future type

A concept may enter this artifact only when all are true:

1. At least three projects require it, or two projects plus TeamCode integration demonstrably duplicate it.
2. Its semantics are identical (generation drift does not count as a new meaning).
3. Sharing materially reduces adapters or ambiguity.
4. It contains no FTC, Android, hardware, or remaining domain-specific behavior.
5. It is unlikely to change frequently.
6. It has clear invariants and tests.
7. It does not force one functional Allsparks project to depend on another.
8. It does not introduce runtime authority (threads, schedulers, hardware, OpMode ownership, recovery).

Widening the project's responsibility requires a new ADR.

## Consequences

- TRACE and HELM are the first adoption pilots. They add a dependency without deleting local types in the first PR.
- AMPER and SHIFT target Java 8 today (drift). They adopt Java 11 when they consume this JAR.
- Lifecycle, capture time, and domain payloads stay out of v0 even though the words overlap.
