package ru.fastdelivery.domain.common.dimensions;

import java.math.BigInteger;

import static java.text.MessageFormat.*;

/**
 * @author Дмитрий Ельцов
 * <p>Класс габаритов</p>
 * @param length длина
 * @param width ширина
 * @param height высота
 */
public record OuterDimensions(
        Length length,
        Length width,
        Length height
) {

    private static final Length MAX_LENGTH_DIMENSION = new Length(BigInteger.valueOf(1_500));

    private static final String ALL_SIDES_MUST_BE_SPECIFIED_LENGTH_WIDTH_HEIGHT =
            "All sides must be specified(Length, Width, Height)";
    private static final String EXCEEDS_MAXIMUM_ALLOWED_VALUE = "{0} exceeds maximum allowed value {1} mm";

    public OuterDimensions {
        if (length == null && width == null && height == null) {
            throw new IllegalArgumentException(ALL_SIDES_MUST_BE_SPECIFIED_LENGTH_WIDTH_HEIGHT);
        }
        validateSide("Length", length);
        validateSide("Width", width);
        validateSide("Height", height);
    }

    /**
     * Проверяет отдельную сторону габаритов на превышение максимально допустимого значения
     * <p>
     * Если сторона не указана ({@code null}), проверка не выполняется
     * @param name имя стороны (length, width, height) для сообщения об ошибке
     * @param side значение стороны для проверки
     * @throws IllegalArgumentException если сторона превышает допустимый максимум
     */
    private void validateSide(String name, Length side) {
        if (side == null) return;
        if (side.isLongerThan(MAX_LENGTH_DIMENSION)) throw new IllegalArgumentException(
                    format(EXCEEDS_MAXIMUM_ALLOWED_VALUE, name, MAX_LENGTH_DIMENSION.millimeters())
        );
    }

    /**
     * Возвращает габариты с нормализованными сторонами
     * <p>
     * Каждая указанная сторона округляется вверх до ближайшего значения,
     * кратного 50 миллиметрам
     * <p>
     * Неуказанные стороны остаются {@code null}
     * @return {@link OuterDimensions} новые нормализованные габариты
     */
    public OuterDimensions normalized() {
        return new OuterDimensions(
                length != null ? length.normalizeBy50() : null,
                width != null ? width.normalizeBy50() : null,
                height != null ? height.normalizeBy50() : null
        );
    }
}
