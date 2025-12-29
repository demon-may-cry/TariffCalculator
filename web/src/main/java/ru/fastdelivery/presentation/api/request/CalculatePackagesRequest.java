package ru.fastdelivery.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Данные для расчета стоимости доставки")
public record CalculatePackagesRequest(
        @Schema(description = "Список упаковок отправления",
                example = "[{\"weight\": 4056.45," +
                        "\"length\": 789," +
                        "\"width\": 412," +
                        "\"height\": 905}]")
        @NotNull
        @NotEmpty
        List<CargoPackage> packages,

        @Schema(description = "Трехбуквенный код валюты", example = "RUB")
        @NotNull
        String currencyCode,

        @Schema(description = "Координаты точки назначения", example = "{\"latitude\" : 55.755864,\n" +
                "\"longitude\" : 37.617698}")
        @NotNull
        DestinationRequest destination,

        @Schema(description = "Координаты точки отправления", example = "{\"latitude\" : 47.222109,\n" +
                "\"longitude\" : 39.718813}")
        @NotNull
        DepartureRequest departure
) {
}
