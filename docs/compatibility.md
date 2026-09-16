# Compatibility

`allsparks-contracts` uses [Semantic Versioning](https://semver.org/). Maven coordinates: `org.allsparks:allsparks-contracts`.

The project is experimental until TRACE and HELM validate the contracts. Version `0.1.0-SNAPSHOT` is not a stability promise.

## Java

Source and target **11**. CI uses Temurin 17. AMPER and SHIFT currently compile as Java 8; that is generation drift. They must move to Java 11 when they consume this artifact. There is no Java 8 contracts line.

## Before 1.0

- Avoid unnecessary breaking changes.
- Mark experimental APIs in Javadoc.
- Do not remove a public type without migration documentation.
- Require an ADR to widen the project's responsibility (runtime, hardware, FTC, lifecycle ownership).

## At and after 1.0

- Public API removals or semantic changes require a major version.
- Adding enum constants is a compatibility event. Consumers may use exhaustive switches. Prefer a major version, or a documented minor with a migration note if the new constant is strictly additive and unused values remain valid.
- Serialized identifiers (`ComponentId`, `CapabilityId`, `Reason` codes) must remain stable.
- Equality and hash semantics must not change in a minor release.
- Clock and timestamp semantics must not change silently. `nowNanos()` must not be redefined as UTC, wall-clock, or camera capture time.

## Enum mapping

Adapters own leftover constants:

| Local leftover | Shared mapping |
| -------------- | -------------- |
| AMPER `SKIPPED` | `Validity.INVALID` |
| MIMIC `DISAGREEING` | `Validity.INVALID` |
| HELM availability `STALE` | `Validity.STALE` or age, not `Availability` |
| TRACE `NOTICE` | `HealthSeverity.INFO` |
| TRACE `ERROR` | `HealthSeverity.DEGRADED` |
| TRACE `FAULT` | `HealthSeverity.STOP_COMPONENT` unless documented otherwise |

## API compatibility check

`./gradlew check` runs japicmp (build/test plugin only; not a production dependency) against the committed baseline JAR:

```text
api/baseline/allsparks-contracts.jar
```

Each build's `jar` output is compared to that file. japicmp's own binary/source flags treat a new enum constant as a compatible `NEW` field (`compatibilityChanges` is empty), so `failOnSourceIncompatibility` would miss it. `japicmpGate` therefore fails `check` on **any** public API modification in the japicmp XML, and labels `ENUM_CONSTANT_ADDED` when the new member is an enum field. Adding `Validity.FOO`, `Availability.FOO`, `Readiness.State.FOO`, or `HealthSeverity.FOO` fails CI until maintainers accept the change and refresh the baseline. Do not add those changes to `compatibilityChangeExcludes`.

Reports land in `build/reports/japicmp/`.

The Java 11 consumer fixture in `src/consumer-fixture/java` remains a compile-against-the-jar canary. It does not replace japicmp.

### Refreshing the baseline

After an intentional, accepted API change (with a CHANGELOG entry and a migration note):

```text
./gradlew updateApiBaseline
```

On Windows: `.\gradlew.bat updateApiBaseline`. Commit `api/baseline/allsparks-contracts.jar` in the same PR as the API change. Do not refresh the baseline to silence an accidental break.

To prove enum-constant detection locally without keeping an API change, add a throwaway constant, run `./gradlew japicmpGate` (it must fail with `ENUM_CONSTANT_ADDED`), then revert the source. Do not commit that constant, and do not run `updateApiBaseline` for it.

## Publication

Maven Central / GitHub Packages is not enabled in this repository yet. Do not treat SNAPSHOT coordinates as a released API.
