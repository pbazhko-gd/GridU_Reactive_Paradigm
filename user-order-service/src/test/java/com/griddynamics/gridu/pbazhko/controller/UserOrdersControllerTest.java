package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.wiremock.spring.EnableWireMock;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.griddynamics.gridu.pbazhko.util.WireMockStubUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_NDJSON;

@ActiveProfiles("wiremock")
@EnableWireMock
@SpringBootTest
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
public class UserOrdersControllerTest {

    @Autowired
    protected WebTestClient webClient;

    private static final String PHONE = "123456789";

    @Test
    void findOrdersByPhone_no_orders() {
        configureEmptyResponseBody(PHONE);
        webClient.get().uri("/users/{phone}/orders", PHONE).exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(APPLICATION_NDJSON)
                .expectBodyList(OrderDto.class).isEqualTo(Collections.emptyList());
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }

    @Test
    void findOrdersByPhone_two_orders() {
        configureTwoOrdersResponseBody(PHONE);
        webClient.get().uri("/users/{phone}/orders", PHONE).exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(APPLICATION_NDJSON)
                .expectBodyList(OrderDto.class)
                .value(dtos -> {
                    assertThat(dtos).hasSize(2);
                    assertThat(dtos.get(0).getPhoneNumber()).isEqualTo(PHONE);
                    assertThat(dtos.get(1).getPhoneNumber()).isEqualTo(PHONE);
                    assertThat(dtos.get(0).getOrderNumber()).isEqualTo("111");
                    assertThat(dtos.get(1).getOrderNumber()).isEqualTo("222");
                    assertThat(dtos.get(0).getProductCode()).isEqualTo("5678");
                    assertThat(dtos.get(1).getProductCode()).isEqualTo("7890");
                });
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }

    @Test
    void findOrdersByPhone_service_unavailable() {
        configureServiceUnavailable(PHONE);
        webClient.get().uri("/users/{phone}/orders", PHONE).exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(APPLICATION_NDJSON)
                .expectBodyList(OrderDto.class).isEqualTo(Collections.emptyList());
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }
}
