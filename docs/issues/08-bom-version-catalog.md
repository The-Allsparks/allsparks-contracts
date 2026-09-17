# [phase] Evaluate a BOM or version catalog

## Problem

Optional Allsparks stacks will otherwise pick independent `allsparks-contracts` versions and fight at TeamCode resolve time.

## Student learning objective

A team can align library versions without making every library require every other library.

## Scope

A BOM or Gradle version catalog that pins optional stack versions. Contracts remains independently adoptable.

## Out of scope

A mandatory meta-package that pulls AMPER+TRACE+HELM.

## Acceptance criteria

- [x] Documented optional catalog or BOM
- [x] Functional libraries still build without it

## Hardware validation required

- [x] None

## This issue is a design review. Do not start implementation until maintainers accept the plan.
