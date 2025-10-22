package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping(value = "/users", produces = APPLICATION_NDJSON_VALUE)
    public Flux<UserInfoDto> findAllUsers() {
        return userInfoService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    public Mono<UserInfoDto> findUserById(@PathVariable("id") String id) {
        return userInfoService.findUserById(id);
    }
}
