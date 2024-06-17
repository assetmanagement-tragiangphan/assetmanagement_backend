package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.config.CookieProperties;
import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.LoginResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private CookieProperties cookieProperties;

    private AuthenticationRequest request;

    @BeforeEach
    void setUp() {
        request = AuthenticationRequest.builder()
                .username("username")
                .password("password")
                .build();
    }

    @Test
    public void testSignIn_whenValidRequest_thenSuccess() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(ResponseDto.<LoginResponse>builder()
                        .data(LoginResponse.builder().username("username").token("token").build())
                        .message("Success")
                        .build());
        when(cookieProperties.getName()).thenReturn("name");
        when(cookieProperties.getHttpOnly()).thenReturn(true);
        when(cookieProperties.getPath()).thenReturn("/");
        when(cookieProperties.getMaxAge()).thenReturn(1l);
        when(cookieProperties.getSameSite()).thenReturn("Lax");
        when(cookieProperties.getSecure()).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

}