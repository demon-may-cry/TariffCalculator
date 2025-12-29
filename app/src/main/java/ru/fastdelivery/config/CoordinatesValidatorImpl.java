package ru.fastdelivery.config;

import lombok.RequiredArgsConstructor;
import ru.fastdelivery.domain.delivery.coordinates.Coordinates;
import ru.fastdelivery.usecase.CoordinatesValidator;

import static java.text.MessageFormat.*;

/**
 * @author Дмитрий Ельцов
 * <p>
 * Класс валидации координат
 */
@RequiredArgsConstructor
public class CoordinatesValidatorImpl implements CoordinatesValidator {

    private static final String LATITUDE_OUT_OF_ALLOWED_RANGE = "Latitude out of allowed range [{0}, {1}]";
    private static final String LONGITUDE_OUT_OF_ALLOWED_RANGE = "Longitude out of allowed range [{0}, {1}]";

    private final TariffCoordinatesProperties properties;

    /**
     * Проверяет, что координаты находятся в допустимых диапазонах,
     * заданных в конфигурации приложения
     * @param coordinates координаты для проверки
     * @throws IllegalArgumentException если широта или долгота выходят
     *         за допустимые границы
     */
    @Override
    public void validate(Coordinates coordinates) {
        if (coordinates.latitude().compareTo(properties.getMinLatitude()) < 0 ||
                coordinates.latitude().compareTo(properties.getMaxLatitude()) > 0)
            throw new IllegalArgumentException(format(LATITUDE_OUT_OF_ALLOWED_RANGE,
                    properties.getMinLatitude(),
                    properties.getMaxLatitude())
            );

        if (coordinates.longitude().compareTo(properties.getMinLongitude()) < 0 ||
                coordinates.longitude().compareTo(properties.getMaxLongitude()) > 0)
            throw new IllegalArgumentException(format(LONGITUDE_OUT_OF_ALLOWED_RANGE,
                    properties.getMinLongitude(),
                    properties.getMaxLongitude())
            );
    }
}
