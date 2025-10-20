package com.griddynamics.gridu.pbazhko.controller;

import com.griddynamics.gridu.pbazhko.config.MongoDBTestContainerConfig;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.model.UserInfo;
import com.griddynamics.gridu.pbazhko.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = MongoDBTestContainerConfig.Initializer.class)
class UserInfoControllerTest {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private WebTestClient webClient;

    private final static UserInfo USER_1 = new UserInfo("1", "User 1", "123");
    private final static UserInfo USER_2 = new UserInfo("2", "User 2", "456");

    @BeforeEach
    void setup() {
        userInfoRepository.deleteAll().block();
    }

    @Test
    void findAllUsers_no_users_exist() {
        webClient.get().uri("/users").exchange()
                .expectStatus().isOk()
                .expectBodyList(UserInfoDto.class).isEqualTo(Collections.emptyList());
    }

    @Test
    void findAllUsers_two_users_exist() {
        userInfoRepository.save(USER_1).block();
        userInfoRepository.save(USER_2).block();

        webClient.get().uri("/users").exchange()
                .expectStatus().isOk()
                .expectBodyList(UserInfoDto.class)
                .value(dtos -> {
                    assertThat(dtos).hasSize(2);
                    assertThat(dtos.get(0).getId()).isEqualTo(USER_1.getId());
                    assertThat(dtos.get(1).getId()).isEqualTo(USER_2.getId());
                    assertThat(dtos.get(0).getName()).isEqualTo(USER_1.getName());
                    assertThat(dtos.get(1).getName()).isEqualTo(USER_2.getName());
                    assertThat(dtos.get(0).getPhone()).isEqualTo(USER_1.getPhone());
                    assertThat(dtos.get(1).getPhone()).isEqualTo(USER_2.getPhone());
                });
    }

    @Test
    void findUserByPhone_user_not_exists() {
        userInfoRepository.save(USER_2).block();
        webClient.get().uri("/users/{phone}", USER_1.getPhone()).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findUserByPhone_user_exists() {
        userInfoRepository.save(USER_1).block();
        webClient.get().uri("/users/{phone}", USER_1.getPhone()).exchange()
                .expectStatus().isOk()
                .expectBody(UserInfoDto.class)
                .value(dto -> {
                    assertThat(dto.getId()).isEqualTo(USER_1.getId());
                    assertThat(dto.getName()).isEqualTo(USER_1.getName());
                    assertThat(dto.getPhone()).isEqualTo(USER_1.getPhone());
                });
    }
}
