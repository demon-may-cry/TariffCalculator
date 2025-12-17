package ru.fastdelivery.useCase.tariff;

import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.coordinate.Distance;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Calculates shipping tariff (price) based on shipment characteristics.
 * Supports pricing by weight, volume, and distance factors.
 *
 * This calculator implements the following pricing strategy:
 * 1. Price by weight: weight(kg) × cost_per_kg
 * 2. Price by volume: volume(cm³) × cost_per_cm³
 * 3. Final price: max(weight_price, volume_price)
 * 4. Distance multiplier: applies distance coefficient if distance > min_distance
 *
 * Example:
 * - Weight: 4.564 kg → 4.564 × 400 = 1825.6 RUB
 * - Volume: 8000 cm³ → 8000 × 0.1 = 800 RUB
 * - Base price: max(1825.6, 800) = 1825.6 RUB
 * - Distance: 500 km, min_distance: 450 km
 * - Coefficient: 500 / 450 = 1.111
 * - Final price: 1825.6 × 1.111 = 2027.08 RUB
 */
public class TariffCalculator {

    private final BigDecimal costPerKilogram;
    private final BigDecimal costPerCubicCentimeter;
    private final Price minimumPrice;
    private final Distance minimumDistance;

    /**
     * Creates a TariffCalculator with specified pricing parameters.
     *
     * @param costPerKilogram cost for one kilogram of weight
     * @param costPerCubicCentimeter cost for one cubic centimeter of volume
     * @param minimumPrice minimum price for any shipment
     * @param minimumDistance minimum distance for coefficient calculation
     */
    public TariffCalculator(
            BigDecimal costPerKilogram,
            BigDecimal costPerCubicCentimeter,
            Price minimumPrice,
            Distance minimumDistance) {
        this.costPerKilogram = Objects.requireNonNull(costPerKilogram,
            "costPerKilogram must not be null");
        this.costPerCubicCentimeter = Objects.requireNonNull(costPerCubicCentimeter,
            "costPerCubicCentimeter must not be null");
        this.minimumPrice = Objects.requireNonNull(minimumPrice,
            "minimumPrice must not be null");
        this.minimumDistance = Objects.requireNonNull(minimumDistance,
            "minimumDistance must not be null");
    }

    /**
     * Calculates price based on weight of all packages in shipment.
     * Formula: weight(kg) × cost_per_kg
     *
     * @param shipment the shipment to calculate price for
     * @return price based on weight
     */
    public Price calcByWeight(Shipment shipment) {
        Objects.requireNonNull(shipment, "shipment must not be null");

        // Get total weight in kilograms
        double weightInGrams = shipment.weightAllPackages().inGrams();
        double weightInKilograms = weightInGrams / 1000.0;

        // Calculate price: kg × cost_per_kg
        BigDecimal price = BigDecimal.valueOf(weightInKilograms)
                .multiply(costPerKilogram)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        return new Price(price, shipment.getCurrency());
    }

    /**
     * Calculates price based on volume of all packages in shipment.
     * Formula: volume(cm³) × cost_per_cm³
     *
     * @param shipment the shipment to calculate price for
     * @return price based on volume
     */
    public Price calcByVolume(Shipment shipment) {
        Objects.requireNonNull(shipment, "shipment must not be null");

        // Get total volume in cubic centimeters
        long volumeCm3 = shipment.volumeAllPackages().getValue();

        // Calculate price: cm³ × cost_per_cm³
        BigDecimal price = BigDecimal.valueOf(volumeCm3)
                .multiply(costPerCubicCentimeter)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        return new Price(price, shipment.getCurrency());
    }

    /**
     * Calculates price by selecting maximum between weight-based and volume-based pricing.
     * Formula: max(weight_price, volume_price), but at least minimum_price
     *
     * This ensures that both weight and volume are considered, and the higher price is used.
     * Also ensures the price doesn't fall below the minimum threshold.
     *
     * @param shipment the shipment to calculate price for
     * @return price based on maximum of weight and volume, with minimum threshold
     */
    public Price calc(Shipment shipment) {
        Objects.requireNonNull(shipment, "shipment must not be null");

        Price weightPrice = calcByWeight(shipment);
        Price volumePrice = calcByVolume(shipment);

        // Select the maximum price
        Price basePrice = weightPrice.isGreaterThan(volumePrice) ? weightPrice : volumePrice;

        // Apply minimum price threshold
        if (basePrice.isLessThan(minimumPrice)) {
            return minimumPrice;
        }

        return basePrice;
    }

    /**
     * Calculates price with distance-based coefficient.
     * Formula: base_price × max(1.0, distance / min_distance)
     *
     * The distance coefficient is calculated as:
     * - If distance < min_distance: use min_distance (coefficient = 1.0)
     * - If distance >= min_distance: use actual distance
     * - Coefficient = distance / min_distance
     *
     * Example:
     * - min_distance = 450 km
     * - actual_distance = 500 km
     * - coefficient = 500 / 450 = 1.111
     * - final_price = base_price × 1.111
     *
     * @param shipment the shipment to calculate price for
     * @return price with distance coefficient applied
     */
    public Price calcWithDistance(Shipment shipment) {
        Objects.requireNonNull(shipment, "shipment must not be null");

        // Get base price (weight and volume combined)
        Price basePrice = calc(shipment);

        // Calculate distance between departure and destination
        Distance distance = shipment.calculateDistance();

        // Determine effective distance (at least the minimum)
        double effectiveDistance = Math.max(
                distance.getKilometers(),
                minimumDistance.getKilometers()
        );

        // Calculate coefficient
        double coefficient = effectiveDistance / minimumDistance.getKilometers();

        // Apply coefficient to base price
        BigDecimal priceWithDistance = basePrice.getValue()
                .multiply(BigDecimal.valueOf(coefficient))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        return new Price(priceWithDistance, shipment.getCurrency());
    }

    /**
     * Gets the cost per kilogram.
     *
     * @return cost per kilogram
     */
    public BigDecimal getCostPerKilogram() {
        return costPerKilogram;
    }

    /**
     * Gets the cost per cubic centimeter.
     *
     * @return cost per cubic centimeter
     */
    public BigDecimal getCostPerCubicCentimeter() {
        return costPerCubicCentimeter;
    }

    /**
     * Gets the minimum price threshold.
     *
     * @return minimum price
     */
    public Price getMinimumPrice() {
        return minimumPrice;
    }

    /**
     * Gets the minimum distance for coefficient calculation.
     *
     * @return minimum distance
     */
    public Distance getMinimumDistance() {
        return minimumDistance;
    }
}
