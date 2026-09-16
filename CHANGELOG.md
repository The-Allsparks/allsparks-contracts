# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Initial experimental contracts: monotonic clock, component and capability identifiers, validity, confidence, availability, readiness, structured reasons, and health findings.
- Cross-project audit and ADR-0001 contract boundary.
- japicmp check against `api/baseline/allsparks-contracts.jar`. Binary or source incompatibilities, including new enum constants, fail `./gradlew check` until the baseline is refreshed with a documented migration.
- Third-party use guide: coordinates, Java 11 with no FTC or Android dependency, leftover enum mapping, custom `CapabilityId` values, and where to file issues.
- GitHub Packages publication for `org.allsparks:allsparks-contracts` (main, sources, and javadoc JARs; MIT POM; no FTC or Android artifacts). Maintainers publish with tag `v0.1.0-rc.1` or Actions `workflow_dispatch`. Consumers authenticate even for public packages; `includeBuild` remains the zero-auth student path. Experimental, not 1.0. Maven Central is not enabled.

### Changed

- ADR-0001 accepted: v0 public allowlist is unchanged and lifecycle interfaces remain deferred.
