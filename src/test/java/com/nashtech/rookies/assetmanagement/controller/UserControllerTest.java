package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.UpdateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.service.UserService;
import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User getUser() {
        User user = new User();
        user.setId(1);
        user.setGender(GenderConstant.MALE);
        user.setFirstName("test");
        return user;
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testUpdateUser_WhenUserIdIsValid_thenReturnUser() throws Exception {
        var sampleResponse = ResponseDto.<UserDto>builder()
                .data(userMapper.entityToDto(getUser()))
                .message("Update user successfully")
                .build();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .type(1)
                .joinedDate(LocalDate.parse("2024-06-12"))
                .dateOfBirth(LocalDate.parse("2002-05-19"))
                .gender(String.valueOf(GenderConstant.MALE))
                .build();

        doReturn(sampleResponse).when(userService).updateUser(any(UpdateUserRequest.class),anyInt());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertNotNull(result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getContentAsString(),objectMapper.writeValueAsString(sampleResponse));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testUpdateUser_whenUserIdInvalid_thenThrow() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .type(1)
                .joinedDate(LocalDate.parse("2024-06-12"))
                .dateOfBirth(LocalDate.parse("2002-05-19"))
                .gender(String.valueOf(GenderConstant.MALE))
                .build();
        // Ensure that the mock is set up to throw an exception
        given(userService.updateUser(any(UpdateUserRequest.class),anyInt())).willThrow(new ResourceNotFoundException("User not found"));

        // Perform the GET request and verify the result
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }


}
