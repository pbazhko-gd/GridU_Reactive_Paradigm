package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import com.griddynamics.gridu.pbazhko.exception.UserNotFoundException;
import com.griddynamics.gridu.pbazhko.model.UserInfo;
import com.griddynamics.gridu.pbazhko.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class UserInfoServiceTest {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserInfoService userInfoService;

    private final static UserInfo USER_1 = new UserInfo("1", "User 1", "123");
    private final static UserInfo USER_2 = new UserInfo("2", "User 2", "456");

    @BeforeEach
    void setup() {
        userInfoRepository.deleteAll().block();
    }

    @Test
    void findAllUsers_no_users_exist() {
        StepVerifier.create(userInfoService.findAllUsers())
                .verifyComplete();
    }

    @Test
    void findAllUsers_two_users_exist() {
        userInfoRepository.save(USER_1).block();
        userInfoRepository.save(USER_2).block();
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
        userInfoRepository.save(USER_2).block();
        StepVerifier.create(userInfoService.findUserById(USER_1.getId()))
                .verifyError(UserNotFoundException.class);
    }

    @Test
    void findUserById_user_exists() {
        userInfoRepository.save(USER_1).block();
        StepVerifier.create(userInfoService.findUserById(USER_1.getId()))
                .assertNext(dto -> {
                    assertEquals(USER_1.getId(), dto.getId());
                    assertEquals(USER_1.getName(), dto.getName());
                    assertEquals(USER_1.getPhone(), dto.getPhone());
                })
                .verifyComplete();
    }
}
