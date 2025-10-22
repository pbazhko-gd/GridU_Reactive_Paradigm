package com.griddynamics.gridu.pbazhko.service;

public interface OrderSearchService<T> {

    T findOrdersByPhone(String phoneNumber);
}
