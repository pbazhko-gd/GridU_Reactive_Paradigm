package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.mapper.UserOrderMapper;
import com.griddynamics.gridu.pbazhko.service.impl.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserOrdersService implements UserOrdersService {

    private final UserInfoService userInfoService;
    private final ReactiveOrderSearchService orderSearchService;
    private final ReactiveProductInfoService productInfoService;
    private final UserOrderMapper userOrderMapper;

    @Override
    public Flux<UserOrderDto> findAllUserOrders() {
        return userInfoService.findAllUsers()
                .flatMap(this::findOrdersByUserPhone);
    }

    @Override
    public Flux<UserOrderDto> findOrdersByUserId(String userId) {
        return userInfoService.findUserById(userId)
                .flatMapMany(this::findOrdersByUserPhone);
    }

    private Flux<UserOrderDto> findOrdersByUserPhone(UserInfoDto userInfo) {
        return orderSearchService.findOrdersByPhone(userInfo.getPhone())
                .flatMap(order -> findProductsAndMapToUserOrderDto(userInfo, order));
    }

    private Mono<UserOrderDto> findProductsAndMapToUserOrderDto(UserInfoDto userInfo, OrderDto order) {
        return productInfoService.findTheMostRelevantProductByCode(order.getProductCode())
                .map(product -> userOrderMapper.toDto(userInfo, order, product))
                .switchIfEmpty(Mono.just(userOrderMapper.toDto(userInfo, order, null)));
    }
}
