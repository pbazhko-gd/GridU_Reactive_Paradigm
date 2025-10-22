package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import reactor.core.publisher.Flux;

public interface OrderSearchService {

    Flux<OrderDto> findOrdersByPhone(String phoneNumber);
}
