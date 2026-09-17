# Optional version catalog

TeamCode can pin Allsparks library versions from one file so TRACE and HELM
resolve the same `allsparks-contracts` release. The catalog is **optional**.
This repository's Gradle build does not apply it. Functional libraries still
build independently without it.

This is a Gradle version catalog, not a Maven BOM and not a meta-package. It
does not create compile-time edges between functional libraries. Selecting
`allsparks.trace` does not pull HELM, AMPER, or MIMIC. There is no bundle that
pulls AMPER + TRACE + HELM.

`includeBuild` remains the zero-auth student path.

## What is pinned

| Alias | Coordinate | Status |
| ----- | ---------- | ------ |
| `contracts` | `org.allsparks:allsparks-contracts:0.1.0-rc.1` | Published on GitHub Packages (not Maven Central) |
| `trace` | `org.allsparks:trace:0.1.0-SNAPSHOT` | Unpublished SNAPSHOT note for `includeBuild` |
| `helm` | `org.allsparks:helm:0.1.0-SNAPSHOT` | Unpublished SNAPSHOT note for `includeBuild` |

TRACE and HELM are not published to Maven Central. Do not invent remote
coordinates for them. Use a sibling checkout and `includeBuild`, the same way
TeamCode already substitutes SHIFT, AMPER, and TRACE.

The catalog file is [`gradle/allsparks-stack.versions.toml`](../gradle/allsparks-stack.versions.toml).
It is not named `libs.versions.toml`, so this library's build does not
auto-import it.

## Opt in from TeamCode `settings.gradle`

Only add this if `allsparks-contracts` is a sibling checkout (or you have
copied the TOML). If the file is missing, Gradle fails configuration. Skip
the catalog and keep hardcoded versions if you do not want that coupling.

```text
dependencyResolutionManagement {
    versionCatalogs {
        allsparks {
            from(files("../allsparks-contracts/gradle/allsparks-stack.versions.toml"))
        }
    }
}

includeBuild('../allsparks-contracts')
includeBuild('../TRACE')
includeBuild('../HELM')
```

You can copy the TOML into the robot project instead:

```text
allsparks {
    from(files("gradle/allsparks-stack.versions.toml"))
}
```

Existing `includeBuild('../SHIFT')` / `includeBuild('../AMPER')` /
`includeBuild('../TRACE')` lines stay. The catalog does not replace composite
builds.

## Opt in from TeamCode `build.gradle`

Use only the aliases you need. Do not add TRACE in order to use contracts, and
do not add HELM in order to use TRACE.

```text
dependencies {
    implementation allsparks.contracts
    implementation allsparks.trace
}
```

Kotlin DSL: `implementation(allsparks.contracts)`.

GitHub Packages still requires authentication for the published contracts
coordinate when `includeBuild('../allsparks-contracts')` is absent. Snippets:
[Compatibility](compatibility.md), [Third-party use](third-party.md).
Students without a PAT should keep `includeBuild`.

## What this does not do

- It does not make `allsparks-contracts` depend on TRACE or HELM.
- It does not make TRACE depend on HELM, or HELM depend on TRACE.
- It does not require consumers of this JAR to import the catalog.
- It does not publish a `java-platform` BOM or a stack meta-package.
- It does not replace `includeBuild` substitution.

Visiting teams that want only this JAR: [Third-party use](third-party.md).
Adoption order and adapter direction: [Adoption](adoption.md).
