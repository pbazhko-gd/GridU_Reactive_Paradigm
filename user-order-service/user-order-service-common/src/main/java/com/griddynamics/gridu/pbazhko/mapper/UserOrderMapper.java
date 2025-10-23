package com.griddynamics.gridu.pbazhko.mapper;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.ProductDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserOrderMapper {

    @Mapping(target = "userName", source = "userInfo.name")
    @Mapping(target = "phoneNumber", source = "userInfo.phone")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "productCode", source = "order.productCode")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.productName")
    UserOrderDto toDto(UserInfoDto userInfo, OrderDto order, ProductDto product);
}
