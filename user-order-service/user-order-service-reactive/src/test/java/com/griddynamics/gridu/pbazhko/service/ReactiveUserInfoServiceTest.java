package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.exception.UserNotFoundException;
import com.griddynamics.gridu.pbazhko.mapper.UserInfoMapper;
import com.griddynamics.gridu.pbazhko.mapper.UserInfoMapperImpl;
import com.griddynamics.gridu.pbazhko.model.UserInfo;
import com.griddynamics.gridu.pbazhko.repository.ReactiveUserInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactiveUserInfoServiceTest {

    @Mock
    private ReactiveUserInfoRepository userInfoRepository;

    @Spy
    private UserInfoMapper userInfoMapper = new UserInfoMapperImpl();

    @InjectMocks
    private ReactiveUserInfoService userInfoService;

    private static final UserInfo USER_1 = new UserInfo("1", "User 1", "123");
    private static final UserInfo USER_2 = new UserInfo("2", "User 2", "456");

    @Test
    void findAllUsers_no_users_exist() {
        when(userInfoRepository.findAll()).thenReturn(Flux.empty());
        StepVerifier.create(userInfoService.findAllUsers())
            .verifyComplete();
    }

    @Test
    void findAllUsers_two_users_exist() {
        when(userInfoRepository.findAll()).thenReturn(Flux.just(USER_1, USER_2));
        StepVerifier.create(userInfoService.findAllUsers())
            .assertNext(dto -> {
                assertEquals(USER_1.getId(), dto.getId());
                assertEquals(USER_1.getName(), dto.getName());
                assertEquals(USER_1.getPhone(), dto.getPhone());
            })
            .assertNext(dto -> {
                assertEquals(USER_2.getId(), dto.getId());
                assertEquals(USER_2.getName(), dto.getName());
                assertEquals(USER_2.getPhone(), dto.getPhone());
            })
            .verifyComplete();
    }

    @Test
    void findUserById_user_not_exists() {
        when(userInfoRepository.findById(USER_1.getId())).thenReturn(Mono.empty());
        StepVerifier.create(userInfoService.findUserById(USER_1.getId()))
            .verifyError(UserNotFoundException.class);
    }

    @Test
    void findUserById_user_exists() {
        when(userInfoRepository.findById(USER_1.getId())).thenReturn(Mono.just(USER_1));
        StepVerifier.create(userInfoService.findUserById(USER_1.getId()))
            .assertNext(dto -> {
                assertEquals(USER_1.getId(), dto.getId());
                assertEquals(USER_1.getName(), dto.getName());
                assertEquals(USER_1.getPhone(), dto.getPhone());
            })
            .verifyComplete();
    }
}
