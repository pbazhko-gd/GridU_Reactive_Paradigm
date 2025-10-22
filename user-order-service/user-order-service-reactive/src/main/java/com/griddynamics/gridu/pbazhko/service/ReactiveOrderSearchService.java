package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.griddynamics.gridu.pbazhko.util.MdcHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveOrderSearchService implements OrderSearchService {

    private final WebClient orderSearchWebClient;

    @Value("${order-search-service.timeout}")
    private long orderSearchServiceTimeout;

    @Override
    public Flux<OrderDto> findOrdersByPhone(String phoneNumber) {
        return orderSearchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/order/phone")
                        .queryParam("phoneNumber", phoneNumber)
                        .build()
                ).retrieve()
                .bodyToFlux(OrderDto.class)
                .timeout(Duration.ofMillis(orderSearchServiceTimeout))
                .transform(useMdcForFlux())
                .onErrorResume(withMdcFlux(ex -> {
                    log.error("Cannot retrieve orders by the phone {}: {}", phoneNumber, ex.getMessage());
                    return Flux.empty();
                }))
                .doOnNext(order -> log.info("Found order {} for phoneNumber '{}'", order, phoneNumber))
                .log();
    }
}
