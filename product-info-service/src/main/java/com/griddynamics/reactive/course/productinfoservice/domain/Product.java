package com.griddynamics.reactive.course.productinfoservice.domain;

import lombok.Data;

@Data
public class Product {
    private final String productId;
    private final String productCode;
    private final String productName;
    private final double score;
}
