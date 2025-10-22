package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.tests.config.MongoDBTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.wiremock.spring.EnableWireMock;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.griddynamics.gridu.pbazhko.tests.util.TestUtils.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

@ActiveProfiles("wiremock")
@EnableWireMock
@SpringBootTest
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class ReactiveOrderSearchServiceTest {

    @Autowired
    private OrderSearchService<Flux<OrderDto>> orderSearchService;

    @Value("${order-search-service.timeout}")
    private long orderSearchServiceTimeout;

    private static final String TEST_PHONE = "123456789";

    private static final  OrderDto TEST_ORDER_1 = new OrderDto(TEST_PHONE, "111", "5678");
    private static final OrderDto TEST_ORDER_2 = new OrderDto(TEST_PHONE, "222", "7890");

    @Test
    void findOrdersByPhone_no_orders() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                .withBody("")));
        StepVerifier.create(orderSearchService.findOrdersByPhone(TEST_PHONE))
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE)));
    }

    @Test
    void findOrdersByPhone_two_orders() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                .withBody("%s\n%s".formatted(toJson(TEST_ORDER_1), toJson(TEST_ORDER_2)))));
        StepVerifier.create(orderSearchService.findOrdersByPhone(TEST_PHONE))
            .assertNext(dto -> {
                assertEquals(TEST_ORDER_1.getPhoneNumber(), dto.getPhoneNumber());
                assertEquals(TEST_ORDER_1.getOrderNumber(), dto.getOrderNumber());
                assertEquals(TEST_ORDER_1.getProductCode(), dto.getProductCode());
            })
            .assertNext(dto -> {
                assertEquals(TEST_ORDER_2.getPhoneNumber(), dto.getPhoneNumber());
                assertEquals(TEST_ORDER_2.getOrderNumber(), dto.getOrderNumber());
                assertEquals(TEST_ORDER_2.getProductCode(), dto.getProductCode());
            })
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE)));
    }

    @Test
    void findOrdersByPhone_service_unavailable() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE))
            .willReturn(aResponse()
                .withStatus(SERVICE_UNAVAILABLE.value())
                .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)));
        StepVerifier.create(orderSearchService.findOrdersByPhone(TEST_PHONE))
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE)));
    }

    @Test
    void findOrdersByPhone_request_timeout() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE))
            .willReturn(aResponse()
                .withFixedDelay((int) orderSearchServiceTimeout + 1000)
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                .withBody("%s\n%s".formatted(toJson(TEST_ORDER_1), toJson(TEST_ORDER_2)))));
        StepVerifier.create(orderSearchService.findOrdersByPhone(TEST_PHONE))
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_PHONE)));
    }
}
