package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import reactor.core.publisher.Flux;

public interface UserOrdersService {

    Flux<UserOrderDto> findAllUserOrders();

    Flux<UserOrderDto> findOrdersByUserId(String userId);
}
