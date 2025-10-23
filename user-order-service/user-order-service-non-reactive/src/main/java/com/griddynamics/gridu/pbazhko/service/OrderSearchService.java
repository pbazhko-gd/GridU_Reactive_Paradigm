package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSearchService {

    private final WebClient orderSearchWebClient;

    @Value("${order-search-service.timeout}")
    private long orderSearchServiceTimeout;

    public List<OrderDto> findOrdersByPhone(String phoneNumber) {
        try {
            return Optional.ofNullable(
                    orderSearchWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                            .path("/order/phone")
                            .queryParam("phoneNumber", phoneNumber)
                            .build()
                        ).retrieve()
                        .bodyToFlux(OrderDto.class)
                        .timeout(Duration.ofMillis(orderSearchServiceTimeout))
                        .collectList()
                        .block()
                ).orElse(Collections.emptyList())
                .stream()
                .peek(order -> log.info("Found order {} for phoneNumber '{}'", order, phoneNumber))
                .toList();
        } catch (Exception ex) {
            log.error("Cannot retrieve orders by the phone {}: {}", phoneNumber, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
