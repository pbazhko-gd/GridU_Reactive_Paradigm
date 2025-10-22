package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import reactor.core.publisher.Mono;

public interface ProductInfoService {

    Mono<ProductDto> findTheMostRelevantProductByCode(String productCode);
}
