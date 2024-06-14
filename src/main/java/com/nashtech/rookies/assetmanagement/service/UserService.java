package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    ResponseDto<List<UserDto>> getAll();
    ResponseDto<UserDto> saveUser(CreateUserRequest request);
}
