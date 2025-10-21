package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.mapper.UserInfoMapper;
import com.griddynamics.gridu.pbazhko.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;

    public Flux<UserInfoDto> findAllUsers() {
        return userInfoRepository.findAll()
                .map(userInfoMapper::toDto)
                .log();
    }

    public Mono<UserInfoDto> findUserById(String id) {
        return userInfoRepository.findById(id)
                .map(userInfoMapper::toDto)
                .log();
    }
}
