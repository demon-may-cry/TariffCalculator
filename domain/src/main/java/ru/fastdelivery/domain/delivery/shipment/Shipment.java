package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.dimensions.Volume;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.coordinates.Coordinates;
import ru.fastdelivery.domain.delivery.pack.Pack;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Класс отгрузки груза
 * @param packages упаковки в грузе
 * @param currency валюта объявленная для груза
 * @param departure  пункт отправления
 * @param destination пункт назначения
 */
public record Shipment(
        List<Pack> packages,
        Currency currency,
        Coordinates departure,
        Coordinates destination
) {
    public Weight weightAllPackages() {
        return packages.stream()
                .map(Pack::weight)
                .reduce(Weight.zero(), Weight::add);
    }

    /**
     * Рассчитывает суммарный объём всех упаковок груза
     * @return {@link Volume} общий объём груза
     */
    public Volume volumeAllPackages() {
        return packages.stream()
                .map(Pack::outerDimensions)
                .filter(Objects::nonNull)
                .map(Volume::from)
                .reduce(
                        new Volume(BigDecimal.ZERO),
                        (v1, v2) -> new Volume(v1.cubicMeters().add(v2.cubicMeters()))
                );
    }
}
