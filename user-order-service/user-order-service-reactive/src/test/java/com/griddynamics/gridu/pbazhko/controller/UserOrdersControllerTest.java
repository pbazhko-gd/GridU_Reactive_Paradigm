package com.griddynamics.gridu.pbazhko.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.model.UserInfo;
import com.griddynamics.gridu.pbazhko.repository.UserInfoRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

@ActiveProfiles("wiremock")
@EnableWireMock
@SpringBootTest
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class UserOrdersControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserInfoRepository userInfoRepository;

    private final static UserInfo TEST_USER = new UserInfo("1", "User 1", "123");

    private final static OrderDto TEST_ORDER_1 = new OrderDto(TEST_USER.getPhone(), "111", "5678");
    private final static OrderDto TEST_ORDER_2 = new OrderDto(TEST_USER.getPhone(), "222", "7890");

    private final static ProductDto TEST_PRODUCT_1 = new ProductDto("pr1", TEST_ORDER_1.getProductCode(), "Prod 1", 2);
    private final static ProductDto TEST_PRODUCT_2 = new ProductDto("pr2", TEST_ORDER_1.getProductCode(), "Prod 2", 5);
    private final static ProductDto TEST_PRODUCT_3 = new ProductDto("pr3", TEST_ORDER_2.getProductCode(), "Prod 3", 7);
    private final static ProductDto TEST_PRODUCT_4 = new ProductDto("pr4", TEST_ORDER_2.getProductCode(), "Prod 4", 3);

    @BeforeEach
    void setup() {
        userInfoRepository.deleteAll().block();
    }

    @Test
    void findAllUserOrders_no_users() {
        webClient.get().uri("/users/orders").exchange()
                .expectStatus().isOk()
                .expectBodyList(UserInfoDto.class).isEqualTo(Collections.emptyList());
        verify(0, anyRequestedFor(urlMatching(".*")));
    }

    @Test
    void findAllUserOrders_one_user_no_orders() {
        userInfoRepository.save(TEST_USER).block();
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_USER.getPhone()))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                        .withBody("")));
        webClient.get().uri("/users/orders").exchange()
                .expectStatus().isOk()
                .expectBodyList(UserOrderDto.class).isEqualTo(Collections.emptyList());
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_USER.getPhone())));
    }

    @Test
    void findAllUserOrders_one_user_two_orders_two_products_per_order() {
        userInfoRepository.save(TEST_USER).block();
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_USER.getPhone()))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                        .withBody("%s\n%s".formatted(toJson(TEST_ORDER_1), toJson(TEST_ORDER_2)))));
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + TEST_ORDER_1.getProductCode()))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody("%s\n%s".formatted(toJson(TEST_PRODUCT_1), toJson(TEST_PRODUCT_2)))));
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + TEST_ORDER_2.getProductCode()))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody("%s\n%s".formatted(toJson(TEST_PRODUCT_3), toJson(TEST_PRODUCT_4)))));
        webClient.get().uri("/users/orders").exchange()
                .expectStatus().isOk()
                .expectBodyList(UserOrderDto.class)
                .value(dtos -> {
                    assertThat(dtos).hasSize(2);

                    assertThat(dtos.get(0).getPhoneNumber()).isEqualTo(TEST_USER.getPhone());
                    assertThat(dtos.get(0).getUserName()).isEqualTo(TEST_USER.getName());
                    assertThat(dtos.get(0).getOrderNumber()).isEqualTo(TEST_ORDER_1.getOrderNumber());
                    assertThat(dtos.get(0).getProductCode()).isEqualTo(TEST_ORDER_1.getProductCode());
                    assertThat(dtos.get(0).getProductId()).isEqualTo(TEST_PRODUCT_2.getProductId());
                    assertThat(dtos.get(0).getProductName()).isEqualTo(TEST_PRODUCT_2.getProductName());

                    assertThat(dtos.get(1).getPhoneNumber()).isEqualTo(TEST_USER.getPhone());
                    assertThat(dtos.get(1).getUserName()).isEqualTo(TEST_USER.getName());
                    assertThat(dtos.get(1).getOrderNumber()).isEqualTo(TEST_ORDER_2.getOrderNumber());
                    assertThat(dtos.get(1).getProductCode()).isEqualTo(TEST_ORDER_2.getProductCode());
                    assertThat(dtos.get(1).getProductId()).isEqualTo(TEST_PRODUCT_3.getProductId());
                    assertThat(dtos.get(1).getProductName()).isEqualTo(TEST_PRODUCT_3.getProductName());
                });
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_USER.getPhone())));
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + TEST_ORDER_1.getProductCode())));
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + TEST_ORDER_2.getProductCode())));
    }

    @Test
    void findAllUserOrders_one_user_order_service_unavailable() {
        userInfoRepository.save(TEST_USER).block();
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_USER.getPhone()))
                .willReturn(aResponse()
                        .withStatus(SERVICE_UNAVAILABLE.value())));
        webClient.get().uri("/users/orders").exchange()
                .expectStatus().isOk()
                .expectBodyList(UserOrderDto.class).isEqualTo(Collections.emptyList());
    }

    @Test
    void findAllUserOrders_one_user_two_orders_product_service_unavailable() {
        userInfoRepository.save(TEST_USER).block();
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_USER.getPhone()))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                        .withBody("%s\n%s".formatted(toJson(TEST_ORDER_1), toJson(TEST_ORDER_2)))));
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=5678"))
                .willReturn(aResponse()
                        .withStatus(SERVICE_UNAVAILABLE.value())));
        webClient.get().uri("/users/orders").exchange()
                .expectStatus().isOk()
                .expectBodyList(UserOrderDto.class)
                .value(dtos -> {
                    assertThat(dtos).hasSize(2);

                    assertThat(dtos.get(0).getPhoneNumber()).isEqualTo(TEST_USER.getPhone());
                    assertThat(dtos.get(0).getUserName()).isEqualTo(TEST_USER.getName());
                    assertThat(dtos.get(0).getOrderNumber()).isEqualTo(TEST_ORDER_1.getOrderNumber());
                    assertThat(dtos.get(0).getProductCode()).isEqualTo(TEST_ORDER_1.getProductCode());
                    assertThat(dtos.get(0).getProductId()).isNull();
                    assertThat(dtos.get(0).getProductName()).isNull();

                    assertThat(dtos.get(1).getPhoneNumber()).isEqualTo(TEST_USER.getPhone());
                    assertThat(dtos.get(1).getUserName()).isEqualTo(TEST_USER.getName());
                    assertThat(dtos.get(1).getOrderNumber()).isEqualTo(TEST_ORDER_2.getOrderNumber());
                    assertThat(dtos.get(1).getProductCode()).isEqualTo(TEST_ORDER_2.getProductCode());
                    assertThat(dtos.get(1).getProductId()).isNull();
                    assertThat(dtos.get(1).getProductName()).isNull();
                });
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + TEST_USER.getPhone())));
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + TEST_ORDER_1.getProductCode())));
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + TEST_ORDER_2.getProductCode())));
    }

    @SneakyThrows
    private String toJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }
}
