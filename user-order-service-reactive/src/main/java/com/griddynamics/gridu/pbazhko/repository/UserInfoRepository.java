package com.griddynamics.gridu.pbazhko.repository;

import com.griddynamics.gridu.pbazhko.model.UserInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserInfoRepository extends ReactiveMongoRepository<UserInfo, String> {
}
