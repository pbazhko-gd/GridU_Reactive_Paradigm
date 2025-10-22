package com.griddynamics.gridu.pbazhko.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderDto {
    private String orderNumber;
    private String userName;
    private String phoneNumber;
    private String productCode;
    private String productName;
    private String productId;
}
