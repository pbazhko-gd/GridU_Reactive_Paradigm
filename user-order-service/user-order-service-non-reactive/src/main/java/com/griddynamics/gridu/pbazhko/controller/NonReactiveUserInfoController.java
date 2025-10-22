package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class NonReactiveUserInfoController {

    private final UserInfoService<List<UserInfoDto>, UserInfoDto> userInfoService;

    @GetMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
    public List<UserInfoDto> findAllUsers() {
        return userInfoService.findAllUsers();
    }

    @GetMapping(value = "/users/{id}", produces = APPLICATION_JSON_VALUE)
    public UserInfoDto findUserById(@PathVariable("id") String id) {
        return userInfoService.findUserById(id);
    }
}
