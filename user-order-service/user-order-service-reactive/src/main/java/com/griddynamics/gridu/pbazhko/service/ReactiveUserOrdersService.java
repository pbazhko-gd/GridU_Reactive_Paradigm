package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.mapper.UserOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserOrdersService {

    private final ReactiveUserInfoService reactiveUserInfoService;
    private final ReactiveOrderSearchService reactiveOrderSearchService;
    private final ReactiveProductInfoService reactiveProductInfoService;
    private final UserOrderMapper userOrderMapper;

    public Flux<UserOrderDto> findAllUserOrders() {
        return reactiveUserInfoService.findAllUsers()
            .flatMap(this::findOrdersByUserPhone);
    }

    public Flux<UserOrderDto> findOrdersByUserId(String userId) {
        return reactiveUserInfoService.findUserById(userId)
            .flatMapMany(this::findOrdersByUserPhone);
    }

    private Flux<UserOrderDto> findOrdersByUserPhone(UserInfoDto userInfo) {
        return reactiveOrderSearchService.findOrdersByPhone(userInfo.getPhone())
            .flatMap(order -> findProductsAndMapToUserOrderDto(userInfo, order));
    }

    private Mono<UserOrderDto> findProductsAndMapToUserOrderDto(UserInfoDto userInfo, OrderDto order) {
        return reactiveProductInfoService.findTheMostRelevantProductByCode(order.getProductCode())
            .map(product -> userOrderMapper.toDto(userInfo, order, product))
            .switchIfEmpty(Mono.just(userOrderMapper.toDto(userInfo, order, null)));
    }
}
