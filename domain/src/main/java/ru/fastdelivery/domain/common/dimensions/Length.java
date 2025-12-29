package ru.fastdelivery.domain.common.dimensions;

import java.math.BigInteger;

/**
 * @author Дмитрий Ельцов
 * <p>Класс длины</p>
 * @param millimeters значение длины в миллиметрах
 */
public record Length(BigInteger millimeters) {

    private static final String DIMENSIONS_CANNOT_BE_BELOW_ZERO = "Dimensions cannot be below zero!";

    public Length {
        if (isLessThanZero(millimeters)) {
            throw new IllegalArgumentException(DIMENSIONS_CANNOT_BE_BELOW_ZERO);
        }
    }

    /**
     * Проверяет, является ли переданное значение отрицательным.
     *
     * @param price числовое значение для проверки
     * @return {@code true}, если значение меньше нуля
     */
    private static boolean isLessThanZero(BigInteger price) {
        return BigInteger.ZERO.compareTo(price) > 0;
    }

    /**
     * Создаёт объект {@link Length} из значения в миллиметрах
     * @param millimeters длина в миллиметрах
     * @return {@link Length}
     */
    public static Length fromMillimeter(long millimeters) {
        return new Length(BigInteger.valueOf(millimeters));
    }

    /**
     * Проверяет, больше ли текущая длина указанной
     * @param other длина для сравнения
     * @return {@code true}, если текущая длина больше указанной
     */
    public boolean isLongerThan(Length other) {
        return millimeters.compareTo(other.millimeters) > 0;
    }

    /**
     * Округляет длину вверх до ближайшего значения, кратного 50 миллиметрам
     * @return {@link Length} нормализованная длина
     */
    public Length normalizeBy50() {
        BigInteger step = BigInteger.valueOf(50);
        BigInteger[] divRem = millimeters.divideAndRemainder(step);
        if (divRem[1].equals(BigInteger.ZERO)) return this;
        return new Length(divRem[0].add(BigInteger.ONE).multiply(step));
    }
}
