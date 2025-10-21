package com.griddynamics.gridu.pbazhko.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderDto {
    private String userId;
    private String userName;
    private String userPhone;
    private String orderNumber;
    private String productId;
    private String productCode;
    private String productName;
    private double productScore;
}
