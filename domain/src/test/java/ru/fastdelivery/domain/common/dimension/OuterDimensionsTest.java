package ru.fastdelivery.domain.common.dimension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OuterDimensions Value Object Tests")
class OuterDimensionsTest {

    private Length length;
    private Length width;
    private Length height;

    @BeforeEach
    void setUp() {
        length = Length.fromMillimeter(100L);  // 10 cm
        width = Length.fromMillimeter(200L);   // 20 cm
        height = Length.fromMillimeter(300L);  // 30 cm
    }

    @Test
    @DisplayName("should create OuterDimensions with valid lengths")
    void givenValidDimensions_whenCreated_thenSuccess() {
        // when
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // then
        assertThat(dimensions).isNotNull();
        assertThat(dimensions.getLength()).isEqualTo(length);
        assertThat(dimensions.getWidth()).isEqualTo(width);
        assertThat(dimensions.getHeight()).isEqualTo(height);
    }

    @Test
    @DisplayName("should throw NullPointerException when length is null")
    void givenNullLength_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> new OuterDimensions(null, width, height))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("length must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when width is null")
    void givenNullWidth_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> new OuterDimensions(length, null, height))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("width must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when height is null")
    void givenNullHeight_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> new OuterDimensions(length, width, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("height must not be null");
    }

    @Test
    @DisplayName("should calculate volume correctly from dimensions in millimeters")
    void givenDimensions_whenCalculateVolume_thenCorrect() {
        // given
        // 100mm × 200mm × 300mm = 10cm × 20cm × 30cm = 6000 cm³
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // when
        Volume volume = dimensions.calculateVolume();

        // then
        assertThat(volume.getValue()).isEqualTo(6000L);
    }

    @Test
    @DisplayName("should calculate volume with zero dimension")
    void givenDimensionsWithZero_whenCalculateVolume_thenZero() {
        // given
        Length zeroLength = Length.fromMillimeter(0L);
        OuterDimensions dimensions = new OuterDimensions(zeroLength, width, height);

        // when
        Volume volume = dimensions.calculateVolume();

        // then
        assertThat(volume.getValue()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should calculate volume with single centimeter sides")
    void givenSingleCentimeterDimensions_whenCalculateVolume_thenOneCC() {
        // given
        Length oneCm = Length.fromMillimeter(10L);
        OuterDimensions dimensions = new OuterDimensions(oneCm, oneCm, oneCm);

        // when
        Volume volume = dimensions.calculateVolume();

        // then
        assertThat(volume.getValue()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should find longest side")
    void givenDimensions_whenGetLongestSide_thenCorrect() {
        // given
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // when
        Length longest = dimensions.getLongestSide();

        // then
        assertThat(longest).isEqualTo(height);
        assertThat(longest.inMillimetersAsLong()).isEqualTo(300L);
    }

    @Test
    @DisplayName("should find longest side when all equal")
    void givenEqualDimensions_whenGetLongestSide_thenAny() {
        // given
        Length same = Length.fromMillimeter(100L);
        OuterDimensions dimensions = new OuterDimensions(same, same, same);

        // when
        Length longest = dimensions.getLongestSide();

        // then
        assertThat(longest.inMillimetersAsLong()).isEqualTo(100L);
    }

    @Test
    @DisplayName("should find longest side with mixed values")
    void givenMixedDimensions_whenGetLongestSide_thenCorrect() {
        // given
        Length small = Length.fromMillimeter(50L);
        Length medium = Length.fromMillimeter(200L);
        Length big = Length.fromMillimeter(500L);
        OuterDimensions dimensions = new OuterDimensions(medium, small, big);

        // when
        Length longest = dimensions.getLongestSide();

        // then
        assertThat(longest).isEqualTo(big);
    }

    @Test
    @DisplayName("should find shortest side")
    void givenDimensions_whenGetShortestSide_thenCorrect() {
        // given
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // when
        Length shortest = dimensions.getShortestSide();

        // then
        assertThat(shortest).isEqualTo(length);
        assertThat(shortest.inMillimetersAsLong()).isEqualTo(100L);
    }

    @Test
    @DisplayName("should find shortest side when all equal")
    void givenEqualDimensions_whenGetShortestSide_thenAny() {
        // given
        Length same = Length.fromMillimeter(100L);
        OuterDimensions dimensions = new OuterDimensions(same, same, same);

        // when
        Length shortest = dimensions.getShortestSide();

        // then
        assertThat(shortest.inMillimetersAsLong()).isEqualTo(100L);
    }

    @Test
    @DisplayName("should calculate sum of dimensions correctly")
    void givenDimensions_whenSumOfDimensions_thenCorrect() {
        // given
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // when
        long sum = dimensions.sumOfDimensions();

        // then
        // 100 + 200 + 300 = 600
        assertThat(sum).isEqualTo(600L);
    }

    @Test
    @DisplayName("should calculate sum of dimensions with zero")
    void givenDimensionsWithZero_whenSumOfDimensions_thenSum() {
        // given
        Length zero = Length.fromMillimeter(0L);
        OuterDimensions dimensions = new OuterDimensions(zero, length, width);

        // when
        long sum = dimensions.sumOfDimensions();

        // then
        // 0 + 100 + 200 = 300
        assertThat(sum).isEqualTo(300L);
    }

    @Test
    @DisplayName("should calculate volume with large dimensions")
    void givenLargeDimensions_whenCalculateVolume_thenCorrect() {
        // given
        Length large1 = Length.fromMillimeter(1000L);  // 100 cm
        Length large2 = Length.fromMillimeter(2000L);  // 200 cm
        Length large3 = Length.fromMillimeter(3000L);  // 300 cm
        OuterDimensions dimensions = new OuterDimensions(large1, large2, large3);

        // when
        Volume volume = dimensions.calculateVolume();

        // then
        // 100 × 200 × 300 = 6,000,000 cm³
        assertThat(volume.getValue()).isEqualTo(6_000_000L);
    }

    @Test
    @DisplayName("should handle dimensions with fractional millimeters")
    void givenDimensionsRoundedFromCentimeters_whenCalculateVolume_thenCorrect() {
        // given
        Length dim1 = Length.fromCentimeter(10.5);
        Length dim2 = Length.fromCentimeter(20.5);
        Length dim3 = Length.fromCentimeter(30.5);
        OuterDimensions dimensions = new OuterDimensions(dim1, dim2, dim3);

        // when
        Volume volume = dimensions.calculateVolume();

        // then - should be approximately 10.5 × 20.5 × 30.5 ≈ 6574 cm³
        assertThat(volume.getValue()).isGreaterThan(6500L);
        assertThat(volume.getValue()).isLessThan(6700L);
    }

    @Test
    @DisplayName("should get all dimensions separately")
    void givenDimensions_whenGetEachDimension_thenCorrect() {
        // given
        OuterDimensions dimensions = new OuterDimensions(length, width, height);

        // when & then
        assertThat(dimensions.getLength()).isEqualTo(length);
        assertThat(dimensions.getWidth()).isEqualTo(width);
        assertThat(dimensions.getHeight()).isEqualTo(height);
    }
}
