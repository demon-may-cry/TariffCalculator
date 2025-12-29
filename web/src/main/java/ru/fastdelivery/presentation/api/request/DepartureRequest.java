package ru.fastdelivery.presentation.api.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 */
public record DepartureRequest(
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude
) {
}
