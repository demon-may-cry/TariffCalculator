package ru.fastdelivery.properties_provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.fastdelivery.config.CoordinatesValidatorImpl;
import ru.fastdelivery.config.TariffCoordinatesProperties;
import ru.fastdelivery.domain.delivery.coordinates.Coordinates;
import ru.fastdelivery.usecase.CoordinatesValidator;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Дмитрий Ельцов
 */
public class CoordinatesValidatorTest {

    private CoordinatesValidator validator;

    @BeforeEach
    void setUp() {
        TariffCoordinatesProperties properties = new TariffCoordinatesProperties();
        properties.setMinLatitude(BigDecimal.valueOf(45));
        properties.setMaxLatitude(BigDecimal.valueOf(65));
        properties.setMinLongitude(BigDecimal.valueOf(30));
        properties.setMaxLongitude(BigDecimal.valueOf(96));

        validator = new CoordinatesValidatorImpl(properties);
    }

    @ParameterizedTest(name = "{index} → lat={0}, lon={1}")
    @MethodSource("invalidCoordinates")
    @DisplayName("Невалидные координаты — выбрасывается исключение")
    void shouldFailForInvalidCoordinates(BigDecimal latitude, BigDecimal longitude) {

        Coordinates coordinates = new Coordinates(latitude, longitude);

        assertThatThrownBy(() -> validator.validate(coordinates))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> invalidCoordinates() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(44), BigDecimal.valueOf(40)), // latitude < min
                Arguments.of(BigDecimal.valueOf(66), BigDecimal.valueOf(40)), // latitude > max
                Arguments.of(BigDecimal.valueOf(55), BigDecimal.valueOf(29)), // longitude < min
                Arguments.of(BigDecimal.valueOf(55), BigDecimal.valueOf(97))  // longitude > max
        );
    }

    @ParameterizedTest(name = "{index} → lat={0}, lon={1}")
    @MethodSource("validCoordinates")
    @DisplayName("Валидные координаты — валидация проходит")
    void shouldPassForValidCoordinates(BigDecimal latitude, BigDecimal longitude) {

        Coordinates coordinates = new Coordinates(latitude, longitude);

        assertThatCode(() -> validator.validate(coordinates))
                .doesNotThrowAnyException();
    }

    static Stream<Arguments> validCoordinates() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(45), BigDecimal.valueOf(30)), // min границы
                Arguments.of(BigDecimal.valueOf(65), BigDecimal.valueOf(96)), // max границы
                Arguments.of(BigDecimal.valueOf(55), BigDecimal.valueOf(40)), // внутри диапазона
                Arguments.of(BigDecimal.valueOf(50), BigDecimal.valueOf(60))
        );
    }
}
