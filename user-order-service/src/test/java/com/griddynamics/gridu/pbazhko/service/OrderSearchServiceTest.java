package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.wiremock.spring.EnableWireMock;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.griddynamics.gridu.pbazhko.util.WireMockStubUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        configureEmptyResponseBody(PHONE);
        StepVerifier.create(orderSearchService.findOrdersByPhone(PHONE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }

    @Test
    void findOrdersByPhone_two_orders() {
        configureTwoOrdersResponseBody(PHONE);
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
        configureServiceUnavailable(PHONE);
        StepVerifier.create(orderSearchService.findOrdersByPhone(PHONE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }
}
