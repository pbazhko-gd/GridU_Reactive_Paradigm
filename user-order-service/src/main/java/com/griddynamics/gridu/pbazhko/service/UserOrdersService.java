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
public class UserOrdersService {

    private final UserInfoService userInfoService;
    private final OrderSearchService orderSearchService;
    private final ProductInfoService productInfoService;
    private final UserOrderMapper userOrderMapper;

    public Flux<UserOrderDto> findAllUserOrders() {
        return userInfoService.findAllUsers()
                .flatMap(this::findOrdersByUserPhone);
    }

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
                .map(product -> userOrderMapper.toDto(userInfo, order, product));
    }
}
