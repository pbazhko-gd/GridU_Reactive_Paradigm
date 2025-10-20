package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class OrderSearchService {

    private final WebClient orderSearchWebClient;

    public Flux<OrderDto> findOrdersByPhone(String phoneNumber) {
        return orderSearchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/order/phone")
                        .queryParam("phoneNumber", phoneNumber)
                        .build()
                ).retrieve()
                .bodyToFlux(OrderDto.class)
                .onErrorResume(e -> Flux.empty())
                .log();
    }
}
