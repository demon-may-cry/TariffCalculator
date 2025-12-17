package ru.fastdelivery.domain.common.coordinate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Coordinate Value Object Tests")
class CoordinateTest {

    private static final double MIN_LATITUDE = 45.0;
    private static final double MAX_LATITUDE = 65.0;
    private static final double MIN_LONGITUDE = 30.0;
    private static final double MAX_LONGITUDE = 96.0;

    @Test
    @DisplayName("should create coordinate with valid value")
    void givenValidValue_whenCreated_thenSuccess() {
        // when
        Coordinate coord = new Coordinate(55.0);

        // then
        assertThat(coord).isNotNull();
        assertThat(coord.getValue()).isEqualTo(55.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 45.0, 55.5, 90.0, 180.0})
    @DisplayName("should create coordinate with various valid values")
    void givenVariousValidValues_whenCreated_thenSuccess(double value) {
        // when
        Coordinate coord = new Coordinate(value);

        // then
        assertThat(coord.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("should create latitude with valid range")
    void givenLatitudeInRange_whenCreated_thenSuccess() {
        // when
        Coordinate lat = Coordinate.latitude(55.0, MIN_LATITUDE, MAX_LATITUDE);

        // then
        assertThat(lat.getValue()).isEqualTo(55.0);
    }

    @Test
    @DisplayName("should throw exception when latitude below minimum")
    void givenLatitudeBelowMinimum_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> 
            Coordinate.latitude(40.0, MIN_LATITUDE, MAX_LATITUDE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not in range");
    }

    @Test
    @DisplayName("should throw exception when latitude above maximum")
    void givenLatitudeAboveMaximum_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> 
            Coordinate.latitude(70.0, MIN_LATITUDE, MAX_LATITUDE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not in range");
    }

    @Test
    @DisplayName("should create longitude with valid range")
    void givenLongitudeInRange_whenCreated_thenSuccess() {
        // when
        Coordinate lon = Coordinate.longitude(55.0, MIN_LONGITUDE, MAX_LONGITUDE);

        // then
        assertThat(lon.getValue()).isEqualTo(55.0);
    }

    @Test
    @DisplayName("should throw exception when longitude below minimum")
    void givenLongitudeBelowMinimum_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> 
            Coordinate.longitude(20.0, MIN_LONGITUDE, MAX_LONGITUDE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not in range");
    }

    @Test
    @DisplayName("should throw exception when longitude above maximum")
    void givenLongitudeAboveMaximum_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> 
            Coordinate.longitude(100.0, MIN_LONGITUDE, MAX_LONGITUDE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not in range");
    }

    @Test
    @DisplayName("should compare coordinates for equality")
    void givenCoordinates_whenCompareForEquality_thenCorrect() {
        // given
        Coordinate coord1 = new Coordinate(55.0);
        Coordinate coord2 = new Coordinate(55.0);
        Coordinate coord3 = new Coordinate(50.0);

        // when & then
        assertThat(coord1.isEqualTo(coord2)).isTrue();
        assertThat(coord1.isEqualTo(coord3)).isFalse();
    }

    @Test
    @DisplayName("should throw NullPointerException when comparing with null")
    void givenNullCoordinate_whenCompare_thenThrowsException() {
        // given
        Coordinate coord = new Coordinate(55.0);

        // when & then
        assertThatThrownBy(() -> coord.isEqualTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("should compare coordinates with isGreaterThan")
    void givenCoordinates_whenCompareGreaterThan_thenCorrect() {
        // given
        Coordinate coord1 = new Coordinate(60.0);
        Coordinate coord2 = new Coordinate(50.0);
        Coordinate coord3 = new Coordinate(60.0);

        // when & then
        assertThat(coord1.isGreaterThan(coord2)).isTrue();
        assertThat(coord2.isGreaterThan(coord1)).isFalse();
        assertThat(coord1.isGreaterThan(coord3)).isFalse();
    }

    @Test
    @DisplayName("should compare coordinates with isLessThan")
    void givenCoordinates_whenCompareLessThan_thenCorrect() {
        // given
        Coordinate coord1 = new Coordinate(50.0);
        Coordinate coord2 = new Coordinate(60.0);
        Coordinate coord3 = new Coordinate(50.0);

        // when & then
        assertThat(coord1.isLessThan(coord2)).isTrue();
        assertThat(coord2.isLessThan(coord1)).isFalse();
        assertThat(coord1.isLessThan(coord3)).isFalse();
    }

    @Test
    @DisplayName("should calculate difference between coordinates")
    void givenCoordinates_whenCalculateDifference_thenCorrect() {
        // given
        Coordinate coord1 = new Coordinate(60.0);
        Coordinate coord2 = new Coordinate(50.0);

        // when
        double diff = coord1.differenceFrom(coord2);

        // then
        assertThat(diff).isEqualTo(10.0);
    }

    @Test
    @DisplayName("should calculate difference as absolute value")
    void givenCoordinatesReversed_whenCalculateDifference_thenAbsoluteValue() {
        // given
        Coordinate coord1 = new Coordinate(50.0);
        Coordinate coord2 = new Coordinate(60.0);

        // when
        double diff = coord1.differenceFrom(coord2);

        // then
        assertThat(diff).isEqualTo(10.0);
    }

    @Test
    @DisplayName("should handle boundary latitude values")
    void givenBoundaryLatitude_whenCreated_thenSuccess() {
        // when
        Coordinate minLat = Coordinate.latitude(MIN_LATITUDE, MIN_LATITUDE, MAX_LATITUDE);
        Coordinate maxLat = Coordinate.latitude(MAX_LATITUDE, MIN_LATITUDE, MAX_LATITUDE);

        // then
        assertThat(minLat.getValue()).isEqualTo(MIN_LATITUDE);
        assertThat(maxLat.getValue()).isEqualTo(MAX_LATITUDE);
    }

    @Test
    @DisplayName("should handle boundary longitude values")
    void givenBoundaryLongitude_whenCreated_thenSuccess() {
        // when
        Coordinate minLon = Coordinate.longitude(MIN_LONGITUDE, MIN_LONGITUDE, MAX_LONGITUDE);
        Coordinate maxLon = Coordinate.longitude(MAX_LONGITUDE, MIN_LONGITUDE, MAX_LONGITUDE);

        // then
        assertThat(minLon.getValue()).isEqualTo(MIN_LONGITUDE);
        assertThat(maxLon.getValue()).isEqualTo(MAX_LONGITUDE);
    }

    @Test
    @DisplayName("should have meaningful string representation")
    void givenCoordinate_whenToString_thenFormatted() {
        // given
        Coordinate coord = new Coordinate(55.123456);

        // when
        String str = coord.toString();

        // then
        assertThat(str).contains("55");
        assertThat(str).contains("°");
    }
}
