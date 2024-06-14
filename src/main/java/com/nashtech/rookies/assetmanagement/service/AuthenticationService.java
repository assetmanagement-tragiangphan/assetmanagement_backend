package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;

public interface AuthenticationService {
    ResponseDto<String> authenticate(AuthenticationRequest request);
}
