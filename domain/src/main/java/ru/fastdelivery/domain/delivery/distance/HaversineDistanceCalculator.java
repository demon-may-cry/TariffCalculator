package ru.fastdelivery.domain.delivery.distance;

import ru.fastdelivery.domain.delivery.coordinates.Coordinates;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

/**
 * @author Дмитрий Ельцов
 * <p>Реализация расчёта расстояния по формуле гаверсинусов
 */
public class HaversineDistanceCalculator implements DistanceCalculator{

    private static final Logger LOG = Logger.getLogger(HaversineDistanceCalculator.class.getName());
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Вычисляет расстояние между двумя точками в километрах.
     * @param from точка отправления
     * @param to точка назначения
     * @return расстояние в километрах
     */
    @Override
    public BigDecimal calculate(Coordinates from, Coordinates to) {
        double lat1 = Math.toRadians(from.latitude().doubleValue());
        double lat2 = Math.toRadians(to.latitude().doubleValue());
        double deltaLat = Math.toRadians(to.latitude().subtract(from.latitude()).doubleValue());
        double deltaLon = Math.toRadians(to.longitude().subtract(from.longitude()).doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = EARTH_RADIUS_KM * c;

        LOG.info("distanceKm: " + BigDecimal.valueOf(distanceKm).setScale(0, RoundingMode.HALF_UP));
        return BigDecimal.valueOf(distanceKm);
    }
}
