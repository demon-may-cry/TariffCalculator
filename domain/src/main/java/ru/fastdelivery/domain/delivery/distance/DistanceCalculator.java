package ru.fastdelivery.domain.delivery.distance;

import ru.fastdelivery.domain.delivery.coordinates.Coordinates;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 * <p>Расчёт расстояния между географическими координатами
 */
public interface DistanceCalculator {

    BigDecimal calculate(Coordinates from, Coordinates to);
}
