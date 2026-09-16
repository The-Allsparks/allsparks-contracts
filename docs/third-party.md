# Third-party use

This JAR is for any Java 11 project that wants shared clocks, identifiers, validity, readiness, and health envelopes. You do not need TRACE, HELM, TeamCode, or any other Allsparks library.

Installing it does nothing by itself. It does not command motors, servos, or other hardware. It does not replace the FTC SDK.

## Coordinates

Maven coordinates: `org.allsparks:allsparks-contracts`.

The current version is `0.1.0-SNAPSHOT`. Maven Central / GitHub Packages is not enabled yet. Until publication lands, consume a sibling checkout with Gradle composite `includeBuild`. Do not treat SNAPSHOT coordinates as a released API.

In the consumer `settings.gradle` (or `settings.gradle.kts`):

```text
includeBuild('../allsparks-contracts')
```

In the consumer `build.gradle`:

```text
dependencies {
    implementation 'org.allsparks:allsparks-contracts:0.1.0-SNAPSHOT'
}
```

`includeBuild` substitutes those coordinates from the checkout. No Maven login is required. Publication is tracked separately; this guide does not enable it.

## Requirements

- Java **11** source and target. There is no Java 8 contracts line.
- No FTC SDK dependency.
- No Android or AndroidX packaging.
- No other Allsparks functional library.

`./gradlew check` in this repository compiles a Java 11 consumer against only this JAR. That fixture is the proof that a visiting team can copy one example without installing TRACE or HELM.

## Copy-paste example

Copy [`src/consumer-fixture/java/org/allsparks/contracts/consumer/LiftReadinessExample.java`](../src/consumer-fixture/java/org/allsparks/contracts/consumer/LiftReadinessExample.java).

That class:

- Creates a custom `CapabilityId` (`mechanism.lift`) without editing this repository
- Builds a `Readiness` value from `SystemMonotonicClock`
- Treats `UNKNOWN` as not ready

The same snippet is in the [README](../README.md). Keep domain payloads (encoder ticks, game-piece types, season constants) in your project. Map them at the edge.

## Custom capability identifiers

This library does not ship a central capability list. Call `CapabilityId.of("your.capability")` with a stable, human-readable value. Examples already used in this repository: `drive.translation`, `mechanism.lift`, `vision.game-piece-observation`, `power.observation`.

Custom values are valid without a pull request here. Do not send season game-piece types or robot-specific names into this repository.

## Mapping local enums

Keep your local types. Convert leftover constants in an adapter, not by widening this JAR.

The leftover table (AMPER `SKIPPED`, MIMIC `DISAGREEING`, HELM `STALE`, TRACE `NOTICE` / `ERROR` / `FAULT`, and the shared `Validity` / `HealthSeverity` mapping) lives in [Compatibility](compatibility.md). Use that table; do not add a fifth `Availability` value or new severity constants to this artifact.

## Support

File bugs, questions, and contract-change requests in this GitHub repository: [The-Allsparks/allsparks-contracts issues](https://github.com/The-Allsparks/allsparks-contracts/issues).

Do not file contracts questions against TRACE, HELM, or an FTC SDK fork. This JAR does not own OpMode lifecycle, hardware, or season types.

See [CONTRIBUTING.md](../CONTRIBUTING.md) for local `./gradlew check` and [SECURITY.md](../SECURITY.md) for vulnerability reports.

## Out of scope

- Android AAR packaging
- Season game-piece types
- Maven Central publication (not enabled in this repository yet)
- A scheduler, event bus, logger, or hardware abstraction
