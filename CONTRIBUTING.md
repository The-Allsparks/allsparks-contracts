# Contributing to allsparks-contracts

`allsparks-contracts` is maintained by [The Allsparks](https://github.com/The-Allsparks) (FTC Team 36117) for our team and the wider FTC community.

## Setup

```powershell
git clone https://github.com/The-Allsparks/allsparks-contracts.git
cd allsparks-contracts
.\gradlew.bat check
```

Coding agents: read [AGENTS.md](AGENTS.md) and [docs/architecture/ADR-0001-contract-boundary.md](docs/architecture/ADR-0001-contract-boundary.md) before adding types.

## Commands

| Command | What it prevents |
|---------|------------------|
| `.\gradlew.bat check` | Broken tests, architecture regressions, consumer-fixture compile failures, format drift, unexpected public API changes vs `api/baseline/allsparks-contracts.jar` |
| `.\gradlew.bat spotlessApply` | Java format drift (Palantir Java Format) |
| `.\gradlew.bat javadoc` | Missing or invalid Javadoc |
| `.\gradlew.bat updateApiBaseline` | (Maintainer) stale baseline after an accepted, documented API change |

## Rules of engagement

1. This JAR must not command motors, servos, or other hardware.
2. This JAR must not depend on the FTC SDK, Android, or another Allsparks functional library.
3. Do not add a scheduler, event bus, logger implementation, feature flags, or recovery engine.
4. Distinguish verified fact, engineering inference, and untested hypothesis in documentation.
5. Do not commit secrets, tokens, or student PII.

## Pull requests

- Prefer small, reviewable PRs.
- Include motivation, API impact, and test evidence.
- Update the audit or ADR when the contract boundary changes.
- Run `.\gradlew.bat check` before requesting review.
- Do not publish a Maven release from a drive-by PR.

## Line endings

The repository stores LF line endings (see [.gitattributes](.gitattributes)).

## License

Contributions are accepted under the MIT License ([LICENSE](LICENSE)). No CLA is required.
