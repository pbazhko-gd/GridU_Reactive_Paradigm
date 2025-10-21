package com.griddynamics.gridu.pbazhko.mapper;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserOrderMapper {

    @Mapping(target = "userId", source = "userInfo.id")
    @Mapping(target = "userName", source = "userInfo.name")
    @Mapping(target = "userPhone", source = "userInfo.phone")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productCode", source = "product.productCode")
    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "productScore", source = "product.score")
    UserOrderDto toDto(UserInfoDto userInfo, OrderDto order, ProductDto product);
}
