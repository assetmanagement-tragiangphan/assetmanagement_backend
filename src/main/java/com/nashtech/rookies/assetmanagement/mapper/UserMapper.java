package com.nashtech.rookies.assetmanagement.mapper;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserMapper {
    @Mapping(target = "roleId", source = "user.role.id")
    UserDto entityToDto(User user);
    @Mapping(target = "roleName", source = "user.role.name")
    UserDetailsDto entityToUserDetailsDto(User user);
    @Mapping(target = "roleId", source = "user.role.id")
    List<UserDto> entitiesToDtos(List<User> users);
    @Mapping(target = "role.id", source = "request.roleId")
    User createUserRequestToEntity(CreateUserRequest request);
}
