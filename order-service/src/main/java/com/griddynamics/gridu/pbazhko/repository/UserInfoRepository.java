package com.griddynamics.gridu.pbazhko.repository;

import com.griddynamics.gridu.pbazhko.model.UserInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserInfoRepository extends ReactiveMongoRepository<UserInfo, String> {

    Mono<UserInfo> findByPhone(String phone);
}
