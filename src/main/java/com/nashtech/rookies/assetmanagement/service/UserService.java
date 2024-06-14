package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;

import java.util.List;

public interface UserService {
    ResponseDto<List<UserDto>> getAll();
    ResponseDto<UserDto> saveUser(CreateUserRequest request);
}
