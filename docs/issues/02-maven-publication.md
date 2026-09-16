# [phase] Establish Maven publication

## Problem

Consumers cannot depend on a released coordinate yet. `0.1.0-SNAPSHOT` is local / includeBuild only.

## Student learning objective

A student can add `org.allsparks:allsparks-contracts` the same way they add other Java libraries.

## Scope

GitHub Packages or Maven Central publication, signing, and a documented install snippet.

## Out of scope

Publishing a 1.0 stability claim. Changing the public API.

## Acceptance criteria

- [ ] A documented non-SNAPSHOT or snapshot repository consumers can resolve
- [ ] Sources and Javadoc JARs publish with the main JAR
- [ ] No FTC or Android artifacts are attached

## Hardware validation required

- [x] None

## This issue is a design review. Do not start implementation until maintainers accept the plan.
