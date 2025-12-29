package ru.fastdelivery.domain.common.dimensions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import static java.text.MessageFormat.*;
import static java.util.logging.Level.*;

/**
 * @author Дмитрий Ельцов
 * Класс объёма груза в кубических метрах
 * @param cubicMeters единица измерения в кубических метрах
 */
public record Volume(BigDecimal cubicMeters) {

    private static final Logger LOG = Logger.getLogger(Volume.class.getName());
    private static final BigDecimal MM3_IN_M3 = BigDecimal.valueOf(1_000_000_000L);
    private static final String VOLUME_EQUALS = "Volume is equal to {0} m3";
    private static final String DIMENSIONS_ARE_ROUNDED = "Dimensions are rounded: " +
            "length = {0}, " +
            "Height = {1}, " +
            "Width = {2}";
    private static final String OUTER_DIMENSIONS_CANNOT_BE_NULL = "OuterDimensions cannot be null";
    private static final String LENGTH_WIDTH_AND_HEIGHT_MUST_BE_SPECIFIED_TO_CALCULATE_VOLUME =
            "Length, width and height must be specified to calculate volume";

    /**
     * Рассчитывает объём груза на основе габаритов упаковки
     * <p>
     * Перед вычислением габариты нормализуются (округляются вверх до значений,
     * кратных 50 миллиметрам)
     * <p>
     * Для расчёта объёма должны быть указаны длина,
     * ширина и высота упаковки.
     * @param dimensions габариты упаковки
     * @return {@link Volume} рассчитанный объём в кубических метрах
     * @throws IllegalArgumentException если габариты не заданы или указаны не полностью
     */
    public static Volume from(OuterDimensions dimensions) {
        if (dimensions == null) throw new IllegalArgumentException(OUTER_DIMENSIONS_CANNOT_BE_NULL);
        if (dimensions.length() == null ||
                dimensions.width() == null ||
                dimensions.height() == null) throw new IllegalArgumentException(
                LENGTH_WIDTH_AND_HEIGHT_MUST_BE_SPECIFIED_TO_CALCULATE_VOLUME
            );

        OuterDimensions d = dimensions.normalized();
        LOG.log(INFO, format(
                DIMENSIONS_ARE_ROUNDED,
                d.length().millimeters(),
                d.width().millimeters(),
                d.height().millimeters()
        ));

        BigDecimal mm3 =
                new BigDecimal(d.length().millimeters())
                        .multiply(new BigDecimal(d.width().millimeters()))
                        .multiply(new BigDecimal(d.height().millimeters()));

        BigDecimal m3 = mm3.divide(MM3_IN_M3, 4, RoundingMode.HALF_UP);

        LOG.log(INFO, format(VOLUME_EQUALS, m3));

        return new Volume(m3);
    }
}
