package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.service.OrderSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserOrdersController {

    private final OrderSearchService orderSearchService;

    @GetMapping(value = "/users/{phone}/orders", produces = APPLICATION_NDJSON_VALUE)
    public Flux<OrderDto> findOrdersByPhone(@PathVariable("phone") String phone) {
        return orderSearchService.findOrdersByPhone(phone).log();
    }
}
