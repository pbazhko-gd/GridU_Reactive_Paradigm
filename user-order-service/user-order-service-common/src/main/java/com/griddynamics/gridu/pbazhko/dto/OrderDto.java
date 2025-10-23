package com.griddynamics.gridu.pbazhko.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String phoneNumber;
    private String orderNumber;
    private String productCode;
}
