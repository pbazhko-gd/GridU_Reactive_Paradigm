package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.service.UserOrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserOrdersController {

    private final UserOrdersService userOrdersService;

    @GetMapping(value = "/users/orders", produces = APPLICATION_JSON_VALUE)
    public List<UserOrderDto> findAllUserOrders() {
        return userOrdersService.findAllUserOrders();
    }

    @GetMapping(value = "/users/{id}/orders", produces = APPLICATION_JSON_VALUE)
    public List<UserOrderDto> findOrdersByUserId(@PathVariable("id") String id) {
        return userOrdersService.findOrdersByUserId(id);
    }
}
