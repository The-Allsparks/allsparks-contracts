package org.allsparks.contracts.health;

/**
 * Recommended impact of a health finding.
 *
 * <p>This enum does not execute a stop, disable hardware, or start recovery.
 * Consumers decide whether to act. There is no {@code FATAL} constant; that
 * word is undefined without an owning runtime.
 */
public enum HealthSeverity {
    INFO,
    WARNING,
    DEGRADED,
    STOP_COMPONENT,
    STOP_DEPENDENCIES,
    STOP_ROBOT
}
