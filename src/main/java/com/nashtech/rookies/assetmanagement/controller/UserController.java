package com.nashtech.rookies.assetmanagement.controller;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.ChangePasswordRequest;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.request.UpdateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.request.User.UserGetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.UserService;

import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Object> getAll(@Valid UserGetRequest requestParams,Pageable pageable, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto)authentication.getPrincipal();
        var users = userService.getAll(requestParams, pageable, requestUser);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<UserDto>> getUserById(@PathVariable("id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<UserDto>> saveUser(@RequestBody CreateUserRequest request, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto)authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(request, requestUser));
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseDto<UserDto>> editUser(@RequestBody UpdateUserRequest request, @PathVariable(name = "id") Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(request, userId));
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseDto<Void>> changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication authentication) {
        var userId = ((UserDetailsDto) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(userId, request));
    }


}
