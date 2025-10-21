package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.service.UserOrdersService;
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

    private final UserOrdersService userOrdersService;

    @GetMapping(value = "/users/orders", produces = APPLICATION_NDJSON_VALUE)
    public Flux<UserOrderDto> findAllUserOrders() {
        return userOrdersService.findAllUserOrders().log();
    }

    @GetMapping(value = "/users/{id}/orders", produces = APPLICATION_NDJSON_VALUE)
    public Flux<UserOrderDto> findOrdersByUserId(@PathVariable("id") String id) {
        return userOrdersService.findOrdersByUserId(id).log();
    }
}
