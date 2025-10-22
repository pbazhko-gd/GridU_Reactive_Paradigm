package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NonReactiveProductInfoService implements ProductInfoService<ProductDto> {

    private final WebClient productInfoWebClient;

    @Value("${product-info-service.timeout}")
    private long productInfoServiceTimeout;

    @Override
    public ProductDto findTheMostRelevantProductByCode(String productCode) {
        try {
            var products = Optional.ofNullable(
                productInfoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/product/names")
                        .queryParam("productCode", productCode)
                        .build()
                    ).retrieve()
                    .bodyToFlux(ProductDto.class)
                    .timeout(Duration.ofMillis(productInfoServiceTimeout))
                    .collectList()
                    .block()
            ).orElse(Collections.emptyList());
            log.info("Found products info {} for code {}", products, productCode);

            var mostRelevantProduct = products.stream().max(Comparator.comparing(ProductDto::getScore)).orElse(null);

            log.info("Detect the most relevant product with the highest score: {}", mostRelevantProduct);
            return mostRelevantProduct;
        } catch (Exception ex) {
            log.error("Cannot retrieve products by the code {}: {}", productCode, ex.getMessage());
            return null;
        }
    }
}
