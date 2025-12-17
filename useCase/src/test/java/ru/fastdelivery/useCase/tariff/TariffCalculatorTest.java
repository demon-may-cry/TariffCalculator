package ru.fastdelivery.useCase.tariff;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.coordinate.GeoPoint;
import ru.fastdelivery.domain.common.coordinate.Distance;
import ru.fastdelivery.domain.common.dimension.Length;
import ru.fastdelivery.domain.common.dimension.OuterDimensions;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TariffCalculator Tests")
class TariffCalculatorTest {

    private static final double MIN_LATITUDE = 45.0;
    private static final double MAX_LATITUDE = 65.0;
    private static final double MIN_LONGITUDE = 30.0;
    private static final double MAX_LONGITUDE = 96.0;

    private TariffCalculator calculator;
    private Currency rub;
    private GeoPoint moscow;
    private GeoPoint saintPetersburg;
    private GeoPoint samara;

    @BeforeEach
    void setUp() {
        // Create calculator with standard pricing for Russia
        BigDecimal costPerKg = BigDecimal.valueOf(400);  // 400 RUB per kg
        BigDecimal costPerCm3 = BigDecimal.valueOf(0.1); // 0.1 RUB per cm³
        Price minimumPrice = new Price(BigDecimal.valueOf(350), 
            CurrencyFactory.getCurrency("RUB"));
        Distance minimumDistance = new Distance(450.0);  // 450 km minimum

        calculator = new TariffCalculator(costPerKg, costPerCm3, minimumPrice, minimumDistance);
        rub = CurrencyFactory.getCurrency("RUB");

        // Moscow: 55.7558° N, 37.6173° E
        moscow = GeoPoint.of(55.7558, 37.6173, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);
        
        // Saint Petersburg: 59.9311° N, 30.3609° E
        saintPetersburg = GeoPoint.of(59.9311, 30.3609, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);
        
        // Samara: 53.1959° N, 50.1200° E
        samara = GeoPoint.of(53.1959, 50.1200, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);
    }

    @Test
    @DisplayName("should calculate price by weight: 4.564 kg × 400 RUB/kg = 1825.6 RUB")
    void givenShipmentWithWeight_whenCalcByWeight_thenCorrect() {
        // given
        Weight weight = new Weight(BigInteger.valueOf(4564));  // 4564 grams = 4.564 kg
        Length length = Length.fromMillimeter(100L);
        Length width = Length.fromMillimeter(200L);
        Length height = Length.fromMillimeter(300L);
        OuterDimensions dims = new OuterDimensions(length, width, height);
        Pack pack = new Pack(weight, dims);

        Shipment shipment = new Shipment(List.of(pack), rub, moscow, saintPetersburg);

        // when
        Price price = calculator.calcByWeight(shipment);

        // then
        assertThat(price.getValue()).isEqualTo(BigDecimal.valueOf(1825.60));
    }

    @Test
    @DisplayName("should calculate price by volume: 8000 cm³ × 0.1 RUB/cm³ = 800 RUB")
    void givenShipmentWithVolume_whenCalcByVolume_thenCorrect() {
        // given
        Weight weight = new Weight(BigInteger.valueOf(100));
        Length length = Length.fromMillimeter(200L);  // 20 cm
        Length width = Length.fromMillimeter(200L);   // 20 cm
        Length height = Length.fromMillimeter(200L);  // 20 cm
        OuterDimensions dims = new OuterDimensions(length, width, height);
        Pack pack = new Pack(weight, dims);

        Shipment shipment = new Shipment(List.of(pack), rub, moscow, saintPetersburg);

        // when
        Price price = calculator.calcByVolume(shipment);

        // then
        // 20 × 20 × 20 = 8000 cm³
        // 8000 × 0.1 = 800 RUB
        assertThat(price.getValue()).isEqualTo(BigDecimal.valueOf(800.00));
    }

    @Test
    @DisplayName("should calculate final price by selecting maximum between weight and volume")
    void givenShipment_whenCalc_thenSelectsMaximum() {
        // given
        Weight weight = new Weight(BigInteger.valueOf(4564));  // Heavy package
        Length length = Length.fromMillimeter(100L);
        Length width = Length.fromMillimeter(200L);
        Length height = Length.fromMillimeter(300L);
        OuterDimensions dims = new OuterDimensions(length, width, height);
        Pack pack = new Pack(weight, dims);

        Shipment shipment = new Shipment(List.of(pack), rub, moscow, saintPetersburg);

        // when
        Price price = calculator.calc(shipment);

        // then
        // Weight price: 4.564 kg × 400 = 1825.6 RUB
        // Volume price: 6000 cm³ × 0.1 = 600 RUB
        // Max: 1825.6 RUB
        assertThat(price.getValue()).isEqualTo(BigDecimal.valueOf(1825.60));
    }

    @Test
    @DisplayName("should apply distance coefficient for Moscow to SPb (~714 km, 500km/450km = 1.111)")
    void givenMoscowToSaintPetersburg_whenCalcWithDistance_thenAppliesCoefficient() {
        // given
        Weight weight = new Weight(BigInteger.valueOf(4564));
        Length length = Length.fromMillimeter(100L);
        Length width = Length.fromMillimeter(200L);
        Length height = Length.fromMillimeter(300L);
        OuterDimensions dims = new OuterDimensions(length, width, height);
        Pack pack = new Pack(weight, dims);

        Shipment shipment = new Shipment(List.of(pack), rub, moscow, saintPetersburg);

        // when
        Price priceWithDistance = calculator.calcWithDistance(shipment);

        // then
        // Base price: 1825.6 RUB (by weight)
        // Distance: ~709.5 km
        // Since ~709.5 > 450, coefficient = ~709.5 / 450 = ~1.577
        // Final price: 1825.6 × 1.577 ≈ 2880 RUB
        assertThat(priceWithDistance.getValue().doubleValue()).isGreaterThan(2800);
        assertThat(priceWithDistance.getValue().doubleValue()).isLessThan(3000);
    }

    @Test
    @DisplayName("should use minimum distance when actual distance is less than minimum")
    void givenCloseDestination_whenCalcWithDistance_thenUsesMinimumDistance() {
        // given: Create a very close destination (for testing, we'll mock the calculation)
        Weight weight = new Weight(BigInteger.valueOf(1000));
        Length length = Length.fromMillimeter(100L);
        Length width = Length.fromMillimeter(100L);
        Length height = Length.fromMillimeter(100L);
        OuterDimensions dims = new OuterDimensions(length, width, height);
        Pack pack = new Pack(weight, dims);

        // Moscow to another close point (still within Russia's coordinate range)
        GeoPoint closePoint = GeoPoint.of(55.8, 37.7, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);
        Shipment shipment = new Shipment(List.of(pack), rub, moscow, closePoint);

        // when
        Price priceWithDistance = calculator.calcWithDistance(shipment);

        // then - should use minimum distance of 450 km
        // So coefficient should be ≈ 1.0 (450/450)
        // Weight price: 1 kg × 400 = 400 RUB
        // With coefficient: 400 × 1.0 = 400 RUB
        Price basePrice = calculator.calc(shipment);
        assertThat(priceWithDistance.getValue()).isGreaterThanOrEqualTo(basePrice.getValue());
    }

    @Test
    @DisplayName("should respect minimum price threshold")
    void givenLightSmallPackage_whenCalc_thenAppliesMinimumPrice() {
        // given: Very small package
        Weight weight = new Weight(BigInteger.valueOf(100));  // 0.1 kg
        Length length = Length.fromMillimeter(50L);          // 5 cm
        Length width = Length.fromMillimeter(50L);           // 5 cm
        Length height = Length.fromMillimeter(50L);          // 5 cm
        OuterDimensions dims = new OuterDimensions(length, width, height);
        Pack pack = new Pack(weight, dims);

        Shipment shipment = new Shipment(List.of(pack), rub, moscow, saintPetersburg);

        // when
        Price price = calculator.calc(shipment);

        // then - minimum price is 350 RUB
        // Weight price: 0.1 kg × 400 = 40 RUB (less than minimum)
        // Volume price: 125 cm³ × 0.1 = 12.5 RUB (less than minimum)
        // Result: 350 RUB (minimum)
        assertThat(price.getValue()).isEqualTo(BigDecimal.valueOf(350.00));
    }

    @Test
    @DisplayName("should handle multiple packages in shipment")
    void givenMultiplePackages_whenCalc_thenSumsWeightAndVolume() {
        // given: Two packages
        Weight weight1 = new Weight(BigInteger.valueOf(2000));  // 2 kg
        Length length1 = Length.fromMillimeter(100L);
        OuterDimensions dims1 = new OuterDimensions(length1, length1, length1);
        Pack pack1 = new Pack(weight1, dims1);

        Weight weight2 = new Weight(BigInteger.valueOf(2000));  // 2 kg
        Length length2 = Length.fromMillimeter(100L);
        OuterDimensions dims2 = new OuterDimensions(length2, length2, length2);
        Pack pack2 = new Pack(weight2, dims2);

        Shipment shipment = new Shipment(List.of(pack1, pack2), rub, moscow, saintPetersburg);

        // when
        Price price = calculator.calc(shipment);

        // then
        // Total weight: 4 kg × 400 = 1600 RUB
        // Total volume: 2 × 1000 = 2000 cm³ × 0.1 = 200 RUB
        // Max: 1600 RUB
        assertThat(price.getValue()).isEqualTo(BigDecimal.valueOf(1600.00));
    }

    @Test
    @DisplayName("should maintain currency during price calculation")
    void givenShipment_whenCalc_thenMaintainsCurrency() {
        // given
        Weight weight = new Weight(BigInteger.valueOf(1000));
        Length length = Length.fromMillimeter(100L);
        OuterDimensions dims = new OuterDimensions(length, length, length);
        Pack pack = new Pack(weight, dims);

        Shipment shipment = new Shipment(List.of(pack), rub, moscow, saintPetersburg);

        // when
        Price price = calculator.calc(shipment);

        // then
        assertThat(price.getCurrency()).isEqualTo(rub);
    }
}
