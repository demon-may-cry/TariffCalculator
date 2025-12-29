package ru.fastdelivery.domain.delivery.coordinates;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 * <p>
 * Класс координат
 * @param latitude широта
 * @param longitude долгота
 */
public record Coordinates(
        BigDecimal latitude,
        BigDecimal longitude
) {

    private static final String LATITUDE_AND_LONGITUDE_CANNOT_BE_NULL = "Latitude and longitude cannot be null";

    public Coordinates {
        if (latitude == null || longitude == null)
            throw new IllegalArgumentException(LATITUDE_AND_LONGITUDE_CANNOT_BE_NULL);
    }
}
