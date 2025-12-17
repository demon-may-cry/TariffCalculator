package ru.fastdelivery.domain.common.coordinate;

import java.util.Objects;

/**
 * Represents a single geographic coordinate (latitude or longitude).
 * This is a value object that encapsulates a coordinate value with validation.
 *
 * @param value the coordinate value in degrees
 */
public record Coordinate(double value) {

    /**
     * Creates a Coordinate with specified value and range validation.
     * This constructor is used internally for validation.
     *
     * @param value the coordinate value
     * @param min minimum allowed value (inclusive)
     * @param max maximum allowed value (inclusive)
     * @throws IllegalArgumentException if value is outside [min, max] range
     */
    public Coordinate(double value, double min, double max) {
        this(value);
        validateRange(value, min, max);
    }

    /**
     * Creates a latitude coordinate with validation.
     * Typical latitude range: 45.0 to 65.0 degrees for Russia.
     *
     * @param lat the latitude value
     * @param minLat minimum allowed latitude
     * @param maxLat maximum allowed latitude
     * @return new Coordinate representing latitude
     * @throws IllegalArgumentException if latitude is outside valid range
     */
    public static Coordinate latitude(double lat, double minLat, double maxLat) {
        return new Coordinate(lat, minLat, maxLat);
    }

    /**
     * Creates a longitude coordinate with validation.
     * Typical longitude range: 30.0 to 96.0 degrees for Russia.
     *
     * @param lon the longitude value
     * @param minLon minimum allowed longitude
     * @param maxLon maximum allowed longitude
     * @return new Coordinate representing longitude
     * @throws IllegalArgumentException if longitude is outside valid range
     */
    public static Coordinate longitude(double lon, double minLon, double maxLon) {
        return new Coordinate(lon, minLon, maxLon);
    }

    /**
     * Gets the coordinate value.
     *
     * @return the coordinate value in degrees
     */
    public double getValue() {
        return value;
    }

    /**
     * Compares this coordinate with another.
     *
     * @param other the other Coordinate to compare with
     * @return true if values are equal, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isEqualTo(Coordinate other) {
        Objects.requireNonNull(other, "other must not be null");
        return Double.compare(this.value, other.value) == 0;
    }

    /**
     * Compares this coordinate with another.
     *
     * @param other the other Coordinate to compare with
     * @return true if this coordinate is greater than other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isGreaterThan(Coordinate other) {
        Objects.requireNonNull(other, "other must not be null");
        return Double.compare(this.value, other.value) > 0;
    }

    /**
     * Compares this coordinate with another.
     *
     * @param other the other Coordinate to compare with
     * @return true if this coordinate is less than other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isLessThan(Coordinate other) {
        Objects.requireNonNull(other, "other must not be null");
        return Double.compare(this.value, other.value) < 0;
    }

    /**
     * Calculates the difference between this coordinate and another.
     * Used for distance calculations.
     *
     * @param other the other Coordinate
     * @return the absolute difference in degrees
     * @throws NullPointerException if other is null
     */
    public double differenceFrom(Coordinate other) {
        Objects.requireNonNull(other, "other must not be null");
        return Math.abs(this.value - other.value);
    }

    /**
     * Validates that a coordinate value is within the specified range.
     *
     * @param value the coordinate value to validate
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @throws IllegalArgumentException if value is outside [min, max] range
     */
    private static void validateRange(double value, double min, double max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                String.format("Coordinate %.6f not in range [%.2f, %.2f]",
                    value, min, max));
        }
    }

    @Override
    public String toString() {
        return String.format("%.6f°", value);
    }
}
