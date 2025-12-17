package ru.fastdelivery.domain.common.dimension;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a length measurement in millimeters.
 * This is a value object that encapsulates length and provides validation.
 *
 * @param millimeters length value in millimeters using BigInteger for precision
 */
public record Length(BigInteger millimeters) {
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger MAX_LENGTH_DIMENSION = BigInteger.valueOf(9999L);

    /**
     * Compact constructor for validation.
     * Ensures that:
     * - millimeters is not null
     * - millimeters is not negative (>= 0)
     * - millimeters does not exceed maximum allowed value
     */
    public Length {
        Objects.requireNonNull(millimeters, "millimeters must not be null");
        
        if (millimeters.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                String.format("Length cannot be negative: %s", millimeters));
        }
        
        if (millimeters.compareTo(MAX_LENGTH_DIMENSION) > 0) {
            throw new IllegalArgumentException(
                String.format("Length %s exceeds maximum %s", 
                    millimeters, MAX_LENGTH_DIMENSION));
        }
    }

    /**
     * Creates a Length from millimeters.
     *
     * @param mm length in millimeters (as long)
     * @return new Length instance
     * @throws IllegalArgumentException if mm is negative or exceeds maximum
     */
    public static Length fromMillimeter(long mm) {
        return new Length(BigInteger.valueOf(mm));
    }

    /**
     * Creates a Length from millimeters.
     *
     * @param mm length in millimeters (as BigInteger)
     * @return new Length instance
     * @throws IllegalArgumentException if mm is negative or exceeds maximum
     */
    public static Length fromMillimeter(BigInteger mm) {
        return new Length(mm);
    }

    /**
     * Creates a Length from centimeters.
     *
     * @param cm length in centimeters
     * @return new Length instance (converted to millimeters)
     * @throws IllegalArgumentException if resulting length is negative or exceeds maximum
     */
    public static Length fromCentimeter(double cm) {
        long mm = Math.round(cm * 10);
        return fromMillimeter(mm);
    }

    /**
     * Compares this length with another length.
     *
     * @param other the other Length to compare with
     * @return true if this length is longer than the other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isLongerThan(Length other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.millimeters.compareTo(other.millimeters) > 0;
    }

    /**
     * Compares this length with another length.
     *
     * @param other the other Length to compare with
     * @return true if this length is shorter than the other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isShorterThan(Length other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.millimeters.compareTo(other.millimeters) < 0;
    }

    /**
     * Compares this length with another length.
     *
     * @param other the other Length to compare with
     * @return true if this length equals the other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isEqualTo(Length other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.millimeters.compareTo(other.millimeters) == 0;
    }

    /**
     * Gets the length value in millimeters.
     *
     * @return length in millimeters as BigInteger
     */
    public BigInteger inMillimeters() {
        return millimeters;
    }

    /**
     * Gets the length value in millimeters as long.
     *
     * @return length in millimeters as long
     * @throws ArithmeticException if value is too large for long
     */
    public long inMillimetersAsLong() {
        return millimeters.longValueExact();
    }

    /**
     * Gets the length value in centimeters.
     *
     * @return length in centimeters as double
     */
    public double inCentimeters() {
        return millimeters.doubleValue() / 10.0;
    }
}
