package ru.fastdelivery.usecase;

import lombok.RequiredArgsConstructor;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.delivery.distance.DistanceCalculator;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import javax.inject.Named;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Класс расчёта стоимости доставки груза
 */
@Named
@RequiredArgsConstructor
public class TariffCalculateUseCase {
    private final WeightPriceProvider weightPriceProvider;
    private final VolumePriceProvider volumePriceProvider;
    private final CoordinatesValidator coordinatesValidator;
    private final DistanceCalculator distanceCalculator;
    private final DistancePolicy distancePolicy;

    /**
     * Выполняет расчёт итоговой стоимости перевозки груза
     * <p>
     * Алгоритм расчёта:
     * <ol>
     *   <li> Валидирует координаты пунктов отправления и назначения;</li>
     *   <li> Рассчитывает стоимость доставки по весу груза;</li>
     *   <li> Рассчитывает стоимость доставки по объёму груза;</li>
     *   <li> Определяет базовую стоимость как максимум из рассчитанных значений
     *       и минимальных тарифов;</li>
     *   <li> Рассчитывает расстояние между пунктами;</li>
     *   <li> Применяет коэффициент расстояния с учётом минимального порога;</li>
     * </ol>
     *
     * @param shipment данные об отгрузке груза
     * @return {@link Price} итоговая стоимость доставки
     * @throws IllegalArgumentException если координаты не проходят валидацию
     */
    public Price calc(Shipment shipment) {

        //№1
        coordinatesValidator.validate(shipment.departure());
        coordinatesValidator.validate(shipment.destination());

        //№2
        Price byWeight = weightPriceProvider
                .costPerKg()
                .multiply(shipment.weightAllPackages().kilograms());

        //№3
        Price byVolume = volumePriceProvider
                .costPerCubicMeter()
                .multiply(shipment.volumeAllPackages().cubicMeters());

        //№4
        Price basePrice = byWeight
                .max(byVolume)
                .max(weightPriceProvider.minimalPrice())
                .max(volumePriceProvider.minimalPrice());

        //№5
        BigDecimal distanceKm = distanceCalculator.calculate(
                shipment.departure(),
                shipment.destination()
        );

        BigDecimal minimalDistance = distancePolicy.minimalDistanceKm();

        //№6
        if (distanceKm.compareTo(minimalDistance) <= 0) {
            return basePrice;
        }

        BigDecimal multiplier = distanceKm.divide(
                minimalDistance,
                10,
                RoundingMode.CEILING
        );

        return basePrice
                .multiply(multiplier);
    }

    /**
     * Возвращает минимальную стоимость доставки по весу
     * @return {@link Price} минимальная цена доставки
     */
    public Price minimalPrice() {
        return weightPriceProvider.minimalPrice();
    }
}
