package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.delivery.coordinates.Coordinates;

/**
 * @author Дмитрий Ельцов
 */
public interface CoordinatesValidator {

    void validate(Coordinates coordinates);
}
