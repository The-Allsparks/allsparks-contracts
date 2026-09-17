# Adoption

Do not modify every Allsparks repository in order to exist. This artifact is useful only when a consumer chooses it.

## Dependency direction

```text
allsparks-contracts
        ^
functional project cores
        ^
optional integration adapters
        ^
TeamCode / OpMode composition root
```

Functional projects may depend on this tiny JAR. They must not require one another. Prefer adapters:

```text
trace-mimic-adapter -> TRACE + MIMIC
trace-vidar-adapter -> TRACE + ViDAR
trace-amper-adapter -> TRACE + AMPER
helm-mimic-adapter  -> HELM + MIMIC
helm-pedro-adapter  -> HELM + Pedro Pathing
helm-vidar-adapter  -> HELM + ViDAR
echo-helm-adapter   -> ECHO + HELM
amper-mimic-adapter -> AMPER + MIMIC
```

Those adapter artifacts are not implemented here.

Avoid:

```text
MIMIC -> TRACE
ViDAR -> HELM
MIMIC -> AMPER
```

## Staged order

1. Validate `allsparks-contracts` independently (`./gradlew check`, javadoc, consumer fixture).
2. Select two low-risk consumers: **TRACE**, then **HELM**.
3. Add the Maven/includeBuild dependency without deleting local domain types.
4. Introduce adapters at the edge (`TraceClock` implements or wraps `MonotonicClock`; HELM `Capability` converts to `CapabilityId`).
5. Verify independent builds and examples still pass without the sibling functional library.
6. Confirm TeamCode dependency resolution when a robot project opts in.
7. Evaluate whether the shared contract reduced adapters or ambiguity.
8. Only then consider BEACON, MIMIC, ECHO, AMPER, SHIFT, and ViDAR.

Do not begin with ViDAR timing or measurement-validity deletion. Capture time is not process monotonic time.

## First pilots

### TRACE

Pilot merged in [TRACE PR #42](https://github.com/The-Allsparks/TRACE/pull/42). TRACE depends on `org.allsparks:allsparks-contracts:0.1.0-rc.1` and remains independently adoptable (no HELM, AMPER, or MIMIC compile dependency).

Mapped without deleting TRACE-local types:

- `TraceClock` extends `MonotonicClock` (`nowNanos()` delegates to `nanoTime()`)
- `TraceQuality` OK/STALE/INVALID/MISSING to `Validity`; `ESTIMATED` and `ASYNC` stay TRACE recording labels
- `TraceSeverity` INFO/NOTICE → `HealthSeverity.INFO`, WARNING → WARNING, ERROR → DEGRADED, FAULT → STOP_COMPONENT

`TraceClock.wallClockMillis()` stays inside TRACE. `TraceRecord` was not deleted.

### HELM

HELM already consumes capability identity, availability, confidence, and readiness-shaped eligibility. Map:

- `HelmClock` to `MonotonicClock`
- `Capability` to `CapabilityId` (keep HELM well-known constants in HELM)
- `CapabilityAvailability` to `Availability` (map `STALE` via freshness, not a fifth availability value)
- `Confidence` to the shared type

Do not enable physical output as part of adoption.

### First adapter after the pilots

A TRACE or HELM edge adapter plus one producer (BEACON or MIMIC) once the two pilots compile independently.

## Java 11

Consumers compile as Java 11. AMPER and SHIFT should bump to Java 11 when they adopt. Do not fork a Java 8 contracts line.
