# [phase] Prepare 1.0 stability criteria

## Problem

The artifact is experimental until TRACE and HELM validate it. 1.0 needs an explicit bar.

## Student learning objective

A student can tell whether a version is still allowed to rename a type.

## Scope

Write 1.0 criteria: two consuming libraries released against the contracts, compatibility tests, no pending lifecycle surprise, documented enum mapping, publication in place.

## Out of scope

Declaring 1.0 in this issue.

## Acceptance criteria

- [x] Written 1.0 checklist in docs/compatibility.md
- [x] TRACE and HELM pilots completed or explicitly waived

TRACE: [TRACE PR #42](https://github.com/The-Allsparks/TRACE/pull/42). HELM: [HELM PR #48](https://github.com/The-Allsparks/HELM/pull/48). Both consume `0.1.0-rc.1` or SNAPSHOT and are not themselves 1.0. Writing the checklist does not declare contracts 1.0.

## Hardware validation required

- [x] None

## This issue is a design review. Do not start implementation until maintainers accept the plan.
