package ru.fastdelivery.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.dimensions.Length;
import ru.fastdelivery.domain.common.dimensions.OuterDimensions;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.coordinates.Coordinates;
import ru.fastdelivery.domain.delivery.distance.DistanceCalculator;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffCalculateUseCaseTest {

    @Mock
    WeightPriceProvider weightPriceProvider;

    @Mock
    VolumePriceProvider volumePriceProvider;

    @Mock
    DistanceCalculator distanceCalculator;

    @Mock
    CoordinatesValidator coordinatesValidator;

    @Mock
    DistancePolicy distancePolicy;

    @InjectMocks
    TariffCalculateUseCase tariffCalculateUseCase;

    private final Currency currency = new CurrencyFactory(code -> true).create("RUB");

    @ParameterizedTest(name = "{index} → distance={0}, expected={1}")
    @MethodSource("distanceScenarios")
    @DisplayName("Расчет стоимости доставки — проверка влияния расстояния")
    void shouldCalculatePriceDependingOnDistance(
            BigDecimal distanceKm,
            BigDecimal expectedPrice
    ) {

        // given
        when(weightPriceProvider.costPerKg())
                .thenReturn(new Price(BigDecimal.valueOf(100), currency));

        when(weightPriceProvider.minimalPrice())
                .thenReturn(new Price(BigDecimal.valueOf(300), currency));

        when(volumePriceProvider.costPerCubicMeter())
                .thenReturn(new Price(BigDecimal.valueOf(1), currency));

        when(volumePriceProvider.minimalPrice())
                .thenReturn(new Price(BigDecimal.ZERO, currency));

        when(distanceCalculator.calculate(any(), any()))
                .thenReturn(distanceKm);

        when(distancePolicy.minimalDistanceKm())
                .thenReturn(BigDecimal.valueOf(450));

        Shipment shipment = shipmentWithMediumCargo();

        // when
        Price actualPrice = tariffCalculateUseCase.calc(shipment);

        // then
        assertThat(actualPrice.amount().setScale(2, RoundingMode.CEILING))
                .isEqualByComparingTo(expectedPrice);
    }

    @Test
    @DisplayName("Получение минимальной стоимости по весу — успешно")
    void whenMinimalPrice_thenSuccess() {

        // given
        Price minimalPrice = new Price(BigDecimal.valueOf(350), currency);
        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);

        // when
        Price actual = tariffCalculateUseCase.minimalPrice();

        // then
        assertThat(actual).isEqualTo(minimalPrice);
    }

    static Stream<Arguments> distanceScenarios() {
        return Stream.of(
                Arguments.of(
                        BigDecimal.valueOf(200),
                        BigDecimal.valueOf(345)     // < minimal → базовая цена
                ),
                Arguments.of(
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(383.34)  // > minimal → коэффициент
                )
        );
    }

    private Shipment shipmentWithMediumCargo() {
        return new Shipment(
                List.of(
                        new Pack(
                                new Weight(BigInteger.valueOf(3450)), // 3.45 кг
                                new OuterDimensions(
                                        Length.fromMillimeter(100),
                                        Length.fromMillimeter(100),
                                        Length.fromMillimeter(100)
                                )
                        )
                ),
                currency,
                new Coordinates(BigDecimal.valueOf(55.030204), BigDecimal.valueOf(82.920430)),
                new Coordinates(BigDecimal.valueOf(47.222109), BigDecimal.valueOf(39.718813))
        );
    }
}