package ru.fastdelivery.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.currency.CurrencyPropertiesProvider;
import ru.fastdelivery.domain.delivery.distance.DistanceCalculator;
import ru.fastdelivery.domain.delivery.distance.HaversineDistanceCalculator;
import ru.fastdelivery.properties.provider.VolumePricesRublesProperties;
import ru.fastdelivery.usecase.*;

/**
 * Определение реализаций бинов для всех модулей приложения
 */
@Configuration
@EnableConfigurationProperties({
        TariffCoordinatesProperties.class,
        TariffDistanceProperties.class,
        VolumePricesRublesProperties.class})
public class Beans {

    @Bean
    public CurrencyFactory currencyFactory(CurrencyPropertiesProvider currencyProperties) {
        return new CurrencyFactory(currencyProperties);
    }

    @Bean
    public DistanceCalculator distanceCalculator() {
        return new HaversineDistanceCalculator();
    }

    @Bean
    public DistancePolicy distancePolicy(TariffDistanceProperties props) {
        return new DistancePolicyImpl(props);
    }

    @Bean
    public CoordinatesValidator coordinatesValidator(TariffCoordinatesProperties properties) {
        return new CoordinatesValidatorImpl(properties);
    }

    @Bean
    public TariffCalculateUseCase tariffCalculateUseCase(
            WeightPriceProvider weightPriceProvider,
            VolumePriceProvider volumePriceProvider,
            CoordinatesValidator coordinatesValidator,
            DistanceCalculator distanceCalculator,
            DistancePolicy distancePolicy
    ) {
        return new TariffCalculateUseCase(
                weightPriceProvider,
                volumePriceProvider,
                coordinatesValidator,
                distanceCalculator,
                distancePolicy
        );
    }
}
