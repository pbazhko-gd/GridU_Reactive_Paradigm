package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.mapper.UserOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NonReactiveUserOrdersService implements UserOrdersService<List<UserOrderDto>> {

    private final UserInfoService<List<UserInfoDto>, UserInfoDto> userInfoService;
    private final OrderSearchService<List<OrderDto>> orderSearchService;
    private final ProductInfoService<ProductDto> productInfoService;
    private final UserOrderMapper userOrderMapper;

    @Override
    public List<UserOrderDto> findAllUserOrders() {
        return userInfoService.findAllUsers().stream()
            .map(this::findOrdersByUserPhone)
            .flatMap(Collection::stream)
            .toList();
    }

    @Override
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
