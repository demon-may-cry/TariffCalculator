package ru.fastdelivery.properties.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.usecase.VolumePriceProvider;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 * <p>
 * Конфигурационная реализация провайдера цен для расчёта стоимости доставки по объёму
 */
@ConfigurationProperties("cost.volume")
@Getter
@Setter
public class VolumePricesRublesProperties implements VolumePriceProvider {

    private BigDecimal perCubicMeter;

    private BigDecimal minimal;

    @Autowired
    private CurrencyFactory currencyFactory;

    /**
     * Возвращает стоимость доставки за один кубический метр груза
     * @return {@link Price} цена за кубический метр
     */
    @Override
    public Price costPerCubicMeter() {
        return new Price(perCubicMeter, currencyFactory.create("RUB"));
    }

    /**
     * Возвращает минимальную стоимость доставки по объёму
     * @return {@link Price} минимальная цена доставки по объёму
     */
    @Override
    public Price minimalPrice() {
        return new Price(minimal, currencyFactory.create("RUB"));
    }
}
