package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.mapper.UserOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOrdersService {

    private final UserInfoService userInfoService;
    private final OrderSearchService orderSearchService;
    private final ProductInfoService productInfoService;
    private final UserOrderMapper userOrderMapper;

    public List<UserOrderDto> findAllUserOrders() {
        return userInfoService.findAllUsers().stream()
            .map(this::findOrdersByUserPhone)
            .flatMap(Collection::stream)
            .toList();
    }

    public List<UserOrderDto> findOrdersByUserId(String userId) {
        return findOrdersByUserPhone(userInfoService.findUserById(userId));
    }

    private List<UserOrderDto> findOrdersByUserPhone(UserInfoDto userInfo) {
        return orderSearchService.findOrdersByPhone(userInfo.getPhone()).stream()
            .map(order -> findProductsAndMapToUserOrderDto(userInfo, order))
            .toList();
    }

    private UserOrderDto findProductsAndMapToUserOrderDto(UserInfoDto userInfo, OrderDto order) {
        var product = productInfoService.findTheMostRelevantProductByCode(order.getProductCode());
        return userOrderMapper.toDto(userInfo, order, product);
    }
}
