# Agent and contributor engineering rules

This file is the short contract for humans and coding agents working in `allsparks-contracts`.

`allsparks-contracts` is a tiny pure-Java interoperability JAR. It is not a robot runtime. Features that "work" while growing into a framework are a defect.

## Commands

```powershell
.\gradlew.bat check
.\gradlew.bat spotlessApply
.\gradlew.bat javadoc
```

`check` compiles production code, tests, the Java 11 consumer fixture, architecture tests, javadoc, and Spotless. Format with `.\gradlew.bat spotlessApply` (Palantir Java Format, 4-space).

## Allowed contents

| May exist | Must not exist |
| --------- | -------------- |
| Immutable value types and enums | FTC SDK, Android, AndroidX |
| `MonotonicClock` SPI | Dependencies on AMPER, ViDAR, MIMIC, TRACE, BEACON, HELM, ECHO |
| Stateless `SystemMonotonicClock` | Global mutable clock, service registry, feature flags |
| Instance `FakeMonotonicClock` | Background threads, scheduler, event bus |
| IDs, validity, confidence, status, health envelopes | Hardware authority, recovery engine, OpMode lifecycle |
| Tests and consumer fixtures | Untyped string maps, Java records, season constants |

Architecture tests enforce forbidden imports and the public type allowlist.

## Admission rule

A concept may enter this artifact only when all are true:

1. At least three projects require it, or two projects plus TeamCode integration demonstrably duplicate it.
2. Its semantics are identical (generation drift is not a new meaning).
3. Sharing materially reduces adapters or ambiguity.
4. It contains no FTC, Android, hardware, or remaining domain-specific behavior.
5. It is unlikely to change frequently.
6. It has clear invariants and tests.
7. It does not force one functional Allsparks project to depend on another.
8. It does not introduce runtime authority.

Widening responsibility requires a new ADR. Do not add lifecycle `start`/`stop`/`close` interfaces without that ADR.

## Public API

Preserve `MonotonicClock.nowNanos()`, ID equality, `Validity` / `Availability` / `Readiness.State` / `HealthSeverity` constants, and `UNKNOWN` never meaning ready or available. Additive enum constants are a compatibility event.

Do not treat `nowNanos()` as UTC. Do not mix camera capture time into this clock.

## Tests expected for new work

- Behavior change: unit test next to the type.
- New public type: update the allowlist test in the same PR and the audit/ADR.
- New production import: architecture tests must still pass.
- No `Thread`, `Executor`, `Timer`, or network I/O in production sources.
