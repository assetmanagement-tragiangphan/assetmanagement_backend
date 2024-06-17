package com.nashtech.rookies.assetmanagement.controller;

import com.nashtech.rookies.assetmanagement.config.CookieProperties;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.LoginResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final CookieProperties cookieProperties;

    @PostMapping("/signIn")
    public ResponseEntity<ResponseDto<LoginResponse>> signIn(@RequestBody @Valid AuthenticationRequest request) {
        var resData = authenticationService.authenticate(request);
        final ResponseCookie responseCookie = ResponseCookie
                .from(cookieProperties.getName(), resData.getData().getToken())
                .httpOnly(cookieProperties.getHttpOnly())
                .path(cookieProperties.getPath())
                .maxAge(cookieProperties.getMaxAge())
                .sameSite(cookieProperties.getSameSite())
                .secure(cookieProperties.getSecure())
                .build();

        var dto = resData.getData();
        dto.setToken(null);
        resData.setData(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resData);
    }
}
