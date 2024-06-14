package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.UserService;
import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ResponseDto<List<UserDto>> getAll() {
        return ResponseDto.<List<UserDto>>builder()
                .data(userMapper.entitiesToDtos(userRepository.findAll()))
                .message("Get all users successfully.")
                .build();
    }

    @Override
    public ResponseDto<UserDto> saveUser(CreateUserRequest request) {
        var mappedUser = userMapper.createUserRequestToEntity(request);
        mappedUser.setGender(GenderConstant.MALE);
        mappedUser.setLocation(LocationConstant.HCM);
        mappedUser.setStatus(StatusConstant.ACTIVE);
        var user = userRepository.save(mappedUser);

        return ResponseDto.<UserDto>builder()
                .data(userMapper.entityToDto(user))
                .message("Create user successfully.")
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsernameAndStatus(username, StatusConstant.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return userMapper.entityToUserDetailsDto(user);
    }
}
