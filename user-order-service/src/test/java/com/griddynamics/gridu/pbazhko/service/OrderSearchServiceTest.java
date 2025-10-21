package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.wiremock.spring.EnableWireMock;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

@ActiveProfiles("wiremock")
@EnableWireMock
@SpringBootTest
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class OrderSearchServiceTest {

    @Autowired
    private OrderSearchService orderSearchService;

    @Value("${order-search-service.timeout}")
    private long orderSearchServiceTimeout;

    private static final String PHONE = "123456789";

    @Test
    void findOrdersByPhone_no_orders() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
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
                        .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                        .withBody("""
                                    {"phoneNumber":"123456789", "orderNumber": "111", "productCode": "5678"} \n
                                    {"phoneNumber":"123456789", "orderNumber": "222", "productCode": "7890"}
                                """.stripIndent())));
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
                        .withStatus(SERVICE_UNAVAILABLE.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)));
        StepVerifier.create(orderSearchService.findOrdersByPhone(PHONE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }

    @Test
    void findOrdersByPhone_request_timeout() {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE))
                .willReturn(aResponse()
                        .withFixedDelay((int) orderSearchServiceTimeout + 1000)
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
                        .withBody("""
                                    {"phoneNumber":"123456789", "orderNumber": "111", "productCode": "5678"} \n
                                    {"phoneNumber":"123456789", "orderNumber": "222", "productCode": "7890"}
                                """.stripIndent())));
        StepVerifier.create(orderSearchService.findOrdersByPhone(PHONE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE)));
    }
}
