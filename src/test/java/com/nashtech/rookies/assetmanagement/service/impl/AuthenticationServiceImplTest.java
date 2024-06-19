package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.LoginResponse;
import com.nashtech.rookies.assetmanagement.entity.Token;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.InvalidUserCredentialException;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.TokenRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.AuthenticationService;
import com.nashtech.rookies.assetmanagement.service.JwtService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;

    private AuthenticationRequest request;
    private String accessToken;
    private String username;
    private Integer userId;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(userRepository, tokenRepository, jwtService, authenticationManager, userMapper);
        userId = 1;
        username = "username";
        accessToken = "accessToken";
        request = AuthenticationRequest.builder()
                .username(username)
                .password("password")
                .build();
    }

    @Test
    public void testAuthenticate_whenValidCredentials_thenSuccess() {
        when(userRepository.findByUsernameAndStatus(username, StatusConstant.ACTIVE))
                .thenReturn(Optional.of(User.builder().id(userId).build()));
        when(userMapper.entityToUserDetailsDto(any(User.class)))
                .thenReturn(UserDetailsDto.builder().id(userId).build());
        when(jwtService.generateToken(any(UserDetailsDto.class)))
                .thenReturn(accessToken);
        when(tokenRepository.findAllByUserId(userId))
                .thenReturn(List.of());
        when(tokenRepository.save(any(Token.class)))
                .thenReturn(Token.builder().build());
        when(userMapper.entityToLoginResponse(any(User.class)))
                .thenReturn(LoginResponse.builder().username(username).build());

        var resData = authenticationService.authenticate(request);

        assertThat(resData.getMessage()).isEqualTo("User login successfully.");
        assertThat(resData.getData().getToken()).isEqualTo(accessToken);

        ArgumentCaptor<Token> captorToken = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository, times(1)).save(captorToken.capture());
        assertThat(captorToken.getValue().getUser().getId()).isEqualTo(userId);
        assertThat(captorToken.getValue().getToken()).isEqualTo(accessToken);
    }

    @Test
    public void testAuthenticate_whenInvalidCredentials_thenInvalidUserCredentialException() {
        when(userRepository.findByUsernameAndStatus(username, StatusConstant.ACTIVE))
                .thenReturn(Optional.empty());

        var ex = assertThrows(InvalidUserCredentialException.class, () ->
                authenticationService.authenticate(request));

        assertThat(ex.getMessage()).isEqualTo("Username or password is incorrect. Please try again.");
    }
}