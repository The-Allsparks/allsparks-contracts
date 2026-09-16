# Third-party use

This JAR is for any Java 11 project that wants shared clocks, identifiers, validity, readiness, and health envelopes. You do not need TRACE, HELM, TeamCode, or any other Allsparks library.

Installing it does nothing by itself. It does not command motors, servos, or other hardware. It does not replace the FTC SDK.

## Coordinates

Maven coordinates: `org.allsparks:allsparks-contracts`.

Published experimental versions (not 1.0) live on GitHub Packages. Maven Central is not enabled. Local development still uses `0.1.0-SNAPSHOT`. Do not treat SNAPSHOT coordinates as a released API.

**Zero-auth student path:** Gradle composite `includeBuild` against a sibling checkout. No Maven login is required, and this remains the recommended classroom path until Maven Central exists.

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

`includeBuild` substitutes those coordinates from the checkout.

**GitHub Packages:** GitHub requires authentication even for public packages. Add the repository and a `read:packages` PAT (see [Compatibility](compatibility.md) for the full snippet):

```text
repositories {
    maven {
        url = uri('https://maven.pkg.github.com/The-Allsparks/allsparks-contracts')
        credentials {
            username = System.getenv('GPR_USERNAME') ?: System.getenv('GITHUB_ACTOR')
            password = System.getenv('GPR_TOKEN') ?: System.getenv('GITHUB_TOKEN')
        }
    }
}

dependencies {
    implementation 'org.allsparks:allsparks-contracts:0.1.0-rc.1'
}
```

Maintainers publish with tag `v0.1.0-rc.1` or Actions `workflow_dispatch`. Students should keep using `includeBuild` unless they already have GitHub Packages credentials.

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
- Maven Central publication (GitHub Packages is the current remote; Central is not enabled)
- A scheduler, event bus, logger, or hardware abstraction
