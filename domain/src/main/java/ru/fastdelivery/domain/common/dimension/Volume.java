package ru.fastdelivery.domain.common.dimension;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a volume measurement in cubic centimeters.
 * This is a value object that encapsulates volume and provides validation.
 *
 * @param cubicCentimeters volume value in cubic centimeters
 */
public record Volume(long cubicCentimeters) {
    private static final long MAX_VOLUME = 1_000_000L;

    /**
     * Compact constructor for validation.
     * Ensures that:
     * - cubicCentimeters is not negative (>= 0)
     * - cubicCentimeters does not exceed maximum allowed value
     */
    public Volume {
        if (cubicCentimeters < 0) {
            throw new IllegalArgumentException(
                String.format("Volume cannot be negative: %d", cubicCentimeters));
        }
        
        if (cubicCentimeters > MAX_VOLUME) {
            throw new IllegalArgumentException(
                String.format("Volume %d exceeds maximum %d", 
                    cubicCentimeters, MAX_VOLUME));
        }
    }

    /**
     * Creates a Volume from cubic centimeters.
     *
     * @param cm3 volume in cubic centimeters
     * @return new Volume instance
     * @throws IllegalArgumentException if cm3 is negative or exceeds maximum
     */
    public static Volume fromCubicCentimeters(long cm3) {
        return new Volume(cm3);
    }

    /**
     * Creates a Volume from cubic meters.
     * 1 cubic meter = 1,000,000 cubic centimeters
     *
     * @param m3 volume in cubic meters
     * @return new Volume instance (converted to cubic centimeters)
     * @throws IllegalArgumentException if resulting volume is negative or exceeds maximum
     */
    public static Volume fromCubicMeters(double m3) {
        if (m3 < 0) {
            throw new IllegalArgumentException(
                String.format("Volume in cubic meters cannot be negative: %.6f", m3));
        }
        
        long cm3 = Math.round(m3 * 1_000_000.0);
        return new Volume(cm3);
    }

    /**
     * Adds another volume to this volume.
     *
     * @param other the volume to add
     * @return new Volume instance representing the sum
     * @throws NullPointerException if other is null
     * @throws IllegalArgumentException if sum exceeds maximum
     */
    public Volume add(Volume other) {
        Objects.requireNonNull(other, "other must not be null");
        return new Volume(this.cubicCentimeters + other.cubicCentimeters);
    }

    /**
     * Compares this volume with another volume.
     *
     * @param other the other Volume to compare with
     * @return true if this volume is greater than the other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isGreaterThan(Volume other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.cubicCentimeters > other.cubicCentimeters;
    }

    /**
     * Compares this volume with another volume.
     *
     * @param other the other Volume to compare with
     * @return true if this volume is less than the other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isLessThan(Volume other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.cubicCentimeters < other.cubicCentimeters;
    }

    /**
     * Gets the volume value in cubic centimeters.
     *
     * @return volume in cubic centimeters
     */
    public long getValue() {
        return cubicCentimeters;
    }

    /**
     * Gets the volume value in cubic centimeters.
     *
     * @return volume in cubic centimeters as BigDecimal for calculations
     */
    public BigDecimal getValueAsBigDecimal() {
        return BigDecimal.valueOf(cubicCentimeters);
    }

    /**
     * Gets the volume value in cubic meters.
     *
     * @return volume in cubic meters
     */
    public double getValueInCubicMeters() {
        return cubicCentimeters / 1_000_000.0;
    }
}
