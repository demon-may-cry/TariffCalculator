package ru.fastdelivery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 * <p>
 * Настройки допустимых диапазонов координат из конфигурации
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "tariff.coordinates")
public class TariffCoordinatesProperties {
    private BigDecimal minLatitude;
    private BigDecimal maxLatitude;
    private BigDecimal minLongitude;
    private BigDecimal maxLongitude;
}
