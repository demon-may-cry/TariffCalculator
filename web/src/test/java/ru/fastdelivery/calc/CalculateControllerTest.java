package ru.fastdelivery.calc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.fastdelivery.ControllerTest;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.request.DepartureRequest;
import ru.fastdelivery.presentation.api.request.DestinationRequest;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CalculateControllerTest extends ControllerTest {

    final String baseCalculateApi = "/api/v1/calculate/";
    @MockBean
    TariffCalculateUseCase useCase;
    @MockBean
    CurrencyFactory currencyFactory;

    @Test
    @DisplayName("Валидные данные для расчета стоимость -> Ответ 200")
    void whenValidInputData_thenReturn200() {
        // given
        CalculatePackagesRequest request = new CalculatePackagesRequest(
                List.of(
                        new CargoPackage(
                                BigInteger.valueOf(4564),
                                BigInteger.valueOf(345),
                                BigInteger.valueOf(589),
                                BigInteger.valueOf(234)
                        )
                ),
                "RUB",
                new DestinationRequest(
                        BigDecimal.valueOf(47.222109),
                        BigDecimal.valueOf(39.718813)
                ),
                new DepartureRequest(
                        BigDecimal.valueOf(55.030204),
                        BigDecimal.valueOf(82.920430)
                )
        );

        Currency rub = new CurrencyFactory(code -> true).create("RUB");

        when(currencyFactory.create("RUB")).thenReturn(rub);
        when(useCase.calc(any()))
                .thenReturn(new Price(BigDecimal.valueOf(1643.69), rub));
        when(useCase.minimalPrice())
                .thenReturn(new Price(BigDecimal.valueOf(500), rub));

        // when
        ResponseEntity<CalculatePackagesResponse> response =
                restTemplate.postForEntity(
                        baseCalculateApi,
                        request,
                        CalculatePackagesResponse.class
                );

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("RUB", response.getBody().currencyCode());
        assertEquals(
                BigDecimal.valueOf(1643.69).setScale(2, RoundingMode.CEILING),
                response.getBody().totalPrice()
        );
    }

    @Test
    @DisplayName("Список упаковок == null -> Ответ 400")
    void whenEmptyListPackages_thenReturn400() {
        var request = new CalculatePackagesRequest(null, "RUB", null, null);

        ResponseEntity<String> response = restTemplate.postForEntity(baseCalculateApi, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
