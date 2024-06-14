package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface UserService extends UserDetailsService {
    ResponseDto<PageableDto<List<UserDto>>> getAll(Pageable pageable);
    ResponseDto<UserDto> getUserById(Integer id);
    ResponseDto<UserDto> saveUser(CreateUserRequest request);
}
