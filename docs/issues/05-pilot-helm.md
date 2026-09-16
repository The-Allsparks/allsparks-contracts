# [phase] Pilot HELM adoption

## Problem

HELM still has `HelmClock`, `Capability`, `CapabilityAvailability`, and `Confidence` as local copies of the shared envelopes.

## Student learning objective

A student can name a custom capability without editing HELM or this contracts repository.

## Scope

Add the contracts dependency. Map clock, `Capability` to `CapabilityId` (keep well-known constants in HELM), availability (map `STALE` via freshness), and confidence. Do not enable physical output.

## Out of scope

Deleting HELM domain types in the first PR. Lifecycle interfaces. Depending on TRACE at compile time.

## Acceptance criteria

- [ ] HELM `check` passes with the contracts dependency
- [ ] `UNKNOWN` availability/readiness still refuse eligibility
- [ ] Physical output remains disabled
- [ ] Local types are not deleted yet

## Hardware validation required

- [x] None
- [ ] Desktop only

## This issue is a design review. Do not start implementation until maintainers accept the plan.
