# allsparks-contracts

`allsparks-contracts` provides small, stable, pure-Java interoperability contracts for independently adoptable Allsparks FTC libraries.

This is not a robot framework. Installing it does nothing by itself. It does not control hardware. It does not replace the FTC SDK. It does not require adoption of the Allsparks stack. Functional libraries remain independently adoptable. Domain-specific state remains owned by the domain project. Optional adapters perform cross-project translation.

The project is experimental until its initial consumers (TRACE, then HELM) validate the contracts.

## What this is

A tiny JAR of immutable value types and one clock SPI:

- `MonotonicClock` / `SystemMonotonicClock` / `FakeMonotonicClock`
- `ComponentId` / `CapabilityId`
- `Validity` / `Confidence`
- `Availability` / `Readiness` / `Reason`
- `HealthSeverity` / `HealthFinding`

Maven coordinates: `org.allsparks:allsparks-contracts`.

## What this is not

- Not a scheduler, event bus, logger, or dependency-injection system
- Not a hardware abstraction
- Not OpMode lifecycle ownership
- Not `SystemCore` or any other mandatory runtime
- Not a reason to make MIMIC depend on TRACE, or ViDAR depend on HELM

## Example

A custom capability provider and consumer with no other Allsparks library:

```java
import java.util.Collections;
import org.allsparks.contracts.identity.CapabilityId;
import org.allsparks.contracts.identity.ComponentId;
import org.allsparks.contracts.status.Readiness;
import org.allsparks.contracts.status.Reason;
import org.allsparks.contracts.time.MonotonicClock;
import org.allsparks.contracts.time.SystemMonotonicClock;

public final class LiftReadinessExample {
    public static Readiness liftReadiness(MonotonicClock clock) {
        CapabilityId lift = CapabilityId.of("mechanism.lift");
        ComponentId source = ComponentId.of("lift-encoder");
        return Readiness.of(
                lift,
                Readiness.State.READY,
                Collections.emptyList(),
                clock.nowNanos(),
                source);
    }

    public static void main(String[] args) {
        Readiness readiness = liftReadiness(SystemMonotonicClock.INSTANCE);
        if (!readiness.isReady()) {
            for (Reason reason : readiness.reasons()) {
                System.out.println(reason.code() + ": " + reason.message());
            }
            return;
        }
        System.out.println(readiness.capability() + " is ready");
    }
}
```

`UNKNOWN` readiness is never treated as ready. Health severity values describe recommended impact; they do not stop a robot.

## Build

```text
./gradlew check
./gradlew javadoc
```

On Windows: `.\gradlew.bat check`.

Java 11 source and target. CI uses Temurin 17. There are no production dependencies.

## Docs

- [Architecture decision](docs/architecture/ADR-0001-contract-boundary.md)
- [Cross-project audit](docs/audits/cross-project-contract-audit.md)
- [Compatibility](docs/compatibility.md)
- [Adoption](docs/adoption.md)
- [Contributing](CONTRIBUTING.md)
- [Security](SECURITY.md)

## License

MIT. See [LICENSE](LICENSE).
