package ru.fastdelivery.useCase.config;

import java.util.Objects;

/**
 * Geographic configuration for coordinate validation.
 *
 * This configuration defines the valid geographic boundaries for Russia's
 * operational area. All shipments must have departure and destination
 * coordinates within these boundaries.
 *
 * Russia Geographic Boundaries:
 * - Latitude (North-South): 45° to 65° N
 *   From: Approximately Krasnodar region (45°N)
 *   To:   Approximately Saint Petersburg (60°N) and Murmansk (68°N, adjusted to 65°)
 * - Longitude (West-East): 30° to 96° E
 *   From: Approximately Kaliningrad region (30°E)
 *   To:   Approximately Vladivostok (130°E, adjusted to 96°)
 *
 * These boundaries cover major Russian cities and delivery routes:
 * - Moscow: 55.7558°N 37.6173°E ✓
 * - Saint Petersburg: 59.9311°N 30.3609°E ✓
 * - Samara: 53.1959°N 50.1200°E ✓
 * - Yekaterinburg: 56.8389°N 60.6057°E ✓
 * - Novosibirsk: 55.0084°N 82.9357°E ✓
 *
 * @author Дмитрий Ельцов
 * @version 1.0
 */
public class GeographicConfig {

    /**
     * Minimum latitude for valid shipment locations (degrees North).
     * Approximately southern border of operational area.
     */
    private final double minLatitude;

    /**
     * Maximum latitude for valid shipment locations (degrees North).
     * Approximately northern border of operational area.
     */
    private final double maxLatitude;

    /**
     * Minimum longitude for valid shipment locations (degrees East).
     * Approximately western border of operational area.
     */
    private final double minLongitude;

    /**
     * Maximum longitude for valid shipment locations (degrees East).
     * Approximately eastern border of operational area.
     */
    private final double maxLongitude;

    /**
     * Creates a GeographicConfig with specified coordinate boundaries.
     *
     * @param minLatitude minimum latitude (must be less than maxLatitude)
     * @param maxLatitude maximum latitude (must be greater than minLatitude)
     * @param minLongitude minimum longitude (must be less than maxLongitude)
     * @param maxLongitude maximum longitude (must be greater than minLongitude)
     * @throws IllegalArgumentException if boundaries are invalid
     */
    public GeographicConfig(double minLatitude, double maxLatitude,
                           double minLongitude, double maxLongitude) {
        if (minLatitude >= maxLatitude) {
            throw new IllegalArgumentException(
                "minLatitude must be less than maxLatitude. Got: "
                    + minLatitude + " >= " + maxLatitude);
        }
        if (minLongitude >= maxLongitude) {
            throw new IllegalArgumentException(
                "minLongitude must be less than maxLongitude. Got: "
                    + minLongitude + " >= " + maxLongitude);
        }
        if (minLatitude < -90 || maxLatitude > 90) {
            throw new IllegalArgumentException(
                "Latitude must be between -90 and 90. Got: "
                    + minLatitude + " to " + maxLatitude);
        }
        if (minLongitude < -180 || maxLongitude > 180) {
            throw new IllegalArgumentException(
                "Longitude must be between -180 and 180. Got: "
                    + minLongitude + " to " + maxLongitude);
        }

        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
    }

    /**
     * Creates a GeographicConfig for Russia with standard boundaries.
     * - Latitude: 45°N to 65°N
     * - Longitude: 30°E to 96°E
     *
     * @return GeographicConfig configured for Russia
     */
    public static GeographicConfig forRussia() {
        return new GeographicConfig(45.0, 65.0, 30.0, 96.0);
    }

    /**
     * Validates that a latitude value is within configured boundaries.
     *
     * @param latitude the latitude to validate (in degrees)
     * @throws IllegalArgumentException if latitude is outside boundaries
     */
    public void validateLatitude(double latitude) {
        if (latitude < minLatitude || latitude > maxLatitude) {
            throw new IllegalArgumentException(
                String.format("Latitude %.4f is outside valid range [%.1f, %.1f]",
                    latitude, minLatitude, maxLatitude));
        }
    }

    /**
     * Validates that a longitude value is within configured boundaries.
     *
     * @param longitude the longitude to validate (in degrees)
     * @throws IllegalArgumentException if longitude is outside boundaries
     */
    public void validateLongitude(double longitude) {
        if (longitude < minLongitude || longitude > maxLongitude) {
            throw new IllegalArgumentException(
                String.format("Longitude %.4f is outside valid range [%.1f, %.1f]",
                    longitude, minLongitude, maxLongitude));
        }
    }

    /**
     * Validates both latitude and longitude values.
     *
     * @param latitude the latitude to validate (in degrees)
     * @param longitude the longitude to validate (in degrees)
     * @throws IllegalArgumentException if either coordinate is outside boundaries
     */
    public void validateCoordinates(double latitude, double longitude) {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    /**
     * Gets the minimum latitude boundary.
     *
     * @return minimum latitude in degrees
     */
    public double getMinLatitude() {
        return minLatitude;
    }

    /**
     * Gets the maximum latitude boundary.
     *
     * @return maximum latitude in degrees
     */
    public double getMaxLatitude() {
        return maxLatitude;
    }

    /**
     * Gets the minimum longitude boundary.
     *
     * @return minimum longitude in degrees
     */
    public double getMinLongitude() {
        return minLongitude;
    }

    /**
     * Gets the maximum longitude boundary.
     *
     * @return maximum longitude in degrees
     */
    public double getMaxLongitude() {
        return maxLongitude;
    }

    /**
     * Returns a string representation of the geographic boundaries.
     *
     * @return formatted string with all boundaries
     */
    @Override
    public String toString() {
        return String.format(
            "GeographicConfig{latitude=[%.1f, %.1f], longitude=[%.1f, %.1f]}",
            minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    /**
     * Checks equality based on all boundary values.
     *
     * @param o object to compare
     * @return true if all boundaries are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeographicConfig that = (GeographicConfig) o;
        return Double.compare(that.minLatitude, minLatitude) == 0
                && Double.compare(that.maxLatitude, maxLatitude) == 0
                && Double.compare(that.minLongitude, minLongitude) == 0
                && Double.compare(that.maxLongitude, maxLongitude) == 0;
    }

    /**
     * Generates hash code based on boundary values.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }
}
