package ru.fastdelivery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 * <p>Настройка минимального расстояния из конфигурации
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "tariff.distance")
public class TariffDistanceProperties {
    private BigDecimal minimalKm;
}
