package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.common.price.Price;

/**
 * @author Дмитрий Ельцов
 */
public interface VolumePriceProvider {

    Price costPerCubicMeter();

    Price minimalPrice();
}
