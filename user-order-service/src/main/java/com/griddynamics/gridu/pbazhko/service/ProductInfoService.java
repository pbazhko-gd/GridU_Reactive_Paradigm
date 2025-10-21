package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
                .onErrorResume(e -> Flux.empty())
                .log();
    }
}
