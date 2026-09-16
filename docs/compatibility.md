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

## Consumer fixtures

`src/consumer-fixture/java` compiles against the published JAR shape (the `jar` task output) with Java 11 and no other Allsparks libraries. That suite is the compatibility canary until japicmp or equivalent lands.

## Publication

Maven Central / GitHub Packages is not enabled in this repository yet. Do not treat SNAPSHOT coordinates as a released API.
