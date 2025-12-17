package ru.fastdelivery.domain.common.coordinate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GeoPoint Value Object Tests")
class GeoPointTest {

    private static final double MIN_LATITUDE = 45.0;
    private static final double MAX_LATITUDE = 65.0;
    private static final double MIN_LONGITUDE = 30.0;
    private static final double MAX_LONGITUDE = 96.0;

    private Coordinate latitude;
    private Coordinate longitude;

    @BeforeEach
    void setUp() {
        latitude = Coordinate.latitude(55.0, MIN_LATITUDE, MAX_LATITUDE);
        longitude = Coordinate.longitude(37.0, MIN_LONGITUDE, MAX_LONGITUDE);
    }

    @Test
    @DisplayName("should create GeoPoint with valid coordinates")
    void givenValidCoordinates_whenCreated_thenSuccess() {
        // when
        GeoPoint point = new GeoPoint(latitude, longitude);

        // then
        assertThat(point).isNotNull();
        assertThat(point.getLatitude()).isEqualTo(latitude);
        assertThat(point.getLongitude()).isEqualTo(longitude);
    }

    @Test
    @DisplayName("should throw NullPointerException when latitude is null")
    void givenNullLatitude_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> new GeoPoint(null, longitude))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("latitude must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when longitude is null")
    void givenNullLongitude_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> new GeoPoint(latitude, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("longitude must not be null");
    }

    @Test
    @DisplayName("should create GeoPoint using factory method")
    void givenCoordinateValues_whenUsedFactoryMethod_thenSuccess() {
        // when
        GeoPoint point = GeoPoint.of(55.0, 37.0,
            MIN_LATITUDE, MAX_LATITUDE,
            MIN_LONGITUDE, MAX_LONGITUDE);

        // then
        assertThat(point.getLatitude().getValue()).isEqualTo(55.0);
        assertThat(point.getLongitude().getValue()).isEqualTo(37.0);
    }

    @Test
    @DisplayName("should throw exception when latitude outside range in factory method")
    void givenOutOfRangeLatitude_whenUsedFactoryMethod_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> GeoPoint.of(40.0, 37.0,
            MIN_LATITUDE, MAX_LATITUDE,
            MIN_LONGITUDE, MAX_LONGITUDE))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should throw exception when longitude outside range in factory method")
    void givenOutOfRangeLongitude_whenUsedFactoryMethod_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> GeoPoint.of(55.0, 20.0,
            MIN_LATITUDE, MAX_LATITUDE,
            MIN_LONGITUDE, MAX_LONGITUDE))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should get latitude from GeoPoint")
    void givenGeoPoint_whenGetLatitude_thenCorrect() {
        // given
        GeoPoint point = new GeoPoint(latitude, longitude);

        // when
        Coordinate lat = point.getLatitude();

        // then
        assertThat(lat).isEqualTo(latitude);
        assertThat(lat.getValue()).isEqualTo(55.0);
    }

    @Test
    @DisplayName("should get longitude from GeoPoint")
    void givenGeoPoint_whenGetLongitude_thenCorrect() {
        // given
        GeoPoint point = new GeoPoint(latitude, longitude);

        // when
        Coordinate lon = point.getLongitude();

        // then
        assertThat(lon).isEqualTo(longitude);
        assertThat(lon.getValue()).isEqualTo(37.0);
    }

    @Test
    @DisplayName("should determine if two GeoPoints are at same location")
    void givenSameGeoPoints_whenCompareLocations_thenEqual() {
        // given
        GeoPoint point1 = new GeoPoint(latitude, longitude);
        GeoPoint point2 = new GeoPoint(
            Coordinate.latitude(55.0, MIN_LATITUDE, MAX_LATITUDE),
            Coordinate.longitude(37.0, MIN_LONGITUDE, MAX_LONGITUDE)
        );

        // when
        boolean same = point1.isAtSameLocation(point2);

        // then
        assertThat(same).isTrue();
    }

    @Test
    @DisplayName("should determine if two GeoPoints are at different locations")
    void givenDifferentGeoPoints_whenCompareLocations_thenNotEqual() {
        // given
        GeoPoint point1 = new GeoPoint(latitude, longitude);
        GeoPoint point2 = new GeoPoint(
            Coordinate.latitude(60.0, MIN_LATITUDE, MAX_LATITUDE),
            Coordinate.longitude(50.0, MIN_LONGITUDE, MAX_LONGITUDE)
        );

        // when
        boolean same = point1.isAtSameLocation(point2);

        // then
        assertThat(same).isFalse();
    }

    @Test
    @DisplayName("should throw NullPointerException when comparing with null location")
    void givenNullGeoPoint_whenCompareLocations_thenThrowsException() {
        // given
        GeoPoint point = new GeoPoint(latitude, longitude);

        // when & then
        assertThatThrownBy(() -> point.isAtSameLocation(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("should calculate latitude difference")
    void givenGeoPoints_whenCalculateLatitudeDifference_thenCorrect() {
        // given
        GeoPoint point1 = new GeoPoint(
            Coordinate.latitude(55.0, MIN_LATITUDE, MAX_LATITUDE),
            longitude
        );
        GeoPoint point2 = new GeoPoint(
            Coordinate.latitude(50.0, MIN_LATITUDE, MAX_LATITUDE),
            longitude
        );

        // when
        double diff = point1.latitudeDifferenceFrom(point2);

        // then
        assertThat(diff).isEqualTo(5.0);
    }

    @Test
    @DisplayName("should calculate longitude difference")
    void givenGeoPoints_whenCalculateLongitudeDifference_thenCorrect() {
        // given
        GeoPoint point1 = new GeoPoint(
            latitude,
            Coordinate.longitude(37.0, MIN_LONGITUDE, MAX_LONGITUDE)
        );
        GeoPoint point2 = new GeoPoint(
            latitude,
            Coordinate.longitude(50.0, MIN_LONGITUDE, MAX_LONGITUDE)
        );

        // when
        double diff = point1.longitudeDifferenceFrom(point2);

        // then
        assertThat(diff).isEqualTo(13.0);
    }

    @Test
    @DisplayName("should have meaningful string representation")
    void givenGeoPoint_whenToString_thenFormatted() {
        // given
        GeoPoint point = new GeoPoint(latitude, longitude);

        // when
        String str = point.toString();

        // then
        assertThat(str).contains("55");
        assertThat(str).contains("37");
        assertThat(str).contains("[");
        assertThat(str).contains("]");
    }

    @Test
    @DisplayName("should calculate differences with same point")
    void givenSamePoint_whenCalculateDifferences_thenZero() {
        // given
        GeoPoint point1 = new GeoPoint(latitude, longitude);
        GeoPoint point2 = new GeoPoint(
            Coordinate.latitude(55.0, MIN_LATITUDE, MAX_LATITUDE),
            Coordinate.longitude(37.0, MIN_LONGITUDE, MAX_LONGITUDE)
        );

        // when
        double latDiff = point1.latitudeDifferenceFrom(point2);
        double lonDiff = point1.longitudeDifferenceFrom(point2);

        // then
        assertThat(latDiff).isEqualTo(0.0);
        assertThat(lonDiff).isEqualTo(0.0);
    }

    @Test
    @DisplayName("should handle Moscow coordinates")
    void givenMoscowCoordinates_whenCreated_thenSuccess() {
        // Moscow: 55.7558° N, 37.6173° E
        // when
        GeoPoint moscow = GeoPoint.of(55.7558, 37.6173,
            MIN_LATITUDE, MAX_LATITUDE,
            MIN_LONGITUDE, MAX_LONGITUDE);

        // then
        assertThat(moscow.getLatitude().getValue()).isCloseTo(55.7558, org.assertj.core.api.Offset.offset(0.0001));
        assertThat(moscow.getLongitude().getValue()).isCloseTo(37.6173, org.assertj.core.api.Offset.offset(0.0001));
    }
}
