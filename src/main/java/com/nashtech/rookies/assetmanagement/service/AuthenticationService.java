package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.LoginResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;

public interface AuthenticationService {
    ResponseDto<LoginResponse> authenticate(AuthenticationRequest request, String jwtToken);
}
