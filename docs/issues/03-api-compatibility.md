# [phase] Add API compatibility checking

## Problem

Enum constants and value-type equality can change silently. Consumer fixtures catch some of that, not all of it.

## Student learning objective

A student can trust that a minor version will still compile their switch statements or get a documented migration.

## Scope

japicmp, Revapi, or equivalent against the previous release. Fail CI on unexpected binary or source incompatible changes.

## Out of scope

Widening the contract boundary.

## Acceptance criteria

- [x] CI compares the public API to the last published baseline
- [x] Adding an enum constant is flagged as a compatibility event
- [x] Documented in docs/compatibility.md

## Hardware validation required

- [x] None

## This issue is a design review. Do not start implementation until maintainers accept the plan.
