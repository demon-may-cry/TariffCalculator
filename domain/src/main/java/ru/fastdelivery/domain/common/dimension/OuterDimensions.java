package ru.fastdelivery.domain.common.dimension;

import java.util.Objects;

/**
 * Represents outer dimensions of a package (length, width, height).
 * This is a value object that encapsulates all three dimensions.
 *
 * @param length the length of the package
 * @param width the width of the package
 * @param height the height of the package
 */
public record OuterDimensions(Length length, Length width, Length height) {
    private static final long MILLIMETERS_PER_CENTIMETER = 10L;

    /**
     * Compact constructor for validation.
     * Ensures that:
     * - All dimensions are not null
     */
    public OuterDimensions {
        Objects.requireNonNull(length, "length must not be null");
        Objects.requireNonNull(width, "width must not be null");
        Objects.requireNonNull(height, "height must not be null");
    }

    /**
     * Calculates the volume of the package from its dimensions.
     * Converts millimeters to centimeters and multiplies.
     *
     * @return Volume object representing the calculated volume in cubic centimeters
     */
    public Volume calculateVolume() {
        long lengthCm = length.inMillimetersAsLong() / MILLIMETERS_PER_CENTIMETER;
        long widthCm = width.inMillimetersAsLong() / MILLIMETERS_PER_CENTIMETER;
        long heightCm = height.inMillimetersAsLong() / MILLIMETERS_PER_CENTIMETER;
        
        long volumeCm3 = lengthCm * widthCm * heightCm;
        
        return Volume.fromCubicCentimeters(volumeCm3);
    }

    /**
     * Finds the longest side among the three dimensions.
     *
     * @return the Length representing the longest side
     */
    public Length getLongestSide() {
        Length longest = length;
        
        if (width.isLongerThan(longest)) {
            longest = width;
        }
        
        if (height.isLongerThan(longest)) {
            longest = height;
        }
        
        return longest;
    }

    /**
     * Finds the shortest side among the three dimensions.
     *
     * @return the Length representing the shortest side
     */
    public Length getShortestSide() {
        Length shortest = length;
        
        if (width.isShorterThan(shortest)) {
            shortest = width;
        }
        
        if (height.isShorterThan(shortest)) {
            shortest = height;
        }
        
        return shortest;
    }

    /**
     * Calculates the sum of all three dimensions.
     * This can be used for validation or tariff calculations.
     *
     * @return sum of all three dimensions in millimeters
     */
    public long sumOfDimensions() {
        return length.inMillimetersAsLong() + 
               width.inMillimetersAsLong() + 
               height.inMillimetersAsLong();
    }

    /**
     * Gets the length dimension.
     *
     * @return the length
     */
    public Length getLength() {
        return length;
    }

    /**
     * Gets the width dimension.
     *
     * @return the width
     */
    public Length getWidth() {
        return width;
    }

    /**
     * Gets the height dimension.
     *
     * @return the height
     */
    public Length getHeight() {
        return height;
    }
}
