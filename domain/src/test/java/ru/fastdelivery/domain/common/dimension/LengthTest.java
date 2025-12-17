package ru.fastdelivery.domain.common.dimension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Length Value Object Tests")
class LengthTest {

    @Test
    @DisplayName("should create valid length with zero millimeters")
    void givenZeroLength_whenCreated_thenSuccess() {
        // when
        Length length = Length.fromMillimeter(0L);

        // then
        assertThat(length.inMillimetersAsLong()).isEqualTo(0L);
        assertThat(length.inMillimeters()).isEqualTo(BigInteger.ZERO);
    }

    @Test
    @DisplayName("should create valid length with positive millimeters")
    void givenPositiveLength_whenCreated_thenSuccess() {
        // when
        Length length = Length.fromMillimeter(500L);

        // then
        assertThat(length.inMillimetersAsLong()).isEqualTo(500L);
        assertThat(length.inCentimeters()).isEqualTo(50.0);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 100L, 1000L, 5000L, 9999L})
    @DisplayName("should create valid lengths with valid values")
    void givenValidLengths_whenCreated_thenSuccess(long mm) {
        // when
        Length length = Length.fromMillimeter(mm);

        // then
        assertThat(length.inMillimetersAsLong()).isEqualTo(mm);
    }

    @Test
    @DisplayName("should throw exception when creating length with negative millimeters")
    void givenNegativeLength_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> Length.fromMillimeter(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be negative")
            .hasMessageContaining("-1");
    }

    @Test
    @DisplayName("should throw exception when creating length exceeding maximum")
    void givenLengthExceedingMax_whenCreated_thenThrowsException() {
        // when & then
        assertThatThrownBy(() -> Length.fromMillimeter(10000L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("exceeds maximum")
            .hasMessageContaining("9999");
    }

    @Test
    @DisplayName("should create length from centimeters")
    void givenCentimeters_whenCreated_thenConvertedToMillimeters() {
        // when
        Length length = Length.fromCentimeter(50.0);

        // then
        assertThat(length.inCentimeters()).isEqualTo(50.0);
        assertThat(length.inMillimetersAsLong()).isEqualTo(500L);
    }

    @Test
    @DisplayName("should create length from centimeters with rounding")
    void givenCentimetersWithDecimal_whenCreated_thenRounded() {
        // when
        Length length = Length.fromCentimeter(50.5);

        // then
        assertThat(length.inCentimeters()).isCloseTo(50.5, org.assertj.core.api.Assertions.within(0.1));
    }

    @Test
    @DisplayName("should compare lengths correctly - isLongerThan")
    void givenTwoLengths_whenComparedWithIsLongerThan_thenCorrect() {
        // given
        Length shorter = Length.fromMillimeter(100L);
        Length longer = Length.fromMillimeter(200L);
        Length equal = Length.fromMillimeter(100L);

        // when & then
        assertThat(longer.isLongerThan(shorter)).isTrue();
        assertThat(shorter.isLongerThan(longer)).isFalse();
        assertThat(shorter.isLongerThan(equal)).isFalse();
    }

    @Test
    @DisplayName("should compare lengths correctly - isShorterThan")
    void givenTwoLengths_whenComparedWithIsShorterThan_thenCorrect() {
        // given
        Length shorter = Length.fromMillimeter(100L);
        Length longer = Length.fromMillimeter(200L);
        Length equal = Length.fromMillimeter(100L);

        // when & then
        assertThat(shorter.isShorterThan(longer)).isTrue();
        assertThat(longer.isShorterThan(shorter)).isFalse();
        assertThat(shorter.isShorterThan(equal)).isFalse();
    }

    @Test
    @DisplayName("should compare lengths correctly - isEqualTo")
    void givenTwoLengths_whenComparedWithIsEqualTo_thenCorrect() {
        // given
        Length length1 = Length.fromMillimeter(100L);
        Length length2 = Length.fromMillimeter(100L);
        Length length3 = Length.fromMillimeter(200L);

        // when & then
        assertThat(length1.isEqualTo(length2)).isTrue();
        assertThat(length1.isEqualTo(length3)).isFalse();
    }

    @Test
    @DisplayName("should throw NullPointerException when comparing with null")
    void givenNullLength_whenCompared_thenThrowsNullPointerException() {
        // given
        Length length = Length.fromMillimeter(100L);

        // when & then
        assertThatThrownBy(() -> length.isLongerThan(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should create length from BigInteger")
    void givenBigInteger_whenCreated_thenSuccess() {
        // when
        Length length = Length.fromMillimeter(BigInteger.valueOf(500L));

        // then
        assertThat(length.inMillimetersAsLong()).isEqualTo(500L);
    }

    @Test
    @DisplayName("should throw exception with null millimeters in BigInteger constructor")
    void givenNullBigInteger_whenCreated_thenThrowsNullPointerException() {
        // when & then
        assertThatThrownBy(() -> new Length(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should handle maximum valid length")
    void givenMaximumValidLength_whenCreated_thenSuccess() {
        // when
        Length length = Length.fromMillimeter(9999L);

        // then
        assertThat(length.inMillimetersAsLong()).isEqualTo(9999L);
    }

    @Test
    @DisplayName("should have correct centimeter conversion")
    void givenVariousLengths_whenConvertedToCentimeters_thenCorrect() {
        // given
        Length length100mm = Length.fromMillimeter(100L);
        Length length1000mm = Length.fromMillimeter(1000L);
        Length length1mm = Length.fromMillimeter(1L);

        // when & then
        assertThat(length100mm.inCentimeters()).isEqualTo(10.0);
        assertThat(length1000mm.inCentimeters()).isEqualTo(100.0);
        assertThat(length1mm.inCentimeters()).isCloseTo(0.1, org.assertj.core.api.Assertions.within(0.01));
    }
}
