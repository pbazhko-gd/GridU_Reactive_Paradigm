package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("wiremock")
@EnableWireMock
@SpringBootTest
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class ProductInfoServiceTest {

    @Autowired
    private ProductInfoService productInfoService;

    private static final String PRODUCT_CODE = "1234";

    @Test
    void findProductsByCode_no_products() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody("[]")));
        StepVerifier.create(productInfoService.findProductsByCode(PRODUCT_CODE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }

    @Test
    void findProductsByCode_two_products() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody("""
                                [{"productId":"111","productCode":"1234","productName":"Name1","score":2},
                                 {"productId":"222","productCode":"1234","productName":"Name2","score":5}]
                                """.stripIndent())));
        StepVerifier.create(productInfoService.findProductsByCode(PRODUCT_CODE))
                .assertNext(dto -> {
                    assertEquals("111", dto.getProductId());
                    assertEquals("1234", dto.getProductCode());
                    assertEquals("Name1", dto.getProductName());
                    assertEquals(2, dto.getScore());
                })
                .assertNext(dto -> {
                    assertEquals("222", dto.getProductId());
                    assertEquals("1234", dto.getProductCode());
                    assertEquals("Name2", dto.getProductName());
                    assertEquals(5, dto.getScore());
                })
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }

    @Test
    void findProductsByCode_service_unavailable() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
                .willReturn(aResponse()
                        .withStatus(SERVICE_UNAVAILABLE.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)));
        StepVerifier.create(productInfoService.findProductsByCode(PRODUCT_CODE))
                .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }
}
