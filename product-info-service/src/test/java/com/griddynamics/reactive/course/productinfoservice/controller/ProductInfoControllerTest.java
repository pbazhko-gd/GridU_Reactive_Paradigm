package com.griddynamics.reactive.course.productinfoservice.controller;

import com.griddynamics.reactive.course.productinfoservice.domain.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class ProductInfoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void shouldReturnListOfProduct_WhenGetByNamesCall() {

        webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(15))
                .build()
                .get()
                .uri(builder -> builder
                        .path("/productInfoService/product/names")
                        .queryParam("productCode", "123")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .consumeWith(response -> {
                    Assertions.assertEquals(4, response.getResponseBody().size());
                });
    }
}