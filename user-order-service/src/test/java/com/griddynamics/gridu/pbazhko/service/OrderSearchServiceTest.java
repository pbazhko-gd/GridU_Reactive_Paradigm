package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.wiremock.spring.EnableWireMock;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

@ActiveProfiles("wiremock")
@EnableWireMock
@SpringBootTest
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class OrderSearchServiceTest {

    @Autowired
    private OrderSearchService orderSearchService;

    private static final String PHONE = "123456789";

    @Test
    void findOrdersByPhone_no_orders() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_NDJSON_VALUE)
                        .withBody("")));
        StepVerifier.create(orderSearchService.findOrdersByPhone(PHONE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }

    @Test
    void findOrdersByPhone_two_orders() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_NDJSON_VALUE)
                        .withBody("""
                                    {"phoneNumber":"123456789", "orderNumber": "111", "productCode": "5678"} \n
                                    {"phoneNumber":"123456789", "orderNumber": "222", "productCode": "7890"}
                                """.stripIndent())
                        .withChunkedDribbleDelay(2, 500)));
        StepVerifier.create(orderSearchService.findOrdersByPhone(PHONE))
                .assertNext(dto -> {
                    assertEquals("123456789", dto.getPhoneNumber());
                    assertEquals("111", dto.getOrderNumber());
                    assertEquals("5678", dto.getProductCode());
                })
                .assertNext(dto -> {
                    assertEquals("123456789", dto.getPhoneNumber());
                    assertEquals("222", dto.getOrderNumber());
                    assertEquals("7890", dto.getProductCode());
                })
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }

    @Test
    void findOrdersByPhone_service_unavailable() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .withHeader("Content-Type", APPLICATION_NDJSON_VALUE)));
        StepVerifier.create(orderSearchService.findOrdersByPhone(PHONE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }
}
