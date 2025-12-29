package ru.fastdelivery.config;

import lombok.RequiredArgsConstructor;
import ru.fastdelivery.usecase.DistancePolicy;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 * <p>
 * Реализация политики расчёта расстояния
 * <p>
 * Предоставляет минимальное расстояние (в километрах),
 * за которое взимается базовая стоимость доставки
 * Значение берётся из конфигурации приложения
 */
@RequiredArgsConstructor
public class DistancePolicyImpl implements DistancePolicy {

    private final TariffDistanceProperties properties;

    /**
     * Минимальное расстояние (км), за которое взимается базовая стоимость
     * @return {@link BigDecimal} расстояние в километрах
     */
    @Override
    public BigDecimal minimalDistanceKm() {
        return properties.getMinimalKm();
    }
}
