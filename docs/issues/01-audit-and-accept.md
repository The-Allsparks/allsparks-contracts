# [phase] Audit and accept initial shared contracts

## Problem

Allsparks libraries duplicate clocks, identifiers, validity, confidence, availability, readiness, reasons, and health envelopes under project prefixes.

## Student learning objective

A student can say: these libraries share a tiny contract JAR, and installing it does not control the robot.

## Scope

Review [docs/audits/cross-project-contract-audit.md](../audits/cross-project-contract-audit.md) and [docs/architecture/ADR-0001-contract-boundary.md](../architecture/ADR-0001-contract-boundary.md). Accept or reject the v0 allowlist.

## Out of scope

Rewriting AMPER, TRACE, HELM, or any other functional repository.

## Acceptance criteria

- [x] Maintainers accept ADR-0001
- [x] Public type allowlist remains the admitted set
- [x] Lifecycle interfaces stay deferred

## Hardware validation required

- [x] None

## This issue is a design review. Do not start implementation until maintainers accept the plan.
