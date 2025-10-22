package com.griddynamics.gridu.pbazhko.service;

public interface UserOrdersService<T> {

    T findAllUserOrders();

    T findOrdersByUserId(String userId);
}
