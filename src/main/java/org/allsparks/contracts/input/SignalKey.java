package org.allsparks.contracts.input;

import java.util.Objects;
import org.allsparks.contracts.identity.ComponentId;

/**
 * Stable identity of a sampled input.
 *
 * <p>Equality and hashing use namespace, property, value type, and (for
 * {@link SignalValueType#OBJECT}) the object class. Two callbacks that read
 * the same physical property are not a key; do not deduplicate lambdas.
 *
 * @param <T> boxed Java type of the sampled value
 */
public final class SignalKey<T> implements Comparable<SignalKey<?>> {

    private final ComponentId namespace;
    private final String property;
    private final SignalValueType valueType;
    private final Class<T> objectType;
    private final int hash;

    private SignalKey(ComponentId namespace, String property, SignalValueType valueType, Class<T> objectType) {
        this.namespace = namespace;
        this.property = property;
        this.valueType = valueType;
        this.objectType = objectType;
        this.hash = hash(namespace, property, valueType, objectType);
    }

    public static SignalKey<Integer> intKey(String namespace, String property) {
        return intKey(ComponentId.of(namespace), property);
    }

    public static SignalKey<Integer> intKey(ComponentId namespace, String property) {
        return new SignalKey<>(requireNamespace(namespace), requireProperty(property), SignalValueType.INT, null);
    }

    public static SignalKey<Long> longKey(String namespace, String property) {
        return longKey(ComponentId.of(namespace), property);
    }

    public static SignalKey<Long> longKey(ComponentId namespace, String property) {
        return new SignalKey<>(requireNamespace(namespace), requireProperty(property), SignalValueType.LONG, null);
    }

    public static SignalKey<Double> doubleKey(String namespace, String property) {
        return doubleKey(ComponentId.of(namespace), property);
    }

    public static SignalKey<Double> doubleKey(ComponentId namespace, String property) {
        return new SignalKey<>(requireNamespace(namespace), requireProperty(property), SignalValueType.DOUBLE, null);
    }

    public static SignalKey<Boolean> booleanKey(String namespace, String property) {
        return booleanKey(ComponentId.of(namespace), property);
    }

    public static SignalKey<Boolean> booleanKey(ComponentId namespace, String property) {
        return new SignalKey<>(requireNamespace(namespace), requireProperty(property), SignalValueType.BOOLEAN, null);
    }

    public static <T> SignalKey<T> of(String namespace, String property, Class<T> type) {
        return of(ComponentId.of(namespace), property, type);
    }

    public static <T> SignalKey<T> of(ComponentId namespace, String property, Class<T> type) {
        Objects.requireNonNull(type, "type");
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("Use intKey/longKey/doubleKey/booleanKey for primitives, not " + type);
        }
        return new SignalKey<>(requireNamespace(namespace), requireProperty(property), SignalValueType.OBJECT, type);
    }

    public ComponentId namespace() {
        return namespace;
    }

    public String property() {
        return property;
    }

    public SignalValueType valueType() {
        return valueType;
    }

    /**
     * Object payload class. Empty for primitive keys.
     */
    public Class<T> objectType() {
        return objectType;
    }

    public String qualifiedName() {
        return namespace.value() + "/" + property;
    }

    @Override
    public int compareTo(SignalKey<?> other) {
        Objects.requireNonNull(other, "other");
        int byNamespace = namespace.value().compareTo(other.namespace.value());
        if (byNamespace != 0) {
            return byNamespace;
        }
        int byProperty = property.compareTo(other.property);
        if (byProperty != 0) {
            return byProperty;
        }
        int byType = valueType.compareTo(other.valueType);
        if (byType != 0) {
            return byType;
        }
        String thisClass = objectType == null ? "" : objectType.getName();
        String otherClass = other.objectType == null ? "" : other.objectType.getName();
        return thisClass.compareTo(otherClass);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SignalKey)) {
            return false;
        }
        SignalKey<?> that = (SignalKey<?>) other;
        return namespace.equals(that.namespace)
                && property.equals(that.property)
                && valueType == that.valueType
                && Objects.equals(objectType, that.objectType);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * Same mixing as {@link Objects#hash(Object...)} without the varargs array.
     * HashMap lookups on the control loop must not allocate.
     */
    private static int hash(ComponentId namespace, String property, SignalValueType valueType, Class<?> objectType) {
        int result = 1;
        result = 31 * result + namespace.hashCode();
        result = 31 * result + property.hashCode();
        result = 31 * result + valueType.hashCode();
        result = 31 * result + (objectType == null ? 0 : objectType.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return qualifiedName() + ":" + valueType;
    }

    private static ComponentId requireNamespace(ComponentId namespace) {
        return Objects.requireNonNull(namespace, "namespace");
    }

    private static String requireProperty(String property) {
        if (property == null) {
            throw new IllegalArgumentException("property must not be null");
        }
        String trimmed = property.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("property must not be blank");
        }
        return trimmed;
    }
}
