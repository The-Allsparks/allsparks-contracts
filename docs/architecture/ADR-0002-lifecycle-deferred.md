# ADR-0002: Lifecycle contracts remain deferred

- Status: Accepted
- Date: 2026-09-16
- Deciders: The Allsparks maintainers

## Acceptance

Maintainers accept this evaluation after TRACE and HELM consumed `allsparks-contracts`. Shared lifecycle interfaces stay out of the public API. No new production types. Closes #7.

## Context

[ADR-0001](ADR-0001-contract-boundary.md) deferred `start` / `stop` / `close` / `periodic` / `attach` until two consumers existed. Those pilots are now merged:

- TRACE: [TRACE PR #42](https://github.com/The-Allsparks/TRACE/pull/42)
- HELM: [HELM PR #48](https://github.com/The-Allsparks/HELM/pull/48)

This ADR compares remaining lifecycle methods on those consumers plus one producer (MIMIC). ViDAR camera attach/detach is extra evidence that overlapping words still mean different operations. See [cross-project-contract-audit.md](../audits/cross-project-contract-audit.md).

A concept may enter this artifact only when the [admission rule](ADR-0001-contract-boundary.md#10-criteria-for-adding-a-future-type) passes. Identical names after code-generation drift are not enough.

## Comparison

| Field | TRACE (consumer) | HELM (consumer) | MIMIC (producer) |
| ----- | ---------------- | --------------- | ---------------- |
| Existing type | `TraceCycle` (`AutoCloseable`); `OpModeLifecycle` | `ActionAdapter` | `MimicMechanism` |
| Package | `org.allsparks.trace.session`; `org.allsparks.trace.ftc` | `org.allsparks.helm.adapter` | `org.allsparks.mimic.api` |
| Operations | `TraceCycle.close()` ends a recording cycle; `OpModeLifecycle.init` / `start` / `stop` map OpMode callbacks onto TRACE events | `tick(WorldSnapshot)` returns `IntentStatus`; `cancel()` aborts the leaf action | `periodic()` runs the observe loop; `stop()` ends the mechanism session; `requestGoal` is a separate command path |
| What close/stop/cancel actually does | Closes the cycle so the session can finish the recording window. Observational in TRACE Phases 0–3. Does not write hardware outputs. | Cancels an intent adapter. The shipped `NoOpActionAdapter` does nothing. HELM core has no OpMode type. | Phase 0 `stop()` is log-only. It is not emergency stop and must not command hardware in Phase 0. |
| Who owns the loop | Caller opens and closes a cycle; optional `OpModeLifecycle` is an event mapper without an FTC SDK import | Caller drives `tick`; HELM does not own OpMode or hardware | Caller drives `periodic` from an OpMode loop |
| Safety effect | Marks evidence; no actuators | Eligibility / intent status; physical output stays disabled in HELM | Degraded sensing labels; Phase 0 does not actuate |
| Same as the others? | Superficial name overlap with `stop` / `close` | Superficial name overlap with `tick` / `cancel` | Superficial name overlap with `periodic` / `stop` |

These are different operations. `AutoCloseable.close()` on a TRACE cycle is not HELM `cancel()`, and neither is MIMIC `stop()`.

### Extra evidence: ViDAR camera attach

ViDAR `attachVision` / `detachVision` / `shutdown` owns VisionPortal USB and camera workers. `VidarSpatial.close()` detaches portals at OpMode stop; `VidarRuntime.shutdown()` tears down the process singleton. Releasing a camera portal is not stopping a MIMIC mechanism session and is not closing a TRACE recording cycle. That overlap was already deferred in the audit (`VidarVisionAttachment.close` versus MIMIC `stop`).

## Admission rule

| Criterion | Result for a shared lifecycle interface |
| --------- | --------------------------------------- |
| 1. At least three projects, or two plus TeamCode, require it | Names appear in many projects. They do not require one shared SPI. |
| 2. Semantics are identical after drift correction | **Fail.** Recording-cycle close, intent cancel, mechanism-session stop, and camera detach are not generation drift of one contract. |
| 3. Sharing reduces adapters or ambiguity | **Fail.** A shared `stop()` / `close()` would teach students that closing a camera portal is the same as stopping a mechanism. |
| 4. No FTC, Android, hardware, or remaining domain behavior | **Fail.** ViDAR attach owns portals. TRACE `OpModeLifecycle` exists to map OpMode callbacks. MIMIC `stop()` is defined against a mechanism session. |
| 5. Unlikely to change frequently | Project-owned. Each library can evolve its session without a contracts major version. |
| 6. Clear invariants and tests | A shared type would have no single invariant that is true for TRACE, HELM, and MIMIC. |
| 7. Does not force one functional project to depend on another | A shared lifecycle SPI would invite HELM to depend on MIMIC sessions or TRACE to depend on OpMode ownership. |
| 8. Does not introduce runtime authority | **Fail.** `start` / `stop` / `attach` imply who owns threads, OpModes, or hardware. This JAR must not. |

The admission rule fails on identical semantics (criterion 2) and on runtime/hardware ownership (criteria 4 and 8).

## Decision

**Keep deferred. Do not share.**

Do not add `start`, `stop`, `close`, `periodic`, `tick`, `attach`, or `detach` lifecycle interfaces to `allsparks-contracts`. `ProductionSafetyTest` already forbids those method shapes on production interfaces; leave it failing-closed. japicmp stays green because the public Java API is unchanged.

Functional projects keep their own lifecycle types. Optional adapters may translate at the edge (for example mapping a MIMIC event into TRACE) without a shared session interface here.

Revisit only if two consumers plus TeamCode later demonstrate the **same** operation, not the same method name. That would require a new ADR and an explicit public-API change.

## Consequences

- v0 public allowlist is unchanged.
- Students can keep the rule: closing a camera portal is not stopping a mechanism session, and neither is closing a TRACE cycle.
- TRACE, HELM, MIMIC, and ViDAR remain independently adoptable.
- Issue #7 is resolved as keep deferred, not as a new SPI.
