package com.griddynamics.gridu.pbazhko.repository;

import com.griddynamics.gridu.pbazhko.model.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NonReactiveUserInfoRepository extends MongoRepository<UserInfo, String> {
}
