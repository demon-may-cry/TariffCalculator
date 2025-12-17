package ru.fastdelivery.domain.common.coordinate;

import java.util.Objects;

/**
 * Represents a geographic point with latitude and longitude coordinates.
 * This is a value object that encapsulates a location on Earth's surface.
 *
 * @param latitude the latitude coordinate
 * @param longitude the longitude coordinate
 */
public record GeoPoint(Coordinate latitude, Coordinate longitude) {

    /**
     * Compact constructor for validation.
     * Ensures that both latitude and longitude are not null.
     */
    public GeoPoint {
        Objects.requireNonNull(latitude, "latitude must not be null");
        Objects.requireNonNull(longitude, "longitude must not be null");
    }

    /**
     * Creates a GeoPoint with specified coordinates and validates them against ranges.
     *
     * @param lat latitude value
     * @param lon longitude value
     * @param minLat minimum allowed latitude
     * @param maxLat maximum allowed latitude
     * @param minLon minimum allowed longitude
     * @param maxLon maximum allowed longitude
     * @return new GeoPoint with validated coordinates
     * @throws IllegalArgumentException if any coordinate is outside valid range
     */
    public static GeoPoint of(double lat, double lon,
                              double minLat, double maxLat,
                              double minLon, double maxLon) {
        return new GeoPoint(
            Coordinate.latitude(lat, minLat, maxLat),
            Coordinate.longitude(lon, minLon, maxLon)
        );
    }

    /**
     * Gets the latitude coordinate.
     *
     * @return the latitude
     */
    public Coordinate getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude coordinate.
     *
     * @return the longitude
     */
    public Coordinate getLongitude() {
        return longitude;
    }

    /**
     * Compares this GeoPoint with another.
     *
     * @param other the other GeoPoint to compare with
     * @return true if both latitude and longitude are equal, false otherwise
     * @throws NullPointerException if other is null
     */
    public boolean isAtSameLocation(GeoPoint other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.latitude.isEqualTo(other.latitude) &&
               this.longitude.isEqualTo(other.longitude);
    }

    /**
     * Calculates the difference in latitude from another point.
     * Useful for distance calculations.
     *
     * @param other the other GeoPoint
     * @return absolute difference in latitude degrees
     * @throws NullPointerException if other is null
     */
    public double latitudeDifferenceFrom(GeoPoint other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.latitude.differenceFrom(other.latitude);
    }

    /**
     * Calculates the difference in longitude from another point.
     * Useful for distance calculations.
     *
     * @param other the other GeoPoint
     * @return absolute difference in longitude degrees
     * @throws NullPointerException if other is null
     */
    public double longitudeDifferenceFrom(GeoPoint other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.longitude.differenceFrom(other.longitude);
    }

    @Override
    public String toString() {
        return String.format("[%.6f°, %.6f°]", latitude.getValue(), longitude.getValue());
    }
}
