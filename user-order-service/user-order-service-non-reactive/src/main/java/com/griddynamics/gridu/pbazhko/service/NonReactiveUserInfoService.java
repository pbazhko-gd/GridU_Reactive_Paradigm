package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.exception.UserNotFoundException;
import com.griddynamics.gridu.pbazhko.mapper.UserInfoMapper;
import com.griddynamics.gridu.pbazhko.repository.NonReactiveUserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NonReactiveUserInfoService implements UserInfoService<List<UserInfoDto>, UserInfoDto> {

    private final NonReactiveUserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;

    public List<UserInfoDto> findAllUsers() {
        return userInfoRepository.findAll().stream()
            .map(userInfoMapper::toDto)
            .peek(user -> log.info("Found user {}", user))
            .toList();
    }

    public UserInfoDto findUserById(String userId) {
        return userInfoRepository.findById(userId)
            .map(userInfoMapper::toDto)
            .map(user -> {
                log.info("Found user {} by userId '{}'", user, userId);
                return user;
            })
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
