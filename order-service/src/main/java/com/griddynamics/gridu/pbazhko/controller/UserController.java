package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserInfoService userInfoService;

    @GetMapping("/users")
    public Flux<UserInfoDto> findAllUsers() {
        return userInfoService.findAllUsers();
    }

    @GetMapping("/users/{phone}")
    public Mono<UserInfoDto> findUserByPhone(@PathVariable("phone") String phone) {
        return userInfoService.findUserByPhone(phone);
    }
}
