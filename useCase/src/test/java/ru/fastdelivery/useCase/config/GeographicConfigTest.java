package ru.fastdelivery.useCase.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GeographicConfig Tests")
class GeographicConfigTest {

    private GeographicConfig russiaConfig;

    @BeforeEach
    void setUp() {
        russiaConfig = GeographicConfig.forRussia();
    }

    @Test
    @DisplayName("should create Russia config with correct boundaries")
    void givenRussiaConfig_thenBoundariesCorrect() {
        // when
        GeographicConfig config = GeographicConfig.forRussia();

        // then
        assertThat(config.getMinLatitude()).isEqualTo(45.0);
        assertThat(config.getMaxLatitude()).isEqualTo(65.0);
        assertThat(config.getMinLongitude()).isEqualTo(30.0);
        assertThat(config.getMaxLongitude()).isEqualTo(96.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = {45.0, 50.0, 55.0, 60.0, 65.0})
    @DisplayName("should accept valid latitude values within range [45, 65]")
    void givenValidLatitude_whenValidate_thenAccepted(double latitude) {
        // when & then - should not throw exception
        russiaConfig.validateLatitude(latitude);
    }

    @ParameterizedTest
    @ValueSource(doubles = {30.0, 50.0, 63.0, 96.0})
    @DisplayName("should accept valid longitude values within range [30, 96]")
    void givenValidLongitude_whenValidate_thenAccepted(double longitude) {
        // when & then - should not throw exception
        russiaConfig.validateLongitude(longitude);
    }

    @ParameterizedTest
    @ValueSource(doubles = {44.9, 44.0, 30.0, 0.0, -45.0})
    @DisplayName("should reject latitude below minimum (45)")
    void givenLatitudeBelowMinimum_whenValidate_thenThrow(double latitude) {
        // when & then
        assertThatThrownBy(() -> russiaConfig.validateLatitude(latitude))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("outside valid range")
            .hasMessageContaining("45.0");
    }

    @ParameterizedTest
    @ValueSource(doubles = {65.1, 66.0, 90.0})
    @DisplayName("should reject latitude above maximum (65)")
    void givenLatitudeAboveMaximum_whenValidate_thenThrow(double latitude) {
        // when & then
        assertThatThrownBy(() -> russiaConfig.validateLatitude(latitude))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("outside valid range")
            .hasMessageContaining("65.0");
    }

    @ParameterizedTest
    @ValueSource(doubles = {29.9, 20.0, 0.0, -96.0})
    @DisplayName("should reject longitude below minimum (30)")
    void givenLongitudeBelowMinimum_whenValidate_thenThrow(double longitude) {
        // when & then
        assertThatThrownBy(() -> russiaConfig.validateLongitude(longitude))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("outside valid range")
            .hasMessageContaining("30.0");
    }

    @ParameterizedTest
    @ValueSource(doubles = {96.1, 100.0, 180.0})
    @DisplayName("should reject longitude above maximum (96)")
    void givenLongitudeAboveMaximum_whenValidate_thenThrow(double longitude) {
        // when & then
        assertThatThrownBy(() -> russiaConfig.validateLongitude(longitude))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("outside valid range")
            .hasMessageContaining("96.0");
    }

    @Test
    @DisplayName("should accept Moscow coordinates")
    void givenMoscow_whenValidate_thenAccepted() {
        // given
        double moscowLatitude = 55.7558;
        double moscowLongitude = 37.6173;

        // when & then - should not throw
        russiaConfig.validateCoordinates(moscowLatitude, moscowLongitude);
    }

    @Test
    @DisplayName("should accept Saint Petersburg coordinates")
    void givenSaintPetersburg_whenValidate_thenAccepted() {
        // given
        double spbLatitude = 59.9311;
        double spbLongitude = 30.3609;

        // when & then - should not throw
        russiaConfig.validateCoordinates(spbLatitude, spbLongitude);
    }

    @Test
    @DisplayName("should accept Samara coordinates")
    void givenSamara_whenValidate_thenAccepted() {
        // given
        double samaraLatitude = 53.1959;
        double samaraLongitude = 50.1200;

        // when & then - should not throw
        russiaConfig.validateCoordinates(samaraLatitude, samaraLongitude);
    }

    @Test
    @DisplayName("should reject invalid latitude in validateCoordinates")
    void givenInvalidLatitudeInCoordinates_whenValidate_thenThrow() {
        // when & then
        assertThatThrownBy(() -> russiaConfig.validateCoordinates(70.0, 37.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Latitude")
            .hasMessageContaining("outside valid range");
    }

    @Test
    @DisplayName("should reject invalid longitude in validateCoordinates")
    void givenInvalidLongitudeInCoordinates_whenValidate_thenThrow() {
        // when & then
        assertThatThrownBy(() -> russiaConfig.validateCoordinates(55.0, 150.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Longitude")
            .hasMessageContaining("outside valid range");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when minLatitude >= maxLatitude")
    void givenMinLatitudeGreaterThanMax_whenCreate_thenThrow() {
        // when & then
        assertThatThrownBy(() -> new GeographicConfig(65.0, 45.0, 30.0, 96.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("minLatitude must be less than maxLatitude");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when minLongitude >= maxLongitude")
    void givenMinLongitudeGreaterThanMax_whenCreate_thenThrow() {
        // when & then
        assertThatThrownBy(() -> new GeographicConfig(45.0, 65.0, 96.0, 30.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("minLongitude must be less than maxLongitude");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when latitude out of bounds [-90, 90]")
    void givenLatitudeOutOfBounds_whenCreate_thenThrow() {
        // when & then
        assertThatThrownBy(() -> new GeographicConfig(45.0, 95.0, 30.0, 96.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Latitude must be between -90 and 90");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when longitude out of bounds [-180, 180]")
    void givenLongitudeOutOfBounds_whenCreate_thenThrow() {
        // when & then
        assertThatThrownBy(() -> new GeographicConfig(45.0, 65.0, -200.0, 96.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Longitude must be between -180 and 180");
    }

    @Test
    @DisplayName("should return correct string representation")
    void givenConfig_whenToString_thenFormattedCorrectly() {
        // when
        String result = russiaConfig.toString();

        // then
        assertThat(result)
            .contains("GeographicConfig")
            .contains("45.0")
            .contains("65.0")
            .contains("30.0")
            .contains("96.0")
            .contains("latitude")
            .contains("longitude");
    }

    @Test
    @DisplayName("should be equal when boundaries are the same")
    void givenTwoConfigsWithSameBoundaries_whenCompare_thenEqual() {
        // given
        GeographicConfig config1 = GeographicConfig.forRussia();
        GeographicConfig config2 = new GeographicConfig(45.0, 65.0, 30.0, 96.0);

        // when & then
        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    @Test
    @DisplayName("should not be equal when boundaries differ")
    void givenTwoConfigsWithDifferentBoundaries_whenCompare_thenNotEqual() {
        // given
        GeographicConfig config1 = GeographicConfig.forRussia();
        GeographicConfig config2 = new GeographicConfig(40.0, 70.0, 25.0, 100.0);

        // when & then
        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    @DisplayName("should support custom geographic boundaries")
    void givenCustomBoundaries_whenCreate_thenValid() {
        // given
        double minLat = 50.0;
        double maxLat = 60.0;
        double minLon = 35.0;
        double maxLon = 45.0;

        // when
        GeographicConfig config = new GeographicConfig(minLat, maxLat, minLon, maxLon);

        // then
        assertThat(config.getMinLatitude()).isEqualTo(50.0);
        assertThat(config.getMaxLatitude()).isEqualTo(60.0);
        assertThat(config.getMinLongitude()).isEqualTo(35.0);
        assertThat(config.getMaxLongitude()).isEqualTo(45.0);

        // Verify validation works within custom boundaries
        config.validateCoordinates(55.0, 40.0);  // Should pass
    }
}
