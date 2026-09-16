package org.allsparks.contracts.identity;

final class Identifiers {
    private Identifiers() {}

    static String requireNonBlank(String value, String kind) {
        if (value == null) {
            throw new IllegalArgumentException(kind + " must not be null");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(kind + " must not be blank");
        }
        return trimmed;
    }
}
