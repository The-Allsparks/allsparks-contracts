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

GitHub Packages is the documented remote repository. Maven Central is not enabled. The project remains experimental; a published `0.1.0-rc.1` is not a 1.0 stability claim. Do not treat SNAPSHOT coordinates as a released API.

### How maintainers publish

Publish is **not** run on every `main` push. Use a version tag or a manual Actions run:

```text
git tag v0.1.0-rc.1
git push origin v0.1.0-rc.1
```

The [Publish](../.github/workflows/publish.yml) workflow also accepts `workflow_dispatch`. A `v*` tag publishes that version (the leading `v` is stripped, so `v0.1.0-rc.1` becomes `0.1.0-rc.1`). Dispatch without a version override publishes `0.1.0-SNAPSHOT`.

Requires `packages: write` on this repository. The workflow uses the default `GITHUB_TOKEN`. Do not commit tokens.

### How consumers resolve GitHub Packages

GitHub Packages requires authentication even for public packages. In the consumer `build.gradle`:

```text
repositories {
    mavenCentral()
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

Create a GitHub PAT with `read:packages` (and SSO-authorize it for The-Allsparks if the org requires that). Set `GPR_USERNAME` to your GitHub username and `GPR_TOKEN` to the PAT. `GITHUB_ACTOR` / `GITHUB_TOKEN` work in GitHub Actions.

Gradle `includeBuild('../allsparks-contracts')` remains the zero-auth student path until Maven Central exists. See [Third-party use](third-party.md).
