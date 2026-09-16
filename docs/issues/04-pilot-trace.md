# [phase] Pilot TRACE adoption

## Problem

TRACE still has `TraceClock`, `TraceQuality`, and `TraceSeverity` as local copies of the shared envelopes.

## Student learning objective

A student can record TRACE data while TRACE depends on `allsparks-contracts` without pulling in HELM or AMPER.

## Scope

Add the contracts dependency. Map `TraceClock.nanoTime()` to `MonotonicClock.nowNanos()`. Map quality/severity at the edge. Do not delete local types in the first PR.

## Out of scope

Deleting `TraceRecord`. Enabling replay. ViDAR capture-time migration. Making TRACE depend on another functional library.

## Acceptance criteria

- [ ] TRACE `check` passes with the contracts dependency
- [ ] Examples still compile without HELM/AMPER/MIMIC
- [ ] Wall-clock remains TRACE-local
- [ ] Local types are not deleted yet

## Hardware validation required

- [x] None
- [ ] Desktop only

## This issue is a design review. Do not start implementation until maintainers accept the plan.
