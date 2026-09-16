# Cross-project contract audit

Accessed local default-branch trees on 2026-09-16. GitHub default branches for AMPER, ViDAR, MIMIC, TRACE, BEACON, HELM, ECHO, and SHIFT match the independently adoptable libraries in this workspace. RELAY does not exist.

**Drift rule:** Independent Allsparks libraries were generated separately. Project prefixes, extra enum constants, Java 8 versus Java 11, SHIFT milliseconds versus nanoseconds, and `FakeClock` / `ManualClock` / `SimulatedClock` names are code-generation drift, not distinct domains. v0 of `allsparks-contracts` therefore publishes one shared envelope for each duplicated concept.

Functional projects may keep richer local types when they add operations this library must not own (motor grants, camera attach, link topology, pathing). This audit does not rewrite those repositories.

## How to read the tables

| Field | Meaning |
| ----- | ------- |
| Project | Repository containing the concept |
| Existing type | Current class, interface, or enum |
| Current package | Fully qualified package |
| Semantics | What it actually means |
| Units | If applicable |
| Lifecycle | When it is created or updated |
| Consumers | Who reads it |
| Safety effect | Whether it can stop or degrade behavior |
| Same as another project? | Exact, partial, or only superficially similar |
| Share now? | Yes, no, or defer |
| Migration risk | Low, medium, or high |

`Same as another project?` is **exact** when the differences are generation drift of one contract.

## Repositories inspected

| Repository | Public GitHub | Java library target | Production deps in core |
| ---------- | ------------- | ------------------- | ----------------------- |
| AMPER | yes | 8 (drift; canonical is 11) | none in `amper-core` |
| ViDAR | yes | 11 (`java-pure`) | `org.json` in pure module |
| MIMIC | yes | 11 | none |
| TRACE | yes | 11 | none |
| BEACON | yes | 11 | none |
| HELM | yes | 11 | none |
| ECHO | yes | 11 | none |
| SHIFT | yes | 8 (drift) | `org.json` in `shift-core` |
| RELAY | absent | n/a | n/a |

## Admitted shared contracts

These rows are the duplicated SPIs and envelopes. Share now: **yes**.

### Monotonic clock

| Field | AMPER | TRACE | HELM | MIMIC | BEACON | ECHO | SHIFT |
| ----- | ----- | ----- | ---- | ----- | ------ | ---- | ----- |
| Existing type | `AmperClock` | `TraceClock` | `HelmClock` | `MimicClock` | `BeaconClock` | `EchoClock` | `ShiftClock` |
| Current package | `org.allsparks.amper.clock` | `org.allsparks.trace.clock` | `org.allsparks.helm.clock` | `org.allsparks.mimic.clock` | `org.allsparks.beacon.clock` | `org.allsparks.echo.clock` | `org.allsparks.shift.clock` |
| Semantics | Injectable monotonic elapsed time | Same, plus optional TRACE wall-clock default | Same | Same | Same | Same | Same intent; method is `nowMillis()` |
| Units | nanoseconds | nanoseconds | nanoseconds | nanoseconds | nanoseconds | nanoseconds | milliseconds (drift) |
| Lifecycle | Injected at construction | Injected via config | Injected via config | Injected at construction | Injected at construction | Injected at construction | Injected at builder |
| Consumers | Session, monitor, tests | Session, tests | Evaluator, tests | Observer, tests | Session, freshness | Engine, tests | `Shift.update`, tests |
| Safety effect | Indirect freshness only | Observational | Stale input reject; no hardware | Stale sensing labels | False STALE/LOST | Silence on age | Edge timing |
| Same as another project? | Exact | Exact; `wallClockMillis()` is TRACE-only additive drift | Exact | Exact | Exact | Exact | Exact intent; units are drift |
| Share now? | Yes as `MonotonicClock.nowNanos()` | Yes | Yes | Yes | Yes | Yes | Yes; adapters convert millis |
| Migration risk | Low | Low | Low | Low | Low | Low | Medium (unit change) |

ViDAR has no clock type. It calls `System.nanoTime()` and also stores VisionPortal `frameCaptureNanos`. Process monotonic time maps to `MonotonicClock`. Camera capture time is **not** this SPI.

### Production clock

| Field | Value |
| ----- | ----- |
| Project | AMPER, TRACE, HELM, MIMIC, BEACON, ECHO (`SystemNanoClock`); SHIFT (`SystemShiftClock`) |
| Existing type | `SystemNanoClock` / `SystemShiftClock` |
| Semantics | Production `System.nanoTime()` (SHIFT uses `currentTimeMillis()`, drift) |
| Units | nanoseconds (canonical) |
| Lifecycle | Stateless; default when no clock is injected |
| Consumers | Production wiring |
| Safety effect | None directly |
| Same as another project? | Exact |
| Share now? | Yes as `SystemMonotonicClock` |
| Migration risk | Low |

### Test / fake clock

| Field | Value |
| ----- | ----- |
| Project | BEACON/ECHO `FakeClock`; TRACE/HELM `ManualClock`; AMPER `SimulatedClock`; MIMIC lambda + `AtomicLong`; SHIFT `ManualClock` |
| Semantics | Deterministic injectable time for tests |
| Units | nanoseconds (canonical) |
| Lifecycle | Test instance; not a process singleton |
| Consumers | Unit tests, replay fixtures |
| Safety effect | Test-only |
| Same as another project? | Exact; names are drift |
| Share now? | Yes as `FakeMonotonicClock` |
| Migration risk | Low |

`FakeMonotonicClock` is a mutable **instance**. There is no mutable global clock singleton.

### Component identity

| Field | Value |
| ----- | ----- |
| Project | AMPER `sourceId` / `channelId` / `motorId`; MIMIC `mechanismId` / `channelId`; BEACON `LinkId`; ViDAR `cameraName`; TRACE signal path prefixes |
| Existing type | Plain `String` (BEACON has `LinkId`) |
| Current package | Project-local |
| Semantics | Human-readable identifier for a producing component |
| Units | n/a |
| Lifecycle | Assigned at wiring; stable for a match |
| Consumers | Logs, health, snapshots |
| Safety effect | None by itself |
| Same as another project? | Exact identity rules; payload prefixes differ |
| Share now? | Yes as `ComponentId` |
| Migration risk | Medium (String to value type) |

### Capability identity

| Field | Value |
| ----- | ----- |
| Project | HELM `Capability`; AMPER sibling-contracts HELM envelope; ECHO `MISSING_CAPABILITY` |
| Existing type | HELM `org.allsparks.helm.capability.Capability` |
| Semantics | Named capability exposed to TeamCode or HELM. Custom names must be possible without editing this repository. |
| Units | n/a |
| Lifecycle | Declared by providers; read by HELM/ECHO |
| Consumers | HELM eligibility, ECHO silence, AMPER envelope docs |
| Safety effect | Missing capability refuses HELM tasks / ECHO HELM source; does not command hardware |
| Same as another project? | Exact identifier; HELM well-known constants stay in HELM |
| Share now? | Yes as `CapabilityId` with no central constant list |
| Migration risk | Medium |

HELM constants such as `LOW_SCORING` remain HELM-owned. They must not appear in `allsparks-contracts`.

### Validity

| Field | AMPER | MIMIC | TRACE | ECHO |
| ----- | ----- | ----- | ----- | ---- |
| Existing type | `MeasurementValidity` | `MeasurementValidity` | `TraceQuality` | `Presence` |
| Current package | `org.allsparks.amper.measure` | `org.allsparks.mimic.observe` | `org.allsparks.trace.core` | `org.allsparks.echo.value` |
| Semantics | Can this sample be trusted as a measurement? | Same enum shape | Recording freshness/trust label | Known versus missing/stale field |
| Units | n/a | n/a | wire byte ids | n/a |
| Lifecycle | Set at sample classify | Set at capture / classify | Per TRACE record | Per snapshot field |
| Consumers | Monitor, BEACON contract, logs | Observer, validator, status | Codec, exporters | Cue selector |
| Safety effect | Invalid sensing blocks intervention | Invalid sensing degrades mechanism status | Marks evidence; no actuators | Forces silence |
| Same as another project? | Exact shared states; extra constants are drift | Exact | Partial (recording labels `ESTIMATED`/`ASYNC` stay TRACE) | Exact overlap on present/stale/missing |
| Share now? | Yes as `Validity` | Yes | Map `OK`→`VALID`, `STALE`, `INVALID`, `MISSING` | Map `PRESENT`→`VALID` |
| Migration risk | Medium | Medium | Medium | Medium |

Canonical `Validity`: `VALID`, `STALE`, `MISSING`, `OUT_OF_RANGE`, `UNSUPPORTED`, `INVALID`.

Adapter mappings of extras:

- AMPER `SKIPPED` → `INVALID`
- MIMIC `DISAGREEING` → `INVALID`
- TRACE `ESTIMATED` / `ASYNC` stay TRACE recording labels
- ECHO `PRESENT` → `VALID`
- ECHO `UNAVAILABLE` / `UNKNOWN` stay distinct from measurement validity when they mean capability or presence, not sample quality

ViDAR `isValid()` on range estimates is perception-specific and maps to `VALID`/`INVALID` plus `Confidence`. It is not a second validity enum.

### Confidence

| Field | HELM | BEACON | AMPER | ViDAR |
| ----- | ---- | ------ | ----- | ----- |
| Existing type | `Confidence` | `Confidence` | `EstimateConfidence` | `double confidence` |
| Current package | `org.allsparks.helm.confidence` | `org.allsparks.beacon.api` | `org.allsparks.amper.battery` | observation fields |
| Semantics | Evidence in `[0, 1]` or explicit unknown | Same | Score in `[0, 1]` plus note | Composite detection trust |
| Units | dimensionless | dimensionless | dimensionless | dimensionless |
| Lifecycle | Carried on snapshot facts | Carried on link health | Battery estimate update | Per observation |
| Consumers | Task evaluator | Health report | Driver feedback | Fusion / world |
| Safety effect | Unknown/low refuses eligibility | Unknown must not drive intervention | Advisory today | Low confidence rejects detections |
| Same as another project? | Exact | Exact | Exact score; note maps to `Reason` | Exact scalar; no unknown sentinel |
| Share now? | Yes as `Confidence` | Yes | Yes | Yes via `Confidence.of` |
| Migration risk | Low | Low | Medium | Medium |

Shared `Confidence` has no `isActionable` / `meets` policy method. Unknown is not `0.0`.

### Availability

| Field | HELM | BEACON |
| ----- | ---- | ------ |
| Existing type | `CapabilityAvailability` | `PreflightStatus` / `LinkState` presence |
| Current package | `org.allsparks.helm.capability` | `org.allsparks.beacon.api` |
| Semantics | Can this capability currently exist or operate? | Declared-link / link presence |
| Units | n/a | n/a |
| Lifecycle | Reported into snapshot | Observe / preflight |
| Consumers | Task evaluator | Preflight, advisory |
| Safety effect | UNKNOWN/STALE block required capabilities | UNKNOWN fail-safe |
| Same as another project? | Exact shared states; `STALE` on availability is mixed freshness (drift) | Partial; `LinkState` also encodes comms topology |
| Share now? | Yes as `Availability` without `mayBeUsed` | Map presence; keep `LinkHealth` in BEACON |
| Migration risk | Medium | Medium |

Canonical `Availability`: `AVAILABLE`, `DEGRADED`, `UNAVAILABLE`, `UNKNOWN`. HELM `STALE` is freshness (`Validity.STALE` or age), not a fifth availability value.

### Readiness

| Field | BEACON | MIMIC | HELM |
| ----- | ------ | ----- | ---- |
| Existing type | `PreflightStatus` | `MechanismStatus` | capability usable-for-action |
| Current package | preflight API | `org.allsparks.mimic.api` | capability + evaluator |
| Semantics | Is a requested action complete or ready to begin? | Telemetry health including motion states | Eligibility to use a capability |
| Units | n/a | n/a | n/a |
| Lifecycle | Preflight snapshot | Each `periodic` | Each decision |
| Consumers | Drivers / match start | Session, DS | Task evaluator |
| Safety effect | Can refuse match-start claims | Degraded sensing; Phase 0 does not actuate | Refuses tasks; no hardware |
| Same as another project? | Exact ready/not-ready/degraded/unknown | Partial; `MOVING`/`HOLDING` stay MIMIC | Exact ready/not-ready split from availability |
| Share now? | Yes as `Readiness` | Map OBSERVING/READY/DEGRADED/FAULTED/STOPPED; keep motion states | Yes |
| Migration risk | Medium | Medium | Medium |

Canonical `Readiness.State`: `READY`, `NOT_READY`, `DEGRADED`, `UNAVAILABLE`, `UNKNOWN`. `UNKNOWN` is never ready. Readiness does not command hardware and does not encode match strategy.

### Structured reason

| Field | Value |
| ----- | ----- |
| Project | AMPER `PowerLimitReason`; MIMIC `GoalResult.reason`; BEACON `LinkFailureReason`; ECHO `SilenceReason` / `RejectionReason`; HELM `FailureReason`; TRACE `DropReason` |
| Existing type | Project enums or strings |
| Semantics | Why something was rejected, degraded, or unavailable |
| Units | n/a |
| Lifecycle | Produced at the decision site |
| Consumers | Grants, goals, silence, eligibility, drops |
| Safety effect | Explains refuse paths; does not execute them |
| Same as another project? | Exact envelope (code + message); taxonomies stay project-owned |
| Share now? | Yes as `Reason` envelope |
| Migration risk | Medium |

Domain fault codes remain in the owning project. The shared type is not a universal error taxonomy.

### Health severity

| Field | TRACE | HELM | MIMIC |
| ----- | ----- | ---- | ----- |
| Existing type | `TraceSeverity` | `ValidationSeverity` | docs ladder INFO→STOP_ROBOT (not Java yet) |
| Current package | `org.allsparks.trace.core` | `org.allsparks.helm.validate` | docs only |
| Semantics | Recommended impact of a finding | Static plan finding level | Recommended stop radius |
| Units | wire byte | n/a | n/a |
| Lifecycle | Event records | Plan validation | Designed for Phase 8 |
| Consumers | Exporters | Plan validator | Future fault handling |
| Safety effect | Logging taxonomy only | Rejects invalid plans | Docs only; must not execute stop |
| Same as another project? | Exact ladder with renamed constants | Exact INFO/WARNING subset | Exact intended ladder |
| Share now? | Yes as `HealthSeverity` | Yes | Yes |
| Migration risk | Medium | Low | Low |

Canonical: `INFO`, `WARNING`, `DEGRADED`, `STOP_COMPONENT`, `STOP_DEPENDENCIES`, `STOP_ROBOT`. TRACE `NOTICE`→`INFO`, `ERROR`→`DEGRADED`, `FAULT`→`STOP_COMPONENT` unless a consumer documents a wider mapping. Severity does not execute a stop. `FATAL` is not used.

### Health finding

| Field | TRACE | HELM | BEACON |
| ----- | ----- | ---- | ------ |
| Existing type | drop stats + event records | `ValidationFinding` | `PreflightFinding` |
| Semantics | Immutable finding: source, code, severity, message, time |
| Units | timestamp nanoseconds |
| Lifecycle | Produced when a condition is observed |
| Consumers | Health APIs, validators, preflight |
| Safety effect | Observability / refuse explanation; no recovery |
| Same as another project? | Exact envelope |
| Share now? | Yes as `HealthFinding` |
| Migration risk | Medium |

## Project-owned leftovers (do not share now)

These types use some of the same words. They are **not** the shared envelope. Share now: **no**.

| Project | Existing type | Current package | Semantics | Units | Lifecycle | Consumers | Safety effect | Same as another project? | Share now? | Migration risk |
| ------- | ------------- | --------------- | --------- | ----- | --------- | --------- | ------------- | ------------------------ | ---------- | -------------- |
| ViDAR | `captureTimeNanos` | `vidar.frame` / observations | VisionPortal camera capture time | ns, SDK capture domain | Per frame | Fusion, age gates | Can reject tag corrections | Superficial vs process `nowNanos` | No | High |
| ViDAR | `VidarMetrics.CameraHealth` | `vidar.runtime` | Camera stream/pipeline ladder | n/a | Attach/tick | Diagnostics | Surfaces FAILED cams | Superficial vs `HealthFinding` | No | High |
| ViDAR | `VidarVisionAttachment.close` | `vidar.runtime` | Camera portal attach/detach | n/a | OpMode | Runtime | Releases USB/portals | Superficial vs MIMIC `stop` | Defer lifecycle | High |
| BEACON | `LinkHealth` | `org.allsparks.beacon.api` | Per-link comms snapshot with domains | ns, optional latency ms | `report`/`observe` | Siblings via manual reports | Observation-only today | Partial; uses shared freshness/confidence | No (payload) | High |
| BEACON | `FailureDomain` | `org.allsparks.beacon.api` | Comms topology classification | n/a | Per finding | Advisory | None by itself | Unique | No | Low |
| MIMIC | `MechanismStatus.MOVING/HOLDING` | `org.allsparks.mimic.api` | Motion phase | n/a | Periodic | Telemetry | Not a command | Superficial vs readiness | No | Medium |
| MIMIC | `CalibrationState` | `org.allsparks.mimic.api` | Homing/calibration | n/a | Session | Mechanism API | Phase 0 always uncalibrated | Unique | No | Low |
| MIMIC | `MimicMechanism` | `org.allsparks.mimic.api` | `periodic`/`stop`/`requestGoal` | n/a | OpMode loop | Examples | Phase 0 stop is log-only | Superficial vs other lifecycles | Defer | High |
| AMPER | `PowerGrant` / `PowerRequest` | `org.allsparks.amper.api` | Electrical allocation | effort, amps | Coordinator (experimental) | Future MIMIC | Must not change motors in Phase 0/1 | Unique | No | High |
| AMPER | `AmperFeatureFlags` | `org.allsparks.amper` | Phase kill switches | n/a | Construction | Observe path | Primary safety gate | Superficial vs other flags | No | High |
| AMPER | `AmperLifecycle` | `org.allsparks.amper` | CONSTRUCTED→CLOSED | n/a | Session methods | Session | Rejects observe when closed | Superficial vs OpMode | Defer | High |
| TRACE | `TraceHealth` | `org.allsparks.trace.session` | Recorder health (drops, writer) | counts, ns | Session | DS, tests | Fail-visible drops | Superficial vs robot health | No | Medium |
| TRACE | `TraceClock.wallClockMillis` | `org.allsparks.trace.clock` | Epoch wall clock | ms | Optional per record | Exporters | Evidence join only | Different time domain | No | Low |
| HELM | `CapabilityAvailability.mayBeUsed` | `org.allsparks.helm.capability` | HELM policy over availability | n/a | Evaluator | Tasks | Eligibility | Policy, not the enum | No | Low |
| HELM | `WorldSnapshot` | `org.allsparks.helm.snapshot` | Decision world model | mixed | Caller-built | Evaluator | Stale world refuses tasks | Superficial vs other snapshots | No | High |
| ECHO | `AudioDeviceStatus` | echo audio | Renderer path | n/a | Step | Engine | Mute | Unique | No | Low |
| SHIFT | `InputSnapshot` | `org.allsparks.shift` | Controller sample | ms timestamp | `update` | Intent mapping | None (no motors) | Superficial vs other snapshots | No | Medium |

Also **no** for: covariance, game-piece identities, season constructs, logging records, configuration loaders, unit systems, hardware devices, controller/motor/servo/scheduler commands, pathing goals, recovery policy, interlock policy, feature flags.

## Lifecycle (deferred)

| Project | Existing type | Semantics | Same as another project? | Share now? | Migration risk |
| ------- | ------------- | --------- | ------------------------ | ---------- | -------------- |
| AMPER | `initialize`/`start`/`observe`/`stop`/`close` | Session aligned to OpMode | Superficial name overlap | Defer | High |
| MIMIC | `periodic`/`stop` | Observe loop; stop is not emergency stop in Phase 0 | Superficial | Defer | High |
| TRACE | `TraceCycle` AutoCloseable, `OpModeLifecycle` | Recording cycle vs OpMode | Superficial | Defer | High |
| HELM | `ActionAdapter.tick`/`cancel` | Caller-driven; no OpMode type | Superficial | Defer | High |
| ViDAR | `attachVision`/`detachVision`/`shutdown` | Camera resource ownership | Different operations | Defer | High |
| ECHO | `step`/`mute` | Audio path; mute is not robot stop | Different | Defer | High |
| BEACON | `observe` once per loop | Aging overlay | Superficial | Defer | High |

`AutoCloseable.close()` is not emergency stop. `stop()` sometimes implies actuator authority and sometimes does not. These are not generation drift of one interface.

## Timestamps

| Domain | Who uses it | Share now? |
| ------ | ----------- | ---------- |
| Monotonic process time | AMPER, TRACE, HELM, MIMIC, BEACON, ECHO (SHIFT millis is drift) | Yes, as `long nowNanos()` from `MonotonicClock` |
| Wall-clock / UTC | TRACE optional `wallClockMillis` | No |
| Camera capture time | ViDAR `captureTimeNanos` | No |
| Externally synchronized time | Not implemented | No |
| Driver Station / gamepad millis | SHIFT snapshot timestamps | No; convert at adapter |

No shared `Timestamp` wrapper in v0. Mixing camera capture time with `nowNanos()` is a consumer bug. A generic timestamp type would hide that.

## Admission summary

| Concept | At least three projects or two plus TeamCode? | Identical semantics after drift correction? | Share now? |
| ------- | --------------------------------------------- | ------------------------------------------- | ---------- |
| Monotonic clock | Yes (6+) | Yes | Yes |
| ComponentId | Yes | Yes | Yes |
| CapabilityId | HELM + AMPER envelope + ECHO | Yes as identifier | Yes |
| Validity | Yes | Yes for shared states | Yes |
| Confidence | Yes | Yes | Yes |
| Availability | HELM + BEACON + HELM/TeamCode | Yes for shared states | Yes |
| Readiness | BEACON + MIMIC + HELM | Yes for shared states | Yes |
| Reason envelope | Yes | Yes as envelope | Yes |
| HealthSeverity | TRACE + HELM + MIMIC docs | Yes as recommended impact | Yes |
| HealthFinding | TRACE + HELM + BEACON | Yes as envelope | Yes |
| Lifecycle interfaces | Many names, different operations | No | Defer |
| LinkHealth / PowerGrant / snapshots | Domain payloads | No | No |

## First consumers (not in this repository)

1. TRACE, mapping `TraceClock` and quality/severity labels without deleting local types yet.
2. HELM, mapping clock, `Capability`, availability, and confidence.

Do not begin with ViDAR capture-time migration.
