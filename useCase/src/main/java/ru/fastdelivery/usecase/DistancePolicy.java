package ru.fastdelivery.usecase;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 */
public interface DistancePolicy {

    BigDecimal minimalDistanceKm();
}
