package ru.fastdelivery.presentation.api.response;

import ru.fastdelivery.domain.common.price.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CalculatePackagesResponse(
        BigDecimal totalPrice,
        BigDecimal minimalPrice,
        String currencyCode
) {
    public CalculatePackagesResponse(Price totalPrice, Price minimalPrice) {
        this(totalPrice.amount().setScale(2, RoundingMode.CEILING),
                minimalPrice.amount().setScale(2, RoundingMode.CEILING),
                totalPrice.currency().getCode());

        if (currencyIsNotEqual(totalPrice, minimalPrice)) {
            throw new IllegalArgumentException("Currency codes must be the same");
        }
    }

    private static boolean currencyIsNotEqual(Price priceLeft, Price priceRight) {
        return !priceLeft.currency().equals(priceRight.currency());
    }
}
