package ru.fastdelivery.domain.common.coordinate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Distance Value Object Tests")
class DistanceTest {

    private static final double MIN_LATITUDE = 45.0;
    private static final double MAX_LATITUDE = 65.0;
    private static final double MIN_LONGITUDE = 30.0;
    private static final double MAX_LONGITUDE = 96.0;
    private static final double TOLERANCE = 10.0; // 10 km tolerance for real-world calculations

    private GeoPoint moscow;
    private GeoPoint saintPetersburg;
    private GeoPoint samara;
    private GeoPoint sameLocationAsMoscow;

    @BeforeEach
    void setUp() {
        // Moscow: 55.7558° N, 37.6173° E
        moscow = GeoPoint.of(55.7558, 37.6173, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);

        // Saint Petersburg: 59.9311° N, 30.3609° E
        saintPetersburg = GeoPoint.of(59.9311, 30.3609, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);

        // Samara: 53.1959° N, 50.1200° E
        samara = GeoPoint.of(53.1959, 50.1200, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);

        // Same location as Moscow for testing
        sameLocationAsMoscow = GeoPoint.of(55.7558, 37.6173, MIN_LATITUDE, MAX_LATITUDE, MIN_LONGITUDE, MAX_LONGITUDE);
    }

    @Test
    @DisplayName("should create distance with valid value")
    void givenValidDistance_whenCreated_thenSuccess() {
        // when
        Distance distance = new Distance(100.0);

        // then
        assertThat(distance).isNotNull();
        assertThat(distance.getKilometers()).isEqualTo(100.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.0, 100.0, 1000.0, 20000.0})
    @DisplayName("should create distance with various valid values")
    void givenVariousValidDistances_whenCreated_thenSuccess(double km) {
        // when
        Distance distance = new Distance(km);

        // then
        assertThat(distance.getKilometers()).isEqualTo(km);
    }

    @Test
    @DisplayName("should throw exception when distance is negative")
    void givenNegativeDistance_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> new Distance(-100.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be negative");
    }

    @Test
    @DisplayName("should throw exception when distance exceeds maximum")
    void givenDistanceExceedsMax_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> new Distance(25000.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("exceeds maximum");
    }

    @Test
    @DisplayName("should calculate distance between Moscow and Saint Petersburg (~714 km)")
    void givenMoscowAndSaintPetersburg_whenCalculateDistance_thenApproximately714km() {
        // when
        Distance distance = Distance.calculate(moscow, saintPetersburg);

        // then
        // Expected distance: ~714 km
        assertThat(distance.getKilometers()).isCloseTo(714.0, org.assertj.core.api.Offset.offset(TOLERANCE));
    }

    @Test
    @DisplayName("should calculate distance between Moscow and Samara (~520 km)")
    void givenMoscowAndSamara_whenCalculateDistance_thenApproximately520km() {
        // when
        Distance distance = Distance.calculate(moscow, samara);

        // then
        // Expected distance: ~520 km
        assertThat(distance.getKilometers()).isCloseTo(520.0, org.assertj.core.api.Offset.offset(TOLERANCE));
    }

    @Test
    @DisplayName("should calculate zero distance between same locations")
    void givenSameLocations_whenCalculateDistance_thenZero() {
        // when
        Distance distance = Distance.calculate(moscow, sameLocationAsMoscow);

        // then
        assertThat(distance.getKilometers()).isCloseTo(0.0, org.assertj.core.api.Offset.offset(0.1));
    }

    @Test
    @DisplayName("should have symmetrical distance calculation")
    void givenTwoPoints_whenCalculateDistanceInBothDirections_thenEqual() {
        // when
        Distance dist1 = Distance.calculate(moscow, saintPetersburg);
        Distance dist2 = Distance.calculate(saintPetersburg, moscow);

        // then
        assertThat(dist1.getKilometers()).isCloseTo(dist2.getKilometers(), org.assertj.core.api.Offset.offset(0.1));
    }

    @Test
    @DisplayName("should throw NullPointerException when from point is null")
    void givenNullFromPoint_whenCalculateDistance_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> Distance.calculate(null, saintPetersburg))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("from point must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when to point is null")
    void givenNullToPoint_whenCalculateDistance_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> Distance.calculate(moscow, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("to point must not be null");
    }

    @Test
    @DisplayName("should convert distance to meters")
    void givenDistance_whenConvertToMeters_thenCorrect() {
        // given
        Distance distance = new Distance(10.5);

        // when
        double meters = distance.getMeters();

        // then
        assertThat(meters).isEqualTo(10500.0);
    }

    @Test
    @DisplayName("should convert distance to miles")
    void givenDistance_whenConvertToMiles_thenCorrect() {
        // given
        Distance distance = new Distance(10.0);

        // when
        double miles = distance.getMiles();

        // then
        assertThat(miles).isCloseTo(6.21371, org.assertj.core.api.Offset.offset(0.01));
    }

    @Test
    @DisplayName("should compare distances with isGreaterThan")
    void givenDistances_whenCompareGreaterThan_thenCorrect() {
        // given
        Distance dist1 = new Distance(100.0);
        Distance dist2 = new Distance(50.0);
        Distance dist3 = new Distance(100.0);

        // when & then
        assertThat(dist1.isGreaterThan(dist2)).isTrue();
        assertThat(dist2.isGreaterThan(dist1)).isFalse();
        assertThat(dist1.isGreaterThan(dist3)).isFalse();
    }

    @Test
    @DisplayName("should compare distances with isLessThan")
    void givenDistances_whenCompareLessThan_thenCorrect() {
        // given
        Distance dist1 = new Distance(50.0);
        Distance dist2 = new Distance(100.0);
        Distance dist3 = new Distance(50.0);

        // when & then
        assertThat(dist1.isLessThan(dist2)).isTrue();
        assertThat(dist2.isLessThan(dist1)).isFalse();
        assertThat(dist1.isLessThan(dist3)).isFalse();
    }

    @Test
    @DisplayName("should add two distances")
    void givenDistances_whenAdd_thenCorrect() {
        // given
        Distance dist1 = new Distance(100.0);
        Distance dist2 = new Distance(50.0);

        // when
        Distance sum = dist1.add(dist2);

        // then
        assertThat(sum.getKilometers()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("should throw exception when adding distances exceeds maximum")
    void givenDistances_whenAddExceedsMax_thenThrowsException() {
        // given
        Distance dist1 = new Distance(15000.0);
        Distance dist2 = new Distance(10000.0);

        // when & then
        assertThatThrownBy(() -> dist1.add(dist2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("exceeds maximum");
    }

    @Test
    @DisplayName("should calculate ratio between distances")
    void givenDistances_whenCalculateRatio_thenCorrect() {
        // given
        Distance dist1 = new Distance(100.0);
        Distance dist2 = new Distance(50.0);

        // when
        double ratio = dist1.ratioTo(dist2);

        // then
        assertThat(ratio).isEqualTo(2.0);
    }

    @Test
    @DisplayName("should throw exception when dividing by zero distance")
    void givenZeroDistance_whenCalculateRatio_thenThrowsException() {
        // given
        Distance dist1 = new Distance(100.0);
        Distance dist2 = new Distance(0.0);

        // when & then
        assertThatThrownBy(() -> dist1.ratioTo(dist2))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("divide by zero");
    }

    @Test
    @DisplayName("should compare distances with tolerance")
    void givenDistances_whenCompareWithTolerance_thenCorrect() {
        // given
        Distance dist1 = new Distance(100.0);
        Distance dist2 = new Distance(100.5);
        Distance dist3 = new Distance(102.0);

        // when & then
        assertThat(dist1.isApproximatelyEqualTo(dist2, 1.0)).isTrue();
        assertThat(dist1.isApproximatelyEqualTo(dist3, 1.0)).isFalse();
    }

    @Test
    @DisplayName("should have meaningful string representation")
    void givenDistance_whenToString_thenFormatted() {
        // given
        Distance distance = new Distance(123.45);

        // when
        String str = distance.toString();

        // then
        assertThat(str).contains("123");
        assertThat(str).contains("km");
    }

    @Test
    @DisplayName("should get distance as BigDecimal")
    void givenDistance_whenGetAsBigDecimal_thenCorrect() {
        // given
        Distance distance = new Distance(123.456);

        // when
        var bd = distance.getKilometersAsBigDecimal();

        // then
        assertThat(bd.doubleValue()).isCloseTo(123.456, org.assertj.core.api.Offset.offset(0.001));
    }

    @Test
    @DisplayName("should handle very short distances")
    void givenVeryShortDistance_whenCreated_thenSuccess() {
        // when
        Distance distance = new Distance(0.1);

        // then
        assertThat(distance.getKilometers()).isEqualTo(0.1);
        assertThat(distance.getMeters()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("should handle maximum distance boundary")
    void givenMaximumDistance_whenCreated_thenSuccess() {
        // when
        Distance distance = new Distance(20000.0);

        // then
        assertThat(distance.getKilometers()).isEqualTo(20000.0);
    }
}
