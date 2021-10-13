package com.griddynamics.reactive.course.ordersearchservice.controller;

import com.griddynamics.reactive.course.ordersearchservice.domain.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock
@AutoConfigureWebTestClient
@DirtiesContext
public class OrderSearchControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void shouldReturnOrdersWhenGetOrdersByPhoneCalls() {

        StepVerifier.create(webTestClient.get()
                        .uri(builder -> builder
                                .path("/orderSearchService/order/phone")
                                .queryParam("phoneNumber", "123")
                                .build())
                        .accept(MediaType.APPLICATION_NDJSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_NDJSON_VALUE)
                        .returnResult(Order.class)
                        .getResponseBody())
                .expectNextCount(4)
                .verifyComplete();
    }

}