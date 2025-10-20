package com.griddynamics.gridu.pbazhko.mapper;

import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserInfoMapper {

    UserInfoDto toDto(UserInfo userInfo);
}
