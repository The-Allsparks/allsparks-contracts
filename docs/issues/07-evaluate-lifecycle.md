# [phase] Evaluate lifecycle contracts after two consumers

## Problem

`start`, `periodic`, `tick`, `attach`, `stop`, and `close` look similar across libraries. They are different operations. Sharing them too early would imply hardware or OpMode ownership.

## Student learning objective

A student can explain that closing a camera portal is not the same as stopping a mechanism session.

## Scope

After TRACE and HELM consume contracts, compare remaining lifecycle methods. Admit a shared interface only if the admission rule passes.

## Out of scope

Adding lifecycle types in this issue before the two pilots exist.

## Acceptance criteria

- [x] Written comparison of TRACE, HELM, and one producer lifecycle
- [x] Decision to share or keep deferred, recorded in a new ADR if sharing

Decision: **keep deferred**. Comparison and admission-rule failure are recorded in [ADR-0002](../architecture/ADR-0002-lifecycle-deferred.md). No shared lifecycle types were added.

## Hardware validation required

- [x] None

## This issue is a design review. Do not start implementation until maintainers accept the plan.
