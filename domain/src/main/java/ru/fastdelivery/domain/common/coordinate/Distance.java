package ru.fastdelivery.domain.common.coordinate;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents distance between two geographic points in kilometers.
 * Uses the Haversine formula for accurate great-circle distance calculation.
 *
 * @param kilometers distance in kilometers
 */
public record Distance(double kilometers) {
    private static final double MAX_DISTANCE = 20000.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Compact constructor for validation.
     * Ensures that distance is not negative and doesn't exceed maximum.
     */
    public Distance {
        if (kilometers < 0) {
            throw new IllegalArgumentException(
                String.format("Distance cannot be negative: %.2f km", kilometers));
        }
        if (kilometers > MAX_DISTANCE) {
            throw new IllegalArgumentException(
                String.format("Distance %.2f km exceeds maximum %.2f km",
                    kilometers, MAX_DISTANCE));
        }
    }

    /**
     * Calculates the great-circle distance between two geographic points
     * using the Haversine formula. This formula is accurate for distances
     * up to the antipodal point (opposite side of Earth).
     *
     * The Haversine formula:
     * a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
     * c = 2 ⋅ atan2(√a, √(1−a))
     * d = R ⋅ c
     *
     * Where:
     * φ is latitude, λ is longitude, R is earth's radius (6,371 km)
     *
     * @param from the starting GeoPoint
     * @param to the ending GeoPoint
     * @return new Distance object representing the distance
     * @throws NullPointerException if either point is null
     */
    public static Distance calculate(GeoPoint from, GeoPoint to) {
        Objects.requireNonNull(from, "from point must not be null");
        Objects.requireNonNull(to, "to point must not be null");

        // Convert degrees to radians
        double lat1 = Math.toRadians(from.latitude().getValue());
        double lat2 = Math.toRadians(to.latitude().getValue());
        double deltaLat = Math.toRadians(to.latitude().getValue() - from.latitude().getValue());
        double deltaLon = Math.toRadians(to.longitude().getValue() - from.longitude().getValue());

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double distance = EARTH_RADIUS_KM * c;

        // Handle floating-point precision issues (distance should be >= 0)
        if (distance < 0) {
            distance = 0;
        }

        return new Distance(distance);
    }

    /**
     * Gets the distance value in kilometers.
     *
     * @return distance in kilometers
     */
    public double getKilometers() {
        return kilometers;
    }

    /**
     * Gets the distance value in kilometers as BigDecimal for precise calculations.
     *
     * @return distance in kilometers as BigDecimal
     */
    public BigDecimal getKilometersAsBigDecimal() {
        return BigDecimal.valueOf(kilometers);
    }

    /**
     * Gets the distance value in meters.
     *
     * @return distance in meters
     */
    public double getMeters() {
        return kilometers * 1000.0;
    }

    /**
     * Gets the distance value in miles (for reference).
     *
     * @return distance in miles
     */
    public double getMiles() {
        return kilometers * 0.621371;
    }

    /**
     * Compares this distance with another.
     *
     * @param other the other Distance to compare with
     * @return true if this distance is greater than other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isGreaterThan(Distance other) {
        Objects.requireNonNull(other, "other must not be null");
        return Double.compare(this.kilometers, other.kilometers) > 0;
    }

    /**
     * Compares this distance with another.
     *
     * @param other the other Distance to compare with
     * @return true if this distance is less than other, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isLessThan(Distance other) {
        Objects.requireNonNull(other, "other must not be null");
        return Double.compare(this.kilometers, other.kilometers) < 0;
    }

    /**
     * Compares this distance with another with tolerance for floating-point comparison.
     *
     * @param other the other Distance
     * @param tolerance acceptable difference in kilometers
     * @return true if distances are approximately equal within tolerance
     * @throws NullPointerException if other is null
     */
    public boolean isApproximatelyEqualTo(Distance other, double tolerance) {
        Objects.requireNonNull(other, "other must not be null");
        return Math.abs(this.kilometers - other.kilometers) <= tolerance;
    }

    /**
     * Adds another distance to this distance.
     *
     * @param other the distance to add
     * @return new Distance representing the sum
     * @throws NullPointerException if other is null
     * @throws IllegalArgumentException if sum exceeds maximum distance
     */
    public Distance add(Distance other) {
        Objects.requireNonNull(other, "other must not be null");
        return new Distance(this.kilometers + other.kilometers);
    }

    /**
     * Calculates the ratio of this distance to another.
     * Useful for calculating distance-based multipliers.
     *
     * @param other the other distance (divisor)
     * @return the ratio (this distance / other distance)
     * @throws NullPointerException if other is null
     * @throws ArithmeticException if dividing by zero distance
     */
    public double ratioTo(Distance other) {
        Objects.requireNonNull(other, "other must not be null");
        if (other.kilometers == 0) {
            throw new ArithmeticException("Cannot divide by zero distance");
        }
        return this.kilometers / other.kilometers;
    }

    @Override
    public String toString() {
        return String.format("%.2f km", kilometers);
    }
}
