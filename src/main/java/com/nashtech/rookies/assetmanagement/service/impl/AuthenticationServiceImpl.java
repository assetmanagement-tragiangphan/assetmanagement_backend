package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.LoginResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Token;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.InvalidUserCredentialException;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.TokenRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.AuthenticationService;
import com.nashtech.rookies.assetmanagement.service.JwtService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public ResponseDto<LoginResponse> authenticate(AuthenticationRequest request, String jwtToken ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsernameAndStatus(request.getUsername(), StatusConstant.ACTIVE)
                .orElseThrow(() -> new InvalidUserCredentialException("Username or password is incorrect. Please try again."));

        var userDetailsDto = userMapper.entityToUserDetailsDto(user);
        var isTokenValid = tokenRepository.findByToken(jwtToken).isPresent();
        var accessToken = isTokenValid ? jwtToken : jwtService.generateToken(userDetailsDto) ;
        //TODO: not returning refresh token
//        var refreshToken = jwtService.generateRefreshToken(userDetailsDto);

        revokeAllUserToken(user);
        saveUserToken(user, accessToken);

        var res = userMapper.entityToLoginResponse(user);
        res.setToken(accessToken);

        return ResponseDto.<LoginResponse>builder()
                .data(res)
                .message("User login successfully.")
                .build();
    }

    private void saveUserToken(User user, String jwt) {
        var token = Token.builder()
                .user(user)
                .token(jwt)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserToken(User user) {
        var validUserTokens = tokenRepository.findAllByUserId(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }

        tokenRepository.deleteAll(validUserTokens);
    }
}
