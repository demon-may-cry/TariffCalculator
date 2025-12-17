package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.common.dimension.Volume;
import ru.fastdelivery.domain.common.coordinate.GeoPoint;
import ru.fastdelivery.domain.common.coordinate.Distance;
import ru.fastdelivery.domain.delivery.pack.Pack;

import java.util.List;
import java.util.Objects;

/**
 * Represents a shipment with packages, currency, and geographic information.
 * This value object combines package information with geographic origin and destination.
 *
 * @param packages list of packages in this shipment
 * @param currency currency for this shipment
 * @param departure geographic point of shipment origin
 * @param destination geographic point of shipment destination
 */
public record Shipment(
        List<Pack> packages,
        Currency currency,
        GeoPoint departure,
        GeoPoint destination
) {

    /**
     * Compact constructor for validation.
     * Ensures that packages list is not empty and all required fields are present.
     */
    public Shipment {
        Objects.requireNonNull(packages, "packages must not be null");
        if (packages.isEmpty()) {
            throw new IllegalArgumentException("shipment must contain at least one package");
        }
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(departure, "departure point must not be null");
        Objects.requireNonNull(destination, "destination point must not be null");
    }

    /**
     * Calculates the total weight of all packages in this shipment.
     * Sums up the weights of all individual packages.
     *
     * @return total weight of all packages
     */
    public Weight weightAllPackages() {
        return packages.stream()
                .map(Pack::getWeight)
                .reduce(Weight.zero(), Weight::add);
    }

    /**
     * Calculates the total volume of all packages in this shipment.
     * Sums up the volumes of all individual packages.
     *
     * @return total volume of all packages in cubic centimeters
     */
    public Volume volumeAllPackages() {
        return packages.stream()
                .map(Pack::getVolume)
                .reduce(Volume.fromCubicCentimeters(0L), Volume::add);
    }

    /**
     * Gets the number of packages in this shipment.
     *
     * @return count of packages
     */
    public int getPackageCount() {
        return packages.size();
    }

    /**
     * Calculates the distance between departure and destination points.
     * Uses the Haversine formula for accurate great-circle distance calculation.
     *
     * @return distance between departure and destination
     */
    public Distance calculateDistance() {
        return Distance.calculate(departure, destination);
    }

    /**
     * Gets the departure geographic point.
     *
     * @return the departure GeoPoint
     */
    public GeoPoint getDeparture() {
        return departure;
    }

    /**
     * Gets the destination geographic point.
     *
     * @return the destination GeoPoint
     */
    public GeoPoint getDestination() {
        return destination;
    }

    /**
     * Gets the packages in this shipment.
     *
     * @return list of packages
     */
    public List<Pack> getPackages() {
        return packages;
    }

    /**
     * Gets the currency for this shipment.
     *
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }
}
