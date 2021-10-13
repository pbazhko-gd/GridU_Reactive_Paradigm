package com.griddynamics.reactive.course.ordersearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
   private String phoneNumber;
   private String orderNumber;
   private String productCode;
}
