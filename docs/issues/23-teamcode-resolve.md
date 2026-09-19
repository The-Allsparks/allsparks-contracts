# [phase] Confirm TeamCode Hub resolve of contracts

GitHub: [#23](https://github.com/The-Allsparks/allsparks-contracts/issues/23)

## Problem

TRACE already depends on `allsparks-contracts`, and TeamCode already `includeBuild`s TRACE. Students still cannot say whether the robot APK gets the sibling contracts JAR without a GitHub Packages token. Nested `includeBuild` from TRACE is unconfirmed on the Hub path. TeamCode still compiles as Java 8 with desugaring.

## Student learning objective

A student can deploy Drive with TRACE on the Control Hub and say: the contracts JAR came from the sibling folder, not from a PAT.

## Scope

In `FtcRobotController` (branch `bumblebee`):

- Assemble TeamCode and show how `org.allsparks:allsparks-contracts` is substituted.
- If nested `includeBuild` from TRACE is missing or flaky, add `includeBuild('../allsparks-contracts')` next to TRACE.
- Install the existing Drive OpMode on a Control Hub. Confirm INIT still runs.
- Record the result in [adoption.md](../adoption.md) step 6.

FtcRobotController GitHub issues are disabled, so this issue lives here. The Java change, if any, is in `FtcRobotController/settings.gradle`. This repository's public Java API does not change.

## Out of scope

Adding HELM to TeamCode. Importing the optional version catalog. New TeamCode types that import contracts envelopes. Declaring contracts 1.0. Mapping MIMIC, BEACON, AMPER, SHIFT, or ViDAR. Match enablement.

## Acceptance criteria

- [ ] TeamCode assemble shows `allsparks-contracts` coming from the sibling checkout (or documents why GitHub Packages was required)
- [ ] Students without a PAT can still build when `../allsparks-contracts` exists
- [ ] Control Hub install of the existing Drive OpMode succeeds
- [ ] Drive INIT still runs; TRACE observe-only; no new `setPower` path
- [ ] [adoption.md](../adoption.md) records step 6
- [ ] No public Java API change in `allsparks-contracts`

## Hardware validation required

- [ ] None
- [ ] Desktop only
- [x] Control Hub
- [ ] Robot
- [ ] Match

## Rollback or disable strategy

Revert the `includeBuild` line in `FtcRobotController/settings.gradle`. TRACE can still declare the Maven coordinate.

## Parent roadmap issue

[Adoption](../adoption.md) staged order step 6. Follows closed #4 (TRACE), #5 (HELM), and #10 (1.0 criteria, not a 1.0 release).

## This issue is a design review. Do not start implementation until maintainers accept the plan.
