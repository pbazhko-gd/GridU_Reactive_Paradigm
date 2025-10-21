package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.exception.UserNotFoundException;
import com.griddynamics.gridu.pbazhko.mapper.UserInfoMapper;
import com.griddynamics.gridu.pbazhko.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.griddynamics.gridu.pbazhko.util.MdcHelper.applyContextForMdc;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;

    public Flux<UserInfoDto> findAllUsers() {
        return userInfoRepository.findAll()
                .transform(applyContextForMdc())
                .map(userInfoMapper::toDto)
                .doOnNext(user -> log.info("Found user {}", user))
                .log();
    }

    public Mono<UserInfoDto> findUserById(String userId) {
        return userInfoRepository.findById(userId)
                .transform(applyContextForMdc())
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .map(userInfoMapper::toDto)
                .doOnNext(user -> log.info("Found user {} by userId '{}'", user, userId))
                .log();
    }
}
