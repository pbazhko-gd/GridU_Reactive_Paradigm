package com.griddynamics.gridu.pbazhko.service;

public interface ProductInfoService<T> {

    T findTheMostRelevantProductByCode(String productCode);
}
