package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final WebClient productInfoWebClient;

    public Flux<ProductDto> findProductsByCode(String productCode) {
        return productInfoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/product/names")
                        .queryParam("productCode", productCode)
                        .build()
                ).retrieve()
                .bodyToFlux(ProductDto.class)
                .onErrorResume(throwable -> {
                    log.error("Cannot retrieve products by the code {}: {}", productCode, throwable.getMessage());
                    return Flux.empty();
                })
                .log();
    }
}
