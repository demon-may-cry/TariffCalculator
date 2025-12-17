package ru.fastdelivery.domain.delivery.pack;

import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.common.dimension.OuterDimensions;
import ru.fastdelivery.domain.common.dimension.Volume;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a physical package with weight and dimensions.
 * This value object combines weight and outer dimensions information
 * for complete package characterization.
 *
 * @param weight the weight of the package and its contents
 * @param dimensions the outer dimensions (length, width, height) of the package
 */
public record Pack(Weight weight, OuterDimensions dimensions) {

    private static final Weight MAX_WEIGHT = new Weight(BigInteger.valueOf(150_000));

    /**
     * Compact constructor for validation.
     * Ensures that weight does not exceed maximum and dimensions are valid.
     */
    public Pack {
        Objects.requireNonNull(weight, "weight must not be null");
        Objects.requireNonNull(dimensions, "dimensions must not be null");

        if (weight.greaterThan(MAX_WEIGHT)) {
            throw new IllegalArgumentException(
                String.format("Package weight cannot exceed %s, got %s",
                    MAX_WEIGHT, weight));
        }
    }

    /**
     * Gets the weight of this package.
     *
     * @return the package weight
     */
    public Weight getWeight() {
        return weight;
    }

    /**
     * Gets the dimensions of this package.
     *
     * @return the outer dimensions
     */
    public OuterDimensions getDimensions() {
        return dimensions;
    }

    /**
     * Calculates the volume of this package.
     * The volume is derived from the outer dimensions.
     *
     * @return the volume in cubic centimeters
     */
    public Volume getVolume() {
        return dimensions.calculateVolume();
    }

    /**
     * Gets the maximum allowed package weight.
     *
     * @return the maximum weight constant
     */
    public static Weight getMaxWeight() {
        return MAX_WEIGHT;
    }
}
