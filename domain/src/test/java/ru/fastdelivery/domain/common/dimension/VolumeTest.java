package ru.fastdelivery.domain.common.dimension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Volume Value Object Tests")
class VolumeTest {

    @Test
    @DisplayName("should create volume with zero cubic centimeters")
    void givenZeroVolume_whenCreated_thenSuccess() {
        // when
        Volume volume = Volume.fromCubicCentimeters(0L);

        // then
        assertThat(volume.getValue()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should create volume with positive cubic centimeters")
    void givenPositiveVolume_whenCreated_thenSuccess() {
        // when
        Volume volume = Volume.fromCubicCentimeters(1000L);

        // then
        assertThat(volume.getValue()).isEqualTo(1000L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 1000L, 100000L, 1_000_000L})
    @DisplayName("should create volumes with valid values")
    void givenValidVolumes_whenCreated_thenSuccess(long cm3) {
        // when
        Volume volume = Volume.fromCubicCentimeters(cm3);

        // then
        assertThat(volume.getValue()).isEqualTo(cm3);
    }

    @Test
    @DisplayName("should throw exception when creating volume with negative value")
    void givenNegativeVolume_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> Volume.fromCubicCentimeters(-100L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be negative")
            .hasMessageContaining("-100");
    }

    @Test
    @DisplayName("should throw exception when creating volume exceeding maximum")
    void givenVolumeExceedingMax_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> Volume.fromCubicCentimeters(1_000_001L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("exceeds maximum")
            .hasMessageContaining("1000000");
    }

    @Test
    @DisplayName("should create volume from cubic meters")
    void givenCubicMeters_whenCreated_thenConvertedToCubicCentimeters() {
        // when
        Volume volume = Volume.fromCubicMeters(1.0);

        // then
        assertThat(volume.getValue()).isEqualTo(1_000_000L);
        assertThat(volume.getValueInCubicMeters()).isCloseTo(1.0, org.assertj.core.api.Assertions.within(0.001));
    }

    @Test
    @DisplayName("should create volume from cubic meters with decimal")
    void givenCubicMetersWithDecimal_whenCreated_thenRounded() {
        // when
        Volume volume = Volume.fromCubicMeters(0.5);

        // then
        assertThat(volume.getValue()).isEqualTo(500_000L);
    }

    @Test
    @DisplayName("should throw exception when creating volume from negative cubic meters")
    void givenNegativeCubicMeters_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> Volume.fromCubicMeters(-1.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be negative");
    }

    @Test
    @DisplayName("should add two volumes")
    void givenTwoVolumes_whenAdded_thenSuccess() {
        // given
        Volume volume1 = Volume.fromCubicCentimeters(500L);
        Volume volume2 = Volume.fromCubicCentimeters(300L);

        // when
        Volume sum = volume1.add(volume2);

        // then
        assertThat(sum.getValue()).isEqualTo(800L);
    }

    @Test
    @DisplayName("should add volume resulting in zero")
    void givenTwoVolumes_whenAddedToZero_thenZero() {
        // given
        Volume volume1 = Volume.fromCubicCentimeters(500L);
        Volume volume2 = Volume.fromCubicCentimeters(0L);

        // when
        Volume sum = volume1.add(volume2);

        // then
        assertThat(sum.getValue()).isEqualTo(500L);
    }

    @Test
    @DisplayName("should throw exception when adding volumes exceeds maximum")
    void givenTwoVolumes_whenAddedExceedsMax_thenThrowsException() {
        // given
        Volume volume1 = Volume.fromCubicCentimeters(600_000L);
        Volume volume2 = Volume.fromCubicCentimeters(500_001L);

        // when & then
        assertThatThrownBy(() -> volume1.add(volume2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("exceeds maximum");
    }

    @Test
    @DisplayName("should throw NullPointerException when adding null")
    void givenNullVolume_whenAdded_thenThrowsNullPointerException() {
        // given
        Volume volume = Volume.fromCubicCentimeters(100L);

        // when & then
        assertThatThrownBy(() -> volume.add(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should compare volumes correctly - isGreaterThan")
    void givenTwoVolumes_whenComparedWithIsGreaterThan_thenCorrect() {
        // given
        Volume smaller = Volume.fromCubicCentimeters(100L);
        Volume larger = Volume.fromCubicCentimeters(200L);
        Volume equal = Volume.fromCubicCentimeters(100L);

        // when & then
        assertThat(larger.isGreaterThan(smaller)).isTrue();
        assertThat(smaller.isGreaterThan(larger)).isFalse();
        assertThat(smaller.isGreaterThan(equal)).isFalse();
    }

    @Test
    @DisplayName("should compare volumes correctly - isLessThan")
    void givenTwoVolumes_whenComparedWithIsLessThan_thenCorrect() {
        // given
        Volume smaller = Volume.fromCubicCentimeters(100L);
        Volume larger = Volume.fromCubicCentimeters(200L);
        Volume equal = Volume.fromCubicCentimeters(100L);

        // when & then
        assertThat(smaller.isLessThan(larger)).isTrue();
        assertThat(larger.isLessThan(smaller)).isFalse();
        assertThat(smaller.isLessThan(equal)).isFalse();
    }

    @Test
    @DisplayName("should throw NullPointerException when comparing with null")
    void givenNullVolume_whenCompared_thenThrowsNullPointerException() {
        // given
        Volume volume = Volume.fromCubicCentimeters(100L);

        // when & then
        assertThatThrownBy(() -> volume.isGreaterThan(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should get value as BigDecimal")
    void givenVolume_whenGetValueAsBigDecimal_thenCorrect() {
        // given
        Volume volume = Volume.fromCubicCentimeters(1000L);

        // when
        BigDecimal value = volume.getValueAsBigDecimal();

        // then
        assertThat(value).isEqualTo(BigDecimal.valueOf(1000L));
    }

    @Test
    @DisplayName("should convert to cubic meters correctly")
    void givenVolume_whenConvertedToCubicMeters_thenCorrect() {
        // given
        Volume volume = Volume.fromCubicCentimeters(1_000_000L);

        // when
        double cubicMeters = volume.getValueInCubicMeters();

        // then
        assertThat(cubicMeters).isCloseTo(1.0, org.assertj.core.api.Assertions.within(0.001));
    }

    @Test
    @DisplayName("should handle maximum valid volume")
    void givenMaximumValidVolume_whenCreated_thenSuccess() {
        // when
        Volume volume = Volume.fromCubicCentimeters(1_000_000L);

        // then
        assertThat(volume.getValue()).isEqualTo(1_000_000L);
    }
}
