package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSearchService {

    private final WebClient orderSearchWebClient;

    @Value("${order-search-service.timeout}")
    private long orderSearchServiceTimeout;

    public Flux<OrderDto> findOrdersByPhone(String phoneNumber) {
        return orderSearchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/order/phone")
                        .queryParam("phoneNumber", phoneNumber)
                        .build()
                ).retrieve()
                .bodyToFlux(OrderDto.class)
                .timeout(Duration.ofMillis(orderSearchServiceTimeout))
                .onErrorResume(throwable -> {
                    log.error("Cannot retrieve orders by the phone {}: {}", phoneNumber, throwable.getMessage());
                    return Flux.empty();
                })
                .log();
    }
}
