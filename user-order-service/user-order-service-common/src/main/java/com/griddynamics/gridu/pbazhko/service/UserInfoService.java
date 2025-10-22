package com.griddynamics.gridu.pbazhko.service;

public interface UserInfoService<T, P> {

    T findAllUsers();

    P findUserById(String userId);
}
