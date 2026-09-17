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
- Optional Gradle version catalog at `gradle/allsparks-stack.versions.toml` pinning `org.allsparks:allsparks-contracts:0.1.0-rc.1` and recording unpublished TRACE/HELM `0.1.0-SNAPSHOT` includeBuild notes. This build does not apply the catalog. It is not a BOM or a meta-package. See [version-catalog.md](docs/version-catalog.md).

### Changed

- ADR-0001 accepted: v0 public allowlist is unchanged and lifecycle interfaces remain deferred.

### Documentation

- TRACE and HELM first-pilot mappings are recorded in [adoption.md](docs/adoption.md). TRACE: clock, quality, and severity mapped at the TRACE edge without deleting local types ([TRACE PR #42](https://github.com/The-Allsparks/TRACE/pull/42)). HELM: clock, capability id, availability (`STALE` via freshness), and confidence mapped at the HELM edge without deleting local types ([HELM PR #48](https://github.com/The-Allsparks/HELM/pull/48)). Physical output remains disabled. No Java API change.
- After TRACE and HELM consumed contracts, lifecycle comparison versus MIMIC `periodic`/`stop` (with ViDAR camera attach as extra evidence) fails the admission rule. [ADR-0002](docs/architecture/ADR-0002-lifecycle-deferred.md) keeps start/stop/close/periodic/attach interfaces deferred. No Java API change.
- 1.0 stability checklist in [compatibility.md](docs/compatibility.md): two consuming libraries (TRACE PR #42, HELM PR #48, still on `0.1.0-rc.1` / SNAPSHOT and not themselves 1.0), japicmpGate required CI, lifecycle remains deferred (ADR-0002), documented enum mapping, GitHub Packages publication (Maven Central optional). Writing the checklist does not declare or publish 1.0. No Java API change.

### Fixed

- Quoted the Publish workflow `workflow_dispatch` version description so GitHub Actions can parse the YAML.
