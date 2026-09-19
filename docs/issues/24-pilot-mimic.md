# [phase] Pilot MIMIC adoption

GitHub: [#24](https://github.com/The-Allsparks/allsparks-contracts/issues/24)

## Problem

MIMIC still has `MimicClock`, `MeasurementValidity`, `FaultSeverity`, and `MechanismStatus` as local copies of the shared envelopes. TRACE and HELM already map at the edge. The first adapter (`trace-mimic-adapter`) still translates across two local type systems.

## Student learning objective

A student can observe a mechanism with MIMIC depending on `allsparks-contracts` without pulling in TRACE, HELM, or AMPER.

## Scope

Add the contracts dependency in MIMIC. Map at the edge in a `MimicMappings` type, same pattern as TRACE PR #42 and HELM PR #48. Do not delete local types in the first PR.

Mapped without deleting MIMIC-local types:

- `MimicClock` / `SystemNanoClock` onto `MonotonicClock` (`nanoTime()` delegates to `nowNanos()`)
- `MeasurementValidity` VALID/STALE/MISSING/OUT_OF_RANGE/UNSUPPORTED onto `Validity` of the same names
- leftover `DISAGREEING` onto `Validity.INVALID` (already in [compatibility.md](../compatibility.md))
- `FaultSeverity` INFO/DEGRADED/STOP_DEPENDENCIES/STOP_ROBOT onto `HealthSeverity`; `STOP_MECHANISM` onto `STOP_COMPONENT`
- `MechanismStatus` OBSERVING/READY/DEGRADED/FAULTED/STOPPED onto `Readiness.State` at the edge

Implementation is a MIMIC pull request. This repository's public Java API does not change.

## Out of scope

Deleting `MimicClock`, `MeasurementValidity`, `FaultSeverity`, or `MechanismStatus`. Deleting or renaming MIMIC `observe.Readiness` (that class is at-speed / in-tolerance settling, not the shared readiness envelope). Sharing `MOVING` / `HOLDING`. Sharing `periodic` / `stop`. Enabling `phase8Faults` or any motor/servo command. Making MIMIC depend on TRACE, HELM, or AMPER. ViDAR capture-time migration. Declaring contracts 1.0.

## Acceptance criteria

- [ ] MIMIC `check` passes with the contracts dependency
- [ ] Examples still compile without TRACE/HELM/AMPER
- [ ] `DISAGREEING` maps to `Validity.INVALID`; local enum is not deleted
- [ ] `MechanismStatus.MOVING` and `HOLDING` stay MIMIC-only
- [ ] MIMIC `observe.Readiness` settling evaluator is unchanged
- [ ] Physical output remains disabled (`phase8Faults` stays off)
- [ ] Local types are not deleted yet
- [ ] No public Java API change in `allsparks-contracts`

## Hardware validation required

- [x] None
- [ ] Desktop only

## Parent roadmap issue

[Adoption](../adoption.md) staged order step 8. Follows closed #4 (TRACE), #5 (HELM), and #6 (`trace-mimic-adapter`). After TeamCode Hub resolve.

## This issue is a design review. Do not start implementation until maintainers accept the plan.
