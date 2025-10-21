package com.griddynamics.gridu.pbazhko.util;

import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

public class WireMockStubUtil {

    public static void configureEmptyResponseBody(String phone) {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + phone))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_NDJSON_VALUE)
                        .withBody("")));
    }

    public static void configureTwoOrdersResponseBody(String phone) {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + phone))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_NDJSON_VALUE)
                        .withBody("""
                                    {"phoneNumber":"123456789", "orderNumber": "111", "productCode": "5678"} \n
                                    {"phoneNumber":"123456789", "orderNumber": "222", "productCode": "7890"}
                                """.stripIndent())
                        .withChunkedDribbleDelay(2, 500)));
    }

    public static void configureServiceUnavailable(String phone) {
        stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + phone))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .withHeader("Content-Type", APPLICATION_NDJSON_VALUE)));
    }
}
