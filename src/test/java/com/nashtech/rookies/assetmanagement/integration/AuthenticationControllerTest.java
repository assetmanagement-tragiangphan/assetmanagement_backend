package com.nashtech.rookies.assetmanagement.integration;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private AuthenticationRequest request;

    @BeforeEach
    void setUp() {
        request = AuthenticationRequest.builder()
                .username("nguyenp")
                .password("Nguyen@123")
                .build();
    }

    @Test
    public void testSignIn_whenValidRequest_thenSuccess() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

}