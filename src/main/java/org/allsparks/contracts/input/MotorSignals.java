package org.allsparks.contracts.input;

/**
 * Shared motor electrical {@link SignalKey} identities.
 *
 * <p>Over-current flags ride in a hub bulk packet; current in amperes does
 * not. Libraries that watch the flag and demand a follow-up current sample
 * must use these keys so a sampler and an observer name the same signals.
 * Device names are HardwareMap labels, not a sampling runtime type.
 */
public final class MotorSignals {
    public static final String NAMESPACE = "motor";

    private MotorSignals() {}

    /**
     * Per-device over-current alert flag (boolean). Not an amp reading.
     */
    public static SignalKey<Boolean> overCurrent(String deviceName) {
        return SignalKey.booleanKey(NAMESPACE, deviceProperty(deviceName, "overCurrent"));
    }

    /**
     * Per-device motor current in amperes. Not bulk-cached on REV Hubs.
     */
    public static SignalKey<Double> currentAmps(String deviceName) {
        return SignalKey.doubleKey(NAMESPACE, deviceProperty(deviceName, "currentAmps"));
    }

    private static String deviceProperty(String deviceName, String field) {
        if (deviceName == null) {
            throw new IllegalArgumentException("deviceName is required");
        }
        String trimmed = deviceName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("deviceName must not be blank");
        }
        return trimmed + "/" + field;
    }
}
