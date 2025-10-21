package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserOrdersService {

    public Flux<UserOrderDto> findOrdersByUserId(String userId) {
        return Flux.empty();
    }
}
