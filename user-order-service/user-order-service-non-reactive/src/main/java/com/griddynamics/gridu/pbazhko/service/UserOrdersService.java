package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.mapper.UserOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserOrdersService {

    private final UserInfoService userInfoService;
    private final OrderSearchService orderSearchService;
    private final ProductInfoService productInfoService;
    private final UserOrderMapper userOrderMapper;

    public List<UserOrderDto> findAllUserOrders() {
        return userInfoService.findAllUsers().stream()
            .map(this::findOrdersForUser)
            .flatMap(Collection::stream)
            .toList();
    }

    public List<UserOrderDto> findOrdersByUserId(String userId) {
        var user = userInfoService.findUserById(userId);
        return findOrdersForUser(user);
    }

    private List<UserOrderDto> findOrdersForUser(UserInfoDto userInfo) {
        var orders = orderSearchService.findOrdersByPhone(userInfo.getPhone());
        var futures = orders.stream()
            .map(order -> CompletableFuture.supplyAsync(() -> getUserOrder(userInfo, order)))
            .toList();

        var allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
            .map(CompletableFuture::join)
            .toList()
        ).join();
    }

    private UserOrderDto getUserOrder(UserInfoDto userInfo, OrderDto order) {
        var product = productInfoService.findTheMostRelevantProductByCode(order.getProductCode());
        return userOrderMapper.toDto(userInfo, order, product);
    }
}
