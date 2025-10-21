package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.BiFunction;

import static com.griddynamics.gridu.pbazhko.util.MdcHelper.applyContextForMdc;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final WebClient productInfoWebClient;

    @Value("${product-info-service.timeout}")
    private long productInfoServiceTimeout;

    public Mono<ProductDto> findTheMostRelevantProductByCode(String productCode) {
        return productInfoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/product/names")
                        .queryParam("productCode", productCode)
                        .build()
                ).retrieve()
                .bodyToFlux(ProductDto.class)
                .timeout(Duration.ofMillis(productInfoServiceTimeout))
                .onErrorResume(throwable -> {
                    log.error("Cannot retrieve products by the code {}: {}", productCode, throwable.getMessage());
                    return Flux.empty();
                })
                .transform(applyContextForMdc())
                .doOnNext(product -> log.info("Found product info {} for code {}", product, productCode))
                .reduce(chooseTheMostRelevantProduct())
                .transform(applyContextForMdc())
                .doOnNext(product -> log.info("Detect the most relevant product with the highest score: {}", product))
                .log();
    }

    private static BiFunction<ProductDto, ProductDto, ProductDto> chooseTheMostRelevantProduct() {
        return (a, b) -> a.getScore() > b.getScore() ? a : b;
    }
}
