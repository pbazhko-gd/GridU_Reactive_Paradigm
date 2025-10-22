package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.wiremock.spring.EnableWireMock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("wiremock")
@EnableWireMock
@SpringBootTest
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class ReactiveProductInfoServiceTest {

    @Autowired
    private ProductInfoService<Mono<ProductDto>> productInfoService;

    @Value("${product-info-service.timeout}")
    private long productInfoServiceTimeout;

    private static final String PRODUCT_CODE = "1234";

    @Test
    void findTheMostRelevantProductByCode_no_products() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody("[]")));
        StepVerifier.create(productInfoService.findTheMostRelevantProductByCode(PRODUCT_CODE))
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }

    @Test
    void findTheMostRelevantProductByCode_one_product() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody("""
                    [{"productId":"111","productCode":"1234","productName":"Name1","score":2}]
                    """.stripIndent())));
        StepVerifier.create(productInfoService.findTheMostRelevantProductByCode(PRODUCT_CODE))
            .assertNext(dto -> {
                assertEquals("111", dto.getProductId());
                assertEquals("1234", dto.getProductCode());
                assertEquals("Name1", dto.getProductName());
                assertEquals(2, dto.getScore());
            })
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }

    @Test
    void findTheMostRelevantProductByCode_two_products() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody("""
                    [{"productId":"111","productCode":"1234","productName":"Name1","score":2},
                     {"productId":"222","productCode":"1234","productName":"Name2","score":5}]
                    """.stripIndent())));
        StepVerifier.create(productInfoService.findTheMostRelevantProductByCode(PRODUCT_CODE))
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
    void findTheMostRelevantProductByCode_code_not_found() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
            .willReturn(aResponse()
                .withStatus(NOT_FOUND.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));
        StepVerifier.create(productInfoService.findTheMostRelevantProductByCode(PRODUCT_CODE))
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }

    @Test
    void findTheMostRelevantProductByCode_service_unavailable() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
            .willReturn(aResponse()
                .withStatus(SERVICE_UNAVAILABLE.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));
        StepVerifier.create(productInfoService.findTheMostRelevantProductByCode(PRODUCT_CODE))
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }

    @Test
    void findTheMostRelevantProductByCode_request_timeout() {
        stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
            .willReturn(aResponse()
                .withFixedDelay((int) productInfoServiceTimeout + 1000)
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody("""
                    [{"productId":"111","productCode":"1234","productName":"Name1","score":2},
                     {"productId":"222","productCode":"1234","productName":"Name2","score":5}]
                    """.stripIndent())));
        StepVerifier.create(productInfoService.findTheMostRelevantProductByCode(PRODUCT_CODE))
            .verifyComplete();
        verify(1, getRequestedFor(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE)));
    }
}
