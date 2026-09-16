# [phase] Create the first external integration adapter

## Problem

Cross-project translation still happens as written docs or TeamCode glue. That tempts MIMIC to depend on TRACE or ViDAR to depend on HELM.

## Student learning objective

A student can say: adapters depend on two libraries; the libraries do not depend on each other.

## Scope

After TRACE and HELM pilots, create one adapter artifact such as `trace-mimic-adapter` or `helm-beacon-adapter` in its own repository or module. Depend on `allsparks-contracts` plus the two functional libraries.

## Out of scope

Implementing that adapter inside `allsparks-contracts`. Adding a scheduler.

## Acceptance criteria

- [ ] Adapter compile-time direction is adapter -> A + B, never A -> B
- [ ] Both functional libraries still build independently
- [ ] No hardware commands in the adapter unless an owning library already exposes them behind flags

## Hardware validation required

- [x] None

## This issue is a design review. Do not start implementation until maintainers accept the plan.
